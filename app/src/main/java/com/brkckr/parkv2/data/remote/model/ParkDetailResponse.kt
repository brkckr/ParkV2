package com.brkckr.parkv2.data.remote.model

import com.google.gson.annotations.SerializedName

data class ParkDetailResponse(
    @SerializedName("parkID", alternate = ["ParkID", "ID"]) val parkID: Int = 0,
    @SerializedName("parkName", alternate = ["ParkName"]) val parkName: String = "",
    @SerializedName("lat", alternate = ["Latitude", "Lat"]) val lat: Double = 0.0,
    @SerializedName("lng", alternate = ["Longitude", "Lng"]) val lng: Double = 0.0,
    @SerializedName("capacity", alternate = ["Capacity"]) val capacity: Int = 0,
    @SerializedName("emptyCapacity", alternate = ["EmptyCapacity"]) val emptyCapacity: Int = 0,
    @SerializedName("isOpen", alternate = ["IsOpen"]) val isOpen: Int = 0,
    @SerializedName("parkType", alternate = ["ParkType"]) val parkType: String = "",
    @SerializedName("district", alternate = ["District"]) val district: String = "",
    @SerializedName("workHours", alternate = ["WorkHours"]) val workHours: String = "",
    @SerializedName("freeTime", alternate = ["FreeTime"]) val freeTime: String = "",
    @SerializedName("fee", alternate = ["Fee"]) val fee: String = "",
    @SerializedName("monthlyFee", alternate = ["MonthlyFee"]) val monthlyFee: String = "",
    @SerializedName("areaPolygon", alternate = ["AreaPolygon"]) val areaPolygon: String = "",
    @SerializedName("address", alternate = ["Address"]) val address: String = "",
    @SerializedName("phone", alternate = ["Phone"]) val phone: String = "",
    @SerializedName("tariff", alternate = ["Tariff"]) val tariff: String = ""
)
