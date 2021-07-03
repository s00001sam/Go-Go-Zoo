package com.sam.gogozoo

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat.startActivityForResult
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.sam.gogozoo.data.animal.AnimalData
import com.sam.gogozoo.data.source.ZooRepository
import com.sam.gogozoo.network.LoadApiStatus
import com.sam.gogozoo.data.*
import com.sam.gogozoo.data.animal.Animal
import com.sam.gogozoo.data.animal.FireAnimal
import com.sam.gogozoo.data.animal.LocalAnimal
import com.sam.gogozoo.data.area.Area
import com.sam.gogozoo.data.area.AreaData
import com.sam.gogozoo.data.area.FireArea
import com.sam.gogozoo.data.area.LocalArea
import com.sam.gogozoo.data.calendar.Calendar
import com.sam.gogozoo.data.calendar.CalendarData
import com.sam.gogozoo.data.calendar.FireCalendar
import com.sam.gogozoo.data.calendar.LocalCalendar
import com.sam.gogozoo.data.facility.Facility
import com.sam.gogozoo.data.facility.FacilityData
import com.sam.gogozoo.data.facility.FireFacility
import com.sam.gogozoo.data.facility.LocalFacility
import com.sam.gogozoo.util.CurrentFragmentType
import com.sam.gogozoo.util.Logger
import com.sam.gogozoo.util.Util.getString
import com.sam.gogozoo.util.Util.jsonToListAnimal
import com.sam.gogozoo.util.Util.jsonToListArea
import com.sam.gogozoo.util.Util.jsonToListCalendar
import com.sam.gogozoo.util.Util.jsonToListFacility
import com.sam.gogozoo.util.Util.readFromFile
import com.sam.gogozoo.util.Util.toLatlng
import com.sam.gogozoo.util.Util.toLatlngs
import com.sam.gogozoo.util.Util.toTimeInMills
import com.sam.gogozoo.util.Util.toast
import kotlinx.android.synthetic.main.item_confirm_friend.view.*
import kotlinx.coroutines.*

class MainViewModel(private val repository: ZooRepository) : ViewModel() {

    private val _refresh = MutableLiveData<Boolean>()

    val refresh: LiveData<Boolean>
        get() = _refresh

    private val _status = MutableLiveData<LoadApiStatus>()

    val status: LiveData<LoadApiStatus>
        get() = _status

    private val _statusAnimal = MutableLiveData<LoadApiStatus>()

    val statusAnimal: LiveData<LoadApiStatus>
        get() = _statusAnimal

    private val _statusArea = MutableLiveData<LoadApiStatus>()

    val statusArea: LiveData<LoadApiStatus>
        get() = _statusArea

    private val _statusFacility = MutableLiveData<LoadApiStatus>()

    val statusFacility: LiveData<LoadApiStatus>
        get() = _statusFacility

    private val _statusCalendar = MutableLiveData<LoadApiStatus>()

    val statusCalendar: LiveData<LoadApiStatus>
        get() = _statusCalendar

    private val _error = MutableLiveData<String>()

    val error: LiveData<String>
        get() = _error

    private val _refreshStatus = MutableLiveData<Boolean>()

    val refreshStatus: LiveData<Boolean>
        get() = _refreshStatus

    private val _animalResult = MutableLiveData<AnimalData>()

    val animalResult: LiveData<AnimalData>
        get() = _animalResult

    private val _calendarResult = MutableLiveData<CalendarData>()

    val calendarResult: LiveData<CalendarData>
        get() = _calendarResult

    private val _areaResult = MutableLiveData<AreaData>()

    val areaResult: LiveData<AreaData>
        get() = _areaResult

    private val _facilityResult = MutableLiveData<FacilityData>()

    val facilityResult: LiveData<FacilityData>
        get() = _facilityResult

    private val _facilityFireResult = MutableLiveData<List<FireFacility>>()

    val facilityFireResult: LiveData<List<FireFacility>>
        get() = _facilityFireResult

    private val _fireAreasGet = MutableLiveData<List<FireArea>>()

    val fireAreasGet: LiveData<List<FireArea>>
        get() = _fireAreasGet

    private val _fireUser = MutableLiveData<User>()

    val fireUser: LiveData<User>
        get() = _fireUser

    private val _fireRoute = MutableLiveData<List<FireRoute>>()

    val fireRoute: LiveData<List<FireRoute>>
        get() = _fireRoute

    private val _user = MutableLiveData<User>()

    val user: LiveData<User>
        get() = _user

    private val _me = MutableLiveData<User>()

