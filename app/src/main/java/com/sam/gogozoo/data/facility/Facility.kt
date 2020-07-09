package com.sam.gogozoo.data.facility

import android.os.Parcelable
import com.squareup.moshi.Json
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Facility(
    @Json(name = "_id") val id: Int = -1,
    @Json(name = "S_Title") val name: String = "",
    @Json(name = "S_Geo") val geo: String = "",
    @Json(name = "S_Location") val location: String = "",
    @Json(name = "S_Category") val category: String = "",
    @Json(name = "S_Item") val item: String = ""
): Parcelable