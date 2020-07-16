package com.sam.gogozoo.data.source.remote

import com.google.android.gms.maps.model.LatLng
import com.google.firebase.firestore.FirebaseFirestore
import com.sam.gogozoo.data.FireSchedule
import com.sam.gogozoo.R
import com.sam.gogozoo.ZooApplication
import com.sam.gogozoo.data.*
import com.sam.gogozoo.data.animal.AnimalData
import com.sam.gogozoo.data.animal.FireAnimal
import com.sam.gogozoo.data.area.AreaData
import com.sam.gogozoo.data.area.FireArea
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

/**
 * Created by Wayne Chen on 2020-01-15.
 *
 * Implementation of the Publisher source that from network.
 */
object ZooRemoteDataSource : ZooDataSource {

    private const val USERS = "users"
    private const val ROUTES = "routes"
    private const val RECOMMEND = "recommend"

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

    override suspend fun getAreas(): Result<List<FireArea>> = suspendCoroutine { continuation ->
        FirebaseFirestore.getInstance()
            .collection("areas")
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val list = mutableListOf<FireArea>()
                    for (document in task.result!!) {
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
                    for (document in task.result!!) {
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
        user.email = user.email
        user.geo = user.geo
        user.key = user.key


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

    override suspend fun getUser(key: String): Result<User> = suspendCoroutine { continuation ->
        FirebaseFirestore.getInstance()
            .collection(USERS)
            .whereEqualTo("key", key)
            .get()
            .addOnCompleteListener { task ->
                var user = User()
                if (task.isSuccessful) {

                    for (document in task.result!!) {
                        Logger.d(document.id + " => " + document.data)
                        user.key = document.getString("key")
                        user.createdTime = document.getLong("createdTime") ?: 0
                        user.email = document.getString("email") ?: ""
                        user.geo = LatLng(document.get("geo.latitude") as Double,  document.get("geo.longitude") as Double)

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

    override suspend fun publishRoute(route: Schedule): Result<Boolean> = suspendCoroutine { continuation ->
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
            FireSchedule(name = route.name, list = list)

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

    override suspend fun getRoute(): Result<List<FireSchedule>> = suspendCoroutine { continuation ->
        val routes = FirebaseFirestore.getInstance()
            .collection(USERS)
            .document(UserManager.user.email)
            .collection(ROUTES)

        routes
            .get()
            .addOnCompleteListener { task ->

                if (task.isSuccessful) {
                    val list = mutableListOf<FireSchedule>()
                    for (document in task.result!!) {

                        val route = document.toObject(FireSchedule::class.java)
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

    override suspend fun publishRecommendRoute(route: Schedule): Result<Boolean> = suspendCoroutine { continuation ->
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
        val fireSchedule = FireSchedule(name = route.name, list = list)

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

    override suspend fun getRecommendRoute(): Result<List<FireSchedule>> = suspendCoroutine { continuation ->
        val routes = FirebaseFirestore.getInstance()
            .collection(RECOMMEND)

        routes
            .get()
            .addOnCompleteListener { task ->

                if (task.isSuccessful) {
                    val list = mutableListOf<FireSchedule>()
                    for (document in task.result!!) {

                        val route = document.toObject(FireSchedule::class.java)
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

}
