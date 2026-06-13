package com.brkckr.parkv2.domain.model

data class Park(
    val parkID: Int,
    val parkName: String,
    val lat: Double,
    val lng: Double,
    val capacity: Int,
    val emptyCapacity: Int,
    val isOpen: Boolean,
    val parkType: String,
    val district: String,
    val workHours: String,
    val freeTime: String,
    val fee: String,
    val monthlyFee: String,
    val status: ParkStatus,
    val isFavorite: Boolean = false,
    val distanceMeters: Double? = null
)
