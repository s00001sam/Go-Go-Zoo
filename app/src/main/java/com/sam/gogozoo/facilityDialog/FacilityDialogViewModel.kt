package com.sam.gogozoo.facilityDialog

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.sam.gogozoo.data.FacilityItem
import com.sam.gogozoo.data.MockData
import com.sam.gogozoo.data.source.ZooRepository
import com.sam.gogozoo.util.Logger

class FacilityDialogViewModel(private val repository: ZooRepository, private val facilityItem: FacilityItem?) : ViewModel() {

    private val _leave = MutableLiveData<Boolean>()

    val leave: LiveData<Boolean>
        get() = _leave

    private val _listFac = MutableLiveData<FacilityItem>()

    val listFac: LiveData<FacilityItem>
        get() = _listFac

    private val _selectItem = MutableLiveData<String>()

    val selectItem: LiveData<String>
        get() = _selectItem

    init {
        forImage()
    }

    fun setSelectItem(string: String?){
        _selectItem.value = string
    }

    fun setListFac(item: FacilityItem?){
        _listFac.value = item
    }

    fun leave() {
        _leave.value = true
    }

    fun onLeaveCompleted() {
        _leave.value = null
    }

    fun nothing() {}

    fun forImage(){
        val list = mutableListOf<String>()
        MockData.localFacility.forEach {
            val name = it.name
            list.add(name)
        }
        MockData.listFotFacImage = list
        Logger.d("listFotFacImage=${MockData.listFotFacImage}")
    }

}
