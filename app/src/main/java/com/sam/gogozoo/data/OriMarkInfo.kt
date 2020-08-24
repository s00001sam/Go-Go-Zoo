package com.sam.gogozoo.data

import com.google.android.gms.maps.model.LatLng

data class OriMarkInfo(
    val latLng: LatLng = LatLng(24.0, 122.0),
    val title: String,
    val drawable: Int
)