package com.sam.gogozoo.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class FacilityItem(
    var category: String = "",
    var item: List<String> = listOf()
):Parcelable