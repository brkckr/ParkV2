package com.brkckr.parkv2.presentation.navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.brkckr.parkv2.presentation.park_detail.ParkDetailScreen
import com.brkckr.parkv2.presentation.park_map.ParkMapScreen
import com.brkckr.parkv2.presentation.splash.SplashScreen
import com.brkckr.parkv2.ui.theme.Animations

@Composable
fun ParkNavGraph() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.Splash,
        enterTransition = { fadeIn(animationSpec = tween(Animations.ShortDuration)) },
        exitTransition = { fadeOut(animationSpec = tween(Animations.ShortDuration)) }
    ) {
        composable<Screen.Splash> {
            SplashScreen(
                onNavigateToMain = {
                    navController.navigate(Screen.Home) {
                        popUpTo(Screen.Splash) { inclusive = true }
                    }
                }
            )
        }

        composable<Screen.Home>(
            enterTransition = { fadeIn(animationSpec = tween(Animations.MediumDuration)) },
            exitTransition = {
                slideOutHorizontally(
                    targetOffsetX = { -it },
                    animationSpec = tween(Animations.MediumDuration)
                ) + fadeOut(animationSpec = tween(Animations.MediumDuration))
            },
            popEnterTransition = {
                slideInHorizontally(
                    initialOffsetX = { -it },
                    animationSpec = tween(Animations.MediumDuration)
                ) + fadeIn(animationSpec = tween(Animations.MediumDuration))
            }
        ) {
            ParkMapScreen(
                onNavigateToDetail = { parkId ->
                    navController.navigate(Screen.ParkDetail(parkId))
                }
            )
        }

        composable<Screen.ParkDetail>(
            enterTransition = {
                slideInHorizontally(
                    initialOffsetX = { it },
                    animationSpec = tween(Animations.MediumDuration)
                ) + fadeIn(animationSpec = tween(Animations.MediumDuration))
            },
            popExitTransition = {
                slideOutHorizontally(
                    targetOffsetX = { it },
                    animationSpec = tween(Animations.MediumDuration)
                ) + fadeOut(animationSpec = tween(Animations.MediumDuration))
            }
        ) { backStackEntry ->
            val detailRoute = backStackEntry.toRoute<Screen.ParkDetail>()
            ParkDetailScreen(
                parkId = detailRoute.parkId,
                onBack = { navController.popBackStack() }
            )
        }
    }
}
