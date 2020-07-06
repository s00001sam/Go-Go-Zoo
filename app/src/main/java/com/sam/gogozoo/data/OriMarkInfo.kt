package com.sam.gogozoo.data

import android.graphics.drawable.BitmapDrawable
import com.google.android.gms.maps.model.LatLng

data class OriMarkInfo(
    val latLng: LatLng,
    val title: String,
    val drawable: Int
)