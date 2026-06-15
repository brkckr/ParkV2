package com.brkckr.parkv2.presentation.park_map.components.filter

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.sp
import com.brkckr.parkv2.R
import com.brkckr.parkv2.domain.model.ParkFilter
import com.brkckr.parkv2.ui.theme.Dimens

@Composable
fun SearchAndFilterSection(
    searchQuery: String,
    onQueryChange: (String) -> Unit,
    activeFilter: ParkFilter,
    onFilterChange: (ParkFilter) -> Unit,
    resultCount: Int
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.8f),
        shadowElevation = Dimens.SearchElevation
    ) {
        Column(modifier = Modifier.padding(horizontal = Dimens.SpacingMedium, vertical = Dimens.SpacingSmall)) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = onQueryChange,
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text(stringResource(R.string.search_placeholder)) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { onQueryChange("") }) {
                            Icon(Icons.Default.Clear, contentDescription = stringResource(R.string.clear_search))
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(Dimens.RadiusMedium),
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = MaterialTheme.colorScheme.primary, unfocusedBorderColor = MaterialTheme.colorScheme.outline),
                keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words, autoCorrectEnabled = false, keyboardType = KeyboardType.Text, imeAction = ImeAction.Search)
            )
            Spacer(modifier = Modifier.height(Dimens.SpacingSmall))
            Row(horizontalArrangement = Arrangement.spacedBy(Dimens.SpacingSmall)) {
                FilterButton(label = stringResource(R.string.available), icon = Icons.Default.CheckCircle, isSelected = activeFilter == ParkFilter.AVAILABLE, onClick = { onFilterChange(ParkFilter.AVAILABLE) })
                FilterButton(label = stringResource(R.string.favorites_filter), icon = Icons.Default.Favorite, isSelected = activeFilter == ParkFilter.FAVORITES, onClick = { onFilterChange(ParkFilter.FAVORITES) })
                Spacer(modifier = Modifier.weight(1f))
                Text(text = stringResource(R.string.results_count, resultCount), fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.align(Alignment.CenterVertically))
            }
        }
    }
}
