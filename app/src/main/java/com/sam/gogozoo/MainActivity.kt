package com.sam.gogozoo

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.firebase.storage.FirebaseStorage
import com.google.zxing.integration.android.IntentIntegrator
import com.sam.gogozoo.PermissionUtils.PermissionDeniedDialog.Companion.newInstance
import com.sam.gogozoo.PermissionUtils.isPermissionGranted
import com.sam.gogozoo.PermissionUtils.requestPermission
import com.sam.gogozoo.data.*
import com.sam.gogozoo.databinding.ActivityMainBinding
import com.sam.gogozoo.databinding.HeaderDrawerBinding
import com.sam.gogozoo.ext.getVmFactory
import com.sam.gogozoo.util.CurrentFragmentType
import com.sam.gogozoo.util.Logger
import com.sam.gogozoo.util.Util.listAnimalToJson
import com.sam.gogozoo.util.Util.listAreaToJson
import com.sam.gogozoo.util.Util.listCalendarToJson
import com.sam.gogozoo.util.Util.listFacilityToJson
import com.sam.gogozoo.util.Util.writeToFile
import nl.joery.animatedbottombar.AnimatedBottomBar
import java.util.*
import com.sam.gogozoo.util.Util.toTimeString
import com.sam.gogozoo.util.Util.toast

