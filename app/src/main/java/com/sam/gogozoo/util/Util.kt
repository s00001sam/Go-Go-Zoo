package com.sam.gogozoo.util

import android.R.attr.bitmap
import android.R.attr.path
import android.content.Context
import android.content.ContextWrapper
import android.graphics.Bitmap
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.net.Uri
import android.util.Log
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.firestore.GeoPoint
import com.sam.gogozoo.ZooApplication
import com.sam.gogozoo.data.animal.LocalAnimal
import com.sam.gogozoo.data.area.LocalArea
import com.sam.gogozoo.data.calendar.LocalCalendar
import com.sam.gogozoo.data.facility.LocalFacility
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import java.io.*
import java.text.SimpleDateFormat
import java.util.*


object Util {

    private const val ZOOQR = "gogozooMyQR.png"

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
        val containN = this.contains("\n")
        var listString: List<String>
        if (containN == false)
            listString = this.split("("," ",")")
        else
            listString = this.split("(","\n",")")

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

    fun listCalendarToJson(list: List<LocalCalendar>?): String? {
        list?.let {
            return Moshi.Builder().build().adapter<List<LocalCalendar>>(List::class.java).toJson(list)
        }
        return null
    }
    /**
     * Convert Json to [List] [String]
     */
    fun jsonToListCalendar(json: String?): List<LocalCalendar>? {
        json?.let {
            val type = Types.newParameterizedType(List::class.java, LocalCalendar::class.java)
            val adapter: JsonAdapter<List<LocalCalendar>> = Moshi.Builder().build().adapter(type)
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

    fun LatLng.getDinstance(end: LatLng): Int{
        val lat1: Double = (Math.PI / 180)*(this.latitude)
        val lat2: Double = (Math.PI / 180)*(end.latitude)
        val lon1: Double = (Math.PI / 180)*(this.longitude)
        val lon2: Double = (Math.PI / 180)*(end.longitude)
        val r: Double = 6371.0
        val d = Math.acos(Math.sin(lat1) * Math.sin(lat2) + Math.cos(lat1)*Math.cos(lat2)*Math.cos(lon2-lon1))*r

        return (d*1000).toInt()
    }

    fun Double.to2fString() :String = String.format("%.2f",this)

    // Method to save an bitmap to a file
    fun Bitmap.toFile(): Uri {
        // Get the context wrapper
        val wrapper = ContextWrapper(ZooApplication.appContext)

        // Initialize a new file instance to save bitmap object
        var file = wrapper.getDir("Images",Context.MODE_PRIVATE)
        file = File(file, ZOOQR)

        try{
            // Compress the bitmap and save in jpg format
            val stream:OutputStream = FileOutputStream(file)
            this.compress(Bitmap.CompressFormat.PNG,100,stream)
            stream.flush()
            stream.close()
        }catch (e:IOException){
            e.printStackTrace()
        }

        // Return the saved bitmap uri
        return Uri.parse(file.absolutePath)
    }

    fun String.toTimeInMills(): Long {
        val df = SimpleDateFormat("yyyy/MM/dd")
        val mills = df.parse(this).time

        return mills
    }

    fun Calendar.toTimeInMills(): Long{
        val year = this.get(Calendar.YEAR)
        val month = this.get(Calendar.MONTH)+1
        val date = this.get(Calendar.DAY_OF_MONTH)
        val b = "$year/$month/$date"
        val df = SimpleDateFormat("yyyy/MM/dd")
        val mills = df.parse(b).time

        return mills
    }


}