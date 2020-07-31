package com.sam.gogozoo.info

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.sam.gogozoo.R
import com.sam.gogozoo.ZooApplication
import com.sam.gogozoo.data.*
import com.sam.gogozoo.data.source.ZooRepository
import com.sam.gogozoo.network.LoadApiStatus
import com.sam.gogozoo.util.Logger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class InfoViewModel(private val repository: ZooRepository, private val navInfo: NavInfo?): ViewModel() {

    private val _leave = MutableLiveData<Boolean>()

    val leave: LiveData<Boolean>
        get() = _leave

    private val _status = MutableLiveData<LoadApiStatus>()

    val status: LiveData<LoadApiStatus>
        get() = _status

    private val _error = MutableLiveData<String>()

    val error: LiveData<String>
        get() = _error

    private val _info = MutableLiveData<NavInfo>()

    val info: MutableLiveData<NavInfo>
        get() = _info

    val listRoute = mutableListOf<String>()

    var context = MutableLiveData<Context>()

    var selectSchedule = MutableLiveData<Route>()

    var isFriend = MutableLiveData<Boolean>()

    // Create a Coroutine scope using a job to be able to cancel when needed
    private var viewModelJob = Job()
    // the Coroutine runs using the Main (UI) dispatcher
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)

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
    fun nothing() {}

    fun getScheduleName(){
        MockData.routes.forEach {
            listRoute.add(it.name)
        }
    }

    fun showRouteName(array: Array<String>){
        val arraySchedule = array
        val mBuilder = AlertDialog.Builder(context.value)
        mBuilder.setTitle(ZooApplication.INSTANCE.getString(R.string.add_to_route))
        mBuilder.setSingleChoiceItems(arraySchedule, -1) { dialog: DialogInterface?, i: Int ->
            dialog?.dismiss()
            val selectRoute = MockData.routes.filter { it.name == arraySchedule[i] }
            val isInRoute = selectRoute[0].list.filter { it.title == info.value?.title }
            if (isInRoute == listOf<NavInfo>()) {
                MockData.routes.forEach {
                    if (it.name == arraySchedule[i]) {
                        info.value?.let { info ->
                            val list = it.list.toMutableList()
                            list.add(NavInfo(title = info.title, latLng = info.latLng, image = info.image, imageUrl = info.imageUrl))
                            it.list = list
                        }
                    }
                }
                toast("${info.value?.title} 成功加入\n${arraySchedule[i]}")
                val changeRoute = MockData.routes.filter { it.name == arraySchedule[i] }
                publishRoute(changeRoute[0])
                Control.addNewAnimal = true
                selectSchedule.value = changeRoute[0]
            }else{
                toast("${info.value?.title} 已存在於\n${arraySchedule[i]}")
            }
            Logger.d("mockdataroute=${MockData.routes}")
        }
        mBuilder.create().show()
    }

    fun publishRoute(route: Route) {

        coroutineScope.launch {

            _status.value = LoadApiStatus.LOADING

            when (val result = repository.publishNewRoute(route)) {
                is Result.Success -> {
                    _error.value = null
                    _status.value = LoadApiStatus.DONE
                }
                is Result.Fail -> {
                    _error.value = result.error
                    _status.value = LoadApiStatus.ERROR
                }
                is Result.Error -> {
                    _error.value = result.exception.toString()
                    _status.value = LoadApiStatus.ERROR
                }
                else -> {
                    _error.value = ZooApplication.INSTANCE.getString(R.string.you_know_nothing)
                    _status.value = LoadApiStatus.ERROR
                }
            }
        }
    }

    fun toast(text: String) {
        val toast = Toast(context.value)
        val view = LayoutInflater.from(context.value).inflate(R.layout.toast, null)
        val textView = view.findViewById<TextView>(R.id.toastText)
        textView.text = text
        toast.view = view
        toast.duration = Toast.LENGTH_SHORT
        toast.setGravity(Gravity.TOP, -220, 100)
        toast.show()
    }

}