class MainActivity : AppCompatActivity(), OnMapReadyCallback,
    ActivityCompat.OnRequestPermissionsResultCallback{

    private var permissionDenied = false
    lateinit var map: GoogleMap
    val viewModel by viewModels<MainViewModel> { getVmFactory() }
    private lateinit var binding: ActivityMainBinding
    private var actionBarDrawerToggle: ActionBarDrawerToggle? = null
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var bindingNavHeader: HeaderDrawerBinding
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.myNavHostFragment) as NavHostFragment
        navController = navHostFragment.navController
        introduction()
        changeTitleAndPage()
        setupDrawer()
        setupNavController()

        //官方 api 無法使用
        viewModel.facilityResult.observe(this, Observer {
            it?.let {
                it.result.results.let {facilities -> viewModel.getLocalFacilities(facilities) }
            }
        })

        viewModel.facilityFireResult.observe(this, Observer {
            it?.let {
                Logger.d("sam00 facilityFireResult=${it}")
                viewModel.getLocalFacilitiesFromFire(it)
            }
        })

        viewModel.areaResult.observe(this, Observer {
            it?.let {
                Logger.d("sam00 areaResult=${it.result.results}")
                it.result.results.let {areas -> viewModel.getLocalAreas(areas) }
            }
        })

        viewModel.animalResult.observe(this, Observer {
            it?.let {
                Logger.d("sam00 animalResult=${it.result.results}")
                it.result.results.let {animals -> viewModel.getLocalAnimals(animals) }
            }
        })

        viewModel.calendarResult.observe(this, Observer {
            it?.let {
                it.result.results.let { calendars ->
                    viewModel.getLocalCalendar(calendars)
                }
            }
        })

        viewModel.localAnimalInMain.observe(this, Observer {
            it?.let {
                val localAnimalString = listAnimalToJson(it)
                Logger.d("localAnimalString=$localAnimalString")
                writeToFile(localAnimalString, ZooApplication.appContext, "animal.txt")
            }
        })

        viewModel.localAreaInMain.observe(this, Observer {
            it?.let {
                val localAreaString = listAreaToJson(it)
                Logger.d("localAreaString=$localAreaString")
                writeToFile(localAreaString, ZooApplication.appContext, "area.txt")
            }
        })

        viewModel.localFacilityInMain.observe(this, Observer {
            it?.let {
                val localFacilityString = listFacilityToJson(it)
                Logger.d("localFacilityString=$localFacilityString")
                writeToFile(localFacilityString, ZooApplication.appContext, "facility.txt")
            }
        })

        viewModel.localCalendarsInMain.observe(this, Observer {list ->
            list?.let {
                val localCalendarsString = listCalendarToJson(it)
                Logger.d("localCalendarsString=$localCalendarsString")
                writeToFile(localCalendarsString, ZooApplication.appContext, "calendars.txt")
            }
        })

        viewModel.currentFragmentType.observe(this, Observer {
            it?.let {
                setBottomNav(it)
            }
        })

        binding.buttonSearch.setOnClickListener {
            navController.navigate(R.id.searchDialog)
        }

        viewModel.fireRoute.observe(this, Observer {
            it?.let {
                viewModel.getRecommendFromFirebase(it)
            }
        })

        viewModel.user.observe(this, Observer {
            it?.let {
                viewModel.publishFriend(UserManager.user.email, it)
            }
        })

        viewModel.checkUser.observe(this, Observer {
            it?.let {
                viewModel.checkHasUser(it, this)
            }
        })

        viewModel.me.observe(this, Observer {
            Logger.d("meUser=$it")
            it?.let {
                UserManager.user.picture = it.picture
                bindImageCircle(bindingNavHeader.imagePhoto, it.picture)
                Control.getPhoto = true
            }
        })

        viewModel.myPhoto.observe(this, Observer {
            it?.let {
                viewModel.publishUser(UserManager.user)
                UserManager.friends.forEach {friend ->
                    viewModel.publishFriend(friend.email, UserManager.user)
                }
            }
        })

        binding.layoutStepCount.setOnClickListener {
            navController.navigate(R.id.stepDialog)
        }

        //handle step count
        Control.step.observe(this, Observer {
            Logger.d("controlStep=$it")
            it?.let {
                viewModel.caculateFromStep(it)
                binding.textStep.text = "$it 步"
            }
        })
        Control.timeCount.observe(this, Observer {
            Logger.d("timeCount=$it")
            it?.let {
                viewModel.setTimeCount(it)
                UserManager.newStep.time = it
                binding.textTime.text = "${((it)/60).toTimeString()} : ${((it)% 60).toTimeString()}"
            }
        })

        //get data from Zoo API or local files
        viewModel.getDatas()

        //start notification
        if (!Control.hasNotification) {
            startService()
        }

    }

    //override back key for the drawer design and safe design for backpress when navigationCount = 0
    override fun onBackPressed() {
        val navController = Navigation.findNavController(this, R.id.myNavHostFragment)
        val navigationCount = supportFragmentManager.findFragmentById(R.id.myNavHostFragment)?.childFragmentManager?.backStackEntryCount
        Logger.d("navigationCount=$navigationCount")

        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawer(GravityCompat.START)
        } else if ((viewModel.currentFragmentType.value == CurrentFragmentType.DETAILAREA || viewModel.currentFragmentType.value == CurrentFragmentType.DETAILANIMAL) && navigationCount == 1){
            navController.navigate(NavigationDirections.navigateToListFragment())
        }else if (navigationCount == 0){
            viewModel.showLeaveOrNot(this, this)
        }else {
            super.onBackPressed()
        }
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        if (currentFocus != null) {
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(currentFocus?.windowToken, 0)
        }
        return super.dispatchTouchEvent(ev)
    }

    //set CurrentFragmentType value when change fragment
    private fun setupNavController() {
        navController.addOnDestinationChangedListener { navController: NavController, _: NavDestination, _: Bundle? ->
            viewModel.currentFragmentType.value = when (navController.currentDestination?.id) {
                R.id.homeFragment -> CurrentFragmentType.HOME
                R.id.listFragment -> CurrentFragmentType.LIST
                R.id.detailAreaFragment -> CurrentFragmentType.DETAILAREA
                R.id.detailAnimalFragment -> CurrentFragmentType.DETAILANIMAL
                else -> viewModel.currentFragmentType.value
            }
        }
    }

    //set up drawer
    private fun setupDrawer() {
        // set up toolbar
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

    //change toolbar title and page
    private fun changeTitleAndPage() {

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
                        navController.navigate(NavigationDirections.navigateToHomeFragment(viewModel.endRoute.value))
                        }, 300L)
                    R.id.list ->
                        navController.navigate(NavigationDirections.navigateToListFragment())
                }
            }
        })
    }
    //map set-up
    override fun onMapReady(googleMap: GoogleMap?) {
        map = googleMap ?: return
        enableMyLocation()
        map.uiSettings.isMyLocationButtonEnabled = false
        map.let {
            val x = 0.0045
            val y = 0.004
            val cameraPosition = CameraPosition.builder()
                .target(LatLng(24.998361-y, 121.581033+x)).zoom(16f).bearing(146f).build()
            it.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
            val boundWS = LatLng(24.993000, 121.582000)
            val boundEN = LatLng(24.998500, 121.589000)
            val bounds = LatLngBounds(boundWS, boundEN)
//            it.setLatLngBoundsForCameraTarget(bounds)
            it.setMinZoomPreference(15.6f)
        }
    }

    //Enables the My Location layer if the fine location permission has been granted.
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
    }
    // [START maps_check_location_permission_result]
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode){
            LOCATION_PERMISSION_REQUEST_CODE ->{
                if (isPermissionGranted(permissions, grantResults, Manifest.permission.ACCESS_FINE_LOCATION) ) {
                    // Enable the my location layer if the permission has been granted.
                    enableMyLocation()
                } else {
                    // Display the missing permission error dialog when the fragments resume.
                    permissionDenied = true
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
    //check if the location of device is enable
    fun isLocationEnable():Boolean{
        var locationManager: LocationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER)
    }
    //QR Code & Image picker result
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        Logger.d("requestCode=$requestCode")
            when(requestCode){
                SCANCODE -> {
                    if (resultCode == Activity.RESULT_OK) {
                        val result =
                            IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
                        Log.d("sam", "resultQR=$result")
                        if (result != null) {
                            Log.d("sam", "result=${result.contents}")
                            if (result.contents == null) {
                                toast(getString(R.string.scan_error), this)
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
    //get images from gallery
    fun pickGalleryImage() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, IMAGE_PICKER_CODE)
    }
    //upload photo image to firebase storage
    fun uploadImage(uri: Uri){
        val filename = UUID.randomUUID().toString()
        val ref = FirebaseStorage.getInstance().getReference("/images/$filename")
        ref.putFile(uri)
            .addOnSuccessListener {
                ref.downloadUrl.addOnSuccessListener {
                    Logger.d("downloadUrl=$it")
                    viewModel.setMyphoto(it.toString())
                    UserManager.user.picture = it.toString()
                }
            }
    }
    //start to count steps
    fun startService() {
        val serviceIntent = Intent(this, StepService::class.java)
        ContextCompat.startForegroundService(this, serviceIntent)
    }
    //stop to count steps
    fun stopService() {
        val serviceIntent = Intent(this, StepService::class.java)
        stopService(serviceIntent)
    }
    //set bottomNav
    fun setBottomNav(currentFragmentType: CurrentFragmentType){
        if (currentFragmentType == CurrentFragmentType.HOME){
            binding.bottomNavView.selectTabById(R.id.home)
        }else if (currentFragmentType == CurrentFragmentType.LIST){
            binding.bottomNavView.selectTabById(R.id.list)
        }
    }
    //introduction for homepage
    fun introduction(){
        if(MockData.isfirstTime) {
            navController.navigate(R.id.introStartDialog)
            MockData.isfirstTime = false
        }
    }

    companion object {
        //Request code for location permission request
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
        private const val IMAGE_PICKER_CODE = 100
        private const val SCANCODE = 49374
    }

}


