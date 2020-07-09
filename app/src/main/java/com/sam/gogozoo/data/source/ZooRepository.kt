package com.sam.gogozoo.data.source

import com.sam.gogozoo.data.animal.AnimalData
import com.sam.gogozoo.data.*
import com.sam.gogozoo.data.animal.FireAnimal
import com.sam.gogozoo.data.area.AreaData
import com.sam.gogozoo.data.area.FireArea
import com.sam.gogozoo.data.area.LocalArea
import com.sam.gogozoo.data.facility.FacilityData
import com.sam.gogozoo.data.facility.FireFacility
import com.sam.gogozoo.data.model.DirectionResponses
import retrofit2.Call

/**
 * Created by Wayne Chen on 2020-01-15.
 *
 * Interface to the Publisher layers.
 */
interface ZooRepository {

    fun getDirection(origin: String, destination: String, apiKey: String, mode: String = "walking"): Call<DirectionResponses>

    suspend fun getApiAnimals(): Result<AnimalData>

    suspend fun getApiAreas(): Result<AreaData>

    suspend fun getApiFacility(): Result<FacilityData>

    suspend fun publishAnimal(fireAnimal: FireAnimal): Result<Boolean>

    suspend fun publishArea(fireArea: FireArea): Result<Boolean>

    suspend fun publishFacility(fireFacility: FireFacility): Result<Boolean>

    suspend fun getAreas(): Result<List<FireArea>>

    suspend fun getAnimals(): Result<List<FireAnimal>>

}
