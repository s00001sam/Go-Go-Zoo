package com.sam.gogozoo.util

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.util.Log
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.firestore.GeoPoint
import com.sam.gogozoo.ZooApplication
import com.sam.gogozoo.data.animal.LocalAnimal
import com.sam.gogozoo.data.area.LocalArea
import com.sam.gogozoo.data.facility.LocalFacility
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import java.io.*

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
    fun GeoPoint.toLatlng(): LatLng{
        val latLng = LatLng(this.latitude, this.longitude)
        return latLng
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

    fun List<GeoPoint>.toLatlngs(): List<LatLng>{
        val listLatlng = mutableListOf<LatLng>()
        this.forEach {
            val latLng = LatLng(it.latitude, it.longitude)
            listLatlng.add(latLng)
        }
        return listLatlng
    }

    fun listAreaToJson(list: List<LocalArea>?): String? {
        list?.let {
            return Moshi.Builder().build().adapter<List<LocalArea>>(List::class.java).toJson(list)
        }
        return null
    }

    /**
     * Convert Json to [List] [String]
     */
    fun jsonToListArea(json: String?): List<LocalArea>? {
        json?.let {
            val type = Types.newParameterizedType(List::class.java, LocalArea::class.java)
            val adapter: JsonAdapter<List<LocalArea>> = Moshi.Builder().build().adapter(type)
            return adapter.fromJson(it)
        }
        return null
    }

    fun listAnimalToJson(list: List<LocalAnimal>?): String? {
        list?.let {
            return Moshi.Builder().build().adapter<List<LocalAnimal>>(List::class.java).toJson(list)
        }
        return null
    }

    /**
     * Convert Json to [List] [String]
     */
    fun jsonToListFacility(json: String?): List<LocalFacility>? {
        json?.let {
            val type = Types.newParameterizedType(List::class.java, LocalFacility::class.java)
            val adapter: JsonAdapter<List<LocalFacility>> = Moshi.Builder().build().adapter(type)
            return adapter.fromJson(it)
        }
        return null
    }

    fun listFacilityToJson(list: List<LocalFacility>?): String? {
        list?.let {
            return Moshi.Builder().build().adapter<List<LocalFacility>>(List::class.java).toJson(list)
        }
        return null
    }

    /**
     * Convert Json to [List] [String]
     */
    fun jsonToListAnimal(json: String?): List<LocalAnimal>? {
        json?.let {
            val type = Types.newParameterizedType(List::class.java, LocalAnimal::class.java)
            val adapter: JsonAdapter<List<LocalAnimal>> = Moshi.Builder().build().adapter(type)
            return adapter.fromJson(it)
        }
        return null
    }


    fun writeToFile(data: String?, context: Context, fileName: String) {
        try {
            val outputStreamWriter = OutputStreamWriter(
                context.openFileOutput(fileName, Context.MODE_PRIVATE)
            )
            Log.d("sam", "outputStreamWriter=$outputStreamWriter")
            outputStreamWriter.write(data)
            outputStreamWriter.close()
        } catch (e: IOException) {
            Log.e("Exception", "File write failed: $e")
        }
    }

    fun readFromFile(context: Context, fileName: String): String? {
        var ret = ""
        try {
            val inputStream: InputStream? = context.openFileInput(fileName)
            if (inputStream != null) {
                val inputStreamReader = InputStreamReader(inputStream)
                val bufferedReader = BufferedReader(inputStreamReader)
                var receiveString: String? = ""
                val stringBuilder = StringBuilder()
                while (bufferedReader.readLine().also({ receiveString = it }) != null) {
                    stringBuilder.append("\n").append(receiveString)
                }
                inputStream.close()
                ret = stringBuilder.toString()
            }
        } catch (e: FileNotFoundException) {
            Log.e("login activity", "File not found: " + e.toString())
        } catch (e: IOException) {
            Log.e("login activity", "Can not read file: $e")
        }
        return ret
    }




}