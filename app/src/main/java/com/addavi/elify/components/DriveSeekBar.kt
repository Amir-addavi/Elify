package com.addavi.elify.components


import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.addavi.elify.ui.theme.vazirFont

@Composable
fun DriveSeekBar(
    currentPosition: Long,
    duration: Long,
    onSeek: (Long) -> Unit,
    primaryColor: Color,
    modifier: Modifier = Modifier
) {
    var isDragging by remember { mutableStateOf(false) }
    var dragPosition by remember { mutableStateOf(0f) }
    var sliderWidth by remember { mutableStateOf(0) }

    val progress = if (duration > 0) currentPosition.toFloat() / duration.toFloat() else 0f
    val displayProgress = if (isDragging) dragPosition else progress

    Column(modifier = modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(10.dp)
                .onGloballyPositioned { sliderWidth = it.size.width }
                .pointerInput(Unit) {
                    detectHorizontalDragGestures(
                        onDragStart = { isDragging = true },
                        onDragEnd = {
                            onSeek((dragPosition * duration).toLong())
                            isDragging = false
                        },
                        onHorizontalDrag = { _, dragAmount ->
                            dragPosition =
                                (dragPosition + dragAmount / sliderWidth).coerceIn(0f, 1f)
                        }
                    )
                },
            contentAlignment = Alignment.CenterStart
        ) {
            // پس زمینه
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(3.dp)
                    .clip(RoundedCornerShape(50))
                    .background(Color.Black.copy(alpha = 0.35f))
            )

            // پروگرس
            Box(
                modifier = Modifier
                    .fillMaxWidth(displayProgress)
                    .height(3.dp)
                    .shadow(
                        elevation = 8.dp,
                        spotColor = primaryColor,
                        ambientColor = primaryColor,
                        shape = RoundedCornerShape(50)
                    )
                    .clip(RoundedCornerShape(50))
                    .background(
                        Brush.horizontalGradient(
                            listOf(
                                primaryColor.copy(alpha = 0.7f),
                                primaryColor
                            )
                        )
                    )
            )

            // دایره
            Box(
                modifier = Modifier
                    .padding(start = with(LocalDensity.current) {
                        (displayProgress * sliderWidth).toDp() - 5.dp
                    }.coerceAtLeast(0.dp))
                    .size(10.dp)
                    .shadow(
                        elevation = 8.dp,
                        spotColor = primaryColor,
                        ambientColor = primaryColor,
                        shape = RoundedCornerShape(50)
                    )
                    .clip(RoundedCornerShape(50))
                    .background(primaryColor)
            )
        }

        Spacer(modifier = Modifier.height(6.dp))

        // زمان
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = formatDuration(currentPosition),
                color = Color.Black.copy(alpha = 0.6f),
                fontSize = 11.sp,
                fontFamily = vazirFont
            )
            Text(
                text = formatDuration(duration),
                color = Color.Black.copy(alpha = 0.6f),
                fontSize = 11.sp,
                fontFamily = vazirFont
            )
        }
    }
}
fun formatDuration(ms: Long): String {
    val minutes = ms / 1000 / 60
    val seconds = ms / 1000 % 60
    return "%02d:%02d".format(minutes, seconds)
}