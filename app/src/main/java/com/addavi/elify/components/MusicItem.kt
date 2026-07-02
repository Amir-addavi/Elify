package com.addavi.elify.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.addavi.elify.tools.cleanTrackTitle
import com.addavi.elify.ui.theme.vazirFont
import com.addavi.elify.viewmodel.MenuItemSpec
import com.addavi.elify.viewmodel.Song


@Composable
fun SongItem(
    song: Song,
    onClick: (Song) -> Unit,
    menuItems: List<MenuItemSpec>,
    onAction: (String) -> Unit = {}
) {


    Row(
        modifier = Modifier
            .padding(vertical = 5.dp)
            .fillMaxWidth()
            .premiumClick(
                shape = 20,
                scaleDown = 0.97f,
                onClick = {onClick(song)}
            )
            .clip(RoundedCornerShape(20.dp))
            .background(MaterialTheme.colorScheme.background.copy(alpha = 0.3f))
            .padding(vertical = 5.dp)
            .padding(start = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(54.dp)
                .clip(RoundedCornerShape(17.dp))
                .background(MaterialTheme.colorScheme.surface),
            contentAlignment = Alignment.Center
        ) {

            SongCover(
                albumId = song.albumId,
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(15.dp)),

            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(
            modifier = Modifier.weight(1f)
        ) {

            Text(
                text = cleanTrackTitle(song.title),
                maxLines = 1,
                fontFamily = vazirFont,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.tertiary
            )

            Text(
                text = song.artist ?: "Unknown artist",
                fontFamily = vazirFont,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onTertiary
            )
        }
        SongOverflowButton(
            song = song,
            menuItems = menuItems,
            onAction = onAction
        )

    }
}




