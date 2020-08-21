package com.sam.gogozoo.data.source.remote

import androidx.lifecycle.MutableLiveData
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.sam.gogozoo.data.FireRoute
import com.sam.gogozoo.R
import com.sam.gogozoo.ZooApplication
import com.sam.gogozoo.data.*
import com.sam.gogozoo.data.animal.AnimalData
import com.sam.gogozoo.data.animal.FireAnimal
import com.sam.gogozoo.data.area.AreaData
import com.sam.gogozoo.data.area.FireArea
import com.sam.gogozoo.data.calendar.CalendarData
import com.sam.gogozoo.data.calendar.FireCalendar
import com.sam.gogozoo.data.facility.FacilityData
import com.sam.gogozoo.data.facility.FireFacility
import com.sam.gogozoo.data.source.ZooDataSource
import com.sam.gogozoo.data.model.DirectionResponses
import com.sam.gogozoo.network.MapApi
import com.sam.gogozoo.network.ZooApi
import com.sam.gogozoo.util.Logger
import com.sam.gogozoo.util.Util.getString
import com.sam.gogozoo.util.Util.isInternetConnected
import com.sam.gogozoo.util.Util.toGeo
import retrofit2.Call
import java.util.*
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

object ZooRemoteDataSource : ZooDataSource {

    private const val USERS = "users"
    private const val CALENDARS = "calendars"
    private const val ROUTES = "routes"
    private const val RECOMMEND = "recommend"
    private const val FRIEND = "friend"
    private const val STEP = "steps"
    private const val CREATETIME = "createdTime"

    override fun getDirection(
        origin: String,
        destination: String,
        apiKey: String,
        mode: String
    ): Call<DirectionResponses> {
        val result = MapApi.apiServices.getDirection(origin, destination, apiKey)
        return result
    }

    override suspend fun getApiAnimals(): Result<AnimalData> {

        if (!isInternetConnected()) {
            return Result.Fail(getString(R.string.internet_not_connected))
        }

        return try {
            // this will run on a thread managed by Retrofit
            val result = ZooApi.retrofitService.getApiAnimals()

            result.error?.let {
                return Result.Fail(it)
            }
            Result.Success(result)

        } catch (e: Exception) {
            Logger.w("[${this::class.simpleName}] exception=${e.message}")
            Result.Error(e)
        }

    }

    override suspend fun getApiAreas(): Result<AreaData> {
        if (!isInternetConnected()) {
            return Result.Fail(getString(R.string.internet_not_connected))
        }

        return try {
            // this will run on a thread managed by Retrofit
            val result = ZooApi.retrofitService.getApiAreas()

            result.error?.let {
                return Result.Fail(it)
            }
            Result.Success(result)

        } catch (e: Exception) {
            Logger.w("[${this::class.simpleName}] exception=${e.message}")
            Result.Error(e)
        }
    }

    override suspend fun getApiFacility(): Result<FacilityData> {
        if (!isInternetConnected()) {
            return Result.Fail(getString(R.string.internet_not_connected))
        }

        return try {
            // this will run on a thread managed by Retrofit
            val result = ZooApi.retrofitService.getApiFacility()

            result.error?.let {
                return Result.Fail(it)
            }
            Result.Success(result)

        } catch (e: Exception) {
            Logger.w("[${this::class.simpleName}] exception=${e.message}")
            Result.Error(e)
        }
    }

