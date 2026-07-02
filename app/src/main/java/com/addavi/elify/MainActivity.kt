package com.addavi.elify

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.media.AudioManager
import android.os.Build
import android.os.Bundle
import android.view.KeyEvent
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.addavi.elify.components.BottomNavigation
import com.addavi.elify.navigate.NavController
import com.addavi.elify.navigate.Screens
import com.addavi.elify.player.PlayerViewModel
import com.addavi.elify.player.PlayerViewModelFactory
import com.addavi.elify.ui.theme.ElifyTheme
import com.addavi.elify.ui.theme.SystemBarManager
import com.addavi.elify.viewmodel.MusicViewModel
import com.addavi.elify.viewmodel.MusicViewModelFactory
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private var currentRoute: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        SystemBarManager.setupTransparentBars(this)
        WindowCompat.setDecorFitsSystemWindows(window, true)
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {

            MainContent()
        }
    }

    @Composable
    private fun MainContent() {




        val context = LocalContext.current

        // ✅ Factory رو remember کن تا هر بار recompose نشه
        val musicFactory = remember { MusicViewModelFactory(context.applicationContext) }
        val playerFactory = remember { PlayerViewModelFactory(context.applicationContext) }

        val viewModel: MusicViewModel = viewModel(factory = musicFactory)
        val playerViewModel: PlayerViewModel = viewModel(factory = playerFactory)

        val isDarkTheme by viewModel.isDarkTheme.collectAsState()
        val navController = rememberNavController()

        ElifyTheme(darkTheme = isDarkTheme) {
            SystemBarManager.ConfigureSystemBars(isDarkTheme)
                AppContent(
                    navController = navController,
                    viewModel = viewModel,
                    playerViewModel = playerViewModel,
                    isDarkTheme = isDarkTheme,
                    onDarkModeChange = { viewModel.setDarkTheme(it) }
                )
        }
    }

    @Composable
    private fun AppContent(
        navController: NavHostController,
        viewModel: MusicViewModel,
        playerViewModel: PlayerViewModel,
        isDarkTheme: Boolean,
        onDarkModeChange: (Boolean) -> Unit
    ) {
        val bottomNavScreens = listOf(
            Screens.Home.route,
            Screens.Favorite.route,
            Screens.Setting.route
        )


        var totalDrag by remember { mutableStateOf(0f) }
        val coroutineScope = rememberCoroutineScope()
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route

        LaunchedEffect(currentRoute) {
            this@MainActivity.currentRoute = currentRoute
        }

        val levelPage = currentRoute in bottomNavScreens

        val swipeModifier = if (levelPage) {
            Modifier.pointerInput(currentRoute) {
                detectHorizontalDragGestures(
                    onDragStart = { totalDrag = 0f },
                    onDragEnd = {
                        when {
                            totalDrag < -100 -> { // swipe چپ
                                val currentIndex = bottomNavScreens.indexOf(currentRoute)
                                if (currentIndex < bottomNavScreens.size - 1) {
                                    coroutineScope.launch {
                                        navController.navigate(bottomNavScreens[currentIndex + 1]) {
                                            popUpTo(bottomNavScreens[0]) { saveState = true }
                                            launchSingleTop = true
                                            restoreState = true
                                        }
                                    }
                                }
                            }

                            totalDrag > 100 -> { // swipe راست
                                val currentIndex = bottomNavScreens.indexOf(currentRoute)
                                if (currentIndex > 0) {
                                    coroutineScope.launch {
                                        navController.navigate(bottomNavScreens[currentIndex - 1]) {
                                            popUpTo(bottomNavScreens[0]) { saveState = true }
                                            launchSingleTop = true
                                            restoreState = true
                                        }
                                    }
                                }
                            }
                        }
                    },
                    onHorizontalDrag = { _, dragAmount ->
                        totalDrag += dragAmount
                    }
                )
            }
        } else Modifier

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .then(swipeModifier)
            ,
            contentAlignment = Alignment.BottomCenter
        ) {
            GetPermission(
                onPermissionGranted = {
                    viewModel.loadSongs()
                }
            )

            // ✅ NavHost اینجا است
            NavController(
                navController = navController,
                viewModel = viewModel,
                playerViewModel = playerViewModel,
                isDarkTheme = isDarkTheme,
                onDarkModeChange = onDarkModeChange
            )

            AnimatedVisibility(
                visible = levelPage,
                enter = slideInVertically(
                    animationSpec = tween(1500),
                    initialOffsetY = { it }
                ) + fadeIn(),
                exit = slideOutVertically(
                    animationSpec = tween(1500),
                    targetOffsetY = { it }
                ) + fadeOut()
            ) {
                BottomNavigation(navController)
            }
        }
    }

    @Composable
    private fun GetPermission(onPermissionGranted: () -> Unit) {
        val context = LocalContext.current
        val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.READ_MEDIA_AUDIO
        } else {
            Manifest.permission.READ_EXTERNAL_STORAGE
        }

        val permissionLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestPermission()
        ) { isGranted ->
            if (isGranted) {
                onPermissionGranted()
            }
        }

        LaunchedEffect(permission) { // ✅ dependency
            if (context.hasPermission(permission)) {
                onPermissionGranted()
            } else {
                permissionLauncher.launch(permission)
            }
        }
    }

    // Extension function
    fun Context.hasPermission(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(
            this, permission
        ) == PackageManager.PERMISSION_GRANTED
    }


    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (currentRoute != Screens.Player.route) return super.onKeyDown(keyCode, event)
        val audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
        return when (keyCode) {
            KeyEvent.KEYCODE_VOLUME_UP -> {
                audioManager.adjustStreamVolume(
                    AudioManager.STREAM_MUSIC,
                    AudioManager.ADJUST_RAISE,
                    0 // بدون FLAG_SHOW_UI
                )
                true
            }

            KeyEvent.KEYCODE_VOLUME_DOWN -> {
                audioManager.adjustStreamVolume(
                    AudioManager.STREAM_MUSIC,
                    AudioManager.ADJUST_LOWER,
                    0 // بدون FLAG_SHOW_UI
                )
                true
            }

            else -> super.onKeyDown(keyCode, event)
        }
    }
}