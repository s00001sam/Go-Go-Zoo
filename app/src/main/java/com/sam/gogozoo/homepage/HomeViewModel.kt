package com.sam.gogozoo.homepage

import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.os.Looper
import android.util.Log
import androidx.appcompat.content.res.AppCompatResources.getDrawable
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.*
import com.google.maps.android.PolyUtil
import com.sam.gogozoo.R
import com.sam.gogozoo.ZooApplication
import com.sam.gogozoo.data.Control
import com.sam.gogozoo.data.source.ZooRepository
import com.sam.gogozoo.data.model.DirectionResponses
import com.sam.gogozoo.network.LoadApiStatus
import com.sam.gogozoo.data.MockData
import com.sam.gogozoo.data.facility.LocalFacility
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeViewModel(private val repository: ZooRepository) : ViewModel() {

    //create some variables for address
    lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    lateinit var locationRequest: LocationRequest
    // status: The internal MutableLiveData that stores the status of the most recent request
    private val _status = MutableLiveData<LoadApiStatus>()
    val status: LiveData<LoadApiStatus>
        get() = _status

    // error: The internal MutableLiveData that stores the error of the most recent request
    private val _error = MutableLiveData<String>()

    val error: LiveData<String>
        get() = _error

    // status for the loading icon of swl
    private val _refreshStatus = MutableLiveData<Boolean>()

    val refreshStatus: LiveData<Boolean>
        get() = _refreshStatus

    val myLatLng = MutableLiveData<LatLng>()

//    val info = MutableLiveData<NavInfo>()
    val navLatLng = MutableLiveData<LatLng>()

    val polyList = mutableListOf<Polyline>()
    val markerList = mutableListOf<Marker>()
    val clickMark = MutableLiveData<Marker>()

    val selectTopItem = MutableLiveData<String>()

    val selectFac = MutableLiveData<LocalFacility>()

    // Create a Coroutine scope using a job to be able to cancel when needed
    private var viewModelJob = Job()

    // the Coroutine runs using the Main (UI) dispatcher
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    /**
     * When the [ViewModel] is finished, we cancel our coroutine [viewModelJob], which tells the
     * Retrofit service to stop.
     */
    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

    /**
     * Call getArticlesResult() on init so we can display status immediately.
     */
    init {

    }

    val callback1 = OnMapReadyCallback { it ->
        val x = 0.0045
        val y = 0.004
        val cameraPosition =
            CameraPosition.builder().target(LatLng(24.998361-y, 121.581033+x)).zoom(16f).bearing(146f)
                .build()
        it.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
    }

    fun markCallback1(latLng: LatLng, title: String) = OnMapReadyCallback { it ->
        val cameraPosition = CameraPosition.builder().target(latLng).zoom(18f).bearing(146f)
                .build()
        it.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
        val marker = it.addMarker(MarkerOptions().position(latLng).title(title))
        marker.showInfoWindow()

        markerList.add(marker)
    }

    fun onlyAddMark(latLng: LatLng, title: String) = OnMapReadyCallback { it ->
        val marker = it.addMarker(MarkerOptions().position(latLng).title(title))
        markerList.add(marker)
    }
    fun onlyMoveCamera(latLng: LatLng) = OnMapReadyCallback { it ->
        val cameraPosition = CameraPosition.builder().target(latLng).zoom(18f).bearing(146f)
            .build()
        it.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
    }


    fun directionCall(location1: LatLng, location2: LatLng, key: String) = OnMapReadyCallback { map ->

        val myPosition =
            CameraPosition.builder().target(location1).zoom(20f).bearing(146f).tilt(45f).build()
        map.animateCamera(CameraUpdateFactory.newCameraPosition(myPosition))

        val fromFKIP = location1.latitude.toString() + "," + location1.longitude.toString()
        val toMonas = location2.latitude.toString() + "," + location2.longitude.toString()

//        val apiServices = RetrofitClient.apiServices(this)
        repository.getDirection(fromFKIP, toMonas, key)
            .enqueue(object : Callback<DirectionResponses> {
                override fun onResponse(call: Call<DirectionResponses>, response: Response<DirectionResponses>) {
                    Log.d("bisa dong oke", "sam1234 ${response.message()}")
                    drawPolyline(map,response)
                }
                override fun onFailure(call: Call<DirectionResponses>, t: Throwable) {
                    Log.e("anjir error", "sam1234 ${t.localizedMessage}")
                }
            })

        Control.hasPolyline = true
    }

    fun drawPolyline(map: GoogleMap, response: Response<DirectionResponses>) {
        val routeList = response.body()?.routes
        val shape = routeList?.get(0)?.overviewPolyline?.points
        //路線總長
        var distance = 0
        routeList?.forEach {route ->
            route?.legs?.forEach {leg ->
                distance += leg?.distance?.value ?:0
            }
        }
        Log.d("sam", "distance=$distance")

        val polylineOption = PolylineOptions()
            .addAll(PolyUtil.decode(shape))
            .width(8f)
            .color(Color.RED)
        val polyline = map.addPolyline(polylineOption)
        polyList.add(polyline)
    }

    fun clearPolyline(){
        polyList.forEach {
            it.remove()
        }
    }
    fun clearMarker(){
        markerList.forEach {
            it.remove()
        }
    }

    //get location LatLng
    //allow us to get the last location
    fun getNewLocation(){
        locationRequest = LocationRequest()
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest.interval = 5
        locationRequest.fastestInterval = 0
        locationRequest.numUpdates = 2
        fusedLocationProviderClient.requestLocationUpdates(locationRequest,locationCallback, Looper.myLooper()
        )
    }

    //create the location callback
    val locationCallback = object : LocationCallback(){
        override fun onLocationResult(p0: LocationResult) {
            var lastLocation = p0.lastLocation
            //set the new location
            Log.d("sam", "sam1234${lastLocation.latitude}, ${lastLocation.longitude}")
            myLatLng.value = LatLng(lastLocation.latitude, lastLocation.longitude)
        }
    }

    val allMarks = OnMapReadyCallback { googleMap ->
        MockData.animals.map { animal ->
            googleMap.addMarker(MarkerOptions().position(animal.latLng).title(animal.title).icon(
                changeBitmapDescriptor(animal.drawable)))
        }
        MockData.areas.map { area ->
            googleMap.addMarker(MarkerOptions().position(area.latLng).title(area.title).icon(
                changeBigBitmapDescriptor(R.drawable.icon_flag)))
        }
    }

    fun changeBitmapDescriptor(drawable: Int):BitmapDescriptor {
        val bitmapdraw = getDrawable(ZooApplication.appContext, drawable) as BitmapDrawable
        val b: Bitmap = bitmapdraw.bitmap
        val smallMarker: Bitmap = Bitmap.createScaledBitmap(b, 35, 35, false)
        return BitmapDescriptorFactory.fromBitmap(smallMarker)
    }
    fun changeBigBitmapDescriptor(drawable: Int):BitmapDescriptor {
        val bitmapdraw = getDrawable(ZooApplication.appContext, drawable) as BitmapDrawable
        val b: Bitmap = bitmapdraw.bitmap
        val smallMarker: Bitmap = Bitmap.createScaledBitmap(b, 50, 50, false)
        return BitmapDescriptorFactory.fromBitmap(smallMarker)
    }



}
