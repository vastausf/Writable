package com.vastausf.writable.ui.widgets

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.vastausf.writable.ui.theme.ThemePreview
import com.vastausf.writable.ui.theme.WritableTheme
import com.vastausf.writable.ui.theme.WritableTheme.colors

@Composable
fun ThemedButton(
    text: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    onClick: () -> Unit,
) {
    TitleText(
        modifier = Modifier
            .alpha(if (enabled) 1f else 0.5f)
            .background(
                color = colors.accent,
                shape = RoundedCornerShape(8.dp),
            )
            .clickable(
                enabled = enabled,
                interactionSource = remember { MutableInteractionSource() },
                onClick = onClick,
            )
            .padding(32.dp, 16.dp)
            .semantics { role = Role.Button }
            .then(modifier),
        text = text,
        color = colors.background,
    )
}

@Composable
@ThemePreview
private fun ButtonPreview() {
    WritableTheme {
        Column {
            ThemedButton(
                text = "Enabled button",
                enabled = true,
            ) { }

            ThemedButton(
                text = "Disabled button",
                enabled = false,
            ) { }
        }
    }
}