package com.sam.gogozoo.data.source.local

import android.content.Context
import com.sam.gogozoo.data.source.PublisherDataSource
import com.sam.gogozoo.data.model.DirectionResponses
import retrofit2.Call

/**
 * Created by Wayne Chen on 2020-01-15.
 *
 * Concrete implementation of a Publisher source as a db.
 */
class PublisherLocalDataSource(val context: Context) : PublisherDataSource {

    override fun getDirection(
        origin: String,
        destination: String,
        apiKey: String,
        mode: String
    ): Call<DirectionResponses> {
        TODO("Not yet implemented")
    }

}
