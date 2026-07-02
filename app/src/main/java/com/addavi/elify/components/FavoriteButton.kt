package com.addavi.elify.components

import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.addavi.elify.R

@Composable
fun FavoriteButton(
    isLove: Boolean,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    var triggerAnim by remember { mutableStateOf(false) }

    val scale by animateFloatAsState(
        targetValue = if (triggerAnim) 1.4f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        finishedListener = { triggerAnim = false },
        label = "scale"
    )

    val color by animateColorAsState(
        targetValue = if (isLove) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.inversePrimary,
        animationSpec = tween(300),
        label = "color"
    )

    Box(
        modifier = modifier
            .scale(scale)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                triggerAnim = true
                onToggle()
            }
    ) {
        Crossfade(
            targetState = isLove,
            animationSpec = tween(300),
            label = "heart"
        ) { loved ->
            Icon(
                painter = painterResource(if (loved) R.drawable.heart_fill_ico else R.drawable.heart_ico),
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(27.dp)
            )
        }
    }
}