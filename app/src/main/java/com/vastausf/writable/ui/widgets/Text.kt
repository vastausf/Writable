package com.vastausf.writable.ui.widgets

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.vastausf.writable.ui.theme.ThemePreview
import com.vastausf.writable.ui.theme.WritableTheme
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

@ThemePreview
@Composable
private fun TextPreview() {
    WritableTheme {
        Column {
            TitleText("Title text")

            ContentText("Content text")

            LabelText("Label text")
        }
    }
}
