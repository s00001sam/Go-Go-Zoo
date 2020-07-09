package com.sam.gogozoo.data.animal

import android.os.Parcelable
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.firestore.GeoPoint
import kotlinx.android.parcel.Parcelize
import kotlinx.android.parcel.RawValue


@Parcelize
data class LocalAnimal(
    var nameCh: String = "",
    var nameEn: String = "",
    var code: String = "",
    var conservation: String = "",
    var geos: @RawValue List<LatLng> = listOf(),
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

