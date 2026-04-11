package com.vastausf.writable.ui.widgets

import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.rememberTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import com.vastausf.writable.ui.theme.WritableTheme.colors

private const val ANIMATION_APPEAR_DELAY = 150
private const val ANIMATION_DISAPPEAR_DELAY = 100

@Composable
fun WritableDropdownMenu(
    expanded: Boolean,
    onDismissRequest: () -> Unit,
    content: @Composable ColumnScope.() -> Unit,
) {
    val expandableState = remember { MutableTransitionState(false) }
    expandableState.targetState = expanded

    if (expandableState.currentState || expandableState.targetState) {
        Popup(
            alignment = Alignment.BottomCenter,
            onDismissRequest = onDismissRequest,
            properties = PopupProperties(
                focusable = true,
            ),
        ) {
            WritableDropdownMenuContent(
                expandedState = expandableState,
                content = content,
            )
        }
    }
}

@Composable
private fun WritableDropdownMenuContent(
    expandedState: MutableTransitionState<Boolean>,
    content: @Composable ColumnScope.() -> Unit,
) {
    val transition = rememberTransition(expandedState, label = "dropdown")

    val scale by transition.animateFloat(
        label = "scale",
        transitionSpec = {
            if (false isTransitioningTo true) {
                tween(ANIMATION_APPEAR_DELAY)
            } else {
                tween(ANIMATION_DISAPPEAR_DELAY)
            }
        },
    ) { if (it) 1f else 0.85f }

    val alpha by transition.animateFloat(
        label = "alpha",
        transitionSpec = {
            if (false isTransitioningTo true) {
                tween(ANIMATION_APPEAR_DELAY)
            } else {
                tween(ANIMATION_DISAPPEAR_DELAY)
            }
        }
    ) { if (it) 1f else 0f }

    Column(
        modifier = Modifier
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
                this.alpha = alpha
                transformOrigin = TransformOrigin(0.5f, 1f)
            }
            .background(colors.surface, RoundedCornerShape(4.dp))
            .border(1.dp, colors.outline, RoundedCornerShape(4.dp))
            .padding(vertical = 4.dp),
        content = content,
    )
}

@Composable
fun WritableDropdownMenuItem(
    text: String,
    icon: ImageVector,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .widthIn(min = 180.dp)
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(2.dp),
    ) {
        Icon(icon, contentDescription = null, tint = colors.textAndIcons)
        ContentText(text)
    }
}
