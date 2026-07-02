package com.addavi.elify.screens

import android.app.Activity
import android.content.pm.ActivityInfo
import android.os.Build
import android.view.View
import android.view.WindowInsets
import android.view.WindowInsetsController
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.AbsoluteAlignment
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.addavi.elify.R
import com.addavi.elify.components.DriveSeekBar
import com.addavi.elify.components.DriveVolumeSeekBar
import com.addavi.elify.components.SongCover
import com.addavi.elify.player.PlayerViewModel
import com.addavi.elify.ui.theme.vazirFont
import com.addavi.elify.viewmodel.MusicViewModel
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@Composable
fun DriveScreen(playerViewModel: PlayerViewModel , viewModel: MusicViewModel) {

    var volume by remember { mutableStateOf(0.7f) }

    val isDriveMode = true
    val context = LocalContext.current

    val primary = Color(0xFFFF8828)
    val back = Color(0xFFFFF3E8)
    val text = Color(0xFF262626)
    val text2 = Color(0xFFFFFFFF)
    val desc = Color(0xFF585858)
    val desc2 = Color(0xFFFFFAF4)

    LaunchedEffect(Unit) {
        val activity = context as? Activity
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            activity?.window?.insetsController?.let {
                it.hide(WindowInsets.Type.statusBars() or WindowInsets.Type.navigationBars())
                it.systemBarsBehavior = WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            }
        } else {
            @Suppress("DEPRECATION")
            activity?.window?.decorView?.systemUiVisibility = (
                    View.SYSTEM_UI_FLAG_FULLSCREEN or
                            View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
                            View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    )
        }
    }

    var currentPosition by remember { mutableStateOf(30000L) }
    val duration = 210000L

    LaunchedEffect(Unit) {
        while (true) {
            delay(1000)
            if (currentPosition < duration) {
                currentPosition += 1000
            }
        }
    }

    var currentTime by remember { mutableStateOf("") }
    var currentDate by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        while (true) {
            val now = Calendar.getInstance()
            val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
            val dateFormat = SimpleDateFormat("EEEE، dd MMMM yyyy", Locale("fa"))
            currentTime = timeFormat.format(now.time)
            currentDate = dateFormat.format(now.time)
            delay(1000)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(back)
    ) {
        Image(
            painter = painterResource(R.drawable.background_drive_player),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxSize()
                .alpha(0.8f)
        )
        Row(
            modifier = Modifier
                .padding(vertical = 35.dp , horizontal = 20.dp)
                .fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Absolute.Left
        ) {
            Column(
                modifier = Modifier
                    .padding(horizontal = 20.dp)
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = AbsoluteAlignment.Left
            ) {
                Icon(
                    painter = painterResource(R.drawable.on_off_ico),
                    contentDescription = null,
                    tint = text2,
                    modifier = Modifier.size(35.dp)
                )
                Spacer(modifier = Modifier.height(30.dp))
                Text(
                    text = currentTime,
                    color = text2,
                    fontSize = 62.sp,
                    fontFamily = vazirFont
                )
                Text(
                    text = currentDate,
                    color = desc2,
                    fontSize = 25.sp,
                    fontFamily = vazirFont
                )
            }
            Spacer(modifier = Modifier.width(20.dp))
            Column(
                modifier = Modifier
                    .size(width = 400.dp , height = 300.dp)
                    .clip(RoundedCornerShape(40.dp))
                    .background(back.copy(alpha = 0.7f))
                    .padding(vertical = 5.dp , horizontal = 15.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = AbsoluteAlignment.Left
            ) {
                Text(
                    text = "Now Playing",
                    fontSize = 14.sp,
                    fontFamily = vazirFont,
                    color = text
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Absolute.Left
                ) {
                    SongCover(0 , modifier = Modifier.size(120.dp).clip(RoundedCornerShape(20.dp)))
                    Spacer(modifier = Modifier.width(30.dp))
                    Column {
                        Text(
                            text = "Parandey E Mohajer",
                            fontSize = 20.sp,
                            fontFamily = vazirFont,
                            color = text
                        )
                        Text(
                            text = "siavah ghomayshi",
                            fontSize = 15.sp,
                            fontFamily = vazirFont,
                            color = desc
                        )
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
                DriveSeekBar(
                    currentPosition = currentPosition,
                    duration = duration,
                    onSeek = { newPosition ->
                        currentPosition = newPosition
                    },
                    primaryColor = primary,
                    modifier = Modifier.padding(horizontal = 8.dp).fillMaxWidth()
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                    ,
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(30.dp))
                            .padding(5.dp)
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.playlist_fill_ico),
                            contentDescription = null,
                            tint = desc,
                            modifier = Modifier.size(23.dp).clickable(
                                onClick = {}
                            )
                        )
                    }
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(30.dp))
                            .padding(5.dp)
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.back_player_fill_ico),
                            contentDescription = null,
                            tint = desc,
                            modifier = Modifier.size(23.dp).clickable(
                                onClick = {}
                            )
                        )
                    }
                    Box(
                        modifier = Modifier
                            .shadow(
                                elevation = 20.dp,
                                spotColor = primary,
                                ambientColor = primary,
                                shape = RoundedCornerShape(50.dp)
                            )
                            .clip(RoundedCornerShape(30.dp))
                            .background(primary)
                            .padding(3.dp)
                    ) {
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
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.next_player_fill_ico),
                            contentDescription = null,
                            tint = desc,
                            modifier = Modifier.size(23.dp).clickable(
                                onClick = {}
                            )
                        )
                    }
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(30.dp))
                            .padding(5.dp)
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.repeat_ico),
                            contentDescription = null,
                            tint = desc,
                            modifier = Modifier.size(23.dp).clickable(
                                onClick = {}
                            )
                        )
                    }
                }
            }
            Spacer(Modifier.width(8.dp))
            Column(
                modifier = Modifier
                    .size(width = 50.dp , 300.dp)
                    .clip(RoundedCornerShape(40.dp))
                    .background(back.copy(alpha = 0.7f)),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                DriveVolumeSeekBar(
                    volume = volume,
                    onVolumeChange = { volume = it },
                    primaryColor = primary,
                    modifier = Modifier
                        .width(30.dp)
                        .fillMaxHeight(0.6f)
                )
                Spacer(Modifier.height(20.dp))
                Icon(
                    painter = painterResource(R.drawable.volume_high_ico),
                    contentDescription = null,
                    tint = text,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}
