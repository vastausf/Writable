package com.vastausf.writable.ui.widgets

import androidx.compose.foundation.layout.Column
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Accessibility
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import com.vastausf.writable.ui.theme.ThemePreview
import com.vastausf.writable.ui.theme.WritableTheme
import com.vastausf.writable.ui.theme.WritableTheme.colors

@Composable
fun ThemedIcon(
    imageVector: ImageVector,
    contentDescription: String?,
    modifier: Modifier = Modifier,
) {
    Icon(
        modifier = modifier,
        imageVector = imageVector,
        contentDescription = contentDescription,
        tint = colors.textAndIcons,
    )
}

@Composable
@ThemePreview
private fun IconPreview() {
    WritableTheme {
        Column {
            ThemedIcon(
                imageVector = Icons.Rounded.Accessibility,
                contentDescription = null,
            )
        }
    }
}
