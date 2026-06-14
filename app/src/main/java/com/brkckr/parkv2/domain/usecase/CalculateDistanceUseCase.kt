package com.brkckr.parkv2.domain.usecase

import android.location.Location
import javax.inject.Inject

// calculates the distance between two geographical points
class CalculateDistanceUseCase @Inject constructor() {
    operator fun invoke(lat1: Double, lng1: Double, lat2: Double, lng2: Double): Double {
        val results = FloatArray(1)
        Location.distanceBetween(lat1, lng1, lat2, lng2, results)
        return results[0].toDouble()
    }
}
