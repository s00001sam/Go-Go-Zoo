package com.sam.gogozoo.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.sam.gogozoo.info.InfoViewModel
import com.sam.gogozoo.data.NavInfo
import com.sam.gogozoo.data.Schedule
import com.sam.gogozoo.data.source.ZooRepository
import com.sam.gogozoo.homepage.HomeViewModel
import com.sam.gogozoo.route.RouteViewModel

@Suppress("UNCHECKED_CAST")
class RouteViewModelFactory(
    private val repository: ZooRepository,
    private val route: Schedule?
) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>) =
        with(modelClass) {
            when {

                isAssignableFrom(RouteViewModel::class.java) ->
                    RouteViewModel(repository, route)

                isAssignableFrom(HomeViewModel::class.java) ->
                    HomeViewModel(repository, route)

                else ->
                    throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
            }
        } as T
}