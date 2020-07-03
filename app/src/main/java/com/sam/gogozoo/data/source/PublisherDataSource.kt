package com.sam.gogozoo.data.source

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.sam.gogozoo.model.DirectionResponses
import retrofit2.Call
import retrofit2.http.Query
import kotlin.coroutines.Continuation

/**
 * Created by Wayne Chen on 2020-01-15.
 *
 * Main entry point for accessing Publisher sources.
 */
interface PublisherDataSource {

    fun getDirection(origin: String, destination: String, apiKey: String, mode: String = "walking"): Call<DirectionResponses>

}
