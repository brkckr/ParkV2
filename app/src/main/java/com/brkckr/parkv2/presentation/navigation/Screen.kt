package com.brkckr.parkv2.presentation.navigation

import kotlinx.serialization.Serializable

@Serializable
sealed interface Screen {
    @Serializable
    data object Splash : Screen

    @Serializable
    data object Home : Screen

    @Serializable
    data class ParkDetail(val parkId: Int) : Screen
}
