package com.sam.gogozoo.data

import android.os.Parcelable
import com.sam.gogozoo.data.NavInfo
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Schedule(
    val name: String = "",
    val list: List<NavInfo> = listOf()
):Parcelable