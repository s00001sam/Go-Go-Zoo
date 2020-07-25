package com.sam.gogozoo.homepage

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.widget.Toast
import androidx.appcompat.content.res.AppCompatResources.getDrawable
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.*
import com.google.maps.android.PolyUtil
import com.sam.gogozoo.R
import com.sam.gogozoo.ZooApplication
import com.sam.gogozoo.data.*
import com.sam.gogozoo.data.source.ZooRepository
import com.sam.gogozoo.data.model.DirectionResponses
import com.sam.gogozoo.network.LoadApiStatus
import com.sam.gogozoo.data.facility.LocalFacility
import com.sam.gogozoo.util.Logger
import com.sam.gogozoo.util.Util.getEmailName
import kotlinx.android.synthetic.main.item_new_route.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeViewModel(private val repository: ZooRepository, private val route: Route?) : ViewModel() {

    private val SPEED = 65

    //create some variables for address
//    lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    lateinit var locationRequest: LocationRequest

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

    private val _edit = MutableLiveData<Boolean>()

    val edit: LiveData<Boolean>
        get() = _edit

    private val _user = MutableLiveData<User>()

    val user: LiveData<User>
        get() = _user

    private val _friendLocation = MutableLiveData<List<User>>()

    val friendLocation: LiveData<List<User>>
        get() = _friendLocation

    private val _routeOwners = MutableLiveData<List<User>>()

    val routeOwners: LiveData<List<User>>
        get() = _routeOwners

    // it for change camera focus
    private val _snapPosition = MutableLiveData<Int>()

    val snapPosition: LiveData<Int>
        get() = _snapPosition

    val myLatLng = MutableLiveData<LatLng>()

    //    val info = MutableLiveData<NavInfo>()
    val navLatLng = MutableLiveData<LatLng>()

    val polyList = mutableListOf<Polyline>()
    val markerList = mutableListOf<Marker>()
    val clickMark = MutableLiveData<Marker>()

    val selectTopItem = MutableLiveData<String>()

    val selectFac = MutableLiveData<LocalFacility>()

    val selectSchedule = MutableLiveData<Route>()

    val deleteNavInfo = MutableLiveData<NavInfo>()

    val context = MutableLiveData<Context>()

    val selectRoutePosition = MutableLiveData<NavInfo>()

    val allOriMarker = mutableListOf<Marker>()

    val routeMarker = MutableLiveData<Marker>()

    val showRouteInfo = MutableLiveData<Boolean>()

    val routeDistance = MutableLiveData<Int>()
    val routeTime = MutableLiveData<Int>()

    var liveFriend = MutableLiveData<List<User>>()

    var liveRoutes = MutableLiveData<List<FireRoute>>()

    val friendMarkers = mutableListOf<Marker>()
    var visibleFriend = 1

    val selectFriend = MutableLiveData<User>()

    val newFacList = MutableLiveData<List<LocalFacility>>()

    val noList = MutableLiveData<Boolean>().apply {
        value = false
    }
    val needfocus = MutableLiveData<Boolean>()

    val clickRoute = MutableLiveData<Boolean>()

    val cooperateConfirm = MutableLiveData<List<String>>()

    var addNewRoute = false

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
        getLiveFriendsResult()
        getLiveRoutesResult()
    }

    val callback1 = OnMapReadyCallback { it ->
        val x = 0.0045
        val y = 0.004
        val cameraPosition =
            CameraPosition.builder().target(LatLng(24.998361 - y, 121.581033 + x)).zoom(16f)
                .bearing(146f)
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
        routeMarker.value = marker
    }

    fun onlyAddMarkFriend(latLng: LatLng, title: String) = OnMapReadyCallback { it ->
        val marker = it.addMarker(
            MarkerOptions().position(latLng).title(title)
                .icon(changeBigBitmapDescriptor(R.drawable.icon_friend_location))
        )
        markerList.add(marker)
        friendMarkers.add(marker)
    }

    fun onlyMoveCamera(latLng: LatLng, float: Float) = OnMapReadyCallback { it ->
        val cameraPosition = CameraPosition.builder().target(latLng).zoom(float).bearing(146f)
            .build()
        it.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
    }

    val myLocationCall = OnMapReadyCallback { it ->
        Logger.d("myLatLngClick=${myLatLng.value}")
        val cameraPosition =
            CameraPosition.builder().target(myLatLng.value).zoom(20f).bearing(146f).build()
        it.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
    }

    val checkCameraMove = OnMapReadyCallback {
        it.setOnMapClickListener {
            Logger.d("mapNow startMove")
            needfocus.value = false
        }
        it.setOnMapLongClickListener {
            needfocus.value = false
        }
        it.setOnCameraMoveStartedListener { number ->
            if (number == GoogleMap.OnCameraMoveStartedListener.REASON_GESTURE) {
                needfocus.value = false
            }
        }
    }

    fun directionCall(location1: LatLng?, location2: LatLng?) = OnMapReadyCallback { map ->

        val position1 =
            CameraPosition.builder().target(location1).zoom(19f).bearing(146f).tilt(45f).build()
        map.animateCamera(CameraUpdateFactory.newCameraPosition(position1))

        val fromFKIP = location1?.latitude.toString() + "," + location1?.longitude.toString()
        val toMonas = location2?.latitude.toString() + "," + location2?.longitude.toString()

//        val apiServices = RetrofitClient.apiServices(this)
        repository.getDirection(
            fromFKIP,
            toMonas,
            ZooApplication.INSTANCE.getString(R.string.google_maps_key)
        )
            .enqueue(object : Callback<DirectionResponses> {
                override fun onResponse(
                    call: Call<DirectionResponses>,
                    response: Response<DirectionResponses>
                ) {
                    Log.d("bisa dong oke", "sam1234 ${response.message()}")
                    drawPolyline(map, response)
                }

                override fun onFailure(call: Call<DirectionResponses>, t: Throwable) {
                    Log.e("anjir error", "sam1234 ${t.localizedMessage}")
                }
            })

        Control.hasPolyline = true
        needfocus.value = true
        showRouteInfo.value = true
    }

    fun drawPolyline(map: GoogleMap, response: Response<DirectionResponses>) {
        val routeList = response.body()?.routes
        val shape = routeList?.get(0)?.overviewPolyline?.points
        //路線總長
        var distance = 0
        routeList?.forEach { route ->
            route?.legs?.forEach { leg ->
                distance += leg?.distance?.value ?: 0
            }
        }
        Log.d("sam", "distance=$distance")

        val polylineOption = PolylineOptions()
            .addAll(PolyUtil.decode(shape))
            .width(8f)
            .color(R.color.yellow_white)
        val polyline = map.addPolyline(polylineOption)
        polyList.add(polyline)
        routeDistance.value = distance
        routeTime.value = distance / SPEED
    }

    fun clearPolyline() {
        polyList.forEach {
            it.remove()
        }
        showRouteInfo.value = false
    }

    fun clearMarker() {
        markerList.forEach {
            it.remove()
        }
    }

    fun clearFriendMarker() {
        friendMarkers.forEach {
            it.remove()
        }
    }

    fun onGalleryScrollChange(
        layoutManager: RecyclerView.LayoutManager?,
        linearSnapHelper: LinearSnapHelper
    ) {
        val snapView = linearSnapHelper.findSnapView(layoutManager)
        snapView?.let {
            layoutManager?.getPosition(snapView)?.let {
                Logger.d("snapposition=$it")
                if (it != snapPosition.value) {
                    _snapPosition.value = it
                }
            }
        }
    }

    //get location LatLng
    //allow us to get the last location
    fun getNewLocation() {
        val fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(ZooApplication.appContext)
        locationRequest = LocationRequest()
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest.interval = 20 * 1000
        locationRequest.fastestInterval = 20 * 1000
        fusedLocationProviderClient.requestLocationUpdates(
            locationRequest, locationCallback, Looper.myLooper()
        )
    }

    //create the location callback
    val locationCallback = object : LocationCallback() {
        override fun onLocationResult(p0: LocationResult) {
            var lastLocation = p0.lastLocation
            //set the new location
            Log.d("sam", "sam1234${lastLocation.latitude}, ${lastLocation.longitude}")
            myLatLng.value = LatLng(lastLocation.latitude, lastLocation.longitude)
        }
    }

    val allMarks = OnMapReadyCallback { googleMap ->
        MockData.animals.map { animal ->
            val markerAnimals = googleMap.addMarker(
                MarkerOptions().position(animal.latLng).title(animal.title).icon(
                    changeBitmapDescriptor(animal.drawable)
                )
            )
            allOriMarker.add(markerAnimals)
        }
        MockData.areas.map { area ->
            val markerAreas = googleMap.addMarker(
                MarkerOptions().position(area.latLng).title(area.title).icon(
                    changeBigBitmapDescriptor(R.drawable.icon_house_marker)
                )
            )
            allOriMarker.add(markerAreas)
        }
    }

    fun clearOriMarkers() {
        allOriMarker.forEach {
            it.remove()
        }
    }

    fun changeBitmapDescriptor(drawable: Int): BitmapDescriptor {
        val bitmapdraw = getDrawable(ZooApplication.appContext, drawable) as BitmapDrawable
        val b: Bitmap = bitmapdraw.bitmap
        val smallMarker: Bitmap = Bitmap.createScaledBitmap(b, 35, 35, false)
        return BitmapDescriptorFactory.fromBitmap(smallMarker)
    }

    fun changeBigBitmapDescriptor(drawable: Int): BitmapDescriptor {
        val bitmapdraw = getDrawable(ZooApplication.appContext, drawable) as BitmapDrawable
        val b: Bitmap = bitmapdraw.bitmap
        val smallMarker: Bitmap = Bitmap.createScaledBitmap(b, 50, 50, false)
        return BitmapDescriptorFactory.fromBitmap(smallMarker)
    }

    fun edit() {
        Logger.d("edit")
        _edit.value = true
    }

    fun startEdit() {
        _edit.value = null
    }

    fun confirm() {
        Logger.d("edit")
        _edit.value = false
    }

    fun showSelectAlert() {
        val list = mutableListOf<String>()
        MockData.routes.forEach { list.add(it.name) }
        list.add("新增行程")
        val arraySchedule = list.toTypedArray()
        val mBuilder = AlertDialog.Builder(context.value)
        mBuilder.setTitle("請選擇行程")
        mBuilder.setSingleChoiceItems(arraySchedule, -1) { dialog: DialogInterface?, i: Int ->
            needfocus.value = false
            Toast.makeText(ZooApplication.appContext, arraySchedule[i], Toast.LENGTH_SHORT).show()
            dialog?.dismiss()
            when (i) {
                (arraySchedule.size - 1) -> {
                    val view =
                        LayoutInflater.from(context.value).inflate(R.layout.item_new_route, null)
                    val cBuilder = AlertDialog.Builder(context.value).setView(view)
                    val cAlertDialog = cBuilder.show()
                    view.buttonConfirm.setOnClickListener {
                        val name = view.textTitle.text.toString()
                        val checkSame = MockData.routes.filter { it.name == name }
                        if (checkSame == listOf<Route>()) {
                            val route = Route()
                            route.name = name
                            route.owners = listOf(UserManager.user.email)
                            val listAdd = MockData.routes.toMutableList()
                            listAdd.add(route)
                            publishRoute(route)
                            addNewRoute = true
                            MockData.routes = listAdd
                            cAlertDialog.dismiss()
                            Handler().postDelayed({ selectSchedule.value = route }, 200L)
                        } else {
                            Toast.makeText(context.value, "$name 已存在行程清單中", Toast.LENGTH_SHORT)
                                .show()
                        }
                    }
                }
                else -> {
                    val schedule = MockData.routes.filter { it.name == arraySchedule[i] }
                    selectSchedule.value = schedule[0]
                    clickRoute.value = true
                }
            }
        }
        mBuilder.create().show()
    }

    fun showCoorperate(owners: List<String>) {
        val multOwners = owners.toMutableList()
        multOwners.remove(UserManager.user.email)
        val friends = mutableListOf<String>()
        UserManager.friends.forEach { friend ->
            friends.add(friend.email)
        }
        friends.removeAll(multOwners)
        val friendsSimple = mutableListOf<String>()
        friends.forEach {
            var name = it.getEmailName()
            friendsSimple.add(name)
        }
        val arrayFriend = friendsSimple.toTypedArray()
        val mBuilder = AlertDialog.Builder(context.value, R.style.AlertDialogCustom)
        val checkFriends = mutableListOf<String>()
        mBuilder.setTitle("選擇協作夥伴")
        mBuilder.setMultiChoiceItems(arrayFriend, BooleanArray(arrayFriend.size))
        { _, num, isChecked ->
            if (isChecked) {
                Logger.d("arrayFriendisChecked=${arrayFriend[num]}")
                checkFriends.add(friends[num])
            } else {
                Logger.d("arrayFriendisCancel=${arrayFriend[num]}")
                checkFriends.remove(friends[num])
            }
        }
        mBuilder.setPositiveButton("確定") { dialog, which ->
            cooperateConfirm.value = checkFriends
        }
        val dialog = mBuilder.create()
        dialog.show()
    }

    fun publishUser(user: User) {
        Logger.d("publishUser")

        coroutineScope.launch {

            _status.value = LoadApiStatus.LOADING

            when (val result = repository.publishUser(user)) {
                is Result.Success -> {
                    _error.value = null
                    _status.value = LoadApiStatus.DONE
                }
                is Result.Fail -> {
                    _error.value = result.error
                    _status.value = LoadApiStatus.ERROR
                }
                is Result.Error -> {
                    _error.value = result.exception.toString()
                    _status.value = LoadApiStatus.ERROR
                }
                else -> {
                    _error.value = ZooApplication.INSTANCE.getString(R.string.you_know_nothing)
                    _status.value = LoadApiStatus.ERROR
                }
            }
        }
    }
    fun getUser(email: String) {

        coroutineScope.launch {

            _status.value = LoadApiStatus.LOADING

            val result = repository.getUser(email)

            _user.value = when (result) {
                is Result.Success -> {
                    _error.value = null
                    _status.value = LoadApiStatus.DONE
                    result.data
                }
                is Result.Fail -> {
                    _error.value = result.error
                    _status.value = LoadApiStatus.ERROR
                    null
                }
                is Result.Error -> {
                    _error.value = result.exception.toString()
                    _status.value = LoadApiStatus.ERROR
                    null
                }
                else -> {
                    _error.value = ZooApplication.INSTANCE.getString(R.string.you_know_nothing)
                    _status.value = LoadApiStatus.ERROR
                    null
                }
            }
            _refreshStatus.value = false
        }
    }

    fun publishFriend(email: String, user: User) {
        Logger.d("publishUser")

        coroutineScope.launch {

            _status.value = LoadApiStatus.LOADING

            when (val result = repository.publishFriend(email, user)) {
                is Result.Success -> {
                    _error.value = null
                    _status.value = LoadApiStatus.DONE
                }
                is Result.Fail -> {
                    _error.value = result.error
                    _status.value = LoadApiStatus.ERROR
                }
                is Result.Error -> {
                    _error.value = result.exception.toString()
                    _status.value = LoadApiStatus.ERROR
                }
                else -> {
                    _error.value = ZooApplication.INSTANCE.getString(R.string.you_know_nothing)
                    _status.value = LoadApiStatus.ERROR
                }
            }
        }
    }

    fun getFriendLocation(listEmail: List<String>) {

        coroutineScope.launch {

            _status.value = LoadApiStatus.LOADING

            val result = repository.getFriendLocation(listEmail)

            _friendLocation.value = when (result) {
                is Result.Success -> {
                    _error.value = null
                    _status.value = LoadApiStatus.DONE
                    result.data
                }
                is Result.Fail -> {
                    _error.value = result.error
                    _status.value = LoadApiStatus.ERROR
                    null
                }
                is Result.Error -> {
                    _error.value = result.exception.toString()
                    _status.value = LoadApiStatus.ERROR
                    null
                }
                else -> {
                    _error.value = ZooApplication.INSTANCE.getString(R.string.you_know_nothing)
                    _status.value = LoadApiStatus.ERROR
                    null
                }
            }
            _refreshStatus.value = false
        }
    }

    fun publishRoute(route: Route) {

        coroutineScope.launch {

            _status.value = LoadApiStatus.LOADING

            when (val result = repository.publishNewRoute(route)) {
                is Result.Success -> {
                    _error.value = null
                    _status.value = LoadApiStatus.DONE
                }
                is Result.Fail -> {
                    _error.value = result.error
                    _status.value = LoadApiStatus.ERROR
                }
                is Result.Error -> {
                    _error.value = result.exception.toString()
                    _status.value = LoadApiStatus.ERROR
                }
                else -> {
                    _error.value = ZooApplication.INSTANCE.getString(R.string.you_know_nothing)
                    _status.value = LoadApiStatus.ERROR
                }
            }
        }
    }

    fun getRouteOwners(listEmail: List<String>) {

        coroutineScope.launch {

            _status.value = LoadApiStatus.LOADING

            val result = repository.getFriendLocation(listEmail)

            _routeOwners.value = when (result) {
                is Result.Success -> {
                    _error.value = null
                    _status.value = LoadApiStatus.DONE
                    result.data
                }
                is Result.Fail -> {
                    _error.value = result.error
                    _status.value = LoadApiStatus.ERROR
                    null
                }
                is Result.Error -> {
                    _error.value = result.exception.toString()
                    _status.value = LoadApiStatus.ERROR
                    null
                }
                else -> {
                    _error.value = ZooApplication.INSTANCE.getString(R.string.you_know_nothing)
                    _status.value = LoadApiStatus.ERROR
                    null
                }
            }
            _refreshStatus.value = false
        }
    }

    fun addFriends(email: String){
        getUser(email)
        publishFriend(email, UserManager.user)
    }

    fun getLiveFriendsResult() {
        liveFriend = repository.getLiveFriend()
        _status.value = LoadApiStatus.DONE
        _refreshStatus.value = false
    }
    fun needFriendLocation(){
        if (UserManager.friends != listOf<User>()){
            val listEmail = mutableListOf<String>()
            UserManager.friends.forEach {
                listEmail.add(it.email)
            }
            getFriendLocation(listEmail)
        }
    }
    fun getLiveRoutesResult() {
        liveRoutes = repository.getLiveRoutes()
        _status.value = LoadApiStatus.DONE
        _refreshStatus.value = false
    }

}
