package com.sam.gogozoo

import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.util.Log
import androidx.annotation.Nullable
import androidx.core.app.NotificationCompat
import androidx.lifecycle.MutableLiveData
import com.sam.gogozoo.ZooApplication.Companion.CHANNEL_ID
import com.sam.gogozoo.data.Control
import com.sam.gogozoo.stepcount.StepDetector
import com.sam.gogozoo.stepcount.StepListener
import com.sam.gogozoo.util.Logger
import com.sam.gogozoo.R



class StepService : Service(), SensorEventListener, StepListener {

    private val desciption = "gogoZoo notification"
    lateinit var notificationManager: NotificationManager
    lateinit var notificationChannel: NotificationChannel
    lateinit var notificationBuilder: Notification.Builder
    var secoundsCount: Long = 0
    private var handler = Handler()
    private lateinit var runnable: Runnable
    private var simpleStepDetector: StepDetector? = null
    private var sensorManager: SensorManager? = null
    private var numSteps: Int = 0
    val step = MutableLiveData<Int>()


    override fun onCreate() {
        super.onCreate()
        // Get an instance of the SensorManager
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        simpleStepDetector = StepDetector()
        simpleStepDetector!!.registerListener(this)
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this, 0, notificationIntent, 0
        )
        Control.hasNotification = true
        numSteps = 0
        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        sensorManager!!.registerListener(this, sensorManager!!.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_FASTEST)
        startNotification(getString(R.string.welcome), pendingIntent)
        startTimer()

        return START_NOT_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        sensorManager!!.unregisterListener(this)
        stopTimer()
    }

    @Nullable
    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event!!.sensor.type == Sensor.TYPE_ACCELEROMETER) {
            simpleStepDetector!!.updateAccelerometer(event.timestamp, event.values[0], event.values[1], event.values[2])
        }
    }

    override fun step(timeNs: Long) {
        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this, 0, notificationIntent, 0
        )
        numSteps++
        Logger.d("numSteps=$numSteps")
        val stepString = "已行走 $numSteps 步"
        startNotification(stepString, pendingIntent)
        Control.step.value = numSteps
    }

    fun startTimer(){
        runnable = Runnable{
            secoundsCount++
            Log.d("sam", " samReceive: $secoundsCount")
            Control.timeCount.value = secoundsCount
            handler.postDelayed(runnable,1000)
        }
        handler.postDelayed(runnable,1000)
    }

    fun stopTimer(){
        handler.removeCallbacks(runnable)
    }

    fun startNotification(count: String, pendingIntent: PendingIntent){

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationChannel = NotificationChannel(CHANNEL_ID, desciption, NotificationManager.IMPORTANCE_DEFAULT)
            notificationManager.createNotificationChannel(notificationChannel)
            notificationBuilder = Notification.Builder(this, CHANNEL_ID)
                .setContentTitle(getString(com.sam.gogozoo.R.string.notification_walking))
                .setContentText(count)
                .setSmallIcon(R.drawable.icon_sloth_round)
                .setOnlyAlertOnce(true)
                .setContentIntent(pendingIntent)
                .setShowWhen(true)

        }else{
            notificationBuilder = Notification.Builder(this)
                .setContentTitle(getString(com.sam.gogozoo.R.string.notification_walking))
                .setContentText(count)
                .setSmallIcon(R.drawable.icon_sloth_round)
                .setContentIntent(pendingIntent)
                .setOnlyAlertOnce(true)
                .setShowWhen(true)

        }
        startForeground(1, notificationBuilder.build())
    }

}
