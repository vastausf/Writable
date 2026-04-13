package com.vastausf.writable.ui.widgets

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import com.vastausf.writable.ui.theme.ThemePreview
import com.vastausf.writable.ui.theme.WritableTheme
import com.vastausf.writable.ui.theme.WritableTheme.colors
import com.vastausf.writable.ui.theme.WritableTheme.typography

@Composable
fun ThemedTextField(
    placeholder: String,
    modifier: Modifier = Modifier,
    style: TextStyle = typography.content,
    state: TextFieldState = rememberTextFieldState(),
    lineLimits: TextFieldLineLimits = TextFieldLineLimits.SingleLine,
    enabled: Boolean = true,
) {
    TextField(
        modifier = modifier,
        state = state,
        enabled = enabled,
        textStyle = style,
        colors = TextFieldDefaults.colors(
            focusedTextColor = colors.textAndIcons,
            unfocusedTextColor = colors.textAndIcons,
            cursorColor = colors.accent,
            focusedContainerColor = colors.surface,
            unfocusedContainerColor = colors.surface,
            disabledContainerColor = colors.surface,
            errorContainerColor = colors.surface,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent,
            errorIndicatorColor = Color.Transparent,
        ),
        lineLimits = lineLimits,
        placeholder = {
            Text(
                text = placeholder,
                style = style,
                color = colors.textAndIcons.copy(alpha = 0.5f),
            )
        }
    )
}

@ThemePreview
@Composable
private fun TextFieldPreview() {
    WritableTheme {
        Column {
            ThemedTextField(
                placeholder = "Field placeholder",
                state = rememberTextFieldState("Text field value")
            )

            ThemedTextField(
                placeholder = "Field placeholder",
                state = rememberTextFieldState("")
            )
        }
    }
}
