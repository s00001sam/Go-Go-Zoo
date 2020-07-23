package com.sam.gogozoo.data

import android.graphics.drawable.BitmapDrawable
import com.google.android.gms.maps.model.LatLng
import com.sam.gogozoo.R

data class OriMarkInfo(
    val latLng: LatLng = LatLng(24.0, 122.0),
    val title: String,
    val drawable: Int
)