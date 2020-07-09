package com.sam.gogozoo.data.facility

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize


@Parcelize
data class FacilityData (
    val error: String? = null,
    val result: FacilitySubData
): Parcelable