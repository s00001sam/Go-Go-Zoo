package com.sam.gogozoo.data.source

import androidx.lifecycle.MutableLiveData
import com.sam.gogozoo.data.FireRoute
import com.sam.gogozoo.data.animal.AnimalData
import com.sam.gogozoo.data.animal.FireAnimal
import com.sam.gogozoo.data.Result
import com.sam.gogozoo.data.Route
import com.sam.gogozoo.data.User
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
 * Concrete implementation to load Publisher sources.
 */
class DefaultZooRepository(private val remoteDataSource: ZooDataSource,
                           private val localDataSource: ZooDataSource
) : ZooRepository {

    override fun getDirection(
        origin: String,
        destination: String,
        apiKey: String,
        mode: String
    ): Call<DirectionResponses> {
        return remoteDataSource.getDirection(origin, destination, apiKey)
    }

    override suspend fun getApiAnimals(): Result<AnimalData> {
        return remoteDataSource.getApiAnimals()
    }

    override suspend fun getApiAreas(): Result<AreaData> {
        return remoteDataSource.getApiAreas()
    }

    override suspend fun getApiFacility(): Result<FacilityData> {
        return remoteDataSource.getApiFacility()
    }

    override suspend fun publishAnimal(fireAnimal: FireAnimal): Result<Boolean> {
        return remoteDataSource.publishAnimal(fireAnimal)
    }

    override suspend fun publishArea(fireArea: FireArea): Result<Boolean> {
        return remoteDataSource.publishArea(fireArea)
    }

    override suspend fun publishFacility(fireFacility: FireFacility): Result<Boolean> {
        return remoteDataSource.publishFacility(fireFacility)
    }

    override suspend fun getAreas(): Result<List<FireArea>> {
        return remoteDataSource.getAreas()
    }

    override suspend fun getAnimals(): Result<List<FireAnimal>> {
        return remoteDataSource.getAnimals()
    }

    override suspend fun publishUser(user: User): Result<Boolean> {
        return remoteDataSource.publishUser(user)
    }

    override suspend fun getUser(email: String): Result<User> {
        return remoteDataSource.getUser(email)
    }

    override suspend fun publishRoute(route: Route): Result<Boolean> {
        return remoteDataSource.publishRoute(route)
    }

    override suspend fun getRoute(): Result<List<FireRoute>> {
        return remoteDataSource.getRoute()
    }

    override suspend fun publishRecommendRoute(route: Route): Result<Boolean> {
        return remoteDataSource.publishRecommendRoute(route)
    }

    override suspend fun getRecommendRoute(): Result<List<FireRoute>> {
        return remoteDataSource.getRecommendRoute()
    }

    override suspend fun publishFriend(email: String, user: User): Result<Boolean> {
        return remoteDataSource.publishFriend(email, user)
    }

    override fun getLiveFriend(): MutableLiveData<List<User>> {
        return remoteDataSource.getLiveFriend()
    }

    override suspend fun getFriendLocation(listEmail: List<String>): Result<List<User>> {
        return remoteDataSource.getFriendLocation(listEmail)
    }

    override suspend fun getApiCalendar(): Result<CalendarData> {
        return remoteDataSource.getApiCalendar()
    }

    override suspend fun publishNewRoute(route: Route): Result<Boolean> {
        return remoteDataSource.publishNewRoute(route)
    }

    override fun getLiveRoutes(): MutableLiveData<List<FireRoute>> {
        return remoteDataSource.getLiveRoutes()
    }

    override suspend fun getRouteOwner(listEmail: List<String>): Result<List<User>> {
        return remoteDataSource.getRouteOwner(listEmail)
    }
}
