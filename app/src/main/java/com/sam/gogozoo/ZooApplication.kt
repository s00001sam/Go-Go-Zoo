package com.sam.gogozoo

import android.app.Application
import android.content.Context
import com.sam.gogozoo.data.source.ZooRepository
import com.sam.gogozoo.util.ServiceLocator
import kotlin.properties.Delegates


class ZooApplication : Application() {

    // Depends on the flavor,
    val repository: ZooRepository
        get() = ServiceLocator.provideRepository(this)

    companion object {
        var INSTANCE: ZooApplication by Delegates.notNull()
        lateinit var appContext: Context
        const val CHANNEL_ID = "exampleServiceChannel"

    }

    override fun onCreate() {
        super.onCreate()
        INSTANCE = this
        appContext = applicationContext
    }

}
