package com.sam.gogozoo.data.source

import com.sam.gogozoo.data.model.DirectionResponses
import retrofit2.Call

/**
 * Created by Wayne Chen on 2020-01-15.
 *
 * Interface to the Publisher layers.
 */
interface ZooRepository {

    fun getDirection(origin: String, destination: String, apiKey: String, mode: String = "walking"): Call<DirectionResponses>

}
