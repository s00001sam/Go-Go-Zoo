package com.sam.gogozoo.data.source.local

import android.content.Context
import com.sam.gogozoo.FireSchedule
import com.sam.gogozoo.data.animal.AnimalData
import com.sam.gogozoo.data.animal.FireAnimal
import com.sam.gogozoo.data.Result
import com.sam.gogozoo.data.Schedule
import com.sam.gogozoo.data.User
import com.sam.gogozoo.data.area.AreaData
import com.sam.gogozoo.data.area.FireArea
import com.sam.gogozoo.data.area.LocalArea
import com.sam.gogozoo.data.facility.FacilityData
import com.sam.gogozoo.data.facility.FireFacility
import com.sam.gogozoo.data.source.ZooDataSource
import com.sam.gogozoo.data.model.DirectionResponses
import retrofit2.Call

/**
 * Created by Wayne Chen on 2020-01-15.
 *
 * Concrete implementation of a Publisher source as a db.
 */
class ZooLocalDataSource(val context: Context) : ZooDataSource {

    override fun getDirection(
        origin: String,
        destination: String,
        apiKey: String,
        mode: String
    ): Call<DirectionResponses> {
        TODO("Not yet implemented")
    }

    override suspend fun getApiAnimals(): Result<AnimalData> {
        TODO("Not yet implemented")
    }

    override suspend fun getApiAreas(): Result<AreaData> {
        TODO("Not yet implemented")
    }

    override suspend fun getApiFacility(): Result<FacilityData> {
        TODO("Not yet implemented")
    }

    override suspend fun publishAnimal(fireAnimal: FireAnimal): Result<Boolean> {
        TODO("Not yet implemented")
    }

    override suspend fun publishArea(fireArea: FireArea): Result<Boolean> {
        TODO("Not yet implemented")
    }

    override suspend fun publishFacility(fireFacility: FireFacility): Result<Boolean> {
        TODO("Not yet implemented")
    }

    override suspend fun getAreas(): Result<List<FireArea>> {
        TODO("Not yet implemented")
    }

    override suspend fun getAnimals(): Result<List<FireAnimal>> {
        TODO("Not yet implemented")
    }

    override suspend fun publishUser(user: User): Result<Boolean> {
        TODO("Not yet implemented")
    }

    override suspend fun getUser(id: String): Result<User> {
        TODO("Not yet implemented")
    }

    override suspend fun publishRoute(route: Schedule): Result<Boolean> {
        TODO("Not yet implemented")
    }

    override suspend fun getRoute(): Result<List<FireSchedule>> {
        TODO("Not yet implemented")
    }

}
