package com.addavi.elify.tools

import android.annotation.SuppressLint
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration

enum class WindowSize { Compact, Medium, Expanded }

@SuppressLint("ConfigurationScreenWidthHeight")
@Composable
fun rememberWindowSize(): WindowSize {
    val configuration = LocalConfiguration.current
    return when {
        configuration.screenWidthDp < 600 -> WindowSize.Compact   // موبایل
        configuration.screenWidthDp < 840 -> WindowSize.Medium     // تبلت کوچیک
        else -> WindowSize.Expanded                                  // تبلت بزرگ
    }
}