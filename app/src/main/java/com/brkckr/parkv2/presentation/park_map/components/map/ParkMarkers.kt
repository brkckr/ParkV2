package com.brkckr.parkv2.presentation.park_map.components.map

import android.graphics.Canvas
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.drawscope.CanvasDrawScope
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.core.graphics.createBitmap
import com.brkckr.parkv2.domain.model.Park
import com.brkckr.parkv2.domain.model.ParkStatus
import com.brkckr.parkv2.ui.theme.AvailableMarkerColor
import com.brkckr.parkv2.ui.theme.ClosedMarkerColor
import com.brkckr.parkv2.ui.theme.Dimens
import com.brkckr.parkv2.ui.theme.FullMarkerColor
import com.brkckr.parkv2.ui.theme.PrimaryColor
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState

@Composable
fun ParkMarkers(
    parks: List<Park>,
    onParkClick: (Park) -> Unit
) {
    val favoriteIcon = rememberMarkerIcon(Icons.Default.LocationOn, PrimaryColor)
    val fullIcon = rememberMarkerIcon(Icons.Default.LocationOn, FullMarkerColor)
    val closedIcon = rememberMarkerIcon(Icons.Default.LocationOn, ClosedMarkerColor)
    val availableIcon = rememberMarkerIcon(Icons.Default.LocationOn, AvailableMarkerColor)

    parks.forEach { park ->
        val icon = when {
            park.isFavorite -> favoriteIcon
            park.status == ParkStatus.FULL -> fullIcon
            park.status == ParkStatus.CLOSED -> closedIcon
            else -> availableIcon
        }

        Marker(
            state = remember(park.parkID) { MarkerState(position = LatLng(park.lat, park.lng)) },
            title = park.parkName,
            icon = icon,
            onClick = {
                onParkClick(park)
                true
            }
        )
    }
}

@Composable
private fun rememberMarkerIcon(
    imageVector: ImageVector,
    color: Color,
    size: Dp = Dimens.SpacingHuge + 4.dp
): BitmapDescriptor {
    val density = LocalDensity.current
    val painter = rememberVectorPainter(imageVector)

    return remember(imageVector, color, size) {
        val sizePx = with(density) { size.toPx() }
        val bitmap = createBitmap(sizePx.toInt(), sizePx.toInt())
        val canvas = Canvas(bitmap)
        val composeCanvas = androidx.compose.ui.graphics.Canvas(canvas)

        val drawScope = CanvasDrawScope()
        drawScope.draw(
            density = density,
            layoutDirection = LayoutDirection.Ltr,
            canvas = composeCanvas,
            size = Size(sizePx, sizePx)
        ) {
            with(painter) {
                draw(
                    size = Size(sizePx, sizePx),
                    colorFilter = ColorFilter.tint(color)
                )
            }
        }

        BitmapDescriptorFactory.fromBitmap(bitmap)
    }
}
