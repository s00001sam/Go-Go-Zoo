package com.sam.gogozoo

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
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
import androidx.navigation.ui.setupWithNavController
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.bottomnavigation.BottomNavigationItemView
import com.google.android.material.bottomnavigation.BottomNavigationMenuView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.firestore.GeoPoint
import com.sam.gogozoo.PermissionUtils.PermissionDeniedDialog.Companion.newInstance
import com.sam.gogozoo.PermissionUtils.isPermissionGranted
import com.sam.gogozoo.PermissionUtils.requestPermission
import com.sam.gogozoo.data.MockData
import com.sam.gogozoo.data.NavInfo
import com.sam.gogozoo.data.Schedule
import com.sam.gogozoo.data.animal.FireAnimal
import com.sam.gogozoo.data.animal.LocalAnimal
import com.sam.gogozoo.data.area.FireArea
import com.sam.gogozoo.data.area.LocalArea
import com.sam.gogozoo.data.facility.FireFacility
import com.sam.gogozoo.data.facility.LocalFacility
import com.sam.gogozoo.databinding.ActivityMainBinding
import com.sam.gogozoo.databinding.HeaderDrawerBinding
import com.sam.gogozoo.ext.getVmFactory
import com.sam.gogozoo.util.CurrentFragmentType
import com.sam.gogozoo.util.Util.listAnimalToJson
import com.sam.gogozoo.util.Util.listAreaToJson
import com.sam.gogozoo.util.Util.listFacilityToJson
import com.sam.gogozoo.util.Util.toGeo
import com.sam.gogozoo.util.Util.toLatlngs
import com.sam.gogozoo.util.Util.writeToFile
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import nl.joery.animatedbottombar.AnimatedBottomBar

