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

    private val _currentCarlendar = MutableLiveData<Calendar>()

    val currentCarlendar: LiveData<Calendar>
        get() = _currentCarlendar

    private val _selectLocalCalendar = MutableLiveData<LocalCalendar>()

    val selectLocalCalendar: LiveData<LocalCalendar>
        get() = _selectLocalCalendar

    init {

    }

    fun setSelectCalendar(localCalendar: LocalCalendar){
        _selectLocalCalendar.value = localCalendar
    }

    fun setCurrentCalendar(calendar: Calendar){
        _currentCarlendar.value = calendar
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

    fun addDate(){
        val current = currentCarlendar.value
        current?.let {
            val day = it.get(Calendar.DAY_OF_MONTH)
            it.set(Calendar.DATE, day+1)
            _currentCarlendar.value = current
        }
    }

    fun cutDate(){
        val current = currentCarlendar.value
        current?.let {
            val day = it.get(Calendar.DAY_OF_MONTH)
            it.set(Calendar.DATE, day-1)
            _currentCarlendar.value = it
        }
    }

}
