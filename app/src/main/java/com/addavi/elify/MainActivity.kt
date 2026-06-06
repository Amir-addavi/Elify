package com.addavi.elify

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.core.app.ActivityCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.addavi.elify.components.BottomNavigation
import com.addavi.elify.navigate.NavController
import com.addavi.elify.navigate.Screens
import com.addavi.elify.player.PlayerViewModel
import com.addavi.elify.ui.theme.ElifyTheme
import com.addavi.elify.viewmodel.MusicViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val levelPage = false
            val viewModel = remember {
                MusicViewModel(this)
            }

            var isDarkTheme by remember { mutableStateOf(true) }
            
            ElifyTheme(darkTheme = isDarkTheme) {

                val navController = rememberNavController()
                val playerViewModel: PlayerViewModel = viewModel()
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route
                val levelPage = currentRoute in listOf(Screens.Home.route, Screens.Setting.route)


                Box(
                    modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background),
                    contentAlignment = Alignment.BottomCenter,

                ) {

                    GetPermission()

                    NavController(
                        navController = navController,
                        viewModel = viewModel,
                        playerViewModel = playerViewModel,
                        isDarkTheme = isDarkTheme,
                        onDarkModeChange = { isDarkTheme = it }
                    )
                        AnimatedVisibility(
                            visible = levelPage,

                            enter = slideInVertically(
                                animationSpec = tween(1200),
                                initialOffsetY = { it }
                            ) + fadeIn(),

                            exit = slideOutVertically(
                                animationSpec = tween(1200),
                                targetOffsetY = { it }
                            ) + fadeOut()
                        )
                         {
                            BottomNavigation(navController)
                         }
                }
            }
        }
    }

    @Composable
    fun GetPermission() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.READ_MEDIA_AUDIO),
                1
            )
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                1
            )
        }

    }
}

