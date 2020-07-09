package com.sam.gogozoo.data.area

import android.os.Parcelable
import com.google.firebase.firestore.GeoPoint
import kotlinx.android.parcel.Parcelize
import kotlinx.android.parcel.RawValue

@Parcelize
data class FireArea(
    var id:String = "",
    var createTime: Long = -1,
    var idNum: Int = -1,
    var name: String = "",
    var geo: @RawValue List<GeoPoint> = listOf(),
    var category: String = "",
    var infomation: String = "",
    var picture: String = "",
    var url: String = ""
):Parcelable