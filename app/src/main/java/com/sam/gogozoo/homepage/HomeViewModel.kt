package com.sam.gogozoo.homepage

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.widget.Toast
import androidx.appcompat.content.res.AppCompatResources.getDrawable
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
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
import com.sam.gogozoo.util.Util.getDinstance
import com.sam.gogozoo.util.Util.getEmailName
import com.sam.gogozoo.util.Util.toRoute
import com.sam.gogozoo.util.Util.toast
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

    private val _myLatLng = MutableLiveData<LatLng>()

    val myLatLng: LiveData<LatLng>
        get() = _myLatLng

    private val _clickMark = MutableLiveData<Marker>()

    val clickMark: LiveData<Marker>
        get() = _clickMark

    private val _selectTopItem = MutableLiveData<String>()

    val selectTopItem: LiveData<String>
        get() = _selectTopItem

    private val _selectFac = MutableLiveData<LocalFacility>()

    val selectFac: LiveData<LocalFacility>
        get() = _selectFac

    private val _selectRoute = MutableLiveData<Route>()

    val selectRoute: LiveData<Route>
        get() = _selectRoute

    private val _deleteNavInfo = MutableLiveData<NavInfo>()

    val deleteNavInfo: LiveData<NavInfo>
        get() = _deleteNavInfo

    private val _context = MutableLiveData<Context>()

    val context: LiveData<Context>
        get() = _context

    private val _selectRoutePosition = MutableLiveData<NavInfo>()

    val selectRoutePosition: LiveData<NavInfo>
        get() = _selectRoutePosition

    private val _showRouteInfo = MutableLiveData<Boolean>()

    val showRouteInfo: LiveData<Boolean>
        get() = _showRouteInfo

    private val _routeDistance = MutableLiveData<Int>()

    val routeDistance: LiveData<Int>
        get() = _routeDistance

    private val _routeTime = MutableLiveData<Int>()

    val routeTime: LiveData<Int>
        get() = _routeTime

    private val _needMapIcon = MutableLiveData<Boolean>()

    val needMapIcon: LiveData<Boolean>
        get() = _needMapIcon

    private val _selectFriend = MutableLiveData<User>()

    val selectFriend: LiveData<User>
        get() = _selectFriend

    private val _newFacList = MutableLiveData<List<LocalFacility>>()

    val newFacList: LiveData<List<LocalFacility>>
        get() = _newFacList

    private val _needfocus = MutableLiveData<Boolean>()

    val needfocus: LiveData<Boolean>
        get() = _needfocus

    private val _clickRoute = MutableLiveData<Boolean>()

    val clickRoute: LiveData<Boolean>
        get() = _clickRoute

    private val _cooperateConfirm = MutableLiveData<List<String>>()

    val cooperateConfirm: LiveData<List<String>>
        get() = _cooperateConfirm

    private val _directionAim = MutableLiveData<LatLng>()

    val directionAim: LiveData<LatLng>
        get() = _directionAim

    var liveFriend = MutableLiveData<List<User>>()
    var liveRoutes = MutableLiveData<List<FireRoute>>()
    val mapCenter = LatLng(24.995750, 121.585500)
    val polyList = mutableListOf<Polyline>()
    val markerList = mutableListOf<Marker>()
    val allOriMarker = mutableListOf<Marker>()
    val friendMarkers = mutableListOf<Marker>()
    var addNewRoute = false
    var visibleFriend = -1
    var isBackMap = -1

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

    init {
        getLiveFriendsResult()
        getLiveRoutesResult()
    }

    fun setDirectionAim(latLng: LatLng?){
        _directionAim.value = latLng
    }

    fun setNeedFocus(boolean: Boolean){
        _needfocus.value = boolean
    }

    fun setNewFacList(list: List<LocalFacility>){
        _newFacList.value = list
    }

    fun setSelectFriend(user: User){
        _selectFriend.value = user
    }

    fun setNeedMapIcon(boolean: Boolean){
        _needMapIcon.value = boolean
    }

    fun setRoutePosition(navInfo: NavInfo){
        _selectRoutePosition.value = navInfo
    }

    fun setContext(context: Context?){
        _context.value = context
    }

    fun setDeleteNavInfo(navInfo: NavInfo){
        _deleteNavInfo.value = navInfo
    }

    fun setSelectRoute(route: Route?){
        _selectRoute.value = route
    }

    fun setSelectFac(localFacility: LocalFacility){
        _selectFac.value = localFacility
    }

    fun setSelectTopItem(string: String){
        _selectTopItem.value = string
    }

    fun setClickMarker(marker: Marker){
        _clickMark.value = marker
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
    }

    fun onlyRouteMark(latLng: LatLng, title: String) = OnMapReadyCallback { it ->
        val marker = it.addMarker(MarkerOptions().position(latLng).title(title).icon(
            changeBitmapDescriptor(R.drawable.icon_route_marker, 60)
        ))
        markerList.add(marker)
    }

    fun onlyAddMarkFriend(latLng: LatLng, title: String, path: String) = OnMapReadyCallback { it ->
        Glide
            .with(ZooApplication.appContext)
            .asBitmap()
            .circleCrop()
            .load(path)
            .into(object : CustomTarget<Bitmap>(){
                override fun onLoadCleared(placeholder: Drawable?) {
                        TODO("Not yet implemented")
                    }
                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    val bitmap = resource
                    val smallMarker: Bitmap = Bitmap.createScaledBitmap(bitmap, 60, 60, false)
                    val marker = it.addMarker(
                        MarkerOptions().position(latLng).title(title)
//                .icon(changeBitmapDescriptor(R.drawable.icon_friend_location, 50))
                            .icon(BitmapDescriptorFactory.fromBitmap(smallMarker))
                        )
                        markerList.add(marker)
                        friendMarkers.add(marker)
                    }
                })
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
            _needfocus.value = false
        }
        it.setOnMapLongClickListener {
            _needfocus.value = false
        }
        it.setOnCameraMoveStartedListener { number ->
            if (number == GoogleMap.OnCameraMoveStartedListener.REASON_GESTURE) {
                _needfocus.value = false
            }
        }
    }

    fun directionCall(location1: LatLng?, location2: LatLng?) = OnMapReadyCallback { map ->

            val position1 =
                CameraPosition.builder().target(location1).zoom(19f).bearing(146f).tilt(45f).build()
            map.animateCamera(CameraUpdateFactory.newCameraPosition(position1))

            val fromFKIP = location1?.latitude.toString() + "," + location1?.longitude.toString()
            val toMonas = location2?.latitude.toString() + "," + location2?.longitude.toString()

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
                        Logger.d( "sam1234 ${response.message()}")
                        drawPolyline(map, response)
                    }

                    override fun onFailure(call: Call<DirectionResponses>, t: Throwable) {
                        Logger.e( "sam1234 ${t.localizedMessage}")
                    }
                })

            Control.hasPolyline = true
            _needfocus.value = true
            _showRouteInfo.value = true
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
        Logger.d( "distance=$distance")

        val polylineOption = PolylineOptions()
            .addAll(PolyUtil.decode(shape))
            .width(8f)
            .color(R.color.yellow_white)
        val polyline = map.addPolyline(polylineOption)
        polyList.add(polyline)
        _routeDistance.value = distance
        _routeTime.value = (distance / SPEED)
    }

    fun clearPolyline() {
        polyList.forEach {
            it.remove()
        }
        _showRouteInfo.value = false
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
        friendMarkers.clear()
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
        locationRequest.interval = 10 * 1000
        locationRequest.fastestInterval = 10 * 1000
        fusedLocationProviderClient.requestLocationUpdates(
            locationRequest, locationCallback, Looper.myLooper()
        )
    }

    //create the location callback
    val locationCallback = object : LocationCallback() {
        override fun onLocationResult(p0: LocationResult) {
            var lastLocation = p0.lastLocation
            //set the new location
            Logger.d( "sam1234${lastLocation.latitude}, ${lastLocation.longitude}")
            _myLatLng.value = LatLng(lastLocation.latitude, lastLocation.longitude)
        }
    }

    val allMarks = OnMapReadyCallback { googleMap ->
        MockData.animals.map { animal ->
            val markerAnimals = googleMap.addMarker(
                MarkerOptions().position(animal.latLng).title(animal.title).icon(
                    changeBitmapDescriptor(animal.drawable, 40)
                )
            )
            allOriMarker.add(markerAnimals)
        }
        MockData.areas.map { area ->
            val markerAreas = googleMap.addMarker(
                MarkerOptions().position(area.latLng).title(area.title).icon(
                    changeBitmapDescriptor(R.drawable.icon_house_marker, 50)
                )
            )
            allOriMarker.add(markerAreas)
        }
    }

    fun clearOriMarkers() {
        allOriMarker.forEach {
            it.remove()
        }
        allOriMarker.clear()
    }

    fun changeBitmapDescriptor(drawable: Int , width: Int): BitmapDescriptor {
        val bitmapdraw = getDrawable(ZooApplication.appContext, drawable) as BitmapDrawable
        val b: Bitmap = bitmapdraw.bitmap
        val smallMarker: Bitmap = Bitmap.createScaledBitmap(b, width, width, false)
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
        list.add(ZooApplication.INSTANCE.getString(R.string.add_new_route))
        val arraySchedule = list.toTypedArray()
        val mBuilder = AlertDialog.Builder(context.value)
        mBuilder.setTitle(ZooApplication.INSTANCE.getString(R.string.select_route))
        mBuilder.setSingleChoiceItems(arraySchedule, -1) { dialog: DialogInterface?, i: Int ->
            _needfocus.value = false
            Toast.makeText(ZooApplication.appContext, arraySchedule[i], Toast.LENGTH_SHORT).show()
            dialog?.dismiss()
            when (i) {
                (arraySchedule.size - 1) -> {
                    setNewRoute()
                }
                else -> {
                    val schedule = MockData.routes.filter { it.name == arraySchedule[i] }
                    _selectRoute.value = schedule[0]
                    _clickRoute.value = true
                }
            }
        }
        mBuilder.create().show()
    }

    fun setNewRoute() {
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
                Handler().postDelayed({ _selectRoute.value = route }, 200L)
            } else {
                toast("$name 已存在路線清單中")
            }
        }
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
        mBuilder.setTitle(ZooApplication.INSTANCE.getString(R.string.select_companion))
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
        mBuilder.setPositiveButton(ZooApplication.INSTANCE.getString(R.string.sure)) { dialog, which ->
            _cooperateConfirm.value = checkFriends
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

    fun getPhotoToNet(){
        if (Control.getPhoto) {
            publishUser(UserManager.user)
        }
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

    fun getAllDistance(latLng: LatLng){
        UserManager.user.geo = latLng
        MockData.allMarkers.forEach {navInfo ->
            navInfo.meter = navInfo.latLng.getDinstance(latLng)
        }
        MockData.localFacility.forEach {facility ->
            facility.meter = facility.geo[0].getDinstance(latLng)
        }
        MockData.localAreas.forEach {area ->
            area.meter = area.geo[0].getDinstance(latLng)
        }
    }

    fun deleteNavFromRoute(navInfo: NavInfo){
        selectRoute.value?.let { schedule ->
            val list = schedule.list.toMutableList()
            list.remove(navInfo)
            val route = Route(schedule.id, schedule.name, schedule.owners, schedule.open, list)
            publishRoute(route)
        }
    }

    fun updateRoutes(routes: List<FireRoute>){
        var list = mutableListOf<Route>()
        routes.forEach {
            val route = it.toRoute()
            list.add(route)
        }
        MockData.routes = list
        addNewRoute = false
        Control.addNewAnimal = false
    }

    fun confirmCooperator(checks: List<String>){
       selectRoute.value?.let { route ->
            val routeNow = route
            val oriOwners = route.owners.toMutableList()
            oriOwners.addAll(checks)
            routeNow.owners = oriOwners
            publishRoute(routeNow)
        }
    }
}
