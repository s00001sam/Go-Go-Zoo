package com.sam.gogozoo.listpage

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.sam.gogozoo.ZooApplication
import com.sam.gogozoo.data.MockData
import com.sam.gogozoo.data.UserManager
import com.sam.gogozoo.data.area.LocalArea
import com.sam.gogozoo.data.source.ZooRepository
import com.sam.gogozoo.util.Util
import com.sam.gogozoo.util.Util.getDinstance

class ListViewModel(private val repository: ZooRepository) : ViewModel() {

    val mockAreas = MutableLiveData<List<LocalArea>>()

    private val _navigationArea = MutableLiveData<LocalArea>()
    val navigationArea: LiveData<LocalArea>
        get() = _navigationArea

    init {
        getDistance()
    }

    fun displayArea(localArea: LocalArea){
        _navigationArea.value = localArea
    }
    fun displayAreaComplete(){
        _navigationArea.value = null
    }
    fun getDistance(){
        MockData.localAreas.forEach {
            it.meter = it.geo[0].getDinstance(UserManager.user.geo)
        }
    }

}
