package com.sam.gogozoo.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.sam.gogozoo.data.Route
import com.sam.gogozoo.data.source.ZooRepository
import com.sam.gogozoo.homepage.HomeViewModel
import com.sam.gogozoo.route.RouteViewModel

@Suppress("UNCHECKED_CAST")
class RouteViewModelFactory(
    private val repository: ZooRepository,
    private val route: Route?
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