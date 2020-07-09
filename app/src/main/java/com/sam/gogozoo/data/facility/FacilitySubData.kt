package com.sam.gogozoo.data.facility

import android.os.Parcelable
import com.sam.gogozoo.data.animal.Animal
import kotlinx.android.parcel.Parcelize
import kotlinx.android.parcel.RawValue


@Parcelize
data class FacilitySubData (
    val limit: Int = 1000,
    val offset: Int = 0,
    val count: Int = 246,
    val sort: String = "",
    val results: @RawValue List<Facility>
): Parcelable