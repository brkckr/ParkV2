package com.brkckr.parkv2.presentation.park_map.components.map

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import com.brkckr.parkv2.ui.theme.Animations
import com.brkckr.parkv2.ui.theme.Dimens
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.MarkerComposable
import com.google.maps.android.compose.rememberUpdatedMarkerState

@Composable
fun UserLocationMarker(location: LatLng) {
    val markerState = rememberUpdatedMarkerState(position = location)
    val primaryColor = MaterialTheme.colorScheme.primary

    val infiniteTransition = rememberInfiniteTransition(label = "ripple")

    val radiusRatio by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(Animations.RippleDuration, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "radius"
    )

    val opacity by infiniteTransition.animateFloat(
        initialValue = 0.6f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(Animations.RippleDuration, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "opacity"
    )

    MarkerComposable(
        state = markerState,
        anchor = Offset(0.5f, 0.5f),
        keys = arrayOf(radiusRatio)
    ) {
        Canvas(modifier = Modifier.size(Dimens.RadiusLarge * 10)) {
            val center = Offset(size.width / 2, size.height / 2)
            val maxRippleRadius = Dimens.SpacingHuge.toPx()

            drawCircle(
                color = primaryColor,
                radius = maxRippleRadius * radiusRatio,
                alpha = opacity,
                center = center
            )

            drawCircle(
                color = Color.White,
                radius = Dimens.SpacingExtraSmall.toPx(),
                center = center
            )
            drawCircle(
                color = primaryColor,
                radius = (Dimens.SpacingTiny + Dimens.SpacingTiny / 4).toPx(),
                center = center
            )
        }
    }
}
