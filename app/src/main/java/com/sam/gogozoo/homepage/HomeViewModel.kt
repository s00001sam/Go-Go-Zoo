package com.sam.gogozoo.homepage

import android.app.Application
import android.graphics.Color
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
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
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import com.google.maps.android.PolyUtil
import com.sam.gogozoo.MainActivity
import com.sam.gogozoo.R
import com.sam.gogozoo.ZooApplication
import com.sam.gogozoo.data.source.PublisherRepository
import com.sam.gogozoo.model.DirectionResponses
import com.sam.gogozoo.network.LoadApiStatus
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeViewModel(private val repository: PublisherRepository) : ViewModel() {

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

    val callback1 = OnMapReadyCallback { googleMap ->
        val location1 = LatLng(24.9931338, 121.5907654)
        googleMap.animateCamera(CameraUpdateFactory.zoomTo(10f), 2000, null)
        googleMap.addMarker(MarkerOptions().position(location1).title("國王企鵝"))
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location1, 17f))
    }
    val callback2 = OnMapReadyCallback { googleMap ->
        val location2 = LatLng(24.9951066, 121.5856424)
        googleMap.animateCamera(CameraUpdateFactory.zoomTo(10f), 2000, null)
        googleMap.addMarker(MarkerOptions().position(location2).title("非洲野驢"))
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location2, 17f))
    }

    fun directionCall(location1: LatLng, location2: LatLng, key: String) = OnMapReadyCallback { map ->
        val markerFkip = MarkerOptions()
            .position(location1)
            .title("A")
        val markerMonas = MarkerOptions()
            .position(location2)
            .title("B")

        map.addMarker(markerFkip)
        map.addMarker(markerMonas)
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(location1, 18f))

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
    }

    fun drawPolyline(map: GoogleMap, response: Response<DirectionResponses>) {
        val shape = response.body()?.routes?.get(0)?.overviewPolyline?.points
        val polyline = PolylineOptions()
            .addAll(PolyUtil.decode(shape))
            .width(8f)
            .color(Color.RED)
        map.addPolyline(polyline)
    }

    //get location LatLng
    //allow us to get the last location


    fun getNewLocation(){
        locationRequest = LocationRequest()
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest.interval = 0
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


//    fun getArticlesResult() {
//
//        coroutineScope.launch {
//
//            _status.value = LoadApiStatus.LOADING
//
//            val result = repository.getArticles()
//
//            _articles.value = when (result) {
//                is Result.Success -> {
//                    _error.value = null
//                    _status.value = LoadApiStatus.DONE
//                    result.data
//                }
//                is Result.Fail -> {
//                    _error.value = result.error
//                    _status.value = LoadApiStatus.ERROR
//                    null
//                }
//                is Result.Error -> {
//                    _error.value = result.exception.toString()
//                    _status.value = LoadApiStatus.ERROR
//                    null
//                }
//                else -> {
//                    _error.value = PublisherApplication.instance.getString(R.string.you_know_nothing)
//                    _status.value = LoadApiStatus.ERROR
//                    null
//                }
//            }
//            _refreshStatus.value = false
//        }
//    }

    fun refresh() {

        if (ZooApplication.INSTANCE.isLiveDataDesign()) {
            _status.value = LoadApiStatus.DONE
            _refreshStatus.value = false

        } else {
            if (status.value != LoadApiStatus.LOADING) {

            }
        }
    }
}
