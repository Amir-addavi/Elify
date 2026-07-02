package com.addavi.elify.components


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.addavi.elify.R
import com.addavi.elify.tools.cleanTrackTitle
import com.addavi.elify.ui.theme.vazirFont
import com.addavi.elify.viewmodel.Song

@Composable
fun PlayerOptionsSheet(
    song: Song?,
    dominantColor: Color,
    isLove: Boolean,
    onDismiss: () -> Unit,
    onAction: (String) -> Unit
) {
    CustomBottomSheet(onDismiss = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    MaterialTheme.colorScheme.surface
                )
                .padding(vertical = 20.dp)
        ) {
            // هدر با preview بزرگ کاور
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .padding(bottom = 20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .shadow(
                            elevation = 12.dp,
                            spotColor = MaterialTheme.colorScheme.outline,
                            ambientColor = MaterialTheme.colorScheme.outline,
                            shape = RoundedCornerShape(16.dp)
                        )
                        .clip(RoundedCornerShape(16.dp))
                ) {
                    SongCover(
                        albumId = song?.albumId ?: 0,
                        modifier = Modifier.fillMaxSize()
                    )
                }
                Spacer(modifier = Modifier.width(14.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = song?.title?.let { cleanTrackTitle(it) } ?: "",
                        fontFamily = vazirFont,
                        fontSize = 17.sp,
                        color = MaterialTheme.colorScheme.tertiary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = song?.artist ?: "",
                        fontFamily = vazirFont,
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onTertiary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            HorizontalDivider(
                modifier = Modifier.padding(horizontal = 20.dp),
                color = MaterialTheme.colorScheme.background
            )

            Spacer(modifier = Modifier.height(12.dp))

            val options = remember(isLove) {
                listOf(
                    PlayerOption("add_to_playlist", "Add to Playlist", R.drawable.playlist_ico),
                    PlayerOption(
                        if (isLove) "remove_favorite" else "add_favorite",
                        if (isLove) "Remove Favorite" else "Add Favorite",
                        if (isLove) R.drawable.heart_fill_ico else R.drawable.heart_ico
                    ),
                    PlayerOption("share", "Share", R.drawable.share_ico),
                    PlayerOption("sleep_timer", "Sleep Timer", R.drawable.search_ico),
                    PlayerOption("info", "Song Info", R.drawable.info_ico),
                    PlayerOption("drive_mode", "Drive Mode", R.drawable.bell_ico)
                )
            }

            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(options, key = { it.id }) { option ->
                    PlayerOptionItem(
                        option = option,
                        dominantColor = dominantColor,
                        onClick = {
                            onAction(option.id)
                            onDismiss()
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

data class PlayerOption(
    val id: String,
    val label: String,
    val icon: Int
)

@Composable
fun PlayerOptionItem(
    option: PlayerOption,
    dominantColor: Color,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .premiumClick(scaleDown = 0.94f) { onClick() }
            .background(MaterialTheme.colorScheme.background)
            .padding(vertical = 16.dp, horizontal = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            painter = painterResource(option.icon),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.tertiary,
            modifier = Modifier.size(26.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = option.label,
            fontFamily = vazirFont,
            fontSize = 11.sp,
            color = MaterialTheme.colorScheme.tertiary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center
        )
    }
}