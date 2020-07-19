package com.sam.gogozoo.data.calendar

import android.os.Parcelable
import com.google.android.gms.maps.model.LatLng
import com.squareup.moshi.Json
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Calendar(
    @Json(name = "_id") val id: Int = -1,
    @Json(name = "D_Title") val title: String = "",
    @Json(name = "D_StartDate") val start: String = "",
    @Json(name = "D_EndDate") val end: String = "",
    @Json(name = "D_Geo") val geo: String= "",
    @Json(name = "D_Location") val location: String = "",
    @Json(name = "D_Brief") val brief: String = "",
    @Json(name = "D_Time") val time: String = "",
    @Json(name = "\uFEFFD_Category") val category: String = "",
    @Json(name = "D_site_URL") val site: String = ""
):Parcelable