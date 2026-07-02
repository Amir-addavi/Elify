package com.addavi.elify.components

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import kotlin.math.abs

@Composable
fun VolumeSeekBar(
    volume: Float,
    onVolumeChange: (Float) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val vibrator = remember {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            (context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager).defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        }
    }

    var isDragging by remember { mutableStateOf(false) }
    var dragVolume by remember { mutableStateOf(volume) }
    var sliderWidth by remember { mutableStateOf(0) }
    var lastHapticVolume by remember { mutableStateOf(volume) }

    val displayVolume = if (isDragging) dragVolume else volume

    val thumbSize by animateDpAsState(
        targetValue = if (isDragging) 11.dp else 8.dp,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "thumbSize"
    )

    LaunchedEffect(volume) {
        if (!isDragging) dragVolume = volume
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(20.dp)
            .onGloballyPositioned { sliderWidth = it.size.width }
            .pointerInput(Unit) {
                detectHorizontalDragGestures(
                    onDragStart = {
                        isDragging = true
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            vibrator.vibrate(
                                VibrationEffect.createOneShot(10, VibrationEffect.DEFAULT_AMPLITUDE)
                            )
                        }
                    },
                    onDragEnd = @androidx.annotation.RequiresPermission(android.Manifest.permission.VIBRATE) {
                        isDragging = false
                        onVolumeChange(dragVolume)
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            vibrator.vibrate(
                                VibrationEffect.createOneShot(10, VibrationEffect.DEFAULT_AMPLITUDE)
                            )
                        }
                    },
                    onHorizontalDrag = { _, dragAmount ->
                        dragVolume = (dragVolume + dragAmount / sliderWidth).coerceIn(0f, 1f)
                        onVolumeChange(dragVolume)
                        if (abs(dragVolume - lastHapticVolume) >= 0.1f) {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                                vibrator.vibrate(
                                    VibrationEffect.createPredefined(VibrationEffect.EFFECT_TICK)
                                )
                            }
                            lastHapticVolume = dragVolume
                        }
                    }
                )
            },
        contentAlignment = Alignment.Center
    ) {
        // پس زمینه
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(3.dp)
                .clip(RoundedCornerShape(50))
                .background(MaterialTheme.colorScheme.background)
        )

        // پروگرس
        Box(
            modifier = Modifier
                .fillMaxWidth(displayVolume)
                .height(3.dp)
                .align(Alignment.CenterStart)
                .clip(RoundedCornerShape(50))
                .background(MaterialTheme.colorScheme.inversePrimary)
        )

        // thumb
        Box(
            modifier = Modifier
                .padding(start = with(LocalDensity.current) {
                    (displayVolume * sliderWidth).toDp() - thumbSize / 2
                }.coerceAtLeast(0.dp))
                .align(Alignment.CenterStart)
                .size(thumbSize)
                .shadow(
                    elevation = 4.dp,
                    shape = RoundedCornerShape(50),
                    spotColor = Color.Black.copy(alpha = 0.2f),
                    ambientColor = Color.Black.copy(alpha = 0.2f)
                )
                .clip(RoundedCornerShape(50))
                .background(MaterialTheme.colorScheme.inversePrimary)
        )
    }
}