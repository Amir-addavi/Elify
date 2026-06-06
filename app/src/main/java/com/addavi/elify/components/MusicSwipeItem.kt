package com.addavi.elify.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import com.addavi.elify.viewmodel.Song

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MusicSwipeItem(
    song: Song,
    onDelete: (Song) -> Unit,
    modifier: Modifier = Modifier,
    onClick: (Song) -> Unit
) {
    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = { dismissValue ->
            if (dismissValue == SwipeToDismissBoxValue.EndToStart) {
                onDelete(song)
                true
            } else {
                false
            }
        }
    )

    val isSwiping = dismissState.targetValue != SwipeToDismissBoxValue.Settled

    val itemBgColor by animateColorAsState(
        targetValue = if (isSwiping)
            MaterialTheme.colorScheme.surface
        else MaterialTheme.colorScheme.background,
        label = "color_anim"
    )

    SwipeToDismissBox(
        modifier = modifier.padding(vertical = 5.dp), // پدینگ کمتر برای چسبندگی بهتر لیست
        state = dismissState,
        enableDismissFromStartToEnd = false, // فقط اسوایپ به چپ برای حذف
        backgroundContent = {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color.Red.copy(alpha = 0.15f)),
                contentAlignment = Alignment.CenterEnd
            ) {
                Box(
                    modifier = Modifier
                        .padding(horizontal = 5.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color.Transparent)
                        .padding(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete",
                        tint = Color.Red
                    )
                }
            }
        },
        content = {
                SongItem(song = song, onClick = onClick , background = itemBgColor)
        }
    )
}
