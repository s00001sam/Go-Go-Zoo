package com.sam.gogozoo.data.source

import androidx.lifecycle.MutableLiveData
import com.sam.gogozoo.data.FireSchedule
import com.sam.gogozoo.data.animal.AnimalData
import com.sam.gogozoo.data.*
import com.sam.gogozoo.data.animal.FireAnimal
import com.sam.gogozoo.data.area.AreaData
import com.sam.gogozoo.data.area.FireArea
import com.sam.gogozoo.data.calendar.CalendarData
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

    suspend fun publishUser(user: User): Result<Boolean>

    suspend fun getUser(email: String): Result<User>

    suspend fun publishRoute(route: Schedule): Result<Boolean>

    suspend fun getRoute(): Result<List<FireSchedule>>

    suspend fun publishRecommendRoute(route: Schedule): Result<Boolean>

    suspend fun getRecommendRoute(): Result<List<FireSchedule>>

    suspend fun publishFriend(email: String, user: User): Result<Boolean>

    fun getLiveFriend(): MutableLiveData<List<User>>

    suspend fun getFriendLocation(listEmail: List<String>): Result<List<User>>

    suspend fun getApiCalendar(): Result<CalendarData>

    suspend fun publishNewRoute(route: Schedule): Result<Boolean>

}
