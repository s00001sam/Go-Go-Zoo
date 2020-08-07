package com.sam.gogozoo.data.source.local

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.sam.gogozoo.data.*
import com.sam.gogozoo.data.animal.AnimalData
import com.sam.gogozoo.data.animal.FireAnimal
import com.sam.gogozoo.data.area.AreaData
import com.sam.gogozoo.data.area.FireArea
import com.sam.gogozoo.data.calendar.CalendarData
import com.sam.gogozoo.data.calendar.FireCalendar
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

    override suspend fun publishCalendar(fireCalendar: FireCalendar): Result<Boolean> {
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

    override suspend fun getUser(email: String): Result<User> {
        TODO("Not yet implemented")
    }

    override suspend fun publishRoute(route: Route): Result<Boolean> {
        TODO("Not yet implemented")
    }

    override suspend fun getRoute(): Result<List<FireRoute>> {
        TODO("Not yet implemented")
    }

    override suspend fun publishRecommendRoute(route: Route): Result<Boolean> {
        TODO("Not yet implemented")
    }

    override suspend fun getRecommendRoute(): Result<List<FireRoute>> {
        TODO("Not yet implemented")
    }

    override suspend fun publishFriend(email: String, user: User): Result<Boolean> {
        TODO("Not yet implemented")
    }

    override fun getLiveFriend(): MutableLiveData<List<User>> {
        TODO("Not yet implemented")
    }

    override suspend fun getFriendLocation(listEmail: List<String>): Result<List<User>> {
        TODO("Not yet implemented")
    }

    override suspend fun getApiCalendar(): Result<CalendarData> {
        TODO("Not yet implemented")
    }

    override suspend fun publishNewRoute(route: Route): Result<Boolean> {
        TODO("Not yet implemented")
    }

    override fun getLiveRoutes(): MutableLiveData<List<FireRoute>> {
        TODO("Not yet implemented")
    }

    override suspend fun getRouteOwner(listEmail: List<String>): Result<List<User>> {
        TODO("Not yet implemented")
    }

    override suspend fun publishStep(stepInfo: StepInfo): Result<Boolean> {
        TODO("Not yet implemented")
    }

    override fun getLiveSteps(): MutableLiveData<List<StepInfo>> {
        TODO("Not yet implemented")
    }

}
