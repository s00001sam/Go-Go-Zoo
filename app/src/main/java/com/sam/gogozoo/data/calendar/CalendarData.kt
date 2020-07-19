package com.sam.gogozoo.data.calendar

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize


@Parcelize
data class CalendarData (
    val error: String? = null,
    val result: CalendarSubData
): Parcelable