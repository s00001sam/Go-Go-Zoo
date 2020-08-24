package com.sam.gogozoo.data.calendar

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import kotlinx.android.parcel.RawValue


@Parcelize
data class CalendarSubData (
    val limit: Int = 1000,
    val offset: Int = 0,
    val count: Int = 17,
    val sort: String = "",
    val results: @RawValue List<Calendar>
): Parcelable