package com.brkckr.parkv2.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "parks")
data class ParkEntity(
    @PrimaryKey val parkID: Int,
    val parkName: String,
    val lat: Double,
    val lng: Double,
    val capacity: Int,
    val emptyCapacity: Int,
    val isOpen: Int,
    val parkType: String,
    val district: String,
    val workHours: String,
    val freeTime: String,
    val fee: String,
    val monthlyFee: String,
    val isFavorite: Boolean = false
)
