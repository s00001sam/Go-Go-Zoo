package com.sam.gogozoo.data.area

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize


@Parcelize
data class AreaData (
    val error: String? = null,
    val result: AreaSubData
): Parcelable