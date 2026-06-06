package com.addavi.elify.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.IntRect
import androidx.compose.ui.unit.dp
import com.addavi.elify.R
import com.addavi.elify.viewmodel.MenuItemSpec

@Composable
fun SongOverflowButton(
    onAction: (String) -> Unit, // یا sealed action
    modifier: Modifier = Modifier
) {
    var expanded by rememberSaveable { mutableStateOf(false) }
    var anchorBounds by remember { mutableStateOf<IntRect?>(null) }

    Box(modifier) {
        IconButton(
            modifier = Modifier.onGloballyPositioned { coords ->
                val pos = coords.positionInWindow()
                val size = coords.size
                anchorBounds = IntRect(
                    left = pos.x.toInt(),
                    top = pos.y.toInt(),
                    right = pos.x.toInt() + size.width,
                    bottom = pos.y.toInt() + size.height
                )
            },
            onClick = { expanded = true }
        ) {
            Icon(
                painter = painterResource(R.drawable.menu_ico),
                contentDescription = "More",
                tint = MaterialTheme.colorScheme.onTertiary,
                modifier = modifier
                    .size(30.dp)
            )
        }

        val menuItems = remember {
            listOf(
                MenuItemSpec("play_next", "Play next", Icons.Default.Share),
                MenuItemSpec("add_queue", "Add to queue", Icons.Default.Add),
                MenuItemSpec("share", "Share", Icons.Default.Share),
                MenuItemSpec("delete", "Delete", Icons.Default.Delete, destructive = true),
            )
        }

        CustomOverflowMenu(
            expanded = expanded,
            anchorBounds = anchorBounds,
            items = menuItems,
            onDismiss = { expanded = false },
            onItemClick = { item -> onAction(item.id) }
        )
    }
}