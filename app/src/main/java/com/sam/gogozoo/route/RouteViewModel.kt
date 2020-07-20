package com.sam.gogozoo.route

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.sam.gogozoo.data.Schedule
import com.sam.gogozoo.data.source.ZooRepository

class RouteViewModel(private val repository: ZooRepository, private val route: Schedule?) : ViewModel() {

    private val _leave = MutableLiveData<Boolean>()

    val leave: LiveData<Boolean>
        get() = _leave

    val selectRoute = MutableLiveData<Schedule>()


    init {

    }

    fun leave() {
        _leave.value = true
    }

    fun onLeaveCompleted() {
        _leave.value = null
    }
    fun nothing() {}

}
