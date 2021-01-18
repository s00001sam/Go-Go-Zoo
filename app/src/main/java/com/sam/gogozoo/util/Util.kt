package com.sam.gogozoo.util

import android.content.Context
import android.content.ContextWrapper
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.net.Uri
import android.os.Environment
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.content.res.AppCompatResources
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.firestore.GeoPoint
import com.sam.gogozoo.MainActivity
import com.sam.gogozoo.R
import com.sam.gogozoo.ZooApplication
import com.sam.gogozoo.data.FireRoute
import com.sam.gogozoo.data.NavInfo
import com.sam.gogozoo.data.Route
import com.sam.gogozoo.data.animal.LocalAnimal
import com.sam.gogozoo.data.area.LocalArea
import com.sam.gogozoo.data.calendar.LocalCalendar
import com.sam.gogozoo.data.facility.Facility
import com.sam.gogozoo.data.facility.LocalFacility
import com.sam.gogozoo.util.Util.toLatlng
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import java.io.*
import java.text.SimpleDateFormat
import java.util.*


object Util {

    private const val ZOOQR = "gogozooMyQR.png"
    private const val GALLERY_IMAGE_REQ_CODE = 102

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
                Logger.d("sam00 1$it")
                var s= it
                if ((it.filter { char -> char.equals('.') }).length > 1) {
                    s = s.replace("..",".")
                }
                x = s.toDouble()
            }
            if (it.startsWith("2")){
                y = it.toDouble()
                val latLng = LatLng(y,x)
                listLatLng.add(latLng)
            }
        }
        return listLatLng.toList()
    }

    fun String.toGeos(): List<GeoPoint> {
        val listGeo = mutableListOf<GeoPoint>()
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
                val geo = GeoPoint(y,x)
                listGeo.add(geo)
            }
        }
        return listGeo.toList()
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
            Logger.d( "outputStreamWriter=$outputStreamWriter")
            outputStreamWriter.write(data)
            outputStreamWriter.close()
        } catch (e: IOException) {
            Logger.e("File write failed: $e")
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
            Logger.e("File not found: " + e.toString())
        } catch (e: IOException) {
            Logger.e("Can not read file: $e")
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

    fun Double.to3fString() :String = String.format("%.3f",this)

    fun Long.toTimeString() :String{
        val text: String
        if (this <10){
            text = "0$this"
        }else{
            text = this.toString()
        }
        return text
    }

    // Method to save an bitmap to a file
    fun Bitmap.toFile(context: Context){

        val imagePath = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        Logger.d("imagePath=$imagePath")

        val newFile = File(imagePath, ZOOQR)

        try{
            // Compress the bitmap and save in jpg format
            val stream = FileOutputStream(newFile)
            this.compress(Bitmap.CompressFormat.PNG,100,stream)
            stream.flush()
            stream.close()

        }catch (e:IOException){
            e.printStackTrace()
        }

    }

    fun String.toTimeInMills(): Long {

        if (this != ""){
            val df = SimpleDateFormat("yyyy/MM/dd")
            val mills = df.parse(this).time

            return mills
        }

        return 0
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

    fun String.getEmailName(): String{
        val list = this.split("@")
        return list[0]
    }

    fun FireRoute.toRoute(): Route {
        val list = mutableListOf<NavInfo>()
        this.list.forEach {fireNav ->
            var nav = NavInfo()
            nav.title = fireNav.title
            nav.meter = fireNav.meter
            nav.latLng = fireNav.geoPoint.toLatlng()
            nav.imageUrl = fireNav.imageUrl
            nav.image = fireNav.image
            list.add(nav)
        }
        val route = Route(this.id, this.name, this.owners, this.open, list)
        return  route
    }

    fun Long.StampToDate(): String {
        // 進來的time以秒為單位，Date輸入為毫秒為單位，要注意
        val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm")
        return simpleDateFormat.format(Date(this))
    }
    
    fun String.toOnePlace(): String{
        val places = this.split("；")
        return places[0]
    }

    fun sortByMeter(navInfos: List<NavInfo>): List<NavInfo>{
        val sortInfo = navInfos.sortedBy { it.meter }
        return sortInfo
    }

    fun toast(text: String, context: Context?) {
        val toast = Toast(context)
        val view = LayoutInflater.from(context).inflate(R.layout.toast, null)
        val textView = view.findViewById<TextView>(R.id.toastText)
        textView.text = text
        toast.view = view
        toast.duration = Toast.LENGTH_SHORT
        toast.setGravity(Gravity.TOP, -220, 100)
        toast.show()
    }

}