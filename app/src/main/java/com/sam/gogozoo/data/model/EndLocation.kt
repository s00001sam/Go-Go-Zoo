
package com.sam.gogozoo.data.model

import com.google.gson.annotations.SerializedName

data class EndLocation(
        @SerializedName("lat")
        var lat: Double?,
        @SerializedName("lng")
        var lng: Double?
)