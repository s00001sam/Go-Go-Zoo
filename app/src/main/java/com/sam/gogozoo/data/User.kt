package com.sam.gogozoo.data

import android.os.Parcelable
import com.google.android.gms.maps.model.LatLng
import kotlinx.android.parcel.Parcelize

@Parcelize
data class User(
    var key: String? = "",
    var createdTime: Long? = -1,
    var email: String? = "",
    var geo: LatLng? = LatLng(24.0, 121.0)

):Parcelable