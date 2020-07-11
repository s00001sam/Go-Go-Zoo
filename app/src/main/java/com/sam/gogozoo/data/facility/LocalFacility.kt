package com.sam.gogozoo.data.facility

import android.os.Parcelable
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.firestore.GeoPoint
import com.squareup.moshi.Json
import kotlinx.android.parcel.Parcelize
import kotlinx.android.parcel.RawValue

@Parcelize
data class LocalFacility(
    var idNum: Int = -1,
    var name: String = "",
    var geo: @RawValue List<LatLng> = listOf(),
    var location: String = "",
    var category: String = "",
    var item: String = "",
    var meter: Int = 100
): Parcelable