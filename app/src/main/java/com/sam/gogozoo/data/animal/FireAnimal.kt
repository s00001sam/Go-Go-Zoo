package com.sam.gogozoo.data.animal

import android.os.Parcelable
import com.google.firebase.firestore.GeoPoint
import kotlinx.android.parcel.Parcelize
import kotlinx.android.parcel.RawValue

@Parcelize
data class FireAnimal(
    var id: String = "",
    var createTime: Long = -1,
    var nameCh: String = "",
    var nameEn: String = "",
    var code: String = "",
    var conservation: String = "",
    var geos: @RawValue List<GeoPoint> = listOf(),
    var location: String = "",
    var phylum: String = "",
    var clas: String = "",
    var order: String = "",
    var family: String = "",
    var diet: String = "",
    var distribution: String = "",
    var interpretation: String = "",
    var pictures: List<String> = listOf(),
    var video: String = ""
) : Parcelable