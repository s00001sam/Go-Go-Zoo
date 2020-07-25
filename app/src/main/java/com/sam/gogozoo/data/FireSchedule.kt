package com.sam.gogozoo.data

import android.os.Parcelable
import com.sam.gogozoo.data.FireNavInfo
import com.sam.gogozoo.data.NavInfo
import kotlinx.android.parcel.Parcelize

@Parcelize
data class FireSchedule(
    var id: String = "",
    var name: String = "",
    var owners: List<String> = listOf(),
    var open: Boolean = false,
    var list: List<FireNavInfo> = listOf()
):Parcelable