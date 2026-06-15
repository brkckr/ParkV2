package com.brkckr.parkv2.presentation.splash

import com.brkckr.parkv2.domain.util.UiText

data class SplashUiState(
    val isLoading: Boolean = true,
    val error: UiText? = null
)