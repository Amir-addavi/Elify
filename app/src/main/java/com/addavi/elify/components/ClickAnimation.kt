package com.addavi.elify.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import kotlin.math.sqrt

@Composable
fun Modifier.premiumClick(
    scaleDown: Float = 0.92f,
    rippleColor: Color = MaterialTheme.colorScheme.inversePrimary,
    shape: Int = 10,
    onClick: () -> Unit
): Modifier = composed {
    val scale = remember { Animatable(1f) }
    val coroutineScope = rememberCoroutineScope()
    var isAnimating by remember { mutableStateOf(false) }

    var pressOffset by remember { mutableStateOf(Offset.Zero) }
    val rippleAlpha = remember { Animatable(0f) }
    val rippleScale = remember { Animatable(0f) }

    this
        .graphicsLayer {
            scaleX = scale.value
            scaleY = scale.value
        }
        .clip(RoundedCornerShape(shape.dp)) // مطمئن میشیم چیزی بیرون از باندری رسم نشه؛ اگه شکل خاصی داری جایگزینش کن
        .drawWithContent {
            drawContent()
            if (rippleAlpha.value > 0f) {
                // فاصله از نقطه لمس تا دورترین گوشه، برای اینکه دایره دقیقاً کل سطح رو بپوشونه نه بیشتر
                val maxRadius = sqrt(
                    maxOf(pressOffset.x, size.width - pressOffset.x).let { it * it } +
                            maxOf(pressOffset.y, size.height - pressOffset.y).let { it * it }
                )
                drawCircle(
                    color = rippleColor.copy(alpha = rippleAlpha.value * 0.25f),
                    radius = maxRadius * rippleScale.value,
                    center = pressOffset
                )
            }
        }
        .pointerInput(Unit) {
            detectTapGestures(
                onPress = { offset ->
                    if (isAnimating) return@detectTapGestures
                    isAnimating = true
                    pressOffset = offset

                    coroutineScope.launch {
                        launch {
                            scale.animateTo(
                                targetValue = scaleDown,
                                animationSpec = tween(90, easing = FastOutSlowInEasing)
                            )
                            scale.animateTo(
                                targetValue = 1f,
                                animationSpec = spring(
                                    dampingRatio = Spring.DampingRatioMediumBouncy,
                                    stiffness = Spring.StiffnessMedium
                                )
                            )
                            isAnimating = false
                        }
                        launch {
                            rippleAlpha.snapTo(1f)
                            rippleScale.snapTo(0f)
                            rippleScale.animateTo(1f, tween(350, easing = FastOutSlowInEasing))
                            rippleAlpha.animateTo(0f, tween(350))
                        }
                    }

                    val released = tryAwaitRelease()
                    if (released) {
                        onClick()
                    }
                }
            )
        }
}