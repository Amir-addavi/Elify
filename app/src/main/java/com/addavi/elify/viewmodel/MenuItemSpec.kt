package com.addavi.elify.viewmodel

import androidx.compose.ui.graphics.vector.ImageVector

data class MenuItemSpec(
    val id: String,
    val title: String,
    val icon: ImageVector? = null,
    val destructive: Boolean = false,
    val enabled: Boolean = true,
)