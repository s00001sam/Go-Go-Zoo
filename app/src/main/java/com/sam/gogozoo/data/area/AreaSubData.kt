package com.sam.gogozoo.data.area

import android.os.Parcelable
import com.sam.gogozoo.data.animal.Animal
import kotlinx.android.parcel.Parcelize
import kotlinx.android.parcel.RawValue


@Parcelize
data class AreaSubData (
    val limit: Int = 1000,
    val offset: Int = 0,
    val count: Int = 17,
    val sort: String = "",
    val results: @RawValue List<Area>
): Parcelable