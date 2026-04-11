package com.vastausf.writable.ui.widgets

import androidx.annotation.StringRes
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource

@Composable
fun RoundButton(
    icon: ImageVector,
    @StringRes
    contentDescription: Int,
    onClick: () -> Unit,
) {
    IconButton(
        onClick = onClick,
    ) {
        Icon(icon, stringResource(contentDescription))
    }
}