class MainActivity : AppCompatActivity(),GoogleMap.OnMyLocationButtonClickListener,
    GoogleMap.OnMyLocationClickListener, OnMapReadyCallback,
    ActivityCompat.OnRequestPermissionsResultCallback{

    val viewModel by viewModels<MainViewModel> { getVmFactory() }
    private lateinit var binding: ActivityMainBinding
    private lateinit var drawerLayout: DrawerLayout
    private var actionBarDrawerToggle: ActionBarDrawerToggle? = null
    private lateinit var appBarConfiguration: AppBarConfiguration

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
    val selectAnimal = MutableLiveData<LocalAnimal>()
    val selectArea = MutableLiveData<LocalArea>()
    val selectRoute = MutableLiveData<Schedule>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        val navController = Navigation.findNavController(this, R.id.myNavHostFragment)



        changeTitleAndPage()
        setupDrawer()

        needNavigation.observe(this, Observer {
            Log.d("sam","need=${needNavigation.value}")
        })

        viewModel.facilityResult.observe(this, Observer {
            Log.d("sam", "facility=$it")
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
                listFacility.add(localFacility)
            }
            Log.d("sam","listFacility=$listFacility")
            MockData.localFacility = listFacility
            Log.d("sam","localFacility=${MockData.localFacility}")
            viewModel.localFacilityInMain.value = listFacility
        })

        viewModel.areaResult.observe(this, Observer {
            Log.d("sam", "areaData=$it")
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
            Log.d("sam","listArea=$listArea")
            MockData.localAreas = listArea
            Log.d("sam","localAreas=${MockData.localAreas}")
            viewModel.localAreaInMain.value = listArea
            viewModel.getNavInfoAreas()
        })

        viewModel.animalResult.observe(this, Observer {
            Log.d("sam", "samaniresult=$it")
            val listLocalAnimal = mutableListOf<LocalAnimal>()
            it.result.results.forEach {animal ->
                Log.d("sam", "animlist=$animal")

                val listGeo = mutableListOf<GeoPoint>()
                animal.geo.toLatlngs().forEach { latlng ->
                    listGeo.add(latlng.toGeo())
                }
                Log.d("sam", "geolist=$listGeo")
                val listPicture = listOf<String>(animal.picture1, animal.picture2, animal.picture3, animal.picture4)
                val pictures = mutableListOf<String>()
                listPicture.forEach {
                    if (it != ""){
                        pictures.add(it)
                    }
                }
                Log.d("sam","listpicture=$pictures")

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
                Log.d("sam","picturesB=${fireAnimal.pictures}")
                Log.d("sam","samvideo=${fireAnimal.video}")
                Log.d("sam","fireAnimal=$fireAnimal")
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
            Log.d("sam","listLocalAnimal=$listLocalAnimal")
            MockData.localAnimals = listLocalAnimal
            Log.d("sam","localAnimals=${MockData.localAnimals}")
            viewModel.localAnimalInMain.value = listLocalAnimal
            viewModel.getNavInfoAnimals()
        })

        viewModel.localAnimalInMain.observe(this, Observer {
            Log.d("sam","localAnimalInMain=$it")
            val localAnimalString = listAnimalToJson(it)
            Log.d("sam","localAnimalString=$localAnimalString")
            writeToFile(localAnimalString, ZooApplication.appContext, "animal.txt")
        })

        viewModel.localAreaInMain.observe(this, Observer {
            it?.let {
                Log.d("sam", "localAreaInMain=$it")
                val localAreaString = listAreaToJson(it)
                Log.d("sam","localAreaString=$localAreaString")
                writeToFile(localAreaString, ZooApplication.appContext, "area.txt")
            }
        })

        viewModel.localFacilityInMain.observe(this, Observer {
            it?.let {
                Log.d("sam", "localFacilityInMain=$it")
                val localFacilityString = listFacilityToJson(it)
                Log.d("sam","localFacilityString=$localFacilityString")
                writeToFile(localFacilityString, ZooApplication.appContext, "facility.txt")
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

        viewModel.getData()
        viewModel.getData2()
        viewModel.getData3()
        setupNavController()


    }

    /**
     * override back key for the drawer design
     */
    override fun onBackPressed() {

        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawer(GravityCompat.START)
        } else if(viewModel.currentFragmentType.value == CurrentFragmentType.DETAILAREA || viewModel.currentFragmentType.value == CurrentFragmentType.DETAILANIMAL){
            findNavController(R.id.myNavHostFragment).navigate(R.id.listFragment)
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
                R.id.scheduleFragment -> CurrentFragmentType.SCHEDULE
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
        val bindingNavHeader = HeaderDrawerBinding.inflate(
            LayoutInflater.from(this), binding.drawerNavView, false)

        bindingNavHeader.lifecycleOwner = this
        bindingNavHeader.viewModel = viewModel
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
                        navController.navigate(NavigationDirections.navigateToHomeFragment())
                        }, 300L)
                    R.id.list ->
                        navController.navigate(NavigationDirections.navigateToListFragment())
//                    R.id.schedule ->
//                        navController.navigate(NavigationDirections.navigateToScheduleFragment())
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
//        val x = 0.003
//        val y = 0.003
//      map.addGroundOverlay(
//            GroundOverlayOptions()
//                .image(BitmapDescriptorFactory.fromResource(R.drawable.taipei_zoo_map2)).anchor(0f, 1f)
//                .position(LatLng(24.999882+y, 121.582586+x), 1050f, 1350f)
//                .bearing(145f)
//        )
        map.let {
            val x = 0.0045
            val y = 0.004
            val cameraPosition =
                CameraPosition.builder().target(LatLng(24.998361-y, 121.581033+x)).zoom(16f).bearing(146f)
                    .build()
            it.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
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
        if (requestCode != LOCATION_PERMISSION_REQUEST_CODE) {
            return
        }
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



    companion object {
        /**
         * Request code for location permission request.
         *
         * @see .onRequestPermissionsResult
         */
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
    }

}


