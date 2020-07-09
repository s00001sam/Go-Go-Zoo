package com.sam.gogozoo.network

import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.sam.gogozoo.data.animal.AnimalData
import com.sam.gogozoo.data.area.AreaData
import com.sam.gogozoo.data.facility.FacilityData
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.*


private const val ZOO_API_URL = "https://data.taipei/api/v1/dataset/"

private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()
private val client = OkHttpClient.Builder()
    .addInterceptor(HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY })
    .build()
private val zooRetrofit = Retrofit.Builder()
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .addCallAdapterFactory(CoroutineCallAdapterFactory())
    .baseUrl(ZOO_API_URL)
    .client(client)
    .build()


//function of call Api
interface ZooApiServices {

    @GET("a3e2b221-75e0-45c1-8f97-75acbd43d613")
    suspend fun getApiAnimals(@Query ("scope") scope: String = "resourceAquire"): AnimalData

    @GET("5a0e5fbb-72f8-41c6-908e-2fb25eff9b8a")
    suspend fun getApiAreas(@Query ("scope") scope: String = "resourceAquire"): AreaData

    @GET("5048d475-7642-43ee-ac6f-af0a368d63bf")
    suspend fun getApiFacility(@Query ("scope") scope: String = "resourceAquire"): FacilityData

}

object ZooApi {
    val retrofitService by lazy { zooRetrofit.create(
        ZooApiServices::class.java) }
}