    override suspend fun publishAnimal(fireAnimal: FireAnimal): Result<Boolean> = suspendCoroutine { continuation ->
        val articles = FirebaseFirestore.getInstance().collection("animals")
        val document = articles.document()

        fireAnimal.id = document.id
        fireAnimal.createTime = Calendar.getInstance().timeInMillis

        document
            .set(fireAnimal)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Logger.i("Publish: $fireAnimal")

                    continuation.resume(Result.Success(true))
                } else {
                    task.exception?.let {

                        Logger.w("[${this::class.simpleName}] Error getting documents. ${it.message}")
                        continuation.resume(Result.Error(it))
                        return@addOnCompleteListener
                    }
                    continuation.resume(Result.Fail(ZooApplication.INSTANCE.getString(R.string.you_know_nothing)))
                }
            }
    }

    override suspend fun publishArea(fireArea: FireArea): Result<Boolean> = suspendCoroutine { continuation ->
        val articles = FirebaseFirestore.getInstance().collection("areas")
        val document = articles.document()

        fireArea.id = document.id
        fireArea.createTime = Calendar.getInstance().timeInMillis

        document
            .set(fireArea)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Logger.i("Publish: $fireArea")

                    continuation.resume(Result.Success(true))
                } else {
                    task.exception?.let {

                        Logger.w("[${this::class.simpleName}] Error getting documents. ${it.message}")
                        continuation.resume(Result.Error(it))
                        return@addOnCompleteListener
                    }
                    continuation.resume(Result.Fail(ZooApplication.INSTANCE.getString(R.string.you_know_nothing)))
                }
            }
    }

    override suspend fun publishFacility(fireFacility: FireFacility): Result<Boolean> = suspendCoroutine { continuation ->
        val articles = FirebaseFirestore.getInstance().collection("facilities")
        val document = articles.document()

        fireFacility.id = document.id
        fireFacility.createTime = Calendar.getInstance().timeInMillis

        document
            .set(fireFacility)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Logger.i("Publish: $fireFacility")

                    continuation.resume(Result.Success(true))
                } else {
                    task.exception?.let {

                        Logger.w("[${this::class.simpleName}] Error getting documents. ${it.message}")
                        continuation.resume(Result.Error(it))
                        return@addOnCompleteListener
                    }
                    continuation.resume(Result.Fail(ZooApplication.INSTANCE.getString(R.string.you_know_nothing)))
                }
            }
    }

    override suspend fun publishCalendar(fireCalendar: FireCalendar): Result<Boolean> = suspendCoroutine { continuation ->
        val calendars = FirebaseFirestore.getInstance().collection(CALENDARS)
        val document = calendars.document()

        fireCalendar.idDocument = document.id
        fireCalendar.createTime = Calendar.getInstance().timeInMillis

        document
            .set(fireCalendar)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Logger.i("Publish: $fireCalendar")

                    continuation.resume(Result.Success(true))
                } else {
                    task.exception?.let {

                        Logger.w("[${this::class.simpleName}] Error getting documents. ${it.message}")
                        continuation.resume(Result.Error(it))
                        return@addOnCompleteListener
                    }
                    continuation.resume(Result.Fail(ZooApplication.INSTANCE.getString(R.string.you_know_nothing)))
                }
            }
    }

    override suspend fun getAreas(): Result<List<FireArea>> = suspendCoroutine { continuation ->
        FirebaseFirestore.getInstance()
            .collection("areas")
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val list = mutableListOf<FireArea>()
                    task.result?.forEach { document ->
                        Logger.d(document.id + " => " + document.data)

                        val fireArea = document.toObject(FireArea::class.java)

                        list.add(fireArea)
                    }
                    continuation.resume(Result.Success(list))
                } else {
                    task.exception?.let {

                        Logger.w("[${this::class.simpleName}] Error getting documents. ${it.message}")
                        continuation.resume(Result.Error(it))
                        return@addOnCompleteListener
                    }
                    continuation.resume(Result.Fail(ZooApplication.INSTANCE.getString(R.string.you_know_nothing)))
                }
            }
    }

    override suspend fun getAnimals(): Result<List<FireAnimal>> = suspendCoroutine { continuation ->
        FirebaseFirestore.getInstance()
            .collection("areas")
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val list = mutableListOf<FireAnimal>()
                    task.result?.forEach { document ->
                        Logger.d(document.id + " => " + document.data)

                        val fireAnimal = document.toObject(FireAnimal::class.java)

                        list.add(fireAnimal)
                    }
                    continuation.resume(Result.Success(list))
                } else {
                    task.exception?.let {

                        Logger.w("[${this::class.simpleName}] Error getting documents. ${it.message}")
                        continuation.resume(Result.Error(it))
                        return@addOnCompleteListener
                    }
                    continuation.resume(Result.Fail(ZooApplication.INSTANCE.getString(R.string.you_know_nothing)))
                }
            }
    }

    override suspend fun publishUser(user: User): Result<Boolean> = suspendCoroutine { continuation ->
        val users = FirebaseFirestore.getInstance().collection(USERS)
        val document = users.document(UserManager.user.email)

        user.createdTime = Calendar.getInstance().timeInMillis
        UserManager.user.createdTime = Calendar.getInstance().timeInMillis

        document
            .set(user)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Logger.i("PublishUser: $user")

                    continuation.resume(Result.Success(true))
                } else {
                    task.exception?.let {

                        Logger.w("[${this::class.simpleName}] Error getting documents. ${it.message}")
                        continuation.resume(Result.Error(it))
                        return@addOnCompleteListener
                    }
                    continuation.resume(Result.Fail(ZooApplication.INSTANCE.getString(R.string.you_know_nothing)))
                }
            }
    }

    override suspend fun getUser(email: String): Result<User> = suspendCoroutine { continuation ->
        FirebaseFirestore.getInstance()
            .collection(USERS)
            .whereEqualTo("email", email)
            .get()
            .addOnCompleteListener { task ->
                var user = User()
                if (task.isSuccessful) {

                    task.result?.forEach { document ->
                        Logger.d(document.id + " => " + document.data)
                        user.key = document.getString("key")
                        user.createdTime = document.getLong("createdTime") ?: 0
                        user.email = document.getString("email") ?: ""
                        user.geo = LatLng(document.get("geo.latitude") as Double,  document.get("geo.longitude") as Double)
                        user.picture = document.getString("picture") ?: ""
                    }
                    continuation.resume(Result.Success(user))
                } else {
                    task.exception?.let {

                        Logger.w("[${this::class.simpleName}] Error getting documents. ${it.message}")
                        continuation.resume(Result.Error(it))
                        return@addOnCompleteListener
                    }
                    continuation.resume(Result.Fail(ZooApplication.INSTANCE.getString(R.string.you_know_nothing)))
                }
            }
    }

    override suspend fun publishRoute(route: Route): Result<Boolean> = suspendCoroutine { continuation ->
        val routes = FirebaseFirestore.getInstance()
            .collection(USERS)
            .document(UserManager.user.email)
            .collection(ROUTES)

        val document = routes.document(route.name)

        val list = mutableListOf<FireNavInfo>()
        route.list.forEach {
            val fireNav = FireNavInfo()
            fireNav.title = it.title
            fireNav.geoPoint = it.latLng.toGeo()
            fireNav.meter = it.meter
            fireNav.image = it.image
            fireNav.imageUrl = it.imageUrl
            list.add(fireNav)
        }
        val fireSchedule =
            FireRoute(name = route.name, list = list)

        document
            .set(fireSchedule)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Logger.i("PublishRoute: $routes")

                    continuation.resume(Result.Success(true))
                } else {
                    task.exception?.let {

                        Logger.w("[${this::class.simpleName}] Error getting documents. ${it.message}")
                        continuation.resume(Result.Error(it))
                        return@addOnCompleteListener
                    }
                    continuation.resume(Result.Fail(ZooApplication.INSTANCE.getString(R.string.you_know_nothing)))
                }
            }
    }

    override suspend fun publishNewRoute(route: Route): Result<Boolean> = suspendCoroutine { continuation ->
        val routes = FirebaseFirestore.getInstance()
            .collection(ROUTES)

        var document = routes.document()
        if (route.id == "") {
            route.id = document.id
        }else{
            document = routes.document(route.id)
        }

        val list = mutableListOf<FireNavInfo>()
        route.list.forEach {
            val fireNav = FireNavInfo()
            fireNav.title = it.title
            fireNav.geoPoint = it.latLng.toGeo()
            fireNav.meter = it.meter
            fireNav.image = it.image
            fireNav.imageUrl = it.imageUrl
            list.add(fireNav)
        }
        val fireSchedule =
            FireRoute(route.id, route.name, route.owners, route.open, list)

        document
            .set(fireSchedule)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Logger.i("PublishRoute: $routes")

                    continuation.resume(Result.Success(true))
                } else {
                    task.exception?.let {

                        Logger.w("[${this::class.simpleName}] Error getting documents. ${it.message}")
                        continuation.resume(Result.Error(it))
                        return@addOnCompleteListener
                    }
                    continuation.resume(Result.Fail(ZooApplication.INSTANCE.getString(R.string.you_know_nothing)))
                }
            }

    }

    override fun getLiveRoutes(): MutableLiveData<List<FireRoute>> {

        val liveData = MutableLiveData<List<FireRoute>>()

        FirebaseFirestore.getInstance()
            .collection(ROUTES)
            .whereArrayContains("owners",UserManager.user.email)
            .addSnapshotListener { snapshot, exception ->

                Logger.i("addSnapshotListener detect")

                exception?.let {
                    Logger.w("[${this::class.simpleName}] Error getting documents. ${it.message}")
                }

                val list = mutableListOf<FireRoute>()
                snapshot?.forEach { document ->
                    Logger.d(document.id + " => " + document.data)

                    val route = document.toObject(FireRoute::class.java)
                    list.add(route)
                    Logger.d("checkSnapRoute=$route")
                }

                liveData.value = list
            }
        return liveData
    }

    override suspend fun getRoute(): Result<List<FireRoute>> = suspendCoroutine { continuation ->
        val routes = FirebaseFirestore.getInstance()
            .collection(USERS)
            .document(UserManager.user.email)
            .collection(ROUTES)

        routes
            .get()
            .addOnCompleteListener { task ->

                if (task.isSuccessful) {
                    val list = mutableListOf<FireRoute>()
                    task.result?.forEach { document ->
                        val route = document.toObject(FireRoute::class.java)
                        list.add(route)
                        Logger.d("checkroute=$route")
                    }
                    continuation.resume(Result.Success(list))
                } else {
                    task.exception?.let {

                        Logger.w("[${this::class.simpleName}] Error getting documents. ${it.message}")
                        continuation.resume(Result.Error(it))
                        return@addOnCompleteListener
                    }
                    continuation.resume(Result.Fail(ZooApplication.INSTANCE.getString(R.string.you_know_nothing)))
                }
            }
    }

    override suspend fun publishRecommendRoute(route: Route): Result<Boolean> = suspendCoroutine { continuation ->
        val routes = FirebaseFirestore.getInstance()
            .collection(RECOMMEND)

        val document = routes.document(route.name)

        val list = mutableListOf<FireNavInfo>()
        route.list.forEach {
            val fireNav = FireNavInfo()
            fireNav.title = it.title
            fireNav.geoPoint = it.latLng.toGeo()
            fireNav.meter = it.meter
            fireNav.image = it.image
            fireNav.imageUrl = it.imageUrl
            list.add(fireNav)
        }
        val fireSchedule = FireRoute(name = route.name, list = list)

        document
            .set(fireSchedule)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Logger.i("PublishRoute: $routes")

                    continuation.resume(Result.Success(true))
                } else {
                    task.exception?.let {

                        Logger.w("[${this::class.simpleName}] Error getting documents. ${it.message}")
                        continuation.resume(Result.Error(it))
                        return@addOnCompleteListener
                    }
                    continuation.resume(Result.Fail(ZooApplication.INSTANCE.getString(R.string.you_know_nothing)))
                }
            }

    }

    override suspend fun getRecommendRoute(): Result<List<FireRoute>> = suspendCoroutine { continuation ->
        val routes = FirebaseFirestore.getInstance()
            .collection(RECOMMEND)

        routes
            .get()
            .addOnCompleteListener { task ->

                if (task.isSuccessful) {
                    val list = mutableListOf<FireRoute>()
                    task.result?.forEach { document ->
                        val route = document.toObject(FireRoute::class.java)
                        route.owners = listOf<String>(UserManager.user.email)
                        list.add(route)
                        Logger.d("checkroute=$route")
                    }
                    continuation.resume(Result.Success(list))
                } else {
                    task.exception?.let {

                        Logger.w("[${this::class.simpleName}] Error getting documents. ${it.message}")
                        continuation.resume(Result.Error(it))
                        return@addOnCompleteListener
                    }
                    continuation.resume(Result.Fail(ZooApplication.INSTANCE.getString(R.string.you_know_nothing)))
                }
            }
    }

    override suspend fun publishFriend(email: String, user: User): Result<Boolean> = suspendCoroutine { continuation ->
        val friends = FirebaseFirestore.getInstance()
            .collection(USERS)
            .document(email)
            .collection(FRIEND)

        val document = friends.document(user.email)

        user.createdTime = Calendar.getInstance().timeInMillis

        document
            .set(user)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Logger.i("PublishUser: $user")

                    continuation.resume(Result.Success(true))
                } else {
                    task.exception?.let {

                        Logger.w("[${this::class.simpleName}] Error getting documents. ${it.message}")
                        continuation.resume(Result.Error(it))
                        return@addOnCompleteListener
                    }
                    continuation.resume(Result.Fail(ZooApplication.INSTANCE.getString(R.string.you_know_nothing)))
                }
            }
    }

    override fun getLiveFriend(): MutableLiveData<List<User>> {

        val liveData = MutableLiveData<List<User>>()

        FirebaseFirestore.getInstance()
            .collection(USERS)
            .document(UserManager.user.email)
            .collection(FRIEND)
            .addSnapshotListener { snapshot, exception ->

                Logger.i("addSnapshotListener detect")

                exception?.let {
                    Logger.w("[${this::class.simpleName}] Error getting documents. ${it.message}")
                }

                val list = mutableListOf<User>()
                snapshot?.forEach { document ->
                    Logger.d(document.id + " => " + document.data)

                    val user = User()
                    user.key = document.getString("key")
                    user.createdTime = document.getLong("createdTime") ?: 0
                    user.email = document.getString("email") ?: ""
                    user.geo = LatLng(document.get("geo.latitude") as Double,  document.get("geo.longitude") as Double)
                    user.picture = document.getString("picture") ?: ""

                    list.add(user)
                }

                liveData.value = list
            }
        return liveData
    }
    override suspend fun getFriendLocation(listEmail: List<String>): Result<List<User>> = suspendCoroutine { continuation ->
        val friends = FirebaseFirestore.getInstance()
            .collection(USERS)

        friends
            .whereIn("email", listEmail)
            .get()
            .addOnCompleteListener { task ->

                if (task.isSuccessful) {
                    val list = mutableListOf<User>()
                    task.result?.forEach { document ->
                        val user = User()
                        user.key = document.getString("key")
                        user.createdTime = document.getLong("createdTime") ?: 0
                        user.email = document.getString("email") ?: ""
                        user.geo = LatLng(document.get("geo.latitude") as Double,  document.get("geo.longitude") as Double)
                        user.picture = document.getString("picture") ?: ""

                        list.add(user)
                    }
                    continuation.resume(Result.Success(list))
                } else {
                    task.exception?.let {

                        Logger.w("[${this::class.simpleName}] Error getting documents. ${it.message}")
                        continuation.resume(Result.Error(it))
                        return@addOnCompleteListener
                    }
                    continuation.resume(Result.Fail(ZooApplication.INSTANCE.getString(R.string.you_know_nothing)))
                }
            }
    }

    override suspend fun getRouteOwner(listEmail: List<String>): Result<List<User>> = suspendCoroutine { continuation ->
        val friends = FirebaseFirestore.getInstance()
            .collection(USERS)

        friends
            .whereIn("email", listEmail)
            .get()
            .addOnCompleteListener { task ->

                if (task.isSuccessful) {
                    val list = mutableListOf<User>()
                    task.result?.forEach { document ->
                        val user = User()
                        user.key = document.getString("key")
                        user.createdTime = document.getLong("createdTime") ?: 0
                        user.email = document.getString("email") ?: ""
                        user.geo = LatLng(document.get("geo.latitude") as Double,  document.get("geo.longitude") as Double)
                        user.picture = document.getString("picture") ?: ""

                        list.add(user)
                    }
                    continuation.resume(Result.Success(list))
                } else {
                    task.exception?.let {

                        Logger.w("[${this::class.simpleName}] Error getting documents. ${it.message}")
                        continuation.resume(Result.Error(it))
                        return@addOnCompleteListener
                    }
                    continuation.resume(Result.Fail(ZooApplication.INSTANCE.getString(R.string.you_know_nothing)))
                }
            }
    }


    override suspend fun getApiCalendar(): Result<CalendarData> {
        if (!isInternetConnected()) {
            return Result.Fail(getString(R.string.internet_not_connected))
        }

        return try {
            // this will run on a thread managed by Retrofit
            val result = ZooApi.retrofitService.getApiCalendar()

            result.error?.let {
                return Result.Fail(it)
            }
            Result.Success(result)

        } catch (e: Exception) {
            Logger.w("[${this::class.simpleName}] exception=${e.message}")
            Result.Error(e)
        }
    }

    override suspend fun publishStep(stepInfo: StepInfo): Result<Boolean> = suspendCoroutine { continuation ->
        val step = FirebaseFirestore.getInstance()
            .collection(STEP)
            .document()

        stepInfo.id = step.id
        stepInfo.createdTime = Calendar.getInstance().timeInMillis
        stepInfo.owner = UserManager.user.email

        step
            .set(stepInfo)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Logger.i("PublishStep: $stepInfo")

                    continuation.resume(Result.Success(true))
                } else {
                    task.exception?.let {

                        Logger.w("[${this::class.simpleName}] Error getting documents. ${it.message}")
                        continuation.resume(Result.Error(it))
                        return@addOnCompleteListener
                    }
                    continuation.resume(Result.Fail(ZooApplication.INSTANCE.getString(R.string.you_know_nothing)))
                }
            }
    }

    override fun getLiveSteps(): MutableLiveData<List<StepInfo>> {

        val liveData = MutableLiveData<List<StepInfo>>()

        Logger.d("useremail=${UserManager.user.email}")

        FirebaseFirestore.getInstance()
            .collection(STEP)
            .whereEqualTo("owner", UserManager.user.email)
            .orderBy(CREATETIME, Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, exception ->

                Logger.i("addSnapshotListener detect")

                exception?.let {
                    Logger.w("[${this::class.simpleName}] Error getting documents. ${it.message}")
                }

                val list = mutableListOf<StepInfo>()
                Logger.d("snapshotstep=$snapshot")
                if (snapshot != null) {
                    for (document in snapshot) {
                        Logger.d(document.id + " => " + document.data)

                        val step = document.toObject(StepInfo::class.java)
                        list.add(step)
                        Logger.d("checkSnapStep=$step")
                    }
                }

                liveData.value = list
            }
        return liveData
    }

}

