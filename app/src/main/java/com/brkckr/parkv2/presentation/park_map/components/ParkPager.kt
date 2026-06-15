package com.brkckr.parkv2.presentation.park_map.components

import android.content.Intent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.LocalParking
import androidx.compose.material.icons.filled.NearMe
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import com.brkckr.parkv2.R
import com.brkckr.parkv2.domain.model.Park
import com.brkckr.parkv2.ui.components.CapacityItem
import com.brkckr.parkv2.ui.components.FavoriteChip
import com.brkckr.parkv2.ui.components.ParkStatusChip
import com.brkckr.parkv2.ui.theme.Dimens

@Composable
fun ParkPager(
    isVisible: Boolean,
    pagerState: PagerState,
    parks: List<Park>,
    modifier: Modifier = Modifier,
    onFavoriteToggle: (Park) -> Unit,
    onClose: () -> Unit,
    onNavigateToDetail: (Park) -> Unit
) {
    val context = LocalContext.current
    AnimatedVisibility(
        visible = isVisible,
        enter = slideInVertically(initialOffsetY = { it }),
        exit = slideOutVertically(targetOffsetY = { it }),
        modifier = modifier
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxWidth(),
            beyondViewportPageCount = 1,
            contentPadding = PaddingValues(horizontal = Dimens.SpacingLarge),
            key = { index -> parks.getOrNull(index)?.parkID ?: index }
        ) { page ->
            parks.getOrNull(page)?.let { park ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = Dimens.SpacingLarge, horizontal = Dimens.SpacingTiny),
                    shape = RoundedCornerShape(Dimens.RadiusLarge),
                    elevation = CardDefaults.cardElevation(defaultElevation = Dimens.CardElevation),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            IconButton(onClick = { onFavoriteToggle(park) }) {
                                Icon(
                                    imageVector = if (park.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                    contentDescription = stringResource(R.string.favorite),
                                    tint = if (park.isFavorite) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(Dimens.IconMedium)
                                )
                            }
                            IconButton(onClick = onClose) {
                                Icon(
                                    Icons.Default.Clear,
                                    contentDescription = stringResource(R.string.clear_search),
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(Dimens.IconMedium)
                                )
                            }
                        }
                        ParkCardContent(
                            park = park,
                            onGoToDetails = onNavigateToDetail,
                            onOpenInMaps = { p ->
                                val uri =
                                    "geo:${p.lat},${p.lng}?q=${p.lat},${p.lng}(${p.parkName})".toUri()
                                context.startActivity(
                                    Intent(
                                        Intent.ACTION_VIEW,
                                        uri
                                    ).apply { setPackage("com.google.android.apps.maps") })
                            })
                    }
                }
            }
        }
    }
}

@Composable
private fun ParkCardContent(
    park: Park,
    onGoToDetails: (Park) -> Unit,
    onOpenInMaps: (Park) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Dimens.SpacingLarge, vertical = Dimens.SpacingSmall)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = park.parkName,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = park.district,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 14.sp
            )
        }
        Spacer(modifier = Modifier.height(Dimens.SpacingMedium))
        Row(horizontalArrangement = Arrangement.spacedBy(Dimens.SpacingSmall)) {
            if (park.isFavorite) FavoriteChip()
            ParkStatusChip(status = park.status)
        }
        Spacer(modifier = Modifier.height(Dimens.SpacingMedium))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(Dimens.SpacingMedium)
        ) {
            CapacityItem(
                modifier = Modifier.weight(1f),
                icon = Icons.Default.DirectionsCar,
                label = stringResource(R.string.capacity),
                value = "${park.capacity}"
            )
            CapacityItem(
                modifier = Modifier.weight(1f),
                icon = Icons.Default.LocalParking,
                label = stringResource(R.string.available),
                value = "${park.emptyCapacity}"
            )
            park.distanceMeters?.let {
                CapacityItem(
                    modifier = Modifier.weight(1f),
                    icon = Icons.Default.NearMe,
                    label = stringResource(R.string.distance),
                    value = if (it >= 1000) stringResource(
                        R.string.distance_km,
                        it / 1000
                    ) else stringResource(R.string.distance_m, it.toInt())
                )
            }
        }
        Spacer(modifier = Modifier.height(Dimens.SpacingLarge))
        ParkActionButtons(park, onGoToDetails, onOpenInMaps)
    }
}


@Composable
private fun ParkActionButtons(
    park: Park,
    onGoToDetails: (Park) -> Unit,
    onOpenInMaps: (Park) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(Dimens.SpacingSmall)
    ) {
        OutlinedButton(
            onClick = { onOpenInMaps(park) },
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(Dimens.RadiusMedium),
            border = ButtonDefaults.outlinedButtonBorder(enabled = true)
                .copy(brush = androidx.compose.ui.graphics.SolidColor(MaterialTheme.colorScheme.primary))
        ) {
            Icon(
                Icons.Default.Place,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(Dimens.IconSmall)
            )
            Spacer(modifier = Modifier.width(Dimens.SpacingSmall))
            Text(
                stringResource(R.string.google_maps),
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.SemiBold
            )
        }
        Button(
            onClick = { onGoToDetails(park) },
            modifier = Modifier.weight(1f),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
            shape = RoundedCornerShape(Dimens.RadiusMedium)
        ) {
            Text(
                stringResource(R.string.go_to_details),
                color = MaterialTheme.colorScheme.onPrimary,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.width(Dimens.SpacingSmall))
            Icon(
                Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                modifier = Modifier.size(Dimens.IconSmall)
            )
        }
    }
}

