
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.semantics.ProgressBarRangeInfo
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.progressBarRangeInfo
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.addavi.elify.ui.theme.vazirFont
import kotlin.math.max

@Composable
fun ScrubSeekBar(
    modifier: Modifier = Modifier,
    progressMs: Long,           // current playback position in ms
    bufferedMs: Long = 0L,      // buffered position in ms
    durationMs: Long,           // total duration in ms (0 if unknown)
    onSeek: (Long) -> Unit,     // called continuously or on drag end depending on use
    onSeekStart: (() -> Unit)? = null,
    onSeekEnd: ((Long) -> Unit)? = null,
    playedColor: Color,
    bufferedColor: Color = MaterialTheme.colorScheme.surface,
    trackColor: Color = MaterialTheme.colorScheme.background,
    thumbColor: Color,
    thumbRadius: Dp = 5.5.dp,
    trackHeight: Dp = 4.dp,
    showTimeLabels: Boolean = true
) {
    // states
    val haptic = LocalHapticFeedback.current
    val density = LocalDensity.current
    var widthPx by remember { mutableStateOf(1f) }
    var dragging by remember { mutableStateOf(false) }
    var dragPosPx by remember { mutableStateOf(0f) } // current drag x in px
    val scope = rememberCoroutineScope()

    // fraction of played from source (if not dragging)
    val playedFraction = remember(progressMs, durationMs) {
        if (durationMs <= 0L) 0f else progressMs.toFloat() / durationMs.toFloat()
    }

    // animated fraction to smooth small jumps when not dragging
    val animatedPlayedFraction by animateFloatAsState(targetValue = if (!dragging) playedFraction else (dragPosPx / widthPx).coerceIn(0f, 1f))

    // compute values for draw
    val bufferedFraction = remember(bufferedMs, durationMs) {
        if (durationMs <= 0L) 0f else bufferedMs.toFloat() / durationMs.toFloat()
    }

    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(thumbRadius * 2 + 24.dp)
                .pointerInput(Unit) {
                    detectTapGestures { offset ->
                        // tap to seek
                        val x = offset.x.coerceIn(0f, size.width.toFloat())
                        val frac = x / size.width.toFloat()
                        val newMs = ((durationMs * frac).toLong()).coerceIn(0L, durationMs)
                        onSeekStart?.invoke()
                        onSeek(newMs)
                        onSeekEnd?.invoke(newMs)
                    }
                }
                .pointerInput(Unit) {
                    detectDragGestures(
                        onDragStart = { offset ->
                            dragging = true
                            widthPx = size.width.toFloat()
                            dragPosPx = offset.x.coerceIn(0f, widthPx)
                            onSeekStart?.invoke()
                            // small haptic
                            haptic.performHapticFeedback(androidx.compose.ui.hapticfeedback.HapticFeedbackType.LongPress)
                        },
                        onDrag = { change, _ ->
                            widthPx = size.width.toFloat()
                            dragPosPx = change.position.x.coerceIn(0f, widthPx)
                            // continuous updates (optional) — you can throttle onSeek if needed
                            if (durationMs > 0L) {
                                val frac = (dragPosPx / widthPx).coerceIn(0f, 1f)
                                val newMs = (durationMs * frac).toLong()
                                onSeek(newMs)
                            }
                        },
                        onDragEnd = {
                            dragging = false
                            if (durationMs > 0L) {
                                val frac = (dragPosPx / widthPx).coerceIn(0f, 1f)
                                val newMs = (durationMs * frac).toLong()
                                onSeekEnd?.invoke(newMs)
                                // small confirming haptic
                                haptic.performHapticFeedback(androidx.compose.ui.hapticfeedback.HapticFeedbackType.TextHandleMove)
                            } else {
                                onSeekEnd?.invoke(0L)
                            }
                        },
                        onDragCancel = {
                            dragging = false
                        }
                    )
                }
                .onSizeChanged {
                    widthPx = it.width.toFloat()
                }
                .semantics {
                    if (durationMs > 0L) {
                        val current =
                            (if (dragging) (dragPosPx / widthPx) else playedFraction).coerceIn(
                                0f,
                                1f
                            )
                        val curMs = (current * durationMs).toLong()
                        contentDescription =
                            "Timeline, position ${formatTimeMs(curMs)} of ${formatTimeMs(durationMs)}"
                        // این خط صحیح است — نام property صحیح progressBarRangeInfo
                        progressBarRangeInfo = ProgressBarRangeInfo(current, 0f..1f)

                        // در صورت نیاز می‌توانید action برای تنظیم مقدار اضافه کنید:
                        // customActions = listOf(CustomAccessibilityAction(label = "Set position") { true })
                    }
                }
        ) {
            // Draw the track and bars using Canvas
            Canvas(modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.Center)
                .height(trackHeight)
            ) {
                val trackY = size.height / 2f
                val radius = with(density) { (trackHeight / 2).toPx() }

                // Background track
                drawRoundRect(
                    color = trackColor,
                    topLeft = Offset(0f, trackY - radius),
                    size = androidx.compose.ui.geometry.Size(size.width, radius * 2),
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(radius, radius)
                )

                // Buffered
                val bufferedX = (bufferedFraction.coerceIn(0f, 1f) * size.width)
                drawRoundRect(
                    color = bufferedColor,
                    topLeft = Offset(0f, trackY - radius),
                    size = androidx.compose.ui.geometry.Size(bufferedX, radius * 2),
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(radius, radius)
                )

                // Played
                val playedX = (animatedPlayedFraction.coerceIn(0f, 1f) * size.width)
                drawRoundRect(
                    color = playedColor,
                    topLeft = Offset(0f, trackY - radius),
                    size = androidx.compose.ui.geometry.Size(playedX, radius * 2),
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(radius, radius)
                )

                // Divider stroke for subtle glossy effect (optional)
                drawLine(
                    color = Color.White.copy(alpha = 0.06f),
                    start = Offset(0f, trackY - radius),
                    end = Offset(size.width, trackY - radius),
                    strokeWidth = 1f
                )
            }

            // Thumb
            val thumbX = (animatedPlayedFraction.coerceIn(0f, 1f) * widthPx)
            val thumbDp = with(density) { thumbRadius.toPx() }

            Box(
                modifier = Modifier
                    .offset { IntOffset((thumbX - thumbDp).toInt(), 0) }
                    .size(thumbRadius * 2)
                    .align(Alignment.CenterStart),
                contentAlignment = Alignment.Center
            ) {
                // shadow + thumb circle
                Box(
                    modifier = Modifier
                        .size(thumbRadius * 2)
                        .shadow(6.dp, CircleShape)
                        .clip(CircleShape)
                        .background(thumbColor)
                )
            }

            // Bubble preview while dragging
            if (dragging && durationMs > 0L) {
                val currentMs = ((dragPosPx / max(1f, widthPx)) * durationMs).toLong().coerceIn(0L, durationMs)
                Box(
                    modifier = Modifier
                        .offset { IntOffset((dragPosPx - 48f).toInt(), -48) }
                        .size(width = 96.dp, height = 36.dp)
                        .align(Alignment.TopStart),
                    contentAlignment = Alignment.Center
                ) {
                    Surface(
                        tonalElevation = 2.dp,
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.9f))
                    ) {
                        Text(
                            text = formatTimeMs(currentMs),
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                            fontWeight = FontWeight.SemiBold,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }

        if (showTimeLabels) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 3.dp, start = 4.dp, end = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = formatTimeMs(if (dragging && durationMs > 0L) ((dragPosPx / max(1f, widthPx)) * durationMs).toLong().coerceIn(0L, durationMs) else progressMs),
                    color = MaterialTheme.colorScheme.inversePrimary,
                    fontSize = 13.sp,
                    fontFamily = vazirFont
                )
                Text(
                    text = if (durationMs > 0L) formatTimeMs(durationMs) else "--:--",
                    color = MaterialTheme.colorScheme.inversePrimary,
                    fontSize = 13.sp,
                    fontFamily = vazirFont
                )
            }
        }
    }
}

// helper
private fun formatTimeMs(ms: Long): String {
    if (ms <= 0L) return "00:00"
    val totalSeconds = (ms / 1000).toInt()
    val seconds = totalSeconds % 60
    val minutes = (totalSeconds / 60) % 60
    val hours = totalSeconds / 3600
    return if (hours > 0) String.format("%d:%02d:%02d", hours, minutes, seconds)
    else String.format("%02d:%02d", minutes, seconds)
}

// Semantics extension for setProgress action