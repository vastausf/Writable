package com.vastausf.writable.ui.widgets

import androidx.compose.foundation.layout.Column
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Accessibility
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import com.vastausf.writable.ui.theme.ThemePreview
import com.vastausf.writable.ui.theme.WritableTheme

@Composable
fun RoundButton(
    imageVector: ImageVector,
    contentDescription: String?,
    onClick: () -> Unit,
) {
    IconButton(
        onClick = onClick,
    ) {
        ThemedIcon(
            imageVector = imageVector,
            contentDescription = contentDescription,
        )
    }
}

@Composable
@ThemePreview
private fun RoundButtonPreview() {
    WritableTheme {
        Column {
            RoundButton(
                imageVector = Icons.Rounded.Accessibility,
                contentDescription = null,
            ) { }
        }
    }
}
