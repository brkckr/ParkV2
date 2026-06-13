package com.brkckr.parkv2.data.remote.model

import com.google.gson.annotations.SerializedName

data class ParkResponse(
    @SerializedName("parkID") val parkID: Int = 0,
    @SerializedName("parkName") val parkName: String = "",
    @SerializedName("lat") val lat: Double = 0.0,
    @SerializedName("lng") val lng: Double = 0.0,
    @SerializedName("capacity") val capacity: Int = 0,
    @SerializedName("emptyCapacity") val emptyCapacity: Int = 0,
    @SerializedName("isOpen") val isOpen: Int = 0,
    @SerializedName("parkType") val parkType: String = "",
    @SerializedName("district") val district: String = "",
    @SerializedName("workHours") val workHours: String = "",
    @SerializedName("freeTime") val freeTime: String = "",
    @SerializedName("fee") val fee: String = "",
    @SerializedName("monthlyFee") val monthlyFee: String = ""
)
