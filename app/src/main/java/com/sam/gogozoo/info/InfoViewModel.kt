package com.sam.gogozoo.info

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.sam.gogozoo.data.NavInfo
import com.sam.gogozoo.data.source.ZooRepository

class InfoViewModel(private val repository: ZooRepository, private val navInfo: NavInfo?): ViewModel() {

    private val _leave = MutableLiveData<Boolean>()

    val leave: LiveData<Boolean>
        get() = _leave

    fun leave() {
        _leave.value = true
    }

    fun onLeaveCompleted() {
        _leave.value = null
    }

    private val _info = MutableLiveData<NavInfo>()

    val info: MutableLiveData<NavInfo>
        get() = _info

    // Initialize the _navInfo MutableLiveData
    init {
        _info.value = navInfo
    }

}