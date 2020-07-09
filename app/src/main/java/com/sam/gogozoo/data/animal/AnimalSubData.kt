package com.sam.gogozoo.data.animal

import android.os.Parcelable
import com.sam.gogozoo.data.animal.Animal
import kotlinx.android.parcel.Parcelize


@Parcelize
data class AnimalSubData (
    val limit: Int = 1000,
    val offset: Int = 0,
    val count: Int = 364,
    val sort: String = "",
    val results: List<Animal>
): Parcelable