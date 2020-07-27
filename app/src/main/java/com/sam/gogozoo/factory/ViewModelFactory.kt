package com.sam.gogozoo.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.sam.gogozoo.login.LoginViewModel
import com.sam.gogozoo.MainViewModel
import com.sam.gogozoo.calendar.CalendarViewModel
import com.sam.gogozoo.data.source.ZooRepository
import com.sam.gogozoo.homepage.HomeViewModel
import com.sam.gogozoo.listpage.ListViewModel
import com.sam.gogozoo.plate.PlateDialogViewModel
import com.sam.gogozoo.route.RouteViewModel
import com.sam.gogozoo.search.SearchViewModel
import com.sam.gogozoo.web.WebDialogViewModel


/**
 * Created by Wayne Chen on 2020-01-15.
 *
 * Factory for all ViewModels.
 */
@Suppress("UNCHECKED_CAST")
class ViewModelFactory constructor(
    private val repository: ZooRepository
) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel> create(modelClass: Class<T>) =
        with(modelClass) {
            when {
                isAssignableFrom(MainViewModel::class.java) ->
                    MainViewModel(repository)

                isAssignableFrom(ListViewModel::class.java) ->
                    ListViewModel(repository)

                isAssignableFrom(SearchViewModel::class.java) ->
                    SearchViewModel(repository)

                isAssignableFrom(LoginViewModel::class.java) ->
                    LoginViewModel(repository)

                isAssignableFrom(PlateDialogViewModel::class.java) ->
                    PlateDialogViewModel(repository)

                isAssignableFrom(CalendarViewModel::class.java) ->
                    CalendarViewModel(repository)

                isAssignableFrom(WebDialogViewModel::class.java) ->
                    WebDialogViewModel(repository)

                else ->
                    throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
            }
        } as T
}
