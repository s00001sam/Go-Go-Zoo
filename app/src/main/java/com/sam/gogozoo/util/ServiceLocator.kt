package com.sam.gogozoo.util

import android.content.Context
import androidx.annotation.VisibleForTesting
import com.sam.gogozoo.data.source.DefaultZooRepository
import com.sam.gogozoo.data.source.ZooDataSource
import com.sam.gogozoo.data.source.ZooRepository
import com.sam.gogozoo.data.source.local.ZooLocalDataSource
import com.sam.gogozoo.data.source.remote.ZooRemoteDataSource

object ServiceLocator {

    @Volatile
    var repository: ZooRepository? = null
        @VisibleForTesting set

    fun provideRepository(context: Context): ZooRepository {
        synchronized(this) {
            return repository
                ?: repository
                ?: createPublisherRepository(context)
        }
    }

    private fun createPublisherRepository(context: Context): ZooRepository {
        return DefaultZooRepository(
            ZooRemoteDataSource,
            createLocalDataSource(context)
        )
    }

    private fun createLocalDataSource(context: Context): ZooDataSource {
        return ZooLocalDataSource(context)
    }
}