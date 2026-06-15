package com.brkckr.parkv2.presentation.splash

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocalParking
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.brkckr.parkv2.R
import com.brkckr.parkv2.presentation.common.UiEvent
import com.brkckr.parkv2.presentation.common.asString
import com.brkckr.parkv2.ui.theme.Dimens

@Composable
fun SplashScreen(
    onNavigateToMain: () -> Unit,
    viewModel: SplashViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.uiEvent.collect { event ->
            when (event) {
                UiEvent.NavigateToHome -> onNavigateToMain()
                else -> Unit
            }
        }
    }

    SplashContent(
        state = uiState,
        onAction = viewModel::onAction
    )
}

@Composable
private fun SplashContent(
    state: SplashUiState,
    onAction: (SplashAction) -> Unit
) {
    val context = LocalContext.current
    val isDarkTheme = isSystemInDarkTheme()
    var showRetryDialog by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    val backgroundColor =
        if (isDarkTheme) MaterialTheme.colorScheme.background else MaterialTheme.colorScheme.primary
    val contentColor = Color.White

    LaunchedEffect(state.error) {
        state.error?.let {
            errorMessage = it.asString(context)
            showRetryDialog = true
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.LocalParking,
                contentDescription = stringResource(R.string.app_name),
                tint = contentColor,
                modifier = Modifier.size(Dimens.IconGiant)
            )
            Spacer(modifier = Modifier.height(Dimens.SpacingMedium))
            Text(
                text = stringResource(R.string.app_name),
                color = contentColor,
                fontSize = 24.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 1.sp
            )
        }

        if (state.isLoading) {
            CircularProgressIndicator(
                color = contentColor,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = Dimens.SpacingGiant)
                    .size(Dimens.IconExtraLarge),
                strokeWidth = 3.dp
            )
        }
    }

    if (showRetryDialog) {
        AlertDialog(
            onDismissRequest = {},
            title = {
                Text(
                    text = stringResource(R.string.connection_error),
                    fontWeight = FontWeight.Bold
                )
            },
            text = { Text(text = errorMessage) },
            confirmButton = {
                Button(
                    onClick = {
                        showRetryDialog = false
                        onAction(SplashAction.LoadParks)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text(
                        stringResource(R.string.please_retry),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }
            },
            dismissButton = null
        )
    }
}