package com.addavi.elify.screens

import NeonGlowSeekBar
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.addavi.elify.R
import com.addavi.elify.player.PlayerViewModel
import com.addavi.elify.tools.cleanTrackTitle
import com.addavi.elify.ui.theme.extractDominantColorFromUri
import com.addavi.elify.ui.theme.vazirFont
import kotlinx.coroutines.delay


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun PlayerScreen(
    playerViewModel: PlayerViewModel,
    navController: NavController
) {

    var currentPosition by remember { mutableStateOf(45000L) } // 45 seconds
    val duration = 180000L

    LaunchedEffect(Unit) {
        while (true) {
            delay(1000)
            if (currentPosition < duration) {
                currentPosition += 1000
            }
        }
    }


    val song = playerViewModel.currentSong

    val context = LocalContext.current

    var dominantColor by remember {
        mutableStateOf(Color.Black)
    }

    val animatedColor by animateColorAsState(
        targetValue = dominantColor,
        animationSpec = tween(700)
    )

    val titleColor = Color(0xFFFFFFFF)
    val description = Color(0xFFA7A3A3)


    LaunchedEffect(song?.id) {

        dominantColor =
            extractDominantColorFromUri(
                context,
                song?.artworkUri
            )
    }


    Box(
        modifier = Modifier
        .fillMaxSize()
    ){
        AsyncImage(
            model = song?.artworkUri,
            contentDescription = null,
            error = painterResource(R.drawable.logo_dark),
            fallback = painterResource(R.drawable.logo_dark),
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxSize()
                .alpha(0.7f)
                .blur(20.dp)
        )
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        MaterialTheme.colorScheme.background.copy(alpha = 0.2f),
                        MaterialTheme.colorScheme.background
                    )
                )
            )
            .statusBarsPadding()
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally

    ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 15.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Icon(
                    painter = painterResource(R.drawable.back_down_ico),
                    contentDescription = "",
                    tint = MaterialTheme.colorScheme.onTertiary,
                    modifier = Modifier
                        .size(40.dp)
                        .shadow(
                            elevation = 12.dp,
                            spotColor = MaterialTheme.colorScheme.outline,
                            ambientColor = MaterialTheme.colorScheme.outline,
                            shape = RoundedCornerShape(12.dp)
                        )
                        .clip(RoundedCornerShape(30.dp))
                        .clickable(
                            onClick = {
                                navController.navigateUp()
                            }
                        )
                        .background(MaterialTheme.colorScheme.surface)
                        .padding(10.dp)
                )
                Text(
                    text = "Now Playing",
                    fontSize = 24.sp,
                    fontFamily = vazirFont,
                    color = MaterialTheme.colorScheme.tertiary
                )
                Icon(
                    imageVector = Icons.Rounded.MoreVert,
                    contentDescription = "",
                    tint = MaterialTheme.colorScheme.onTertiary,
                    modifier = Modifier
                        .size(40.dp)
                        .shadow(
                            elevation = 12.dp,
                            spotColor = MaterialTheme.colorScheme.outline,
                            ambientColor = MaterialTheme.colorScheme.outline,
                            shape = RoundedCornerShape(12.dp)
                        )
                        .clip(RoundedCornerShape(30.dp))
                        .background(MaterialTheme.colorScheme.surface)
                        .padding(7.dp)
                )
            }
            Spacer(modifier = Modifier.height(20.dp))
            Box(
                modifier = Modifier
                    .size(300.dp)
                    .shadow(
                        elevation = 20.dp,
                        spotColor = MaterialTheme.colorScheme.outline,
                        ambientColor = MaterialTheme.colorScheme.outline,
                        shape = RoundedCornerShape(50.dp)
                    )
                    .clip(RoundedCornerShape(50.dp))
                    .background(MaterialTheme.colorScheme.background),
                contentAlignment = Alignment.Center
            ) {
                AsyncImage(
                    model = song?.artworkUri,
                    contentDescription = null,
                    error = painterResource(R.drawable.logo_dark),
                    fallback = painterResource(R.drawable.logo_dark),
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(300.dp)
                        .clip(RoundedCornerShape(50.dp))
                )
            }
            Spacer(modifier = Modifier.height(15.dp))
            Column(
                modifier = Modifier
                    .padding(top = 8.dp)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .padding(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row {
                    Text(
                        text = cleanTrackTitle(song?.title.toString()),
                        fontSize = 22.sp,
                        fontFamily = vazirFont,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        color = MaterialTheme.colorScheme.tertiary,
                        modifier = Modifier.basicMarquee(
                            iterations = Int.MAX_VALUE,
                            velocity = 40.dp,
                        )
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Row {
                    Text(
                        text = song?.artist.toString(),
                        fontFamily = vazirFont,
                        fontSize = 15.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        color = description,
                        modifier = Modifier.basicMarquee(
                            iterations = Int.MAX_VALUE,
                            velocity = 40.dp,
                        )
                    )
                }
                NeonGlowSeekBar(
                    currentPosition = currentPosition,
                    duration = duration,
                    onSeek = { newPosition ->
                        currentPosition = newPosition
                        println("Seeked to: $newPosition")
                    },
                    neonColor = MaterialTheme.colorScheme.primary,
                    glowIntensity = 0.9f
                )
                Row(
                    modifier = Modifier
                        .width(180.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(30.dp))
                            .padding(5.dp)
                    ){
                        Icon(
                            painter = painterResource(R.drawable.back_player_ico),
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onTertiary,
                            modifier = Modifier.size(23.dp)
                        )
                    }
                    Box(
                        modifier = Modifier
                            .shadow(
                                elevation = 20.dp,
                                spotColor = MaterialTheme.colorScheme.primary,
                                ambientColor = MaterialTheme.colorScheme.primary,
                                shape = RoundedCornerShape(50.dp)
                            )
                            .clip(RoundedCornerShape(30.dp))
                            .background(MaterialTheme.colorScheme.primary)
                            .padding(5.dp)
                    ){
                        Icon(
                            painter = painterResource(R.drawable.pause_player_ico),
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.surface,
                            modifier = Modifier.size(48.dp)
                        )
                    }
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(30.dp))
                            .padding(5.dp)
                    ){
                        Icon(
                            painter = painterResource(R.drawable.next_player_ico),
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onTertiary,
                            modifier = Modifier.size(23.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(30.dp))
            }
        }
    }
}
