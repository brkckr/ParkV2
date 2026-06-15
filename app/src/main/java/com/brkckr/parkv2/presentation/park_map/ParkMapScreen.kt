package com.brkckr.parkv2.presentation.park_map

import android.Manifest
import android.annotation.SuppressLint
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.brkckr.parkv2.R
import com.brkckr.parkv2.presentation.common.UiEvent
import com.brkckr.parkv2.presentation.common.asString
import com.brkckr.parkv2.presentation.park_map.components.LocationPermissionDialog
import com.brkckr.parkv2.presentation.park_map.components.ParkPager
import com.brkckr.parkv2.presentation.park_map.components.TopBar
import com.brkckr.parkv2.presentation.park_map.components.filter.SearchAndFilterSection
import com.brkckr.parkv2.presentation.park_map.components.map.ParkMap
import com.brkckr.parkv2.ui.components.ParkErrorView
import com.brkckr.parkv2.ui.components.ParkLoadingView
import com.brkckr.parkv2.ui.theme.Animations
import com.brkckr.parkv2.ui.theme.Dimens
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.maps.android.compose.rememberCameraPositionState
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch

@Composable
fun ParkMapScreen(
    onNavigateToDetail: (Int) -> Unit,
    viewModel: ParkMapViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val uiState by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.uiEvent.collect { event ->
            when (event) {
                is UiEvent.ShowSnackbar -> snackbarHostState.showSnackbar(
                    event.message.asString(context)
                )

                else -> Unit
            }
        }
    }

    ParkMapContent(
        state = uiState,
        onAction = viewModel::onAction,
        onNavigateToDetail = onNavigateToDetail,
        snackbarHostState = snackbarHostState
    )
}

