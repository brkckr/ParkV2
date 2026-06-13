package com.brkckr.parkv2.data.mapper

import com.brkckr.parkv2.data.local.entity.ParkEntity
import com.brkckr.parkv2.data.remote.model.ParkResponse
import com.brkckr.parkv2.domain.model.Park

fun ParkResponse.toEntity(): ParkEntity {
    return ParkEntity(
        parkID = parkID,
        parkName = parkName,
        lat = lat,
        lng = lng,
        capacity = capacity,
        emptyCapacity = emptyCapacity,
        isOpen = isOpen,
        parkType = parkType,
        district = district,
        workHours = workHours,
        freeTime = freeTime,
        fee = fee,
        monthlyFee = monthlyFee
    )
}

fun ParkEntity.toDomain(): Park {
    return Park(
        parkID = parkID,
        parkName = parkName,
        lat = lat,
        lng = lng,
        capacity = capacity,
        emptyCapacity = emptyCapacity,
        isOpen = isOpen == 1,
        parkType = parkType,
        district = district,
        workHours = workHours,
        freeTime = freeTime,
        fee = fee,
        monthlyFee = monthlyFee,
        status = calculateParkStatus(isOpen == 1, emptyCapacity),
        isFavorite = isFavorite
    )
}
