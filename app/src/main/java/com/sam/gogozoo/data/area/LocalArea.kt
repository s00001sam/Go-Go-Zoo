package com.sam.gogozoo.data.area

import android.os.Parcelable
import com.google.android.gms.maps.model.LatLng
import kotlinx.android.parcel.Parcelize
import kotlinx.android.parcel.RawValue

@Parcelize
data class LocalArea(
    var idNum: Int = -1,
    var name: String = "",
    var geo: @RawValue List<LatLng> = listOf(),
    var category: String = "",
    var infomation: String = "",
    var picture: String = "",
    var url: String = ""
): Parcelable