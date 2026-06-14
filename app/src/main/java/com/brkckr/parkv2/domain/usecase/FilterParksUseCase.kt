package com.brkckr.parkv2.domain.usecase

import com.google.android.gms.maps.model.LatLng
import com.brkckr.parkv2.domain.model.Park
import com.brkckr.parkv2.domain.model.ParkFilter
import com.brkckr.parkv2.domain.model.ParkStatus
import java.util.Locale
import javax.inject.Inject

// filters and sorts parks based on search query, filter and location
class FilterParksUseCase @Inject constructor(
    private val calculateDistanceUseCase: CalculateDistanceUseCase
) {
    operator fun invoke(
        parks: List<Park>,
        query: String,
        filter: ParkFilter,
        favoriteIds: Set<Int>,
        userLocation: LatLng?
    ): List<Park> {
        val trLocale = Locale.forLanguageTag("tr")
        // filter and sort parks based on state
        var result = parks.map { park ->
            park.copy(
                isFavorite = park.parkID in favoriteIds,
                distanceMeters = userLocation?.let { loc ->
                    calculateDistanceUseCase(loc.latitude, loc.longitude, park.lat, park.lng)
                }
            )
        }

        if (query.isNotBlank()) {
            val lowerQuery = query.lowercase(trLocale)
            result = result.filter {
                it.parkName.lowercase(trLocale).contains(lowerQuery) ||
                        it.district.lowercase(trLocale).contains(lowerQuery)
            }
        }

        result = when (filter) {
            ParkFilter.AVAILABLE -> result.filter { it.status == ParkStatus.OPEN }
            ParkFilter.FAVORITES -> result.filter { it.isFavorite }
            ParkFilter.ALL -> result
        }

        if (userLocation != null) {
            result = result.sortedBy { it.distanceMeters ?: Double.MAX_VALUE }
        }

        return result
    }
}