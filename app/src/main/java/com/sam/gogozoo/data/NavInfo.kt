package com.sam.gogozoo.data

import android.os.Parcelable
import com.google.android.gms.maps.model.LatLng
import kotlinx.android.parcel.Parcelize

@Parcelize
data class NavInfo(
    val title: String = "",
    val latLng: LatLng = LatLng(24.998361, 121.581033),
    val type: String = "",
    val image: Int = 0,
    val meter: Int =  100
):Parcelable