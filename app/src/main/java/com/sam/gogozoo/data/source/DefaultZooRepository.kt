package com.sam.gogozoo.data.source

import com.sam.gogozoo.data.model.DirectionResponses
import retrofit2.Call


/**
 * Created by Wayne Chen on 2020-01-15.
 *
 * Concrete implementation to load Publisher sources.
 */
class DefaultZooRepository(private val remoteDataSource: ZooDataSource,
                           private val localDataSource: ZooDataSource
) : ZooRepository {

    override fun getDirection(
        origin: String,
        destination: String,
        apiKey: String,
        mode: String
    ): Call<DirectionResponses> {
        return remoteDataSource.getDirection(origin, destination, apiKey)
    }
}
