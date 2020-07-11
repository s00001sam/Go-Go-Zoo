package com.sam.gogozoo.listpage

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.sam.gogozoo.data.MockData
import com.sam.gogozoo.data.area.LocalArea
import com.sam.gogozoo.data.source.ZooRepository

class ListViewModel(private val repository: ZooRepository) : ViewModel() {

    val mockAreas = MutableLiveData<List<LocalArea>>()

    private val _navigationArea = MutableLiveData<LocalArea>()
    val navigationArea: LiveData<LocalArea>
        get() = _navigationArea

    init {

    }

    fun displayArea(localArea: LocalArea){
        _navigationArea.value = localArea
    }
    fun displayAreaComplete(){
        _navigationArea.value = null
    }



}
