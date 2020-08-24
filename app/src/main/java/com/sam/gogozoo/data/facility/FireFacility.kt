package com.sam.gogozoo.data.facility

import android.os.Parcelable
import com.google.firebase.firestore.GeoPoint
import kotlinx.android.parcel.Parcelize
import kotlinx.android.parcel.RawValue

@Parcelize
data class FireFacility(
    var id:String = "",
    var createTime: Long = -1,
    var idNum: Int = -1,
    var name: String = "",
    var geo: @RawValue List<GeoPoint> = listOf(),
    var location: String = "",
    var category: String = "",
    var item: String = ""
): Parcelable