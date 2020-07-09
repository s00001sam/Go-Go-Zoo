package com.sam.gogozoo.data.source.remote

import com.google.firebase.firestore.FirebaseFirestore
import com.sam.gogozoo.R
import com.sam.gogozoo.ZooApplication
import com.sam.gogozoo.data.animal.AnimalData
import com.sam.gogozoo.data.animal.FireAnimal
import com.sam.gogozoo.data.Result
import com.sam.gogozoo.data.area.AreaData
import com.sam.gogozoo.data.area.FireArea
import com.sam.gogozoo.data.area.LocalArea
import com.sam.gogozoo.data.facility.FacilityData
import com.sam.gogozoo.data.facility.FireFacility
import com.sam.gogozoo.data.source.ZooDataSource
import com.sam.gogozoo.data.model.DirectionResponses
import com.sam.gogozoo.network.MapApi
import com.sam.gogozoo.network.ZooApi
import com.sam.gogozoo.util.Logger
import com.sam.gogozoo.util.Util.getString
import com.sam.gogozoo.util.Util.isInternetConnected
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

}
