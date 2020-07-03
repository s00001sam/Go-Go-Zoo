package com.sam.gogozoo.data.source

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.sam.gogozoo.model.DirectionResponses
import retrofit2.Call

/**
 * Created by Wayne Chen on 2020-01-15.
 *
 * Interface to the Publisher layers.
 */
interface PublisherRepository {

    fun getDirection(origin: String, destination: String, apiKey: String, mode: String = "walking"): Call<DirectionResponses>

}
