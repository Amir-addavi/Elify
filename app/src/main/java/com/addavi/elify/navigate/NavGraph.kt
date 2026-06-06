package com.addavi.elify.navigate

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.addavi.elify.screens.SettingScreen
import com.addavi.elify.player.PlayerViewModel
import com.addavi.elify.screens.HomeScreen
import com.addavi.elify.screens.PlayerScreen
import com.addavi.elify.screens.SplashScreen
import com.addavi.elify.viewmodel.MusicViewModel

@Composable
fun NavController(
    navController: NavHostController,
    viewModel: MusicViewModel,
    playerViewModel: PlayerViewModel,
    isDarkTheme: Boolean,
    onDarkModeChange: (Boolean) -> Unit
){


    NavHost(
        navController = navController,
        startDestination = Screens.Splash.route
    ){
        composable(Screens.Splash.route ,
            enterTransition = {

                fadeIn(
                    animationSpec = tween(800)
                )

            },

            exitTransition = {

                fadeOut(
                    animationSpec = tween(800)
                ) + scaleOut(

                    targetScale = 1.1f,
                    animationSpec = tween(800)

                )

            }

        ) {
            SplashScreen(navController)
        }
        composable(Screens.Home.route ,
            enterTransition = {

                slideInVertically(

                    initialOffsetY = { it / 4 },

                    animationSpec = tween(
                        durationMillis = 700,
                        easing = FastOutSlowInEasing
                    )

                ) + fadeIn(

                    animationSpec = tween(700)

                )
            },

            exitTransition = {

                fadeOut(
                    animationSpec = tween(300)
                )

            }
        ){
            HomeScreen(
                viewModel,
                playerViewModel,
                navController
                )
        }
        composable(
            route = Screens.Player.route,
            enterTransition = {
                slideInVertically(
                    initialOffsetY = { it },
                    animationSpec = tween(400)
                )
            },

            popExitTransition = {
                slideOutVertically(
                    targetOffsetY = { it },
                    animationSpec = tween(400)
                )
            }
        ) {
            PlayerScreen(playerViewModel , navController)
        }
        composable(Screens.Setting.route ,
            enterTransition = {

                slideInVertically(

                    initialOffsetY = { it / 4 },

                    animationSpec = tween(
                        durationMillis = 700,
                        easing = FastOutSlowInEasing
                    )

                ) + fadeIn(

                    animationSpec = tween(700)

                )
            },

            exitTransition = {

                fadeOut(
                    animationSpec = tween(300)
                )

            }
        ){
            SettingScreen(darkMode = isDarkTheme ,onDarkModeChange = onDarkModeChange)
        }

    }

}