package com.brkckr.parkv2.data.mapper

import com.brkckr.parkv2.data.remote.model.ParkDetailResponse
import com.brkckr.parkv2.domain.model.Coordinate
import com.brkckr.parkv2.domain.model.ParkDetail

fun ParkDetailResponse.toDomain(
    isFavorite: Boolean = false,
    fallbackIsOpen: Boolean = false
): ParkDetail {
    // map api status with local fallback
    val openStatus = if (isOpen == 0) fallbackIsOpen else (isOpen == 1)

    val cleanedFee = fee.replace(",", ".")
    val cleanedMonthlyFee = monthlyFee.replace(",", ".")
    val cleanedFreeTime = freeTime.replace(",", ".")

    return ParkDetail(
        parkID = parkID,
        parkName = parkName,
        lat = lat,
        lng = lng,
        capacity = capacity,
        emptyCapacity = emptyCapacity,
        isOpen = openStatus,
        parkType = parkType,
        district = district,
        workHours = workHours,
        freeTime = if (cleanedFreeTime.toDoubleOrNull() != 0.0) freeTime else "",
        fee = if (cleanedFee.toDoubleOrNull() != 0.0) fee else "",
        monthlyFee = if (cleanedMonthlyFee.toDoubleOrNull() != 0.0) monthlyFee else "",
        areaPoints = parsePolygonPoints(areaPolygon),
        address = address,
        phone = phone,
        status = calculateParkStatus(openStatus, emptyCapacity),
        isFavorite = isFavorite,
        tariffs = parseTariffs(tariff, cleanedFee, cleanedMonthlyFee)
    )
}

private fun parseTariffs(tariff: String, fee: String, monthlyFee: String): List<String> {
    val tariffList = mutableListOf<String>()

    if (fee.isNotBlank() && fee.toDoubleOrNull() != 0.0) {
        tariffList.add("hourly_fee\n$fee")
    }

    if (monthlyFee.isNotBlank() && monthlyFee.toDoubleOrNull() != 0.0) {
        tariffList.add("monthly_fee\n$monthlyFee")
    }

    if (tariff.isNotBlank()) {
        val otherTariffs = tariff.split(";")
            .filter { it.isNotBlank() }
            .map { it.trim() }
        tariffList.addAll(otherTariffs)
    }

    return tariffList
}

private fun parsePolygonPoints(areaPolygon: String): List<Coordinate> {
    if (areaPolygon.isBlank()) return emptyList()
    // parse map area coordinates
    return try {
        val cleaned = areaPolygon.replace(Regex("[a-zA-Z()]"), " ").trim()
        val numbers = cleaned.split(Regex("[,\\s]+")).filter { it.isNotBlank() }

        val points = mutableListOf<Coordinate>()
        for (i in 0 until numbers.size - 1 step 2) {
            val val1 = numbers[i].toDoubleOrNull()
            val val2 = numbers[i + 1].toDoubleOrNull()

            if (val1 != null && val2 != null) {
                val lat = if (val1 > val2) val1 else val2
                val lng = if (val1 > val2) val2 else val1
                points.add(Coordinate(lat, lng))
            }
        }
        points
    } catch (_: Exception) {
        emptyList()
    }
}