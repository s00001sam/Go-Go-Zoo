package com.sam.gogozoo

import android.Manifest
import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.storage.FirebaseStorage
import com.google.zxing.integration.android.IntentIntegrator
import com.sam.gogozoo.PermissionUtils.PermissionDeniedDialog.Companion.newInstance
import com.sam.gogozoo.PermissionUtils.isPermissionGranted
import com.sam.gogozoo.PermissionUtils.requestPermission
import com.sam.gogozoo.data.*
import com.sam.gogozoo.data.animal.FireAnimal
import com.sam.gogozoo.data.animal.LocalAnimal
import com.sam.gogozoo.data.area.FireArea
import com.sam.gogozoo.data.area.LocalArea
import com.sam.gogozoo.data.calendar.LocalCalendar
import com.sam.gogozoo.data.facility.FireFacility
import com.sam.gogozoo.data.facility.LocalFacility
import com.sam.gogozoo.databinding.ActivityMainBinding
import com.sam.gogozoo.databinding.HeaderDrawerBinding
import com.sam.gogozoo.ext.getVmFactory
import com.sam.gogozoo.util.CurrentFragmentType
import com.sam.gogozoo.util.Logger
import com.sam.gogozoo.util.Util.listAnimalToJson
import com.sam.gogozoo.util.Util.listAreaToJson
import com.sam.gogozoo.util.Util.listCalendarToJson
import com.sam.gogozoo.util.Util.listFacilityToJson
import com.sam.gogozoo.util.Util.toGeo
import com.sam.gogozoo.util.Util.toLatlng
import com.sam.gogozoo.util.Util.toLatlngs
import com.sam.gogozoo.util.Util.writeToFile
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import nl.joery.animatedbottombar.AnimatedBottomBar
import com.sam.gogozoo.util.Util.toTimeInMills
import java.io.File
import java.util.*
import com.sam.gogozoo.util.Util.getDinstance
import com.sam.gogozoo.util.Util.toTimeString

