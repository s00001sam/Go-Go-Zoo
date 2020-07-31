package com.sam.gogozoo.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize


@Parcelize
data class StepInfo(
    var step: Int = 0,
    var kilometer: Double = 0.000,
    var kcal: Double = 0.00,
    var time: Long = 0,
    var id: String = "",
    var createdTime: Long = -1,
    var owner: String = ""
):Parcelable