package com.sam.gogozoo.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.sam.gogozoo.Login.LoginViewModel
import com.sam.gogozoo.MainViewModel
import com.sam.gogozoo.calendar.CalendarViewModel
import com.sam.gogozoo.data.source.ZooRepository
import com.sam.gogozoo.introductions.IntroStartViewModel
import com.sam.gogozoo.listpage.ListViewModel
import com.sam.gogozoo.plate.PlateDialogViewModel
import com.sam.gogozoo.search.SearchViewModel
import com.sam.gogozoo.stepcount.StepViewModel
import com.sam.gogozoo.stepcount.item.CountViewModel
import com.sam.gogozoo.stepcount.item.RecordViewModel
import com.sam.gogozoo.web.WebDialogViewModel

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

                isAssignableFrom(StepViewModel::class.java) ->
                    StepViewModel(repository)

                isAssignableFrom(CountViewModel::class.java) ->
                    CountViewModel(repository)

                isAssignableFrom(RecordViewModel::class.java) ->
                    RecordViewModel(repository)

                isAssignableFrom(IntroStartViewModel::class.java) ->
                    IntroStartViewModel(repository)

                else ->
                    throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
            }
        } as T
}
