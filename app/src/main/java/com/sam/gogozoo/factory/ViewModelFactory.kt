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
import com.sam.gogozoo.schedulepage.ScheduleViewModel
import com.sam.gogozoo.search.SearchViewModel


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

                isAssignableFrom(HomeViewModel::class.java) ->
                    HomeViewModel(repository)

                isAssignableFrom(ListViewModel::class.java) ->
                    ListViewModel(repository)

                isAssignableFrom(ScheduleViewModel::class.java) ->
                    ScheduleViewModel(repository)

                isAssignableFrom(SearchViewModel::class.java) ->
                    SearchViewModel(repository)

                isAssignableFrom(LoginViewModel::class.java) ->
                    LoginViewModel(repository)

                isAssignableFrom(PlateDialogViewModel::class.java) ->
                    PlateDialogViewModel(repository)

                isAssignableFrom(CalendarViewModel::class.java) ->
                    CalendarViewModel(repository)

                else ->
                    throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
            }
        } as T
}
