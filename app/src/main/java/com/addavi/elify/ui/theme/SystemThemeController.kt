package com.addavi.elify.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
object SystemBarManager {

    @Composable
    fun ConfigureSystemBars(isDarkTheme: Boolean) {
        val view = LocalView.current
        if (view.isInEditMode) return

        DisposableEffect(isDarkTheme) {
            val activity = view.context as Activity
            val window = activity.window
            val controller = WindowInsetsControllerCompat(window, view)

            controller.isAppearanceLightStatusBars = !isDarkTheme
            controller.isAppearanceLightNavigationBars = !isDarkTheme

            // ✅ تشخیص Gesture Navigation
            val isGestureNav = isGestureNavigation(activity)

            if (!isGestureNav) {
                // Button navigation — پس‌زمینه شفاف با رنگ مناسب آیکون
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
                    controller.isAppearanceLightNavigationBars = !isDarkTheme
                }
            }
            // Gesture navigation — فقط رنگ آیکون (handle) مهمه، پس‌زمینه شفافه

            onDispose {}
        }
    }

    fun setupTransparentBars(activity: Activity) {
        WindowCompat.setDecorFitsSystemWindows(activity.window, false)

        activity.window.apply {
            navigationBarColor = android.graphics.Color.TRANSPARENT
            statusBarColor = android.graphics.Color.TRANSPARENT

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                isNavigationBarContrastEnforced = false
                isStatusBarContrastEnforced = false
            }
        }
    }

    // ✅ تشخیص نوع navigation
    private fun isGestureNavigation(activity: Activity): Boolean {
        val resourceId = activity.resources.getIdentifier(
            "config_navBarInteractionMode", "integer", "android"
        )
        return if (resourceId > 0) {
            activity.resources.getInteger(resourceId) == 2 // 2 = Gesture
        } else {
            false
        }
    }
}