@SuppressLint("MissingPermission")
@OptIn(ExperimentalPermissionsApi::class, ExperimentalMaterial3Api::class)
@Composable
private fun ParkMapContent(
    state: ParkMapUiState,
    onAction: (ParkMapAction) -> Unit,
    onNavigateToDetail: (Int) -> Unit,
    snackbarHostState: SnackbarHostState
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val locationPermissionState = rememberPermissionState(Manifest.permission.ACCESS_FINE_LOCATION)
    var showLocationDialog by remember { mutableStateOf(false) }
    var hasShownLocationDialog by rememberSaveable { mutableStateOf(false) }
    var hasInitialLocationBeenSet by rememberSaveable { mutableStateOf(false) }
    var mapLoaded by remember { mutableStateOf(false) }

    val pagerState = rememberPagerState(pageCount = { state.filteredParks.size })
    var isInternalPageChange by remember { mutableStateOf(false) }
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(LatLng(41.0082, 28.9784), 11f)
    }

    val isDarkTheme = isSystemInDarkTheme()
    val mapStyleOptions = remember(context, isDarkTheme) {
        try {
            val styleRes = if (isDarkTheme) R.raw.map_style_main_dark else R.raw.map_style_main
            MapStyleOptions.loadRawResourceStyle(context, styleRes)
        } catch (_: Exception) {
            null
        }
    }

    // handle location and focus
    LaunchedEffect(locationPermissionState.status.isGranted) {
        if (locationPermissionState.status.isGranted) {
            if (!hasInitialLocationBeenSet) {
                onAction(ParkMapAction.OnParkSelected(null))
            }

            val fusedClient = LocationServices.getFusedLocationProviderClient(context)
            fusedClient.lastLocation.addOnSuccessListener { location ->
                location?.let {
                    val latLng = LatLng(it.latitude, it.longitude)
                    onAction(ParkMapAction.OnUserLocationChanged(latLng))
                    scope.launch {
                        if (!hasInitialLocationBeenSet) {
                            cameraPositionState.animate(
                                CameraUpdateFactory.newLatLngZoom(
                                    latLng,
                                    15f
                                )
                            )
                            hasInitialLocationBeenSet = true
                        }
                    }
                }
            }
        }
    }

    LaunchedEffect(mapLoaded, locationPermissionState.status.isGranted) {
        if (mapLoaded && !locationPermissionState.status.isGranted && !hasShownLocationDialog) {
            showLocationDialog = true
            hasShownLocationDialog = true
        }
    }

    // sync pager with map
    LaunchedEffect(state.selectedPark?.parkID, state.filteredParks) {
        val park = state.selectedPark ?: return@LaunchedEffect
        val index = state.filteredParks.indexOfFirst { it.parkID == park.parkID }

        if (index != -1) {
            if (index != pagerState.currentPage) {
                try {
                    isInternalPageChange = true
                    pagerState.scrollToPage(index)
                } finally {
                    isInternalPageChange = false
                }
            }
        } else {
            onAction(ParkMapAction.OnParkSelected(null))
        }
    }

    // animate camera to park
    var lastAnimatedParkId by rememberSaveable { mutableStateOf<Int?>(null) }
    LaunchedEffect(state.selectedPark?.parkID) {
        val park = state.selectedPark ?: run {
            lastAnimatedParkId = null
            return@LaunchedEffect
        }

        if (park.parkID != lastAnimatedParkId) {
            cameraPositionState.animate(
                CameraUpdateFactory.newLatLngZoom(LatLng(park.lat, park.lng), 15f),
                Animations.MediumDuration
            )
            lastAnimatedParkId = park.parkID
        }
    }

    // sync map with pager swipe
    LaunchedEffect(pagerState, state.filteredParks, state.selectedPark?.parkID) {
        snapshotFlow { pagerState.currentPage }
            .distinctUntilChanged()
            .collect { page ->
                if (!isInternalPageChange && pagerState.isScrollInProgress) {
                    state.filteredParks.getOrNull(page)?.let { park ->
                        if (park.parkID != state.selectedPark?.parkID) {
                            onAction(ParkMapAction.OnParkSelected(park))
                        }
                    }
                }
            }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopBar(
                onLocationClick = {
                    if (locationPermissionState.status.isGranted) {
                        onAction(ParkMapAction.OnParkSelected(null))
                        state.userLocation?.let {
                            scope.launch {
                                cameraPositionState.animate(
                                    CameraUpdateFactory.newLatLngZoom(
                                        it,
                                        15f
                                    )
                                )
                            }
                        }
                    } else {
                        locationPermissionState.launchPermissionRequest()
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            ParkMap(
                cameraPositionState = cameraPositionState,
                paddingValues = PaddingValues(Dimens.SpacingTiny),
                mapStyleOptions = mapStyleOptions,
                filteredParks = state.filteredParks,
                userLocation = state.userLocation,
                onParkClick = { onAction(ParkMapAction.OnParkSelected(it)) },
                onMapClick = { onAction(ParkMapAction.OnParkSelected(null)) },
                onMapLoaded = { mapLoaded = true }
            )

            SearchAndFilterSection(
                searchQuery = state.searchQuery,
                onQueryChange = { onAction(ParkMapAction.OnSearchQueryChange(it)) },
                activeFilter = state.activeFilter,
                onFilterChange = { onAction(ParkMapAction.OnFilterChange(it)) },
                resultCount = state.filteredParks.size
            )

            ParkPager(
                isVisible = state.selectedPark != null,
                pagerState = pagerState,
                parks = state.filteredParks,
                modifier = Modifier.align(Alignment.BottomCenter),
                onFavoriteToggle = { onAction(ParkMapAction.ToggleFavorite(it)) },
                onClose = { onAction(ParkMapAction.OnParkSelected(null)) },
                onNavigateToDetail = { park ->
                    onNavigateToDetail(park.parkID)
                }
            )

            if (state.isLoading && state.filteredParks.isEmpty()) {
                ParkLoadingView()
            }

            if (state.error != null && state.filteredParks.isEmpty()) {
                ParkErrorView(
                    message = state.error.asString(context),
                    onRetry = { onAction(ParkMapAction.RetryLoadParks) }
                )
            }
        }
    }

    if (showLocationDialog && !locationPermissionState.status.isGranted) {
        LocationPermissionDialog(
            onDismiss = { showLocationDialog = false },
            onConfirm = {
                showLocationDialog = false
                locationPermissionState.launchPermissionRequest()
            }
        )
    }
}