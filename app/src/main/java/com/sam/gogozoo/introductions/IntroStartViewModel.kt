package com.sam.gogozoo.introductions

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.sam.gogozoo.data.source.ZooRepository

class IntroStartViewModel(private val repository: ZooRepository) : ViewModel() {

    private val _leave = MutableLiveData<Int>()

    val leave: LiveData<Int>
        get() = _leave

    init {
        setLeave(0)
    }

    fun setLeave(number: Int){
        _leave.value = number
    }

    fun leave() {
        _leave.value = _leave.value?.plus(1)
    }

    fun onLeaveCompleted() {
        _leave.value = null
    }

    fun nothing() {}


}
