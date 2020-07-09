package com.sam.gogozoo.data.animal

import android.os.Parcelable
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.firestore.GeoPoint
import kotlinx.android.parcel.Parcelize
import kotlinx.android.parcel.RawValue


@Parcelize
data class LocalAnimal(
    val nameCh: String = "",
    val nameEn: String = "",
    val code: String = "",
    val conservation: String = "",
    val geo: List<LatLng>,
    val location: String = "",
    val phylum: String = "",
    val clas: String = "",
    val order: String = "",
    val family: String = "",
    val diet: String = "",
    val distribution: String = "",
    val interpretation: String = "",
    val pictures: List<String>,
    val video: String = ""
) : Parcelable