    val me: LiveData<User>
        get() = _me

    private val _checkUser = MutableLiveData<User>()

    val checkUser: LiveData<User>
        get() = _checkUser

    private val _localAreaInMain = MutableLiveData<List<LocalArea>>()

    val localAreaInMain: LiveData<List<LocalArea>>
        get() = _localAreaInMain

    private val _localAnimalInMain = MutableLiveData<List<LocalAnimal>>()

    val localAnimalInMain: LiveData<List<LocalAnimal>>
        get() = _localAnimalInMain

    private val _localFacilityInMain = MutableLiveData<List<LocalFacility>>()

    val localFacilityInMain: LiveData<List<LocalFacility>>
        get() = _localFacilityInMain

    private val _localCalendarsInMain = MutableLiveData<List<LocalCalendar>>()

    val localCalendarsInMain: LiveData<List<LocalCalendar>>
        get() = _localCalendarsInMain

    private val _nowStepInfo = MutableLiveData<StepInfo>()

    val nowStepInfo: LiveData<StepInfo>
        get() = _nowStepInfo

    private val _timeCount = MutableLiveData<Long>()

    val timeCount: LiveData<Long>
        get() = _timeCount

    // Record current fragment to support data binding
    val currentFragmentType = MutableLiveData<CurrentFragmentType>()

    private val _myPhoto = MutableLiveData<String>()

    val myPhoto: LiveData<String>
        get() = _myPhoto

    private val _needNavigation = MutableLiveData<Boolean>()

    val needNavigation: LiveData<Boolean>
        get() = _needNavigation

    private val _info = MutableLiveData<NavInfo>()

    val info: LiveData<NavInfo>
        get() = _info

    private val _markInfo = MutableLiveData<NavInfo>()

    val markInfo: LiveData<NavInfo>
        get() = _markInfo

    private val _selectFacility = MutableLiveData<List<LocalFacility>>()

    val selectFacility: LiveData<List<LocalFacility>>
        get() = _selectFacility

    private val _selectRoute = MutableLiveData<Route>()

    val selectRoute: LiveData<Route>
        get() = _selectRoute

    private val _endRoute = MutableLiveData<Route>()

    val endRoute: LiveData<Route>
        get() = _endRoute

    private val _selectNavAnimal = MutableLiveData<List<LocalFacility>>()

    val selectNavAnimal: LiveData<List<LocalFacility>>
        get() = _selectNavAnimal
    // Create a Coroutine scope using a job to be able to cancel when needed
    private var viewModelJob = Job()
    // the Coroutine runs using the Main (UI) dispatcher
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    val currentUser = FirebaseAuth.getInstance().currentUser

    /**
     * When the [ViewModel] is finished, we cancel our coroutine [viewModelJob], which tells the
     * Retrofit service to stop.
     */
    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

    init {
        _needNavigation.value = false
        getWhichRoute()
        getAuthUser()
    }

    fun setEndRoute(route: Route?){
        _endRoute.value = route
    }

    fun setSelectRoute(route: Route?){
        _selectRoute.value = route
    }

    fun setSelectFacility(list: List<LocalFacility>?){
        _selectFacility.value = list
    }

    fun setMarkInfo(navInfo: NavInfo?){
        _markInfo.value = navInfo
    }

    fun setInfo(navInfo: NavInfo?){
        _info.value = navInfo
    }

    fun setNeedNavigation(boolean: Boolean){
        _needNavigation.value = boolean
    }

    fun setTimeCount(number: Long){
        _timeCount.value = number
    }

    fun setMyphoto(photo: String){
        _myPhoto.value = photo
    }

    fun setSelectNavAnimal(list: List<LocalFacility>?){
        _selectNavAnimal.value = list
    }

    fun getWhichRoute(){
        if (MockData.isfirstTime)
            getRecommendRoutes()
    }

    fun getAuthUser(){
        currentUser?.let {
            UserManager.user.key = it.uid
            UserManager.user.email = it.email ?: ""
            UserManager.user.picture = it.photoUrl.toString()
            Logger.d("UserManageruser=${UserManager.user}")
            getMe(it.email ?: "")
        }
    }

    fun getFacilityImage(facility: Facility): Int{
        val filter = MockData.facilityPicture.filter { it.title == facility.item }
        if (filter != listOf<OriMarkInfo>())
            return filter[0].drawable
        else
            return R.drawable.icon_house
    }

