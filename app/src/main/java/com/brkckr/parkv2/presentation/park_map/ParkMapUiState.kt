package com.brkckr.parkv2.presentation.park_map

import com.brkckr.parkv2.domain.model.Park
import com.brkckr.parkv2.domain.model.ParkFilter
import com.brkckr.parkv2.domain.util.UiText
import com.google.android.gms.maps.model.LatLng

data class ParkMapUiState(
    val filteredParks: List<Park> = emptyList(),
    val searchQuery: String = "",
    val activeFilter: ParkFilter = ParkFilter.ALL,
    val selectedPark: Park? = null,
    val userLocation: LatLng? = null,
    val isLoading: Boolean = false,
    val error: UiText? = null
)
