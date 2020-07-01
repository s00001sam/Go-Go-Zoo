package com.sam.gogozoo

import android.app.Application
import com.sam.gogozoo.data.source.PublisherRepository
import com.sam.gogozoo.util.ServiceLocator
import kotlin.properties.Delegates

/**
 * Created by Wayne Chen on 2020-01-15.
 *
 * An application that lazily provides a repository. Note that this Service Locator pattern is
 * used to simplify the sample. Consider a Dependency Injection framework.
 */
class ZooApplication : Application() {

    // Depends on the flavor,
    val repository: PublisherRepository
        get() = ServiceLocator.provideRepository(this)

    companion object {
        var INSTANCE: ZooApplication by Delegates.notNull()
    }

    override fun onCreate() {
        super.onCreate()
        INSTANCE = this
    }

    fun isLiveDataDesign() = true
}
