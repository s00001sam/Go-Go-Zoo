package com.sam.gogozoo.data.source

import androidx.lifecycle.MutableLiveData


/**
 * Created by Wayne Chen on 2020-01-15.
 *
 * Concrete implementation to load Publisher sources.
 */
class DefaultPublisherRepository(private val remoteDataSource: PublisherDataSource,
                                 private val localDataSource: PublisherDataSource
) : PublisherRepository {


}
