package com.vastausf.writable.ui.theme

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

data class WritableTypography(
    val display: TextStyle,
    val title: TextStyle,
    val content: TextStyle,
    val label: TextStyle,
)

val defaultTypography = WritableTypography(
    display = TextStyle(
        fontSize = 32.sp,
        lineHeight = 40.sp,
        fontWeight = FontWeight.Medium,
    ),
    title = TextStyle(
        fontSize = 22.sp,
        lineHeight = 28.sp,
        fontWeight = FontWeight.Medium,
    ),
    content = TextStyle(
        fontSize = 16.sp,
        lineHeight = 18.sp,
        fontWeight = FontWeight.Normal,
    ),
    label = TextStyle(
        fontSize = 14.sp,
        lineHeight = 12.sp,
        fontWeight = FontWeight.Normal,
    ),
)

val LocalWritableTypography = staticCompositionLocalOf { defaultTypography }
