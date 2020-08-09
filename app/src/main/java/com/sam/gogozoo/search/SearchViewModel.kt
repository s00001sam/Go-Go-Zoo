package com.sam.gogozoo.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.sam.gogozoo.data.MockData
import com.sam.gogozoo.data.NavInfo
import com.sam.gogozoo.data.UserManager
import com.sam.gogozoo.data.source.ZooRepository
import com.sam.gogozoo.util.Util.getDinstance
import com.sam.gogozoo.util.Util.sortByMeter

class SearchViewModel(private val repository: ZooRepository): ViewModel() {

    private val _leave = MutableLiveData<Boolean>()

    val leave: LiveData<Boolean>
        get() = _leave

    private val _selectInfo = MutableLiveData<NavInfo>()

    val selectInfo: LiveData<NavInfo>
        get() = _selectInfo

    private val _listNav = MutableLiveData<List<NavInfo>>()

    val listNav: LiveData<List<NavInfo>>
        get() = _listNav

    //test for search system
    val infos = listOf(
        NavInfo(title = "國王企鵝", meter = 200),
        NavInfo(title = "黑角企鵝", meter = 150),
        NavInfo(title = "企鵝館", meter = 201),
        NavInfo(title = "駱駝", meter = 500),
        NavInfo(title = "雙峰駱駝", meter = 1000),
        NavInfo(title = "單峰駱駝", meter = 10),
        NavInfo(title = "台灣黑熊", meter = 330),
        NavInfo(title = "台灣館", meter = 800)
    )
    val sortInfo = sortByMeter(infos)


    init {
        getMeter()
    }

    fun setListNav(list: List<NavInfo>){
        _listNav.value = list
    }

    fun setSelectInfo(navInfo: NavInfo?){
        _selectInfo.value = navInfo
    }

    fun leave() {
        _leave.value = true
    }

    fun onLeaveCompleted() {
        _leave.value = null
    }

    fun nothing() {}

    fun getMeter(){
        MockData.allMarkers.forEach {
            it.meter = it.latLng.getDinstance(UserManager.user.geo)
        }
    }



}