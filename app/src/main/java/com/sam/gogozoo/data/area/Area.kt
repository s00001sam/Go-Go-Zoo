package com.sam.gogozoo.data.area

import com.squareup.moshi.Json

data class Area(
    @Json(name = "_id") val id: Int = -1,
    @Json(name = "E_Name") val name: String = "",
    @Json(name = "E_Geo") val geo: String = "",
    @Json(name = "E_Category") val category: String = "",
    @Json(name = "E_Info") val infomation: String = "",
    @Json(name = "E_Pic_URL") val picture: String = "",
    @Json(name = "E_URL") val url: String = ""
)