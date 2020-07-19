package com.sam.gogozoo.calendar

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.sam.gogozoo.data.MockData
import com.sam.gogozoo.data.calendar.LocalCalendar
import com.sam.gogozoo.data.source.ZooRepository
import java.util.*
import com.sam.gogozoo.util.Util.toTimeInMills

class CalendarViewModel(private val repository: ZooRepository) : ViewModel() {

    private val _leave = MutableLiveData<Boolean>()

    val leave: LiveData<Boolean>
        get() = _leave

    private val _selectCalendars = MutableLiveData<List<LocalCalendar>>()

    val selectCalendars: LiveData<List<LocalCalendar>>
        get() = _selectCalendars

    val currentCarlendar = MutableLiveData<Calendar>()


    init {

    }


    fun leave() {
        _leave.value = true
    }

    fun onLeaveCompleted() {
        _leave.value = null
    }
    fun nothing() {}

    fun getListCalendar(calendar: Calendar){
        val currentTime = calendar.toTimeInMills()
        val list = MockData.localCalendars.filter { (it.start <= currentTime) && (it.end >= currentTime) }
        _selectCalendars.value = list
    }
}
