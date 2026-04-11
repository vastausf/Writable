package com.vastausf.writable.ui.widgets

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.vastausf.writable.ui.theme.WritableTheme.colors
import com.vastausf.writable.ui.theme.WritableTheme.typography

@Composable
fun TitleText(
    text: String
) {
    Text(
        text = text,
        color = colors.textAndIcons,
        style = typography.title,
    )
}

@Composable
fun ContentText(
    text: String
) {
    Text(
        text = text,
        color = colors.textAndIcons,
        style = typography.content,
    )
}

@Composable
fun LabelText(
    text: String
) {
    Text(
        text = text,
        color = colors.textAndIcons,
        style = typography.label,
    )
}
