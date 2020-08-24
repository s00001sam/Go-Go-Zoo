package com.sam.gogozoo.data.animal

import android.os.Parcelable
import com.squareup.moshi.Json
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Animal(
    @Json(name = "\uFEFFA_Name_Ch") val nameCh: String = "",
    @Json(name = "A_Name_En") val nameEn: String = "",
    @Json(name = "A_Name_Latin") val nameLat: String = "",
    @Json(name = "A_Code") val code: String = "",
    @Json(name = "A_Conservation") val conservation: String = "",
    @Json(name = "A_Geo") val geo: String = "",
    @Json(name = "A_Location") val location: String = "",
    @Json(name = "A_Phylum") val phylum: String = "",
    @Json(name = "A_Class") val clas: String = "",
    @Json(name = "A_Order") val order: String = "",
    @Json(name = "A_Family") val family: String = "",
    @Json(name = "A_Diet") val diet: String = "",
    @Json(name = "A_Distribution") val distribution: String = "",
    @Json(name = "A_Interpretation") val interpretation: String = "",
    @Json(name = "A_Pic01_URL") val picture1: String = "",
    @Json(name = "A_pdf02_URL") val picture2: String = "",
    @Json(name = "A_Pic03_URL") val picture3: String = "",
    @Json(name = "A_Pic04_URL") val picture4: String = "",
    @Json(name = "A_Vedio_URL") val video: String = ""
): Parcelable