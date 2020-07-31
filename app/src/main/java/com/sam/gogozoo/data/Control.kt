package com.sam.gogozoo.data

import androidx.lifecycle.MutableLiveData

object Control {

    var hasPolyline = false

    var getPhoto = false

    var addNewAnimal = false

    val step = MutableLiveData<Int>()

    val timeCount = MutableLiveData<Long>()

    var showStop = true

    var hasNotification = false

}