package com.sam.gogozoo.info

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.sam.gogozoo.ZooApplication
import com.sam.gogozoo.data.MockData
import com.sam.gogozoo.data.NavInfo
import com.sam.gogozoo.data.Schedule
import com.sam.gogozoo.data.source.ZooRepository
import com.sam.gogozoo.util.Logger

class InfoViewModel(private val repository: ZooRepository, private val navInfo: NavInfo?): ViewModel() {

    private val _leave = MutableLiveData<Boolean>()

    val leave: LiveData<Boolean>
        get() = _leave

    private val _info = MutableLiveData<NavInfo>()

    val info: MutableLiveData<NavInfo>
        get() = _info

    val listRoute = mutableListOf<String>()

    var context = MutableLiveData<Context>()

    var selectSchedule = MutableLiveData<Schedule>()

    // Initialize the _navInfo MutableLiveData
    init {
        _info.value = navInfo
        getScheduleName()
    }

    fun leave() {
        _leave.value = true
    }

    fun onLeaveCompleted() {
        _leave.value = null
    }

    fun getScheduleName(){
        MockData.schedules.forEach {
            listRoute.add(it.name)
        }
    }

    fun showRouteName(array: Array<String>){
        val arraySchedule = array
        val mBuilder = AlertDialog.Builder(context.value)
        mBuilder.setTitle("加入行程")
        mBuilder.setSingleChoiceItems(arraySchedule, -1) { dialog: DialogInterface?, i: Int ->
            dialog?.dismiss()
            val selectRoute = MockData.schedules.filter { it.name == arraySchedule[i] }
            val isInRoute = selectRoute[0].list.filter { it.title == info.value?.title }
            if (isInRoute == listOf<NavInfo>()) {
                MockData.schedules.forEach {
                    if (it.name == arraySchedule[i]) {
                        info.value?.let { info ->
                            val list = it.list.toMutableList()
                            list.add(NavInfo(title = info.title, latLng = info.latLng))
                            it.list = list
                        }
                    }
                }
                Toast.makeText(ZooApplication.appContext, "${info.value?.title} 成功加入 ${arraySchedule[i]}", Toast.LENGTH_SHORT).show()
                val changeRoute = MockData.schedules.filter { it.name == arraySchedule[i] }
                selectSchedule.value = changeRoute[0]
            }else{
                Toast.makeText(context.value, "${info.value?.title} 已存在於 ${arraySchedule[i]}", Toast.LENGTH_SHORT).show()
            }
            Logger.d("mockdataroute=${MockData.schedules}")
        }
        mBuilder.create().show()
    }
}