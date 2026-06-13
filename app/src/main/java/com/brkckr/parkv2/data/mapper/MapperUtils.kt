package com.brkckr.parkv2.data.mapper

import com.brkckr.parkv2.domain.model.ParkStatus

fun calculateParkStatus(isOpen: Boolean, emptyCapacity: Int): ParkStatus {
    return when {
        !isOpen -> ParkStatus.CLOSED
        emptyCapacity <= 0 -> ParkStatus.FULL
        else -> ParkStatus.OPEN
    }
}
