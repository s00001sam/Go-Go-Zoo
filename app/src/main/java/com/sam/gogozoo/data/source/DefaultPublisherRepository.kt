package com.sam.gogozoo.data.source

import com.sam.gogozoo.data.model.DirectionResponses
import retrofit2.Call


/**
 * Created by Wayne Chen on 2020-01-15.
 *
 * Concrete implementation to load Publisher sources.
 */
class DefaultPublisherRepository(private val remoteDataSource: PublisherDataSource,
                                 private val localDataSource: PublisherDataSource
) : PublisherRepository {

    override fun getDirection(
        origin: String,
        destination: String,
        apiKey: String,
        mode: String
    ): Call<DirectionResponses> {
        return remoteDataSource.getDirection(origin, destination, apiKey)
    }
}
