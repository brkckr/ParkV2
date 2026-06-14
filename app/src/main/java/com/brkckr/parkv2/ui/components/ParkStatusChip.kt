package com.brkckr.parkv2.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.brkckr.parkv2.domain.model.ParkStatus
import com.brkckr.parkv2.ui.theme.AvailableMarkerColor
import com.brkckr.parkv2.ui.theme.ClosedMarkerColor
import com.brkckr.parkv2.ui.theme.Dimens
import com.brkckr.parkv2.ui.theme.FullMarkerColor

@Composable
fun ParkStatusChip(
    status: ParkStatus,
    modifier: Modifier = Modifier
) {
    val color = when (status) {
        ParkStatus.OPEN -> AvailableMarkerColor
        ParkStatus.FULL -> FullMarkerColor
        ParkStatus.CLOSED -> ClosedMarkerColor
    }

    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(Dimens.RadiusLarge),
        color = color.copy(alpha = 0.1f)
    ) {
        Text(
            text = stringResource(status.resId),
            color = color,
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp,
            modifier = Modifier.padding(horizontal = Dimens.SpacingMedium, vertical = Dimens.SpacingExtraSmall)
        )
    }
}

@Composable
fun FavoriteChip(
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(Dimens.RadiusLarge),
        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
    ) {
        Text(
            text = stringResource(com.brkckr.parkv2.R.string.favorite),
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp,
            modifier = Modifier.padding(horizontal = Dimens.SpacingMedium, vertical = Dimens.SpacingExtraSmall)
        )
    }
}
