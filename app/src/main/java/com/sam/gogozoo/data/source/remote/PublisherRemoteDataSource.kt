package com.sam.gogozoo.data.source.remote

import android.icu.util.Calendar
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.sam.gogozoo.data.source.PublisherDataSource
import com.sam.gogozoo.model.DirectionResponses
import com.sam.gogozoo.network.ZooApi
import retrofit2.Call
import java.util.*
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

/**
 * Created by Wayne Chen on 2020-01-15.
 *
 * Implementation of the Publisher source that from network.
 */
object PublisherRemoteDataSource : PublisherDataSource {

    override fun getDirection(
        origin: String,
        destination: String,
        apiKey: String,
        mode: String
    ): Call<DirectionResponses> {
        val result = ZooApi.apiServices.getDirection(origin, destination, apiKey)
        return result
    }

}
