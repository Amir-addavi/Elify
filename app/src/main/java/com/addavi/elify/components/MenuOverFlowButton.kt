package com.addavi.elify.components

import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.addavi.elify.R
import com.addavi.elify.viewmodel.MenuItemSpec
import com.addavi.elify.viewmodel.Song


@Composable
fun SongOverflowButton(
    song: Song,
    menuItems: List<MenuItemSpec> = defaultMenuItems(),
    onAction: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var showSheet by remember { mutableStateOf(false) }

    IconButton(
        onClick = { showSheet = true },
        modifier = modifier
    ) {
        Icon(
            painter = painterResource(R.drawable.menu_ico),
            contentDescription = "More",
            tint = MaterialTheme.colorScheme.onTertiary,
            modifier = Modifier.size(30.dp)
        )
    }

    if (showSheet) {
        SongBottomSheet(
            song = song,
            menuItems = menuItems,
            onDismiss = { showSheet = false },
            onAction = onAction
        )
    }
}
fun defaultMenuItems() = listOf(
    MenuItemSpec("delete", "Delete", R.drawable.delete_ico, destructive = true),
)