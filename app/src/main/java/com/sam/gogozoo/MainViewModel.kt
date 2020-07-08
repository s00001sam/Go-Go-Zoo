package com.sam.gogozoo

import android.location.Location
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.firestore.GeoPoint
import com.sam.gogozoo.data.source.ZooRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job

/**
 * Created by Wayne Chen on 2020-01-15.
 *
 * The [ViewModel] that is attached to the [MainActivity].
 */
class MainViewModel(private val repository: ZooRepository) : ViewModel() {

    private val _refresh = MutableLiveData<Boolean>()

    val refresh: LiveData<Boolean>
        get() = _refresh

    // Create a Coroutine scope using a job to be able to cancel when needed
    private var viewModelJob = Job()

    var a = listOf<LatLng>()

    // the Coroutine runs using the Main (UI) dispatcher
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    /**
     * When the [ViewModel] is finished, we cancel our coroutine [viewModelJob], which tells the
     * Retrofit service to stop.
     */
    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

    init {

    }

    fun toLatlng (mutliPoint: String): List<LatLng> {
        val listLatLng = mutableListOf<LatLng>()
        val listString = mutliPoint.split("("," ",")")
        var  x: Double = 0.0
        var  y: Double = 0.0
        listString.forEach {
                if (it.startsWith("1")) {
                    x = it.toDouble()
                }
                if (it.startsWith("2")){
                    y = it.toDouble()
                    val latLng = LatLng(y,x)
                    listLatLng.add(latLng)
                }
            }

        return listLatLng.toList()
    }

    fun refresh() {
        if (!ZooApplication.INSTANCE.isLiveDataDesign()) {
            _refresh.value = true
        }
    }

    fun onRefreshed() {
        if (!ZooApplication.INSTANCE.isLiveDataDesign()) {
            _refresh.value = null
        }
    }
}
