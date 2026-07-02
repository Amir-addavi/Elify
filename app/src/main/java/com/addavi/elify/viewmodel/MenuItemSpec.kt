package com.addavi.elify.viewmodel

data class MenuItemSpec(
    val id: String,
    val title: String,
    val icon: Int? = null,
    val destructive: Boolean = false,
    val enabled: Boolean = true,
)