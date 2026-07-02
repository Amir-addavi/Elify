package com.addavi.elify.navigate

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.TransformOrigin
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.addavi.elify.player.PlayerViewModel
import com.addavi.elify.screens.DriveScreen
import com.addavi.elify.screens.FavoriteScreen
import com.addavi.elify.screens.HomeScreen
import com.addavi.elify.screens.PlayerScreen
import com.addavi.elify.screens.SearchScreen
import com.addavi.elify.screens.SettingScreen
import com.addavi.elify.viewmodel.MusicViewModel
@Composable
fun NavController(
    navController: NavHostController,
    viewModel: MusicViewModel,
    playerViewModel: PlayerViewModel,
    isDarkTheme: Boolean,
    onDarkModeChange: (Boolean) -> Unit
) {
    NavHost(
        navController = navController,
        startDestination = Screens.Home.route  // ✅ اضافه کردیم
    ) {
        // Home Screen
        composable(
            route = Screens.Home.route,
            enterTransition = { scaleEnterTransition() },
            exitTransition = { scaleExitTransition() }
        ) {
            HomeScreen(
                viewModel,
                playerViewModel,
                navController
            )
        }

        // Player Screen
        composable(
            route = Screens.Player.route,
            enterTransition = {
                slideInVertically(
                    initialOffsetY = { it },
                    animationSpec = tween(600, easing = FastOutSlowInEasing)
                ) + fadeIn(animationSpec = tween(500))
            },
            popExitTransition = {
                slideOutVertically(
                    targetOffsetY = { it },
                    animationSpec = tween(600, easing = FastOutSlowInEasing)
                ) + fadeOut(animationSpec = tween(500))
            }
        ) {
            PlayerScreen(playerViewModel, navController, viewModel)
        }

        // Setting Screen
        composable(
            route = Screens.Setting.route,
            enterTransition = { scaleEnterTransition() },
            exitTransition = { scaleExitTransition() }
        ) {
            SettingScreen(
                darkMode = isDarkTheme,
                onDarkModeChange = onDarkModeChange,
                navController
            )
        }

        // Favorite Screen
        composable(
            route = Screens.Favorite.route,
            enterTransition = { scaleEnterTransition() },
            exitTransition = { scaleExitTransition() }
        ) {
            FavoriteScreen(
                viewModel = viewModel,
                navController = navController,
                playerViewModel = playerViewModel
            )
        }

        // Search Screen
        composable(
            route = Screens.Search.route,
            enterTransition = { searchEnterTransition() },
            exitTransition = { searchExitTransition() },
            popExitTransition = { searchExitTransition() }
        ) {
            SearchScreen(viewModel, playerViewModel, navController)
        }

        // Drive Screen
        composable(
            route = Screens.DriveScreen.route
        ) {
            DriveScreen(playerViewModel, viewModel)
        }
    }
}

// ============================================
// 🎨 Transition Functions (Helper)
// ============================================

private fun scaleEnterTransition(): EnterTransition {
    return scaleIn(
        initialScale = 1.15f,
        animationSpec = tween(500, easing = FastOutSlowInEasing)
    ) + fadeIn(animationSpec = tween(500))
}

private fun scaleExitTransition(): ExitTransition {
    return scaleOut(
        targetScale = 0.85f,
        animationSpec = tween(500, easing = FastOutSlowInEasing)
    ) + fadeOut(animationSpec = tween(400))
}

private fun searchEnterTransition(): EnterTransition {
    return scaleIn(
        initialScale = 0.3f,
        transformOrigin = TransformOrigin(0.5f, 0.15f),
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        )
    ) + fadeIn(
        animationSpec = tween(250, easing = LinearOutSlowInEasing)
    ) + expandVertically(
        expandFrom = Alignment.Top,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioLowBouncy,
            stiffness = Spring.StiffnessMedium
        )
    )
}

private fun searchExitTransition(): ExitTransition {
    return scaleOut(
        targetScale = 0.3f,
        transformOrigin = TransformOrigin(0.5f, 0.15f),
        animationSpec = tween(300, easing = FastOutLinearInEasing)
    ) + fadeOut(animationSpec = tween(200))
}