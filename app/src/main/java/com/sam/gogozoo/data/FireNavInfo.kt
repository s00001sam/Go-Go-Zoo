package com.sam.gogozoo.data

import android.os.Parcelable
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.firestore.GeoPoint
import kotlinx.android.parcel.Parcelize
import kotlinx.android.parcel.RawValue

@Parcelize
data class FireNavInfo(
    var title: String = "",
    var geoPoint: @RawValue GeoPoint =  GeoPoint(24.998361, 121.581033),
    var type: String = "",
    var image: Int = 0,
    var imageUrl: String = "",
    var meter: Int =  100
):Parcelable