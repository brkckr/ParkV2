package com.brkckr.parkv2.presentation.park_map.components.map

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.brkckr.parkv2.domain.model.Park

@Composable
fun ParkMap(
    cameraPositionState: CameraPositionState,
    paddingValues: PaddingValues,
    mapStyleOptions: MapStyleOptions?,
    filteredParks: List<Park>,
    userLocation: LatLng?,
    onParkClick: (Park) -> Unit,
    onMapClick: () -> Unit,
    onMapLoaded: () -> Unit = {}
) {
    val mapProperties = remember(mapStyleOptions) { MapProperties(mapStyleOptions = mapStyleOptions, isMyLocationEnabled = false) }
    val uiSettings = remember { MapUiSettings(zoomControlsEnabled = false, myLocationButtonEnabled = false) }

    GoogleMap(
        modifier = Modifier.fillMaxSize(),
        cameraPositionState = cameraPositionState,
        contentPadding = paddingValues,
        properties = mapProperties,
        uiSettings = uiSettings,
        onMapClick = { onMapClick() },
        onMapLoaded = onMapLoaded,
        onPOIClick = {}
    ) {
        ParkMarkers(filteredParks, onParkClick = onParkClick)
        userLocation?.let { UserLocationMarker(location = it) }
    }
}
