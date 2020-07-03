package com.sam.gogozoo.network

import com.sam.gogozoo.R
import com.sam.gogozoo.model.DirectionResponses
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query


private const val GOOGLE_API_URL = "https://maps.googleapis.com"


private val retrofit = Retrofit.Builder()
    .addConverterFactory(GsonConverterFactory.create())
    .baseUrl(GOOGLE_API_URL)
    .build()



interface ApiServices {

    @GET("maps/api/directions/json")
    fun getDirection(@Query("origin") origin: String,
                     @Query("destination") destination: String,
                     @Query("key") apiKey: String,
                     @Query("mode") mode: String = "walking"): Call<DirectionResponses>

}

object ZooApi {
    val apiServices by lazy { retrofit.create<ApiServices>(ApiServices::class.java) }
}