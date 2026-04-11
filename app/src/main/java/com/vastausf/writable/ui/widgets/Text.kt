package com.vastausf.writable.ui.widgets

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.vastausf.writable.ui.theme.WritableTheme.colors
import com.vastausf.writable.ui.theme.WritableTheme.typography

@Composable
fun TitleText(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = colors.textAndIcons,
) {
    Text(
        modifier = modifier,
        text = text,
        color = color,
        style = typography.title,
    )
}

@Composable
fun ContentText(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = colors.textAndIcons,
) {
    Text(
        modifier = modifier,
        text = text,
        color = color,
        style = typography.content,
    )
}

@Composable
fun LabelText(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = colors.textAndIcons,
) {
    Text(
        modifier = modifier,
        text = text,
        color = color,
        style = typography.label,
    )
}
