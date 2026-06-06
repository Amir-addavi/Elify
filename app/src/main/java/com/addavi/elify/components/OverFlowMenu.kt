package com.addavi.elify.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.IntRect
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import com.addavi.elify.tools.AnchorDropdownPositionProvider
import com.addavi.elify.viewmodel.MenuItemSpec

@Composable
fun CustomOverflowMenu(
    expanded: Boolean,
    anchorBounds: IntRect?,
    items: List<MenuItemSpec>,
    onDismiss: () -> Unit,
    onItemClick: (MenuItemSpec) -> Unit,
    modifier: Modifier = Modifier,
) {
    if (anchorBounds == null) return

    val transitionState = remember { androidx.compose.animation.core.MutableTransitionState(false) }
    transitionState.targetState = expanded

    val showPopup = transitionState.currentState || transitionState.targetState
    if (!showPopup) return

    Popup(
        popupPositionProvider = AnchorDropdownPositionProvider(anchorBounds),
        onDismissRequest = onDismiss,
        properties = PopupProperties(
            focusable = true,
            dismissOnBackPress = true,
            dismissOnClickOutside = true,
        ),
    ) {
        AnimatedVisibility(
            visibleState = transitionState,
            enter = fadeIn(
                animationSpec = spring(stiffness = Spring.StiffnessMediumLow)
            ) + scaleIn(
                initialScale = 0.92f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessMediumLow
                ),
                transformOrigin = TransformOrigin(1f, 0f)
            ),
            exit = fadeOut(
                animationSpec = spring(stiffness = Spring.StiffnessMediumLow)
            ) + scaleOut(
                targetScale = 0.98f,
                animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
                transformOrigin = TransformOrigin(1f, 0f)
            )
        ) {
            Surface(
                modifier = modifier
                    .widthIn(min = 150.dp, max = 200.dp)
                    .heightIn(min = 150.dp , max = 200.dp)
                ,
                shape = RoundedCornerShape(16.dp),
                tonalElevation = 0.dp,
                color = MaterialTheme.colorScheme.surface
            ) {
                    Column(Modifier.padding(vertical = 6.dp)) {
                        items.forEachIndexed { index, item ->
                            MenuRow(
                                title = item.title,
                                icon = item.icon,
                                destructive = item.destructive,
                                enabled = item.enabled,
                                onClick = {
                                    onDismiss()
                                    onItemClick(item)
                                }
                            )
                            if (index != items.lastIndex) {
                                HorizontalDivider(
                                    modifier = Modifier.padding(horizontal = 12.dp),
                                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f)
                                )
                            }
                        }
                    }
                }
            }
        }
    }

@Composable
private fun MenuRow(
    title: String,
    icon: ImageVector?,
    destructive: Boolean,
    enabled: Boolean,
    onClick: () -> Unit
) {
    val contentColor = when {
        !enabled -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
        destructive -> MaterialTheme.colorScheme.error
        else -> MaterialTheme.colorScheme.onSurface
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 44.dp)
            .clip(RoundedCornerShape(12.dp))
            .clickable(enabled = enabled, onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (icon != null) {
            Icon(icon, contentDescription = null, tint = contentColor)
            Spacer(Modifier.width(12.dp))
        } else {
            Spacer(Modifier.width(2.dp))
        }
        Text(
            text = title,
            color = contentColor,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}