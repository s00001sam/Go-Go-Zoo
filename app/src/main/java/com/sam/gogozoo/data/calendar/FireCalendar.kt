package com.sam.gogozoo.data.calendar

import android.os.Parcelable
import com.google.firebase.firestore.GeoPoint
import kotlinx.android.parcel.Parcelize
import kotlinx.android.parcel.RawValue

@Parcelize
data class FireCalendar(
    var idDocument:String = "",
    var createTime: Long = -1,
    var id: Int = -1,
    var title: String = "",
    var start: Long = -1,
    var end: Long = -1,
    var geo: @RawValue List<GeoPoint> = listOf(),
    var location: String = "",
    var brief: String = "",
    var time: String = "",
    var category: String = "",
    var site: String = ""
):Parcelable