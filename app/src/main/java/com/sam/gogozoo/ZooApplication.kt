package com.sam.gogozoo

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import com.sam.gogozoo.data.source.ZooRepository
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
    val repository: ZooRepository
        get() = ServiceLocator.provideRepository(this)

    companion object {
        var INSTANCE: ZooApplication by Delegates.notNull()
        lateinit var appContext: Context
        const val CHANNEL_ID = "exampleServiceChannel"

    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        INSTANCE = this
        appContext = applicationContext
    }


    fun isLiveDataDesign() = true

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel = NotificationChannel(
                CHANNEL_ID,
                "Example Service Channel",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            val manager = getSystemService(
                NotificationManager::class.java
            )
            manager.createNotificationChannel(serviceChannel)
        }
    }
}
