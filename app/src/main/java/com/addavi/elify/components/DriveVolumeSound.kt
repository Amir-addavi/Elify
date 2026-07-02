package com.addavi.elify.components

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
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

@Composable
fun DriveVolumeSeekBar(
    volume: Float,
    onVolumeChange: (Float) -> Unit,
    primaryColor: Color,
    modifier: Modifier = Modifier
) {
    var isDragging by remember { mutableStateOf(false) }
    var dragPosition by remember { mutableStateOf(volume) }
    var sliderHeight by remember { mutableStateOf(0) }

    val displayProgress = if (isDragging) dragPosition else volume

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Box(
                modifier = Modifier
                    .width(10.dp)
                    .fillMaxHeight()
                    .onGloballyPositioned { sliderHeight = it.size.height }
                    .pointerInput(Unit) {
                        detectVerticalDragGestures(
                            onDragStart = { isDragging = true },
                            onDragEnd = {
                                onVolumeChange(dragPosition)
                                isDragging = false
                            },
                            onVerticalDrag = { _, dragAmount ->
                                dragPosition =
                                    (dragPosition - dragAmount / sliderHeight).coerceIn(0f, 1f)
                            }
                        )
                    },
                contentAlignment = Alignment.BottomCenter
            ) {
                // پس زمینه
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .width(3.dp)
                        .clip(RoundedCornerShape(50))
                        .background(Color.Black.copy(alpha = 0.35f))
                        .align(Alignment.Center)
                )

                // پروگرس
                Box(
                    modifier = Modifier
                        .fillMaxHeight(displayProgress)
                        .width(3.dp)
                        .shadow(
                            elevation = 8.dp,
                            spotColor = primaryColor,
                            ambientColor = primaryColor,
                            shape = RoundedCornerShape(50)
                        )
                        .clip(RoundedCornerShape(50))
                        .background(
                            Brush.verticalGradient(
                                listOf(
                                    primaryColor,
                                    primaryColor.copy(alpha = 0.7f)
                                )
                            )
                        )
                        .align(Alignment.BottomCenter)
                )

                // دایره
                Box(
                    modifier = Modifier
                        .padding(bottom = with(LocalDensity.current) {
                            (displayProgress * sliderHeight).toDp() - 5.dp
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
                        .align(Alignment.BottomCenter)
                )
            }
        }
    }
}