package com.sam.gogozoo.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class FireRoute(
    var id: String = "",
    var name: String = "",
    var owners: List<String> = listOf(),
    var open: Boolean = false,
    var list: List<FireNavInfo> = listOf()
):Parcelable