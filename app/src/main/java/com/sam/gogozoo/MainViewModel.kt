package com.sam.gogozoo

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import androidx.core.app.ActivityCompat.startActivityForResult
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.sam.gogozoo.data.animal.AnimalData
import com.sam.gogozoo.data.source.ZooRepository
import com.sam.gogozoo.network.LoadApiStatus
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import com.sam.gogozoo.data.*
import com.sam.gogozoo.data.animal.FireAnimal
import com.sam.gogozoo.data.animal.LocalAnimal
import com.sam.gogozoo.data.area.AreaData
import com.sam.gogozoo.data.area.FireArea
import com.sam.gogozoo.data.area.LocalArea
import com.sam.gogozoo.data.calendar.CalendarData
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
import kotlinx.android.synthetic.main.item_confirm_friend.view.*

/**
 * Created by Wayne Chen on 2020-01-15.
 *
 * The [ViewModel] that is attached to the [MainActivity].
 */
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

    private val _fireAreasGet = MutableLiveData<List<FireArea>>()

    val fireAreasGet: LiveData<List<FireArea>>
        get() = _fireAreasGet

    private val _fireUser = MutableLiveData<User>()

    val fireUser: LiveData<User>
        get() = _fireUser

    private val _fireRoute = MutableLiveData<List<FireSchedule>>()

    val fireRoute: LiveData<List<FireSchedule>>
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

    // Create a Coroutine scope using a job to be able to cancel when needed
    private var viewModelJob = Job()

    val localAreaInMain = MutableLiveData<List<LocalArea>>()

    val localAnimalInMain = MutableLiveData<List<LocalAnimal>>()

    val localFacilityInMain = MutableLiveData<List<LocalFacility>>()

    val localCalendarsInMain = MutableLiveData<List<LocalCalendar>>()

    // Record current fragment to support data binding
    val currentFragmentType = MutableLiveData<CurrentFragmentType>()

    val myPhoto = MutableLiveData<String>()

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
        getWhichRoute()
        getAuthUser()
    }

    fun getWhichRoute(){
        if (MockData.isfirstTime)
            getRecommendRoutes()
        else
            getRoutes()
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

    fun publishSchedules(){
        MockData.schedules.forEach {
            publishRoute(it)
        }
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
            Log.d("sam","samanimals=$result")

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

    fun publishAnimals(fireAnimal: FireAnimal) {

        coroutineScope.launch {

            _status.value = LoadApiStatus.LOADING

            when (val result = repository.publishAnimal(fireAnimal)) {
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

    fun publishAreas(fireArea: FireArea) {

        coroutineScope.launch {

            _status.value = LoadApiStatus.LOADING

            when (val result = repository.publishArea(fireArea)) {
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

    fun publishFacility(fireFacility: FireFacility) {

        coroutineScope.launch {

            _status.value = LoadApiStatus.LOADING

            when (val result = repository.publishFacility(fireFacility)) {
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

    fun publishRoute(route: Schedule) {

        coroutineScope.launch {

            _status.value = LoadApiStatus.LOADING

            when (val result = repository.publishRoute(route)) {
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

    fun publishRecommendRoute(route: Schedule) {

        coroutineScope.launch {

            _status.value = LoadApiStatus.LOADING

            when (val result = repository.publishRecommendRoute(route)) {
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

    fun getFireAreas() {

        coroutineScope.launch {

            _status.value = LoadApiStatus.LOADING

            val result = repository.getAreas()

            _fireAreasGet.value = when (result) {
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

    fun getRoutes() {

        coroutineScope.launch {

            _status.value = LoadApiStatus.LOADING

            val result = repository.getRoute()

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



    fun getDataAnimal() {
        val saveAnimal = readFromFile(ZooApplication.appContext, "animal.txt")
        Log.d("sam", "saveAnimal=$saveAnimal")
        if (saveAnimal == "") {
            getApiAnimals(true)
        } else {
            MockData.localAnimals = jsonToListAnimal(saveAnimal) ?: listOf()
            Log.d("sam", "localAnimals123=${MockData.localAnimals}")
            getNavInfoAnimals()
        }
    }

        fun getDataArea() {
            val saveArea = readFromFile(ZooApplication.appContext, "area.txt")
            Log.d("sam", "saveArea=$saveArea")
            if (saveArea == "") {
                getApiAreas(true)
            } else {
                MockData.localAreas = jsonToListArea(saveArea) ?: listOf()
                Log.d("sam", "localAreas123=${MockData.localAreas}")
                getNavInfoAreas()
            }
        }

        fun getDataFacility() {
            val saveFacility = readFromFile(ZooApplication.appContext, "facility.txt")
            Log.d("sam", "saveFacility=$saveFacility")
            if (saveFacility == "") {
                getApiFacility(true)
            } else {
                MockData.localFacility = jsonToListFacility(saveFacility) ?: listOf()
                Log.d("sam", "localFacility123=${MockData.localFacility}")
            }
        }

        fun getDataCalendar() {
            val saveCalendars = readFromFile(ZooApplication.appContext, "calendars.txt")
            Log.d("sam", "saveCalendars=$saveCalendars")
            if (saveCalendars == "") {
                getApiCalendars(true)
            } else {
                MockData.localCalendars = jsonToListCalendar(saveCalendars) ?: listOf()
                Log.d("sam", "localCalendars123=${MockData.localCalendars}")
            }
        }

    fun getNavInfoAnimals(){
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
        Log.d("sam", "allmarker=${MockData.allMarkers.toList()}")
    }

    fun getNavInfoAreas(){
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
        Log.d("sam", "sam1234567=$a")
        Log.d("sam", "allmarker=${MockData.allMarkers.toList()}")
    }

    fun addFriends(email: String){
        getUser(email)
        publishFriend(email, UserManager.user)
    }

    fun showAddFriend(email: String, context: Context){
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

    fun refresh() {
        if (!ZooApplication.INSTANCE.isLiveDataDesign()) {
            _refresh.value = true
        }
    }

    fun onRefreshed() {
        if (!ZooApplication.INSTANCE.isLiveDataDesign()) {
            _refresh.value = null
        }
    }
}
