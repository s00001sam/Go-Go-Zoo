package com.sam.gogozoo.util

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.firestore.GeoPoint
import com.sam.gogozoo.ZooApplication

object Util {

    fun isInternetConnected(): Boolean {
        val cm = ZooApplication.INSTANCE
            .getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork: NetworkInfo? = cm.activeNetworkInfo
        return activeNetwork?.isConnectedOrConnecting == true
    }

    fun getString(resourceId: Int): String {
        return ZooApplication.INSTANCE.getString(resourceId)
    }

    fun LatLng.toGeo(): GeoPoint{
        val geoPoint = GeoPoint(this.latitude, this.longitude)
        return geoPoint
    }

    fun String.toLatlngs (): List<LatLng> {
        val listLatLng = mutableListOf<LatLng>()
        val listString = this.split("("," ",")")
        var  x: Double = 0.0
        var  y: Double = 0.0
        listString.forEach {
            if (it.startsWith("1")) {
                x = it.toDouble()
            }
            if (it.startsWith("2")){
                y = it.toDouble()
                val latLng = LatLng(y,x)
                listLatLng.add(latLng)
            }
        }
        return listLatLng.toList()
    }
}