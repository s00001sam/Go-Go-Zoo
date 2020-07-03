package com.sam.gogozoo.factory

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.sam.gogozoo.MainViewModel
import com.sam.gogozoo.data.source.PublisherRepository
import com.sam.gogozoo.homepage.HomeViewModel
import com.sam.gogozoo.listpage.ListViewModel
import com.sam.gogozoo.personpage.PersonViewModel
import com.sam.gogozoo.schedulepage.ScheduleViewModel


/**
 * Created by Wayne Chen on 2020-01-15.
 *
 * Factory for all ViewModels.
 */
@Suppress("UNCHECKED_CAST")
class ViewModelFactory constructor(
    private val repository: PublisherRepository
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

                isAssignableFrom(PersonViewModel::class.java) ->
                    PersonViewModel(repository)

                else ->
                    throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
            }
        } as T
}
