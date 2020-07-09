package com.sam.gogozoo.data.animal

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize


@Parcelize
data class AnimalData (
    val error: String? = null,
    val result: AnimalSubData
): Parcelable