class MainActivity : AppCompatActivity(),GoogleMap.OnMyLocationButtonClickListener,
    GoogleMap.OnMyLocationClickListener, OnMapReadyCallback,
    ActivityCompat.OnRequestPermissionsResultCallback{

    val viewModel by viewModels<MainViewModel> { getVmFactory() }
    private lateinit var binding: ActivityMainBinding
    private lateinit var drawerLayout: DrawerLayout
    private var actionBarDrawerToggle: ActionBarDrawerToggle? = null
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var bindingNavHeader: HeaderDrawerBinding
    private var mGalleryFile: File? = null
    var receiver: BroadcastReceiver? = null

    // Create a Coroutine scope using a job to be able to cancel when needed
    private var viewModelJob = Job()

    // the Coroutine runs using the Main (UI) dispatcher
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    val needNavigation = MutableLiveData<Boolean>().apply {
        value = false
    }
    val info = MutableLiveData<NavInfo>()
    val markInfo = MutableLiveData<NavInfo>()
    val selectFacility = MutableLiveData<List<LocalFacility>>()
    val selectRoute = MutableLiveData<Route>()
    val endRoute = MutableLiveData<Route>()

    val selectNavAnimal = MutableLiveData<List<LocalFacility>>()

    override fun onCreate(savedInstanceState: Bundle?) {

        Logger.d("mockisfirsttime=${MockData.isfirstTime}")

        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        val navController = Navigation.findNavController(this, R.id.myNavHostFragment)

        Logger.d("isfirstTime=${MockData.isfirstTime}")

        //start home page introduction
        if(MockData.isfirstTime) {
            navController.navigate(R.id.introSearchDialog)
            MockData.isfirstTime = false
        }

        changeTitleAndPage()
        setupDrawer()

        needNavigation.observe(this, Observer {
            Logger.d("need=${needNavigation.value}")
        })

        viewModel.facilityResult.observe(this, Observer {
            Logger.d( "facility=$it")
            val listFacility = mutableListOf<LocalFacility>()
            it.result.results.forEach { facility ->
                val fireFacility = FireFacility()
                val listGeo = mutableListOf<GeoPoint>()
                facility.geo.toLatlngs().forEach {
                    listGeo.add(it.toGeo())
                }
                fireFacility.idNum = facility.id
                fireFacility.name = facility.name
                fireFacility.geo = listGeo
                fireFacility.location = facility.location
                fireFacility.category = facility.category
                fireFacility.item = facility.item
//                viewModel.publishFacility(fireFacility)
                val localFacility = LocalFacility()
                localFacility.idNum = facility.id
                localFacility.name = facility.name
                localFacility.geo = facility.geo.toLatlngs()
                localFacility.location = facility.location
                localFacility.category = facility.category
                localFacility.item = facility.item
                localFacility.image = viewModel.getFacilityImage(facility)
                listFacility.add(localFacility)
            }
            Logger.d("listFacility=$listFacility")
            MockData.localFacility = listFacility
            Logger.d("localFacility=${MockData.localFacility}")
            viewModel.localFacilityInMain.value = listFacility
        })

        viewModel.areaResult.observe(this, Observer {
            Logger.d( "areaData=$it")
            val listArea = mutableListOf<LocalArea>()
            it.result.results.forEach {area ->
                val fireArea = FireArea()
                val listGeo = mutableListOf<GeoPoint>()
                area.geo.toLatlngs().forEach {
                    listGeo.add(it.toGeo())
                }
                fireArea.idNum = area.id
                fireArea.geo = listGeo
                fireArea.category = area.category
                fireArea.name = area.name
                fireArea.infomation = area.infomation
                fireArea.picture = area.picture
                fireArea.url = area.url
//                viewModel.publishAreas(fireArea)
                val localArea = LocalArea()
                localArea.idNum = area.id
                localArea.geo = area.geo.toLatlngs()
                localArea.category = area.category
                localArea.name = area.name
                localArea.infomation = area.infomation
                localArea.picture = area.picture
                localArea.url = area.url
                val imageList = MockData.areas.filter { it.title == area.name }
                localArea.image = imageList[0].drawable
                listArea.add(localArea)
            }
            Logger.d("listArea=$listArea")
            MockData.localAreas = listArea
            Logger.d("localAreas=${MockData.localAreas}")
            viewModel.localAreaInMain.value = listArea
            viewModel.getNavInfoAreas()
        })

        viewModel.animalResult.observe(this, Observer {
            Logger.d("samaniresult=$it")
            val listLocalAnimal = mutableListOf<LocalAnimal>()
            it.result.results.forEach {animal ->
                Logger.d("animlist=$animal")

                val listGeo = mutableListOf<GeoPoint>()
                animal.geo.toLatlngs().forEach { latlng ->
                    listGeo.add(latlng.toGeo())
                }
                Logger.d( "geolist=$listGeo")
                val listPicture = listOf<String>(animal.picture1, animal.picture2, animal.picture3, animal.picture4)
                val pictures = mutableListOf<String>()
                listPicture.forEach {
                    if (it != ""){
                        pictures.add(it)
                    }
                }
                Logger.d("listpicture=$pictures")

                val fireAnimal = FireAnimal()
                fireAnimal.clas = animal.clas
                fireAnimal.code = animal.code
                fireAnimal.conservation = animal.conservation
                fireAnimal.diet = animal.diet
                fireAnimal.distribution = animal.distribution
                fireAnimal.nameCh = animal.nameCh
                fireAnimal.nameEn = animal.nameEn
                fireAnimal.location = animal.location
                fireAnimal.phylum = animal.phylum
                fireAnimal.order = animal.order
                fireAnimal.family = animal.family
                fireAnimal.interpretation = animal.interpretation
                fireAnimal.video = animal.video
                fireAnimal.geos = listGeo
                fireAnimal.pictures = pictures
                Logger.d("picturesB=${fireAnimal.pictures}")
                Logger.d("samvideo=${fireAnimal.video}")
                Logger.d("fireAnimal=$fireAnimal")
//                viewModel.publishAnimals(fireAnimal)
                val localAnimal = LocalAnimal()
                localAnimal.clas = animal.clas
                localAnimal.code = animal.code
                localAnimal.conservation = animal.conservation
                localAnimal.diet = animal.diet
                localAnimal.distribution = animal.distribution
                localAnimal.nameCh = animal.nameCh
                localAnimal.nameEn = animal.nameEn
                localAnimal.nameLat = animal.nameLat
                localAnimal.location = animal.location
                localAnimal.phylum = animal.phylum
                localAnimal.order = animal.order
                localAnimal.family = animal.family
                localAnimal.interpretation = animal.interpretation
                localAnimal.video = animal.video
                localAnimal.geos = animal.geo.toLatlngs()
                localAnimal.pictures = pictures
                listLocalAnimal.add(localAnimal)
            }
            Logger.d("listLocalAnimal=$listLocalAnimal")
            MockData.localAnimals = listLocalAnimal
            Logger.d("localAnimals=${MockData.localAnimals}")
            viewModel.localAnimalInMain.value = listLocalAnimal
            viewModel.getNavInfoAnimals()
        })

        viewModel.calendarResult.observe(this, Observer {
            Logger.d("calendarResult=$it")
            val listCalender = mutableListOf<LocalCalendar>()
            it.result.results.forEach {calendar ->
                val localCalendar = LocalCalendar()
                localCalendar.id = calendar.id
                localCalendar.title = calendar.title
                localCalendar.start = calendar.start.toTimeInMills()
                localCalendar.end = calendar.end.toTimeInMills()
                localCalendar.geo = calendar.geo.toLatlngs()
                localCalendar.location = calendar.location
                localCalendar.brief = calendar.brief
                localCalendar.time = calendar.time
                localCalendar.category = calendar.category
                localCalendar.site = calendar.site
                listCalender.add(localCalendar)
            }
            Logger.d("listCalendar=$listCalender")
            MockData.localCalendars = listCalender
            Logger.d("mockLocalCalendars=${MockData.localCalendars}")
            viewModel.localCalendarsInMain.value = listCalender
        })


        viewModel.localAnimalInMain.observe(this, Observer {
            Logger.d("localAnimalInMain=$it")
            val localAnimalString = listAnimalToJson(it)
            Logger.d("localAnimalString=$localAnimalString")
            writeToFile(localAnimalString, ZooApplication.appContext, "animal.txt")
        })

        viewModel.localAreaInMain.observe(this, Observer {
            it?.let {
                Logger.d( "localAreaInMain=$it")
                val localAreaString = listAreaToJson(it)
                Logger.d("localAreaString=$localAreaString")
                writeToFile(localAreaString, ZooApplication.appContext, "area.txt")
            }
        })

        viewModel.localFacilityInMain.observe(this, Observer {
            it?.let {
                Logger.d("localFacilityInMain=$it")
                val localFacilityString = listFacilityToJson(it)
                Logger.d("localFacilityString=$localFacilityString")
                writeToFile(localFacilityString, ZooApplication.appContext, "facility.txt")
            }
        })

        viewModel.localCalendarsInMain.observe(this, Observer {list ->
            list?.let {
                Logger.d("localCalendarsInMain=$it")
                val localCalendarsString = listCalendarToJson(it)
                Logger.d("localCalendarsString=$localCalendarsString")
                writeToFile(localCalendarsString, ZooApplication.appContext, "calendars.txt")
            }
        })

        viewModel.currentFragmentType.observe(this, Observer {
            if (it == CurrentFragmentType.HOME){
                binding.bottomNavView.selectTabById(R.id.home)
            }else if (it == CurrentFragmentType.LIST){
                binding.bottomNavView.selectTabById(R.id.list)
            }
        })

        binding.buttonSearch.setOnClickListener {
            navController.navigate(R.id.searchDialog)
        }

        viewModel.fireSchedule.observe(this, Observer {
            Logger.d("fireRoute=$it")
            val listRoute = mutableListOf<Route>()
            it.forEach {route ->
                val listNav = mutableListOf<NavInfo>()
                route.list.forEach {fireNav ->
                    var nav = NavInfo()
                    nav.title = fireNav.title
                    nav.meter = fireNav.meter
                    nav.latLng = fireNav.geoPoint.toLatlng()
                    nav.imageUrl = fireNav.imageUrl
                    nav.image = fireNav.image
                    listNav.add(nav)
                }
                val route = Route(route.id, route.name, route.owners, route.open, listNav)
                listRoute.add(route)
                viewModel.publishRoute(route)
            }
            MockData.routes = listRoute
            Logger.d("mockroutes=${MockData.routes}")
        })

        viewModel.user.observe(this, Observer {
            viewModel.publishFriend(UserManager.user.email, it)
        })

        viewModel.checkUser.observe(this, Observer {
            Logger.d("checkuser=$it")
            if (it == User()){
                toast(getString(R.string.cant_find_user))
            }else{
                val filter = UserManager.friends.filter { friend -> friend.email == it.email }
                if (filter == listOf<User>())
                    viewModel.showAddFriend(it.email, this)
                else
                    toast("和 ${it.email} 早已成為同伴")
            }
        })

        viewModel.me.observe(this, Observer {
            Logger.d("meUser=$it")
            UserManager.user.picture = it.picture
            bindImageCircle(bindingNavHeader.imagePhoto, it.picture)
            Control.getPhoto = true
        })

        viewModel.myPhoto.observe(this, Observer {
            viewModel.publishUser(UserManager.user)
            UserManager.friends.forEach {friend ->
                viewModel.publishFriend(friend.email, UserManager.user)
            }
        })

        binding.layoutStepCount.setOnClickListener {
            navController.navigate(R.id.stepDialog)
        }

        Control.step.observe(this, Observer {
            Logger.d("controlStep=$it")
            it?.let {
                val step = StepInfo()
                step.step = it
                step.kilometer = it*(0.7)*(0.001)
                step.kcal = it*0.035
                UserManager.newStep = step
                viewModel.nowStepInfo.value = step

                binding.textStep.text = "$it 步"
            }
        })

        Control.timeCount.observe(this, Observer {
            Logger.d("timeCount=$it")
            it?.let {
                viewModel.timeCount.value = it
                UserManager.newStep.time = it
                Logger.d("UserTime=${UserManager.newStep.time}")

                val min = ((it)/60).toTimeString()
                val sec = ((it)% 60).toTimeString()
                binding.textTime.text = "$min : $sec"
            }
        })

        if (MockData.allMarkers == listOf<NavInfo>()){
            viewModel.getDataAnimal()
            viewModel.getDataArea()
            viewModel.getDataFacility()
            viewModel.getDataCalendar()
        }

        setupNavController()

        if (!Control.hasNotification) {
            startService()
        }

    }

    /**
     * override back key for the drawer design
     */
    override fun onBackPressed() {
        val navController = Navigation.findNavController(this, R.id.myNavHostFragment)
        val navigationCount = supportFragmentManager.findFragmentById(R.id.myNavHostFragment)?.childFragmentManager?.backStackEntryCount
        Logger.d("navigationCount=$navigationCount")

        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawer(GravityCompat.START)
        } else if(viewModel.currentFragmentType.value == CurrentFragmentType.DETAILAREA || viewModel.currentFragmentType.value == CurrentFragmentType.DETAILANIMAL){
            navController.navigate(NavigationDirections.navigateToListFragment())
        }else if (navigationCount == 0){
            viewModel.showLeaveOrNot(this, this)
        }else{
            super.onBackPressed()
        }
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        if (currentFocus != null) {
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(currentFocus!!.windowToken, 0)
        }
        return super.dispatchTouchEvent(ev)
    }

    private fun setupNavController() {
        findNavController(R.id.myNavHostFragment).addOnDestinationChangedListener { navController: NavController, _: NavDestination, _: Bundle? ->
            viewModel.currentFragmentType.value = when (navController.currentDestination?.id) {
                R.id.homeFragment -> CurrentFragmentType.HOME
                R.id.listFragment -> CurrentFragmentType.LIST
                R.id.detailAreaFragment -> CurrentFragmentType.DETAILAREA
                R.id.detailAnimalFragment -> CurrentFragmentType.DETAILANIMAL
                else -> viewModel.currentFragmentType.value
            }
        }
    }

    private fun setupDrawer() {

        // set up toolbar
        val navController = this.findNavController(R.id.myNavHostFragment)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.title = null

        appBarConfiguration = AppBarConfiguration(navController.graph, binding.drawerLayout)
        NavigationUI.setupWithNavController(binding.drawerNavView, navController)

        binding.drawerLayout.fitsSystemWindows = true
        binding.drawerLayout.clipToPadding = false
//        binding.drawerLayout.defaultFocusHighlightEnabled = false

        actionBarDrawerToggle = object : ActionBarDrawerToggle(
            this,
            binding.drawerLayout,
            binding.toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        ) {
            override fun onDrawerOpened(drawerView: View) {
                super.onDrawerOpened(drawerView)
            }
        }.apply {
            binding.drawerLayout.addDrawerListener(this)
            syncState()
        }

        // Set up header of drawer ui using data binding
        bindingNavHeader = HeaderDrawerBinding.inflate(
            LayoutInflater.from(this), binding.drawerNavView, false)

        bindingNavHeader.lifecycleOwner = this
        bindingNavHeader.viewModel = viewModel
        bindImageCircle(bindingNavHeader.imagePhoto, UserManager.user.picture)
        bindingNavHeader.imageChange.setOnClickListener {
                pickGalleryImage()
        }

        bindingNavHeader.textEmail.text = UserManager.user.email
        binding.drawerNavView.addHeaderView(bindingNavHeader.root)


    }

    private fun changeTitleAndPage() {

        val navController = Navigation.findNavController(this, R.id.myNavHostFragment)

        binding.bottomNavView.setOnTabSelectListener(object : AnimatedBottomBar.OnTabSelectListener{
            override fun onTabSelected(
                lastIndex: Int,
                lastTab: AnimatedBottomBar.Tab?,
                newIndex: Int,
                newTab: AnimatedBottomBar.Tab
            ) {
                when (newTab.id){
                    R.id.home ->
                        Handler().postDelayed(Runnable {
                        navController.navigate(NavigationDirections.navigateToHomeFragment(endRoute.value))
                        }, 300L)
                    R.id.list ->
                        navController.navigate(NavigationDirections.navigateToListFragment())
                }
            }
        })

    }

    //map
    private var permissionDenied = false
    lateinit var map: GoogleMap
    //get location
    private var PERMISSION_ID = 1000

    override fun onMapReady(googleMap: GoogleMap?) {
        map = googleMap ?: return
        map
//        map.setOnMyLocationButtonClickListener(this)
        map.setOnMyLocationClickListener(this)
        enableMyLocation()
        map.uiSettings.isMyLocationButtonEnabled = false

        map.let {
            val x = 0.0045
            val y = 0.004
            val cameraPosition =
                CameraPosition.builder().target(LatLng(24.998361-y, 121.581033+x)).zoom(16f).bearing(146f)
                    .build()
            it.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
            val boundWS = LatLng(24.993000, 121.582000)
            val boundEN = LatLng(24.998500, 121.589000)
            val bounds = LatLngBounds(boundWS, boundEN)
//            it.setLatLngBoundsForCameraTarget(bounds)
            it.setMinZoomPreference(15.6f)
        }
    }
    /**
     * Enables the My Location layer if the fine location permission has been granted.
     */
    private fun enableMyLocation() {
        if (!::map.isInitialized) return
        // [START maps_check_location_permission]
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED) {
            map.isMyLocationEnabled = true
        } else {
            // Permission to access the location is missing. Show rationale and request permission
            requestPermission(this, LOCATION_PERMISSION_REQUEST_CODE,
                Manifest.permission.ACCESS_FINE_LOCATION, true
            )
        }
        // [END maps_check_location_permission]
    }

    override fun onMyLocationButtonClick(): Boolean {
        Toast.makeText(this, "MyLocation button clicked", Toast.LENGTH_SHORT).show()
        // Return false so that we don't consume the event and the default behavior still occurs
        // (the camera animates to the user's current position).
        return false
    }

    override fun onMyLocationClick(location: Location) {
        Toast.makeText(this, "Current location:\n$location", Toast.LENGTH_LONG).show()
    }

    // [START maps_check_location_permission_result]
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode){
            LOCATION_PERMISSION_REQUEST_CODE ->{
                if (isPermissionGranted(permissions, grantResults, Manifest.permission.ACCESS_FINE_LOCATION)) {
                    // Enable the my location layer if the permission has been granted.
                    enableMyLocation()
                } else {
                    // Permission was denied. Display an error message
                    // [START_EXCLUDE]
                    // Display the missing permission error dialog when the fragments resume.
                    permissionDenied = true
                    // [END_EXCLUDE]
                }
            }
        }
    }

    // [END maps_check_location_permission_result]
    override fun onResumeFragments() {
        super.onResumeFragments()
        if (permissionDenied) {
            // Permission was not granted, display error dialog.
            showMissingPermissionError()
            permissionDenied = false
        }
    }

    /**
     * Displays a dialog with error message explaining that the location permission is missing.
     */
    private fun showMissingPermissionError() {
        newInstance(true).show(supportFragmentManager, "dialog")
    }

    //check user permission
    fun checkPermission(): Boolean {
        if (
            ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
            ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
        ){
            return true
        }
        return false
    }

    //check if the location of device is enable
    fun isLocationEnable():Boolean{
        var locationManager: LocationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER)
    }

    //QR Code & Image picker
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        Logger.d("requestCode=$requestCode")

            when(requestCode){
                49374 ->{
                    if (resultCode == Activity.RESULT_OK) {
                        val result =
                            IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
                        Log.d("sam", "resultQR=$result")
                        if (result != null) {
                            Log.d("sam", "result=${result.contents}")
                            if (result.contents == null) {
                                toast(getString(R.string.scan_error))
                                Logger.d("scanResult=null")
                            } else {
                                Logger.d("sscanResult=${result.contents}")
                                viewModel.checkUser(result.contents)
                            }
                        } else {
                            Logger.d("resultCancel")
                            super.onActivityResult(requestCode, resultCode, data)
                        }
                    }
                }
                else -> {
                    if (resultCode == Activity.RESULT_OK){
                        Logger.d("datauri2=${data?.data}")
                        bindingNavHeader.imagePhoto.setImageURI(data?.data)
                        data?.data?.let { uploadImage(it) }
                    }
                }
            }
    }

    fun pickGalleryImage() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, IMAGE_PICKER_CODE)
    }

    fun uploadImage(uri: Uri){
        val filename = UUID.randomUUID().toString()
        val ref = FirebaseStorage.getInstance().getReference("/images/$filename")
        ref.putFile(uri)
            .addOnSuccessListener {
                ref.downloadUrl.addOnSuccessListener {
                    Logger.d("downloadUrl=$it")
                    viewModel.myPhoto.value = it.toString()
                    UserManager.user.picture = it.toString()
                }
            }
    }

    override fun onStop() {
//        viewModel.publishSchedules()
//        MockData.routes.forEach {
//            viewModel.publishRecommendRoute(it)
//        }
        super.onStop()
    }

    fun getFriend(email: String){
        viewModel.checkUser(email)
    }

    fun startService() {
        val serviceIntent = Intent(this, StepService::class.java)
        ContextCompat.startForegroundService(this, serviceIntent)
    }

    fun stopService() {
        val serviceIntent = Intent(this, StepService::class.java)
        stopService(serviceIntent)
    }

    fun toast(text: String) {
        val toast = Toast(this)
        val view = layoutInflater.inflate(R.layout.toast, null)
        val textView = view.findViewById<TextView>(R.id.toastText)
        textView.text = text
        toast.view = view
        toast.duration = Toast.LENGTH_SHORT
        toast.setGravity(Gravity.TOP, -220, 100)
        toast.show()
    }

    companion object {
        /**
         * Request code for location permission request.
         *
         * @see .onRequestPermissionsResult
         */
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
        private const val PERMISSION_REQUEST = 10
        private const val GALLERY_IMAGE_REQ_CODE = 102
        private const val IMAGE_PICKER_CODE = 100
        private const val PERMISSION_PICK = 101
    }

}


