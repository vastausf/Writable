package com.vastausf.writable.ui.widgets

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import com.vastausf.writable.ui.theme.ThemePreview
import com.vastausf.writable.ui.theme.WritableTheme
import com.vastausf.writable.ui.theme.WritableTheme.colors

@Composable
fun StrokeWidthSlider(
    current: Float,
    onChange: (Float) -> Unit,
    modifier: Modifier = Modifier,
    thumbColor: Color = colors.accent,
    lineColor: Color = colors.outline,
    minWidth: Float = StylusPanelDefaults.MIN_WIDTH,
    maxWidth: Float = StylusPanelDefaults.MAX_WIDTH,
) {
    LaunchedEffect(minWidth, maxWidth) {
        onChange(current.coerceIn(minWidth, maxWidth))
    }

    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(40.dp)
            .pointerInput(Unit) {
                awaitEachGesture {
                    awaitFirstDown()

                    do {
                        val event = awaitPointerEvent()
                        val change = event.changes.firstOrNull() ?: break

                        change.consume()

                        val leftX = minWidth.dp.toPx() / 2f
                        val rightX = size.width - maxWidth.dp.toPx() / 2f
                        val positionX = change.position.x

                        val progress = ((positionX - leftX) / (rightX - leftX)).coerceIn(0.0f, 1.0f)

                        onChange(minWidth + progress * (maxWidth - minWidth))
                    } while (event.changes.any { it.pressed })
                }
            }
    ) {
        val centerY = size.height / 2f

        val lineHeight = size.height * 0.1f

        val progress = (current - minWidth) / (maxWidth - minWidth)
        val thumbPosition = (size.width - minWidth - maxWidth) * progress

        drawRoundRect(
            color = lineColor,
            topLeft = Offset(minWidth, centerY - lineHeight / 2f),
            size = Size(size.width - minWidth - maxWidth,  lineHeight),
            cornerRadius = CornerRadius(lineHeight / 2f),
        )

        drawCircle(
            color = thumbColor,
            radius = current / 2f,
            center = Offset(minWidth + thumbPosition, centerY),
        )
    }
}

@ThemePreview
@Composable
private fun StrokeWidthSliderPreview() {
    WritableTheme {
        var selectedWidth by remember { mutableFloatStateOf(1f) }

        StrokeWidthSlider(
            modifier = Modifier
                .height(40.dp)
                .width(200.dp),
            current = selectedWidth,
            onChange = { selectedWidth = it },
        )
    }
}
