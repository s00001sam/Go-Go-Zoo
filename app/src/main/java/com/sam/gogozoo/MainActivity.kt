package com.sam.gogozoo

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.CompoundButton
import android.widget.SeekBar
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupWithNavController
import com.google.android.gms.maps.CameraUpdate
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.*
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.ismaeldivita.chipnavigation.ChipNavigationBar
import com.sam.gogozoo.PermissionUtils.PermissionDeniedDialog.Companion.newInstance
import com.sam.gogozoo.PermissionUtils.isPermissionGranted
import com.sam.gogozoo.PermissionUtils.requestPermission
import com.sam.gogozoo.databinding.ActivityMainBinding
import com.sam.gogozoo.databinding.HeaderDrawerBinding
import com.sam.gogozoo.ext.getVmFactory
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.lifecycleOwner = this
        viewModel
        val navController = Navigation.findNavController(this, R.id.myNavHostFragment)
        if (savedInstanceState == null){
            binding.bottomNavView.setItemSelected(R.id.home, true)
        }
        changeTitleAndPage()
        setupDrawer()

        needNavigation.observe(this, Observer {
            Log.d("sam","need=${needNavigation.value}")
        })

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

        binding.bottomNavView.setOnItemSelectedListener(object :
            ChipNavigationBar.OnItemSelectedListener {
            override fun onItemSelected(id: Int) {
                when (id) {
                    R.id.home -> {
                        navController.navigate(R.id.homeFragment)
                    }
                    R.id.list -> {
                        navController.navigate(R.id.listFragment)
                    }
                    R.id.schedule -> {
                        navController.navigate(R.id.scheduleFragment)
                    }
                    R.id.person -> {
                        navController.navigate(R.id.personFragment)
                    }
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
        map.setOnMyLocationButtonClickListener(this)
        map.setOnMyLocationClickListener(this)
        enableMyLocation()
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


