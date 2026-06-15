package com.brkckr.parkv2.presentation.park_map.components.filter

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.sp
import com.brkckr.parkv2.ui.theme.Dimens

@Composable
fun FilterButton(
    label: String,
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val containerColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface
    val contentColor = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.primary

    OutlinedButton(
        onClick = onClick,
        shape = RoundedCornerShape(Dimens.RadiusLarge),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = containerColor,
            contentColor = contentColor
        ),
        border = if (isSelected) null else ButtonDefaults.outlinedButtonBorder(enabled = true),
        modifier = Modifier.padding(end = Dimens.SpacingTiny)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(Dimens.IconTiny)
        )
        Text(
            text = label,
            modifier = Modifier.padding(start = Dimens.SpacingExtraSmall),
            fontSize = 12.sp
        )
    }
}
