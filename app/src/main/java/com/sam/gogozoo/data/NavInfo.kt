package com.sam.gogozoo.data

import android.os.Parcelable
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import kotlinx.android.parcel.Parcelize
import kotlinx.android.parcel.RawValue

@Parcelize
data class NavInfo(
    var title: String = "",
    var latLng: LatLng = LatLng(24.998361, 121.581033),
    var type: String = "",
    var image: Int = 0,
    var imageUrl: String = "",
    var meter: Int =  100,
    var marker: @RawValue Marker? = null
):Parcelable