    fun getFacilityImage(facility: FireFacility): Int{
        val filter = MockData.facilityPicture.filter { it.title == facility.item }
        if (filter != listOf<OriMarkInfo>())
            return filter[0].drawable
        else
            return R.drawable.icon_house
    }

    fun getLocalFacilities(facilities: List<Facility>){
        val listFacility = mutableListOf<LocalFacility>()
        facilities.forEach {facility ->
            val localFacility = LocalFacility()
            Logger.d("sam00 facility.id=${facility.id}")
            localFacility.idNum = facility.id
            localFacility.name = facility.name
            localFacility.geo = facility.geo.toLatlngs()
            localFacility.location = facility.location
            localFacility.category = facility.category
            localFacility.item = facility.item
            localFacility.image = getFacilityImage(facility)
            listFacility.add(localFacility)
        }
        MockData.localFacility = listFacility
        _localFacilityInMain.value = listFacility
    }

    fun getLocalFacilitiesFromFire(facilities: List<FireFacility>){
        val listFacility = mutableListOf<LocalFacility>()
        facilities.forEach {facility ->
            val localFacility = LocalFacility()
            Logger.d("sam00 facility.id=${facility.id}")
            localFacility.idNum = facility.idNum
            localFacility.name = facility.name
            localFacility.geo = facility.geo.toLatlngs()
            localFacility.location = facility.location
            localFacility.category = facility.category
            localFacility.item = facility.item
            localFacility.image = getFacilityImage(facility)
            listFacility.add(localFacility)
        }
        MockData.localFacility = listFacility
        _localFacilityInMain.value = listFacility
    }

    fun getLocalAreas(areas: List<Area>){
        val listArea = mutableListOf<LocalArea>()
        areas.forEach {area ->
            val imageList = MockData.areas.filter { it.title == area.name }
            val localArea = LocalArea()
            localArea.idNum = area.id
            localArea.geo = area.geo.toLatlngs()
            localArea.category = area.category
            localArea.name = area.name
            localArea.infomation = area.infomation
            localArea.picture = area.picture
            localArea.url = area.url
            if (!imageList.isNullOrEmpty()) localArea.image = imageList[0].drawable
            listArea.add(localArea)
        }
        MockData.localAreas = listArea
        _localAreaInMain.value = listArea
        getNavInfoAreas()
    }

