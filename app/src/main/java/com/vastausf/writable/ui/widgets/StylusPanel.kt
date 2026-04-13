package com.vastausf.writable.ui.widgets

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.vastausf.writable.ui.theme.ThemePreview
import com.vastausf.writable.ui.theme.WritableTheme
import com.vastausf.writable.ui.theme.WritableTheme.colors
import kotlin.collections.first

object StylusPanelDefaults {
    const val MIN_WIDTH: Float = 4f
    const val MAX_WIDTH: Float = 64f
}

@Composable
fun StylusPanel(
    selectedColor: Color,
    onColorSelect: (Color) -> Unit,
    selectedWidth: Float,
    onWidthSelect: (Float) -> Unit,
    stylusColors: List<Color>,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
            .background(
                color = colors.surface,
                shape = RoundedCornerShape(4.dp),
            )
            .border(
                width = 1.dp,
                color = colors.outline,
                shape = RoundedCornerShape(4.dp),
            )
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        LazyRow(
            modifier = Modifier,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            item {
                StrokeWidthSlider(
                    modifier = Modifier
                        .height(56.dp)
                        .width(200.dp),
                    current = selectedWidth,
                    onChange = onWidthSelect,
                    thumbColor = selectedColor,
                )
            }

            stylusColors.forEach { color ->
                item {
                    ColorOption(
                        modifier = Modifier
                            .padding(4.dp),
                        color = color,
                        isSelected = selectedColor == color,
                        onClick = { onColorSelect(color) }
                    )
                }
            }
        }
    }
}

@Composable
private fun ColorOption(
    color: Color,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .clip(CircleShape)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                onClick = onClick,
            )
            .let {
                if (isSelected) {
                    it.background(
                        color = color,
                        shape = CircleShape,
                    )
                } else {
                    it
                }
            }
            .padding(2.dp)
            .background(
                color = colors.surface,
                shape = CircleShape,
            )
            .padding(1.dp)
            .size(32.dp)
            .background(
                color = color,
                shape = CircleShape,
            )
            .padding(14.dp),
    )
}

@ThemePreview
@Composable
private fun StylusPanelPreview() {
    WritableTheme {
        Box {
            val colors = colors
            var selectedColor by remember { mutableStateOf(colors.palette.first()) }
            var selectedWidth by remember { mutableFloatStateOf(1f) }

            StylusPanel(
                modifier = Modifier,
                selectedColor = selectedColor,
                onColorSelect = { selectedColor = it },
                selectedWidth = selectedWidth,
                onWidthSelect = { selectedWidth = it },
                stylusColors = colors.palette,
            )
        }
    }
}

@ThemePreview
@Composable
private fun ColorOptionPreview() {
    WritableTheme {
        Column {
            ColorOption(
                color = colors.accent,
                isSelected = false,
                onClick = { },
            )

            ColorOption(
                color = colors.accent,
                isSelected = true,
                onClick = { },
            )
        }
    }
}
