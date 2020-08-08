package com.sam.gogozoo.stepcount.item

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.sam.gogozoo.data.StepInfo
import com.sam.gogozoo.data.source.ZooRepository
import com.sam.gogozoo.network.LoadApiStatus

class RecordViewModel(private val repository: ZooRepository) : ViewModel() {

    private val _status = MutableLiveData<LoadApiStatus>()
    val status: LiveData<LoadApiStatus>
        get() = _status

    // status for the loading icon of swl
    private val _refreshStatus = MutableLiveData<Boolean>()

    val refreshStatus: LiveData<Boolean>
        get() = _refreshStatus

    private val _hasData = MutableLiveData<Boolean>()

    val hasData: LiveData<Boolean>
        get() = _hasData

    var liveSteps = MutableLiveData<List<StepInfo>>()

    init {
        getLiveRoutesResult()
    }

    fun setHasData(boolean: Boolean){
        _hasData.value = boolean
    }

    fun getLiveRoutesResult() {
        liveSteps = repository.getLiveSteps()
        _status.value = LoadApiStatus.DONE
        _refreshStatus.value = false
    }

}
