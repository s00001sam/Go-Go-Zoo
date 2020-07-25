package com.sam.gogozoo.data

import android.os.Parcelable
import com.sam.gogozoo.data.NavInfo
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Route(
    var id: String = "",
    var name: String = "",
    var owners: List<String> = listOf(),
    var open: Boolean = false,
    var list: List<NavInfo> = listOf()
):Parcelable