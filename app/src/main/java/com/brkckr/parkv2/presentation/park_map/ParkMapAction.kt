package com.brkckr.parkv2.presentation.park_map

import com.brkckr.parkv2.domain.model.Park
import com.brkckr.parkv2.domain.model.ParkFilter
import com.google.android.gms.maps.model.LatLng

sealed interface ParkMapAction {
    data class OnSearchQueryChange(val query: String) : ParkMapAction
    data class OnFilterChange(val filter: ParkFilter) : ParkMapAction
    data class OnParkSelected(val park: Park?) : ParkMapAction
    data class OnUserLocationChanged(val location: LatLng) : ParkMapAction
    data class ToggleFavorite(val park: Park) : ParkMapAction
    data object RetryLoadParks : ParkMapAction
}
