package com.sam.gogozoo.data.calendar

import android.os.Parcelable
import com.google.android.gms.maps.model.LatLng
import com.squareup.moshi.Json
import kotlinx.android.parcel.Parcelize

@Parcelize
data class LocalCalendar(
    @Json(name = "_id") var id: Int = -1,
    @Json(name = "D_Title") var title: String = "",
    @Json(name = "D_StartDate") var start: Long = -1,
    @Json(name = "D_EndDate") var end: Long = -1,
    @Json(name = "D_Geo") var geo: List<LatLng> = listOf(),
    @Json(name = "D_Location") var location: String = "",
    @Json(name = "D_Brief") var brief: String = "",
    @Json(name = "D_Time") var time: String = "",
    @Json(name = "\uFEFFD_Category") var category: String = "",
    @Json(name = "D_site_URL") var site: String = ""
):Parcelable