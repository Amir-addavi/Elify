package com.addavi.elify.screens

import ScrubSeekBar
import android.app.Activity
import android.media.AudioManager
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.addavi.elify.MainActivity
import com.addavi.elify.R
import com.addavi.elify.components.FavoriteButton
import com.addavi.elify.components.PlayerOptionsSheet
import com.addavi.elify.components.SongCover
import com.addavi.elify.components.VolumeSeekBar
import com.addavi.elify.components.premiumClick
import com.addavi.elify.player.PlayerViewModel
import com.addavi.elify.tools.cleanTrackTitle
import com.addavi.elify.ui.theme.getContrastingIconColor
import com.addavi.elify.ui.theme.vazirFont
import com.addavi.elify.viewmodel.MusicViewModel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.math.abs


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun PlayerScreen(
    playerViewModel: PlayerViewModel,
    navController: NavController,
    viewModel: MusicViewModel
) {

    var songChangeDirection by remember { mutableIntStateOf(0) } // -1 next، 1 previous، 0 هیچی

    var isDragging2 by remember { mutableStateOf(false) }

    val volume by remember { derivedStateOf { playerViewModel.volume } }

    var offsetX2 by remember { mutableFloatStateOf(0f) }

    val scrollState = rememberScrollState()

    val context = LocalContext.current

    val durationMs = 3 * 60 * 1000L // 180_000 ms

    var showOptionsSheet by remember { mutableStateOf(false) }

    // UI-local state (وقتی بعداً ViewModel اضافه می‌کنید اینها از VM خواهند آمد)
    var positionMs by remember { mutableStateOf(0L) }
    var bufferedMs by remember { mutableStateOf(0L) }
    var isPlaying by remember { mutableStateOf(false) }

    LaunchedEffect(isPlaying) {
        // وقتی play شد، هر 500ms پوزیشن افزایش می‌یابد
        // این شبیه‌سازی ساده است؛ در حالت واقعی پوزیشن را از پلیر بگیرید
        while (isActive && isPlaying) {
            delay(500L)
            // جلو بردن پخش
            positionMs = (positionMs + 500L).coerceAtMost(durationMs)
            // buffered کمی جلوتر پیش می‌رود تا جلوتر از position باشه (اما نه بیشتر از duration)
            bufferedMs = (bufferedMs + 700L).coerceAtMost(durationMs)
            // اگر رسید به انتها، متوقف کن
            if (positionMs >= durationMs) {
                isPlaying = false
            }
        }
    }


    // هماهنگی با دکمه های گوشی
    DisposableEffect(Unit) {
        val activity = context as? Activity
        activity?.volumeControlStream = AudioManager.STREAM_MUSIC
        onDispose {
            activity?.volumeControlStream = AudioManager.USE_DEFAULT_STREAM_TYPE
        }
    }

// sync با دکمه های گوشی
    LaunchedEffect(Unit) {
        while (true) {
            delay(100)
            val systemVolume = playerViewModel.audioManager.getStreamVolume(AudioManager.STREAM_MUSIC).toFloat() / playerViewModel.maxVolume
            if (abs(systemVolume - playerViewModel.volume) > 0.01f) {
                playerViewModel.updateVolume(systemVolume)
            }
        }
    }

    val activity = context as? MainActivity

    DisposableEffect(Unit) {
        val activity = context as? Activity
        activity?.volumeControlStream = AudioManager.STREAM_MUSIC
        onDispose {
            activity?.volumeControlStream = AudioManager.USE_DEFAULT_STREAM_TYPE
        }
    }

    val song = playerViewModel.currentSong

    val isLove by viewModel.isFavorite(song?.id ?: 0).collectAsState(initial = false)

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

    val dominantColor by playerViewModel.dominantColor.collectAsState()

    val animatedColor by animateColorAsState(
        targetValue = dominantColor,
        animationSpec = tween(500),
        label = "dominantColor"
    )

    var showHeart by remember { mutableStateOf(false) }

    val heartScale by animateFloatAsState(
        targetValue = if (showHeart) 1f else 0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "heartScale"
    )

    val heartAlpha by animateFloatAsState(
        targetValue = if (showHeart) 1f else 0f,
        animationSpec = tween(300),
        label = "heartAlpha"
    )

    LaunchedEffect(showHeart) {
        if (showHeart) {
            delay(800)
            showHeart = false
        }
    }

    var offsetX by remember { mutableStateOf(0f) }
    var isDragging by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    val animatedOffsetX = remember { Animatable(0f) }
    val coverScale = remember { Animatable(1f) }

    val scaleScreen by animateFloatAsState(
        targetValue = if(showOptionsSheet) 0.85f else 1f,
        animationSpec = tween(500)
    )



    suspend fun animateSongChange(direction: Int, onComplete: () -> Unit) {
        val exitOffset = if (direction == -1) -1200f else 1200f

        // مرحله ۱: کاور فعلی با scale down و swipe خارج میشه
        coroutineScope {
            launch {
                animatedOffsetX.animateTo(
                    targetValue = exitOffset,
                    animationSpec = tween(280, easing = FastOutLinearInEasing)
                )
            }
            launch {
                coverScale.animateTo(
                    targetValue = 0.6f,
                    animationSpec = tween(280, easing = FastOutLinearInEasing)
                )
            }
        }

        onComplete() // اینجا song عوض میشه

        // مرحله ۲: کاور جدید از طرف مخالف با scale up وارد میشه
        animatedOffsetX.snapTo(-exitOffset)
        coverScale.snapTo(0.6f)

        coroutineScope {
            launch {
                animatedOffsetX.animateTo(
                    targetValue = 0f,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessLow
                    )
                )
            }
            launch {
                coverScale.animateTo(
                    targetValue = 1f,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessLow
                    )
                )
            }
        }
    }




    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(animatedColor.copy(alpha = 0.7f))

    ) {
        Crossfade(
            targetState = song?.albumId,
            animationSpec = tween(600, easing = LinearEasing),
            modifier = Modifier.fillMaxSize(),
            label = "backgroundCover"
        ) { albumId ->
            SongCover(
                albumId = albumId ?: 0,
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(16.dp))
                    .alpha(0.7f)
                    .blur(20.dp)
            )
        }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background.copy(alpha = 0.5f))
                .verticalScroll(scrollState)
                .statusBarsPadding()
                .padding(horizontal = 16.dp)
                .graphicsLayer(
                    scaleX = scaleScreen,
                    scaleY = scaleScreen
                )
            ,
            horizontalAlignment = Alignment.CenterHorizontally

        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 15.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(30.dp))
                        .premiumClick(shape = 30) { navController.navigateUp() },
                    contentAlignment = Alignment.Center
                ){
                    Icon(
                        painterResource(R.drawable.back_down_ico),
                        contentDescription = "",
                        tint = MaterialTheme.colorScheme.inversePrimary,
                        modifier = Modifier
                            .size(20.dp)
                    )
                }
                Text(
                    text = "Now Playing",
                    fontSize = 24.sp,
                    fontFamily = vazirFont,
                    color = MaterialTheme.colorScheme.tertiary
                )
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(30.dp))
                        .premiumClick(shape = 30) { showOptionsSheet = true },
                    contentAlignment = Alignment.Center
                ){
                    Icon(
                        painterResource(R.drawable.menu_cube_ico),
                        contentDescription = "",
                        tint = MaterialTheme.colorScheme.inversePrimary,
                        modifier = Modifier
                            .size(20.dp)
                    )
                }
            }

            if (showOptionsSheet) {
                PlayerOptionsSheet(
                    song = song,
                    dominantColor = animatedColor,
                    isLove = isLove,
                    onDismiss = { showOptionsSheet = false },
                    onAction = { action ->
                        when (action) {
                            "add_favorite" -> song?.let { viewModel.addToFavorite(it) }
                            "remove_favorite" -> song?.let { viewModel.removeFromFavorite(it) }
                            "drive_mode" -> playerViewModel.toggleDriveMode()
                        }
                    }
                )
            }

            Spacer(modifier = Modifier.height(20.dp))
            Box(
                modifier = Modifier
                    .width(500.dp)
                    .pointerInput(Unit) {
                        detectHorizontalDragGestures(
                            onDragStart = { isDragging = true },
                            onDragEnd = {
                                isDragging = false
                                val currentOffset = animatedOffsetX.value
                                coroutineScope.launch {
                                    when {
                                        currentOffset < -120 -> {
                                            animateSongChange(direction = -1) {
                                                playerViewModel.nextSong()
                                            }
                                        }
                                        currentOffset > 120 -> {
                                            animateSongChange(direction = 1) {
                                                playerViewModel.previousSong()
                                            }
                                        }
                                        else -> {
                                            animatedOffsetX.animateTo(
                                                0f,
                                                spring(dampingRatio = Spring.DampingRatioMediumBouncy)
                                            )
                                            coverScale.animateTo(1f, spring())
                                        }
                                    }
                                }
                            },
                            onHorizontalDrag = { _, dragAmount ->
                                coroutineScope.launch {
                                    animatedOffsetX.snapTo(animatedOffsetX.value + dragAmount)
                                    val dragRatio = (abs(animatedOffsetX.value) / 600f).coerceIn(0f, 0.4f)
                                    coverScale.snapTo(1f - dragRatio)
                                }
                            }
                        )
                    }
                    .graphicsLayer {
                        translationX = animatedOffsetX.value
                        scaleX = coverScale.value
                        scaleY = coverScale.value
                        alpha = 1f - (abs(animatedOffsetX.value) / 1200f).coerceIn(0f, 0.5f)
                    },
                contentAlignment = Alignment.Center
            ) {
                SongCover(
                    albumId = song?.albumId ?: 0,
                    modifier = Modifier
                        .size(300.dp)
                        .shadow(
                            elevation = 20.dp,
                            spotColor = MaterialTheme.colorScheme.outline,
                            ambientColor = MaterialTheme.colorScheme.outline,
                            shape = RoundedCornerShape(50.dp)
                        )
                        .clip(RoundedCornerShape(50.dp))
                        .pointerInput(song?.id) {
                            detectTapGestures(
                                onDoubleTap = {
                                    if (!isLove) song?.let { viewModel.addToFavorite(it) }
                                    showHeart = true
                                }
                            )
                        }
                )
                Icon(
                    painter = painterResource(R.drawable.heart_fill_ico),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier
                        .size(120.dp)
                        .scale(heartScale)
                        .alpha(heartAlpha)
                )
            }
                Spacer(modifier = Modifier.height(15.dp))
                Column(
                    modifier = Modifier
                        .padding(top = 15.dp)
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(
                            modifier = Modifier
                                .width(250.dp)
                        ) {
                            Text(
                                text = cleanTrackTitle(song?.title.toString()),
                                fontSize = 18.sp,
                                fontFamily = vazirFont,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                color = MaterialTheme.colorScheme.tertiary,
                                modifier = Modifier.basicMarquee(
                                    iterations = Int.MAX_VALUE,
                                    velocity = 40.dp,
                                )
                            )
                            Text(
                                text = song?.artist.toString(),
                                fontFamily = vazirFont,
                                fontSize = 14.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                color = MaterialTheme.colorScheme.inversePrimary,
                                modifier = Modifier.basicMarquee(
                                    iterations = Int.MAX_VALUE,
                                    velocity = 40.dp,
                                )
                            )
                        }

                        Spacer(modifier = Modifier.width(20.dp))
                        FavoriteButton(isLove = isLove, {
                            song?.let {
                                if (isLove) viewModel.removeFromFavorite(it)
                                else viewModel.addToFavorite(it)
                            }
                        }
                        )
                    }

                    ScrubSeekBar(
                        modifier = Modifier.fillMaxWidth(),
                        progressMs = positionMs,
                        bufferedMs = bufferedMs,
                        durationMs = durationMs,
                        playedColor = animatedColor,
                        thumbColor = animatedColor,
                        onSeekStart = {
                            // هنگام شروع اسکراب معمولاً پلِیر را pause می‌کنیم تا UX بهتر شود
                            isPlaying = true
                        },
                        onSeek = { ms ->
                            // هنگام drag می‌توان موقعیت محلی را به‌روزرسانی کنیم (اینجا آنی اعمال می‌کنیم)
                            positionMs = ms.coerceIn(0L, durationMs)
                        },
                        onSeekEnd = { ms ->
                            // کاربر اسکراب را تمام کرد؛ موقعیت را نهایی می‌کنیم و بسته به نیاز resume می‌کنیم
                            positionMs = ms.coerceIn(0L, durationMs)
                            // برای شبیه‌سازی: بعد از seek، دوباره پخش نمی‌کنیم مگر کاربر بزند Play
                        }
                    )
                    Row(
                        modifier = Modifier
                            .width(220.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(30.dp))
                                .padding(5.dp)
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.back_player_fill_ico),
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.inversePrimary,
                                modifier = Modifier.size(40.dp).premiumClick(
                                    shape = 30,
                                    scaleDown = 0.8f,
                                    onClick = {
                                        coroutineScope.launch {
                                            animateSongChange(direction = 1) {
                                                playerViewModel.previousSong()
                                            }
                                        }
                                    }
                                )
                                    .padding(10.dp)
                            )
                        }
                        Box(
                            modifier = Modifier
                                .shadow(
                                    elevation = 20.dp,
                                    spotColor = animatedColor,
                                    ambientColor = animatedColor,
                                    shape = RoundedCornerShape(30.dp)
                                )
                                .premiumClick(
                                    shape = 30,
                                    scaleDown = 0.85f,
                                    onClick = {}
                                )
                                .clip(RoundedCornerShape(30.dp))
                                .background(animatedColor)
                                .padding(5.dp)
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.pause_player_ico),
                                contentDescription = null,
                                tint = getContrastingIconColor(animatedColor),
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
                                tint = MaterialTheme.colorScheme.inversePrimary,
                                modifier = Modifier.size(40.dp).premiumClick(
                                    shape = 30,
                                    scaleDown = 0.8f,
                                    onClick = {
                                        coroutineScope.launch {
                                            animateSongChange(direction = -1) {
                                                playerViewModel.nextSong()
                                            }
                                        }
                                    }
                                )
                                    .padding(10.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(30.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth().widthIn(max = 500.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.volume_low_ico),
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.inversePrimary,
                            modifier = Modifier.size(18.dp)
                        )
                        VolumeSeekBar(
                            volume = volume,
                            onVolumeChange = { playerViewModel.updateVolume(it) },
                            modifier = Modifier
                                .width(230.dp)
                                .padding(horizontal = 8.dp)
                        )
                        Icon(
                            painter = painterResource(R.drawable.volume_high_ico),
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.inversePrimary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }

