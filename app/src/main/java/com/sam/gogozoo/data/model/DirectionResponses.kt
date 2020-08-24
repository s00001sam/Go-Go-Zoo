package com.sam.gogozoo.data.model

import com.google.gson.annotations.SerializedName

data class DirectionResponses(
        @SerializedName("geocoded_waypoints")
        var geocodedWaypoints: List<GeocodedWaypoint?>?,
        @SerializedName("routes")
        var routes: List<Route?>? = null,
        @SerializedName("status")
        var status: String?
)