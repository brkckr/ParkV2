package com.brkckr.parkv2.presentation.park_detail

import android.content.Intent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.LocalParking
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.NearMe
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.brkckr.parkv2.R
import com.brkckr.parkv2.domain.model.ParkDetail
import com.brkckr.parkv2.domain.model.ParkStatus
import com.brkckr.parkv2.presentation.common.UiEvent
import com.brkckr.parkv2.presentation.common.asString
import com.brkckr.parkv2.presentation.park_detail.components.AddressRow
import com.brkckr.parkv2.presentation.park_detail.components.TariffCard
import com.brkckr.parkv2.ui.components.CapacityItem
import com.brkckr.parkv2.ui.components.FavoriteChip
import com.brkckr.parkv2.ui.components.ParkErrorView
import com.brkckr.parkv2.ui.components.ParkLoadingView
import com.brkckr.parkv2.ui.components.ParkStatusChip
import com.brkckr.parkv2.ui.theme.Dimens
import com.brkckr.parkv2.ui.theme.ParkV2Theme
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Polygon
import com.google.maps.android.compose.rememberCameraPositionState

@Composable
fun ParkDetailScreen(
    parkId: Int,
    onBack: () -> Unit,
    viewModel: ParkDetailViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(parkId) {
        viewModel.onAction(ParkDetailAction.LoadParkDetail(parkId))
    }

    LaunchedEffect(Unit) {
        viewModel.uiEvent.collect { event ->
            when (event) {
                is UiEvent.ShowSnackbar -> snackbarHostState.showSnackbar(
                    event.message.asString(context)
                )

                UiEvent.NavigateBack -> onBack()
                UiEvent.NavigateToHome -> onBack()
            }
        }
    }

    ParkDetailContent(
        state = uiState,
        onAction = viewModel::onAction,
        snackbarHostState = snackbarHostState
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ParkDetailContent(
    state: ParkDetailUiState,
    onAction: (ParkDetailAction) -> Unit,
    snackbarHostState: SnackbarHostState
) {
    val context = LocalContext.current

    Scaffold(
        topBar = {
            DetailTopBar(
                title = state.parkDetail?.parkName ?: stringResource(R.string.park_detail),
                isFavorite = state.parkDetail?.isFavorite ?: false,
                onBack = { onAction(ParkDetailAction.NavigateBack) },
                onFavoriteToggle = {
                    state.parkDetail?.let {
                        onAction(ParkDetailAction.ToggleFavorite(it))
                    }
                },
                onNavigateClick = {
                    state.parkDetail?.let { p ->
                        val uri = "geo:${p.lat},${p.lng}?q=${p.lat},${p.lng}(${p.parkName})".toUri()
                        context.startActivity(
                            Intent(
                                Intent.ACTION_VIEW,
                                uri
                            ).apply { setPackage("com.google.android.apps.maps") })
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                state.isLoading -> ParkLoadingView()
                state.error != null -> ParkErrorView(
                    message = state.error.asString(context),
                    onRetry = {
                        state.parkDetail?.let {
                            onAction(ParkDetailAction.LoadParkDetail(it.parkID))
                        }
                    }
                )

                state.parkDetail != null -> ParkDetailLayout(
                    state.parkDetail
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DetailTopBar(
    title: String,
    isFavorite: Boolean,
    onBack: () -> Unit,
    onFavoriteToggle: () -> Unit,
    onNavigateClick: () -> Unit
) {
    TopAppBar(
        title = { Text(text = title, style = MaterialTheme.typography.titleMedium, maxLines = 1) },
        navigationIcon = {
            IconButton(onClick = onBack) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = stringResource(R.string.back)
                )
            }
        },
        actions = {
            IconButton(onClick = onNavigateClick) {
                Icon(
                    Icons.Default.NearMe,
                    contentDescription = stringResource(R.string.google_maps)
                )
            }
            IconButton(onClick = onFavoriteToggle) {
                Icon(
                    if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = stringResource(R.string.favorite)
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primary,
            titleContentColor = MaterialTheme.colorScheme.onPrimary,
            navigationIconContentColor = MaterialTheme.colorScheme.onPrimary,
            actionIconContentColor = MaterialTheme.colorScheme.onPrimary
        )
    )
}

@Composable
private fun ParkDetailLayout(park: ParkDetail) {
    Column(modifier = Modifier.fillMaxSize()) {
        MapSection(park)
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(Dimens.SpacingLarge)
        ) {
            StatusSection(park)
            Spacer(modifier = Modifier.height(Dimens.SpacingLarge))
            CapacitySection(park)
            Spacer(modifier = Modifier.height(Dimens.SpacingExtraLarge))
            AddressSection(park)
            Spacer(modifier = Modifier.height(Dimens.SpacingExtraLarge))
            TariffSection(park.tariffs)
        }
    }
}

@Composable
private fun MapSection(park: ParkDetail) {
    val context = LocalContext.current
    val isDarkTheme = isSystemInDarkTheme()
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(LatLng(park.lat, park.lng), 15f)
    }

    val mapStyleOptions = remember(isDarkTheme) {
        try {
            val styleRes = if (isDarkTheme) R.raw.map_style_detail_dark else R.raw.map_style_detail
            MapStyleOptions.loadRawResourceStyle(context, styleRes)
        } catch (_: Exception) {
            null
        }
    }

    val mapProperties = remember(mapStyleOptions) {
        MapProperties(
            mapStyleOptions = mapStyleOptions,
            isMyLocationEnabled = false
        )
    }
    val uiSettings = remember {
        MapUiSettings(
            zoomControlsEnabled = false,
            myLocationButtonEnabled = false,
            scrollGesturesEnabled = true,
            zoomGesturesEnabled = true,
            tiltGesturesEnabled = true
        )
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(Dimens.MapPolygonHeight)
    ) {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            properties = mapProperties,
            uiSettings = uiSettings
        ) {
            if (park.areaPoints.isNotEmpty()) {
                Polygon(
                    points = park.areaPoints.map { LatLng(it.lat, it.lng) },
                    fillColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                    strokeColor = MaterialTheme.colorScheme.primary,
                    strokeWidth = 2f
                )
            }
        }

        LaunchedEffect(park.areaPoints) {
            if (park.areaPoints.isNotEmpty()) {
                val bounds = LatLngBounds.builder()
                    .apply { park.areaPoints.forEach { include(LatLng(it.lat, it.lng)) } }.build()
                cameraPositionState.animate(CameraUpdateFactory.newLatLngBounds(bounds, 50))
            }
        }
    }
}

@Composable
private fun StatusSection(park: ParkDetail) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(Dimens.SpacingSmall)) {
            if (park.isFavorite) FavoriteChip()
            ParkStatusChip(status = park.status)
        }
        Text(
            text = park.parkType,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 13.sp
        )
    }
}

@Composable
private fun CapacitySection(park: ParkDetail) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = Dimens.SpacingSmall),
        horizontalArrangement = Arrangement.spacedBy(Dimens.SpacingSmall)
    ) {
        CapacityItem(
            label = stringResource(R.string.total),
            value = park.capacity.toString(),
            icon = Icons.Default.DirectionsCar,
            modifier = Modifier.weight(1f)
        )
        CapacityItem(
            label = stringResource(R.string.available),
            value = park.emptyCapacity.toString(),
            icon = Icons.Default.LocalParking,
            modifier = Modifier.weight(1f)
        )
        CapacityItem(
            label = stringResource(R.string.occupied),
            value = (park.capacity - park.emptyCapacity).toString(),
            icon = Icons.Default.Block,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun AddressSection(park: ParkDetail) {
    if (park.address.isNotBlank()) AddressRow(
        Icons.Default.LocationOn,
        stringResource(R.string.address),
        park.address
    )
    if (park.district.isNotBlank()) AddressRow(
        Icons.Default.Place,
        stringResource(R.string.district),
        park.district
    )
    if (park.workHours.isNotBlank()) AddressRow(
        Icons.Default.Schedule,
        stringResource(R.string.working_hours),
        park.workHours
    )
    if (park.freeTime.isNotBlank()) AddressRow(
        Icons.Default.Timer,
        stringResource(R.string.free_time),
        park.freeTime
    )
}

@Composable
private fun TariffSection(items: List<String>) {
    if (items.isNotEmpty()) {
        Column(verticalArrangement = Arrangement.spacedBy(Dimens.SpacingSmall)) {
            Text(
                stringResource(R.string.detailed_tariff),
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )
            items.chunked(3).forEach { rowItems ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(Dimens.SpacingSmall)
                ) {
                    rowItems.forEach { item ->
                        val text = when {
                            item.startsWith("hourly_fee\n") -> item.replace(
                                "hourly_fee\n",
                                stringResource(R.string.hourly_fee) + "\n"
                            )

                            item.startsWith("monthly_fee\n") -> item.replace(
                                "monthly_fee\n",
                                stringResource(R.string.monthly_fee) + "\n"
                            )

                            else -> item
                        }
                        TariffCard(text, Modifier.weight(1f))
                    }
                    repeat(3 - rowItems.size) { Spacer(Modifier.weight(1f)) }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ParkDetailContentPreview() {
    ParkV2Theme {
        ParkDetailContent(
            state = ParkDetailUiState(
                parkDetail = ParkDetail(
                    parkID = 1,
                    parkName = "Sample Park",
                    lat = 41.0082,
                    lng = 28.9784,
                    capacity = 100,
                    emptyCapacity = 20,
                    isOpen = true,
                    parkType = "Open",
                    district = "Fatih",
                    workHours = "24 Hours",
                    freeTime = "15 min",
                    fee = "50 TL",
                    monthlyFee = "1500 TL",
                    areaPoints = emptyList(),
                    address = "Sample Address",
                    phone = "0212 123 45 67",
                    status = ParkStatus.OPEN,
                    isFavorite = true,
                    tariffs = listOf("hourly_fee\n20 TL", "monthly_fee\n1500 TL")
                )
            ),
            onAction = {},
            snackbarHostState = remember { SnackbarHostState() }
        )
    }
}
