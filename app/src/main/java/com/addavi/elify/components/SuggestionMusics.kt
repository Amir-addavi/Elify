package com.addavi.elify.components

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.snapping.SnapPosition
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.boundsInParent
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.addavi.elify.R
import com.addavi.elify.tools.cleanTrackTitle
import com.addavi.elify.ui.theme.vazirFont
import com.addavi.elify.viewmodel.MusicViewModel
import com.addavi.elify.viewmodel.Song
import kotlin.math.abs

@Composable
fun SuggestionMusics(viewModel: MusicViewModel) {

    val randomSongs by viewModel.randomSongs.collectAsStateWithLifecycle()

    val listState = rememberLazyListState()

    var boxCenterX by remember { mutableFloatStateOf(0f) }

    val flingBehavior = rememberSnapFlingBehavior(
        lazyListState = listState,
        snapPosition = SnapPosition.Start
    )
    Box(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .size(width = 160.dp , height = 150.dp)
                .onGloballyPositioned {
                    val bounds = it.boundsInParent()
                    boxCenterX = bounds.left + bounds.width / 2
                }
        )
        LazyRow(
            state = listState,
            flingBehavior = flingBehavior,
            contentPadding = PaddingValues(horizontal = 15.dp)
        ) {
            itemsIndexed(randomSongs) { index, song ->

                val layoutInfo = listState.layoutInfo
                val itemInfo = layoutInfo.visibleItemsInfo
                    .firstOrNull { it.index == index }

                val scale = if (itemInfo == null) {
                    1f
                } else {

                    val itemCenter =
                        itemInfo.offset + itemInfo.size / 2f

                    val distance =
                        abs(itemCenter - boxCenterX)

                    val maxDistance = 400f

                    val rawScale = 1.15f - (distance / maxDistance)

                    rawScale.coerceIn(0.85f, 1.2f)
                }

                Box(
                    modifier = Modifier.graphicsLayer {
                        scaleX = scale
                        scaleY = scale
                    }
                ) {
                    SuggestionItem(song , index)
                }
            }
        }
    }
}

@Composable
fun SuggestionItem(song: Song , Index : Int) {
    Column(
        modifier = Modifier.width(150.dp),
        horizontalAlignment = Alignment.Start
    ) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(20.dp)),
            contentAlignment = Alignment.BottomEnd
        ) {
            AsyncImage(
                model = song.artworkUri,
                contentDescription = null,
                fallback = painterResource(R.drawable.default_cover),
                error = painterResource(R.drawable.default_cover),
                contentScale = ContentScale.Crop,
                modifier = Modifier.size(120.dp).clip(RoundedCornerShape(20.dp))
            )
            Box(
                modifier = Modifier
                    .padding(8.dp)
                    .size(30.dp)
                    .clip(RoundedCornerShape(30.dp))
                    .background(MaterialTheme.colorScheme.primary)
                    .padding(5.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(R.drawable.play_player_ico),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.surface
                )
            }
        }
        Spacer(modifier = Modifier.height(3.dp))
        Text(
            text = cleanTrackTitle(song.title),
            fontFamily = vazirFont,
            maxLines = 1,
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.tertiary,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(horizontal = 2.dp),
            lineHeight = 12.sp
        )
        Text(
            text = song.artist ?: "Unknown artist",
            fontFamily = vazirFont,
            maxLines = 1,
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onTertiary,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(horizontal = 2.dp).offset(y = (-2).dp),
            lineHeight = 12.sp
        )
    }
}
