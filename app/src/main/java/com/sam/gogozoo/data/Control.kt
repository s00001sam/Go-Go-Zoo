package com.sam.gogozoo.data

import androidx.lifecycle.MutableLiveData
import com.google.android.gms.maps.model.LatLng

object Control {

    var hasPolyline = false

    var getPhoto = false

    var addNewAnimal = false

    val step = MutableLiveData<Int>()

    val timeCount = MutableLiveData<Long>()

    var showStop = true

    var hasNotification = false

}