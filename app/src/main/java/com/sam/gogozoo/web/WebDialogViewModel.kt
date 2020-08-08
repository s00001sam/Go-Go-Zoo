package com.sam.gogozoo.web

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.sam.gogozoo.data.source.ZooRepository

class WebDialogViewModel(private val repository: ZooRepository): ViewModel() {

    private val _leave = MutableLiveData<Boolean>()

    val leave: LiveData<Boolean>
        get() = _leave

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
