package com.addavi.elify.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.addavi.elify.ui.theme.vazirFont
import com.addavi.elify.viewmodel.MenuItemSpec
import com.addavi.elify.viewmodel.Song

@Composable
fun SongBottomSheet(
    song: Song,
    menuItems: List<MenuItemSpec>,
    onDismiss: () -> Unit,
    onAction: (String) -> Unit
) {
    CustomBottomSheet(onDismiss = onDismiss) {

        val scrollState = rememberScrollState()

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
                .verticalScroll(scrollState)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                SongCover(
                    albumId = song.albumId,
                    modifier = Modifier
                        .size(55.dp)
                        .clip(RoundedCornerShape(12.dp))
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = song.title,
                        fontFamily = vazirFont,
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.tertiary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = song.artist,
                        fontFamily = vazirFont,
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onTertiary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            HorizontalDivider(
                modifier = Modifier.padding(horizontal = 16.dp),
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
            )

            Spacer(modifier = Modifier.height(8.dp))

            menuItems.forEach { item ->
                Row(
                    modifier = Modifier
                        .padding(horizontal = 8.dp)
                        .fillMaxWidth()
                        .alpha(if (item.enabled) 1f else 0.4f)
                        .premiumClick(
                            shape = 10,
                            scaleDown = 0.95f,
                            onClick = {
                                onAction(item.id)
                                onDismiss()
                            }
                        )
                    .padding(horizontal = 6.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    item.icon?.let {
                        Icon(
                            painter = painterResource(it),
                            contentDescription = null,
                            tint = if (item.destructive) MaterialTheme.colorScheme.error
                            else MaterialTheme.colorScheme.surfaceTint,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                    }
                    Text(
                        text = item.title,
                        fontFamily = vazirFont,
                        fontSize = 15.sp,
                        color = if (item.destructive) MaterialTheme.colorScheme.error
                        else MaterialTheme.colorScheme.surfaceTint
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}