package com.brkckr.parkv2.presentation.splash

sealed interface SplashAction {
    data object LoadParks : SplashAction
}