    fun getLocalAnimals(animals: List<Animal>){
        val listLocalAnimal = mutableListOf<LocalAnimal>()
        animals.forEach {animal ->
            val listPicture = listOf<String>(animal.picture1, animal.picture2, animal.picture3, animal.picture4)
            val pictures = mutableListOf<String>()
            listPicture.forEach {
                if (it != ""){
                    pictures.add(it)
                }
            }
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
        MockData.localAnimals = listLocalAnimal
        _localAnimalInMain.value = listLocalAnimal
        getNavInfoAnimals()
    }

    fun getLocalCalendar(calendars: List<Calendar>){
        val listCalender = mutableListOf<LocalCalendar>()
        calendars.forEach {calendar ->
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
        MockData.localCalendars = listCalender
        _localCalendarsInMain.value = listCalender
    }

    fun getRecommendFromFirebase(fireRoutes: List<FireRoute>){
        val listRoute = mutableListOf<Route>()
        fireRoutes.forEach {route ->
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
            publishRoute(route)
        }
        MockData.routes = listRoute
    }

    fun checkHasUser(user: User, context: Context){
        if (user == User()){
            toast(getString(R.string.cant_find_user), context)
        }else{
            val filter = UserManager.friends.filter { friend -> friend.email == user.email }
            if (filter == listOf<User>())
                showAddFriend(user.email, context)
            else
                toast("和 ${user.email} 早已成為同伴", context)
        }
    }

    fun caculateFromStep(number: Int){
        val step = StepInfo()
        step.step = number
        step.kilometer = number * (0.7) * (0.001)
        step.kcal = number*0.035
        UserManager.newStep = step
        _nowStepInfo.value = step
    }

    private fun getDataAnimal() {
        val saveAnimal = readFromFile(ZooApplication.appContext, "animal.txt")
        Logger.d("saveAnimal=$saveAnimal")
        if (saveAnimal == "") {
            getApiAnimals(true)
        } else {
            MockData.localAnimals = jsonToListAnimal(saveAnimal) ?: listOf()
            Logger.d( "localAnimals123=${MockData.localAnimals}")
            getNavInfoAnimals()
        }
    }

    private fun getDataArea() {
        val saveArea = readFromFile(ZooApplication.appContext, "area.txt")
        Logger.d("saveArea=$saveArea")
        if (saveArea == "") {
            getApiAreas(true)
        } else {
            MockData.localAreas = jsonToListArea(saveArea) ?: listOf()
            Logger.d( "localAreas123=${MockData.localAreas}")
            getNavInfoAreas()
        }
    }

    private fun getDataFacility() {
        val saveFacility = readFromFile(ZooApplication.appContext, "facility.txt")
        Logger.d("saveFacility=$saveFacility")
        if (saveFacility == "") {
//            getApiFacility(true)
            getFireFacility(true)
        } else {
            MockData.localFacility = jsonToListFacility(saveFacility) ?: listOf()
            Logger.d("localFacility123=${MockData.localFacility}")
        }
    }

    private fun getDataCalendar() {
        val saveCalendars = readFromFile(ZooApplication.appContext, "calendars.txt")
        Logger.d("saveCalendars=$saveCalendars")
        if (saveCalendars == "") {
            getApiCalendars(true)
        } else {
            MockData.localCalendars = jsonToListCalendar(saveCalendars) ?: listOf()
            Logger.d("localCalendars123=${MockData.localCalendars}")
        }
    }

    fun getDatas(){
        if (MockData.allMarkers == listOf<NavInfo>()){
            getDataAnimal()
            getDataArea()
            getDataFacility()
            getDataCalendar()
        }
    }

    private fun getNavInfoAnimals(){
        MockData.localAnimals.forEach {
            val navInfo = NavInfo()
            navInfo.title = it.nameCh
            if (it.pictures != listOf<String>())
                navInfo.imageUrl = it.pictures[0]
            it.geos.forEach {latLng ->
                navInfo.latLng = latLng
                MockData.allMarkers.add(navInfo)
            }
        }
        Logger.d("allmarker=${MockData.allMarkers.toList()}")
    }

    private fun getNavInfoAreas(){
        MockData.localAreas.forEach {
            val navInfo = NavInfo()
            navInfo.title = it.name
            navInfo.imageUrl = it.picture
            it.geo.forEach {latLng ->
                navInfo.latLng = latLng
                MockData.allMarkers.add(navInfo)
            }
        }
        val a = MockData.allMarkers.filter { it.title == "小貓熊" }
        Logger.d("allmarker=${MockData.allMarkers.toList()}")
    }

    private fun addFriends(email: String){
        getUser(email)
        publishFriend(email, UserManager.user)
    }

    private fun showAddFriend(email: String, context: Context){
        val view = LayoutInflater.from(context).inflate(R.layout.item_confirm_friend, null)
        val cBuilder = AlertDialog.Builder(context).setView(view)
        val cAlertDialog = cBuilder.show()
        view.textTitle.text = "和 ${email} 成為好友嗎 ?"
        view.buttonConfirm.setOnClickListener {
            addFriends(email)
            cAlertDialog.dismiss()
        }
        view.buttonCancel.setOnClickListener {
            cAlertDialog.dismiss()
        }
    }

    fun showLeaveOrNot(context: Context, activity: MainActivity){
        val mBuilder = AlertDialog.Builder(context, R.style.AlertDialogCustom)
        mBuilder.setTitle("確定要離開應用程式嗎")
        mBuilder.setPositiveButton("確定") { dialog, which -> activity.finish()}
        mBuilder.setNegativeButton("取消"){ d: DialogInterface, i: Int -> }
        val dialog = mBuilder.create()
        dialog.show()
    }

    private fun getApiAnimals(isInitial: Boolean = false) {

        coroutineScope.launch {

            if (isInitial) _statusAnimal.value = LoadApiStatus.LOADING

            val result = repository.getApiAnimals()
            Log.d("sam","samanimals=$result")

            _animalResult.value = when (result) {
                is Result.Success -> {
                    _error.value = null
                    if (isInitial) _statusAnimal.value = LoadApiStatus.DONE
                    result.data
                }
                is Result.Fail -> {
                    _error.value = result.error
                    if (isInitial) _statusAnimal.value = LoadApiStatus.ERROR
                    null
                }
                is Result.Error -> {
                    _error.value = result.exception.toString()
                    if (isInitial) _statusAnimal.value = LoadApiStatus.ERROR
                    null
                }
                else -> {
                    _error.value = getString(R.string.you_know_nothing)
                    if (isInitial) _statusAnimal.value = LoadApiStatus.ERROR
                    null
                }
            }
            _refreshStatus.value = false
        }
    }

    private fun getApiAreas(isInitial: Boolean = false) {

        coroutineScope.launch {

            if (isInitial) _statusArea.value = LoadApiStatus.LOADING

            val result = repository.getApiAreas()
            Log.d("sam","samanimals=$result")

            _areaResult.value = when (result) {
                is Result.Success -> {
                    _error.value = null
                    if (isInitial) _statusArea.value = LoadApiStatus.DONE
                    result.data
                }
                is Result.Fail -> {
                    _error.value = result.error
                    if (isInitial) _statusArea.value = LoadApiStatus.ERROR
                    null
                }
                is Result.Error -> {
                    _error.value = result.exception.toString()
                    if (isInitial) _statusArea.value = LoadApiStatus.ERROR
                    null
                }
                else -> {
                    _error.value = getString(R.string.you_know_nothing)
                    if (isInitial) _statusArea.value = LoadApiStatus.ERROR
                    null
                }
            }
            _refreshStatus.value = false
        }
    }

    private fun getApiFacility(isInitial: Boolean = false) {

        coroutineScope.launch {

            if (isInitial) _statusFacility.value = LoadApiStatus.LOADING

            val result = repository.getApiFacility()
            Log.d("sam","samanimalsApiFacility=$result")

            _facilityResult.value = when (result) {
                is Result.Success -> {
                    _error.value = null
                    if (isInitial) _statusFacility.value = LoadApiStatus.DONE
                    result.data
                }
                is Result.Fail -> {
                    _error.value = result.error
                    if (isInitial) _statusFacility.value = LoadApiStatus.ERROR
                    null
                }
                is Result.Error -> {
                    _error.value = result.exception.toString()
                    if (isInitial) _statusFacility.value = LoadApiStatus.ERROR
                    null
                }
                else -> {
                    _error.value = getString(R.string.you_know_nothing)
                    if (isInitial) _statusFacility.value = LoadApiStatus.ERROR
                    null
                }
            }
            _refreshStatus.value = false
        }
    }

    private fun getFireFacility(isInitial: Boolean = false) {

        coroutineScope.launch {

            if (isInitial) _statusFacility.value = LoadApiStatus.LOADING

            val result = repository.getFacilities()
            Log.d("sam","samanimalsApiFacility=$result")

            _facilityFireResult.value = when (result) {
                is Result.Success -> {
                    _error.value = null
                    if (isInitial) _statusFacility.value = LoadApiStatus.DONE
                    result.data
                }
                is Result.Fail -> {
                    _error.value = result.error
                    if (isInitial) _statusFacility.value = LoadApiStatus.ERROR
                    null
                }
                is Result.Error -> {
                    _error.value = result.exception.toString()
                    if (isInitial) _statusFacility.value = LoadApiStatus.ERROR
                    null
                }
                else -> {
                    _error.value = getString(R.string.you_know_nothing)
                    if (isInitial) _statusFacility.value = LoadApiStatus.ERROR
                    null
                }
            }
            _refreshStatus.value = false
        }
    }

    private fun getApiCalendars(isInitial: Boolean = false) {

        coroutineScope.launch {

            if (isInitial) _statusCalendar.value = LoadApiStatus.LOADING

            val result = repository.getApiCalendar()
            Log.d("sam","samcalendars=$result")

            _calendarResult.value = when (result) {
                is Result.Success -> {
                    _error.value = null
                    if (isInitial) _statusCalendar.value = LoadApiStatus.DONE
                    result.data
                }
                is Result.Fail -> {
                    _error.value = result.error
                    if (isInitial) _statusCalendar.value = LoadApiStatus.ERROR
                    null
                }
                is Result.Error -> {
                    _error.value = result.exception.toString()
                    if (isInitial) _statusCalendar.value = LoadApiStatus.ERROR
                    null
                }
                else -> {
                    _error.value = getString(R.string.you_know_nothing)
                    if (isInitial) _statusCalendar.value = LoadApiStatus.ERROR
                    null
                }
            }
            _refreshStatus.value = false
        }
    }

    fun publishUser(user: User) {

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

    fun getRecommendRoutes() {

        coroutineScope.launch {

            _status.value = LoadApiStatus.LOADING

            val result = repository.getRecommendRoute()

            _fireRoute.value = when (result) {
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

    fun getMe(email: String) {

        coroutineScope.launch {

            _status.value = LoadApiStatus.LOADING

            val result = repository.getUser(email)

            _me.value = when (result) {
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

    fun checkUser(email: String) {

        coroutineScope.launch {

            _status.value = LoadApiStatus.LOADING

            val result = repository.getUser(email)

            _checkUser.value = when (result) {
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

}
