package com.vastausf.writable.ui.theme

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

data class WritableColors(
    val primary: Color,
    val accent: Color,
    val background: Color,
    val surface: Color,
    val textAndIcons: Color,
    val textAndIconsSecondary: Color,
    val outline: Color,
)

val LocalWritableColors = staticCompositionLocalOf {
    WritableColors(
        primary = Color.Unspecified,
        accent = Color.Unspecified,
        background = Color.Unspecified,
        surface = Color.Unspecified,
        textAndIcons = Color.Unspecified,
        textAndIconsSecondary = Color.Unspecified,
        outline = Color.Unspecified,
    )
}

val LightColors = WritableColors(
    primary = Color(0xFF3D2B1F),
    accent = Color(0xFFB5651D),
    background = Color(0xFFFCF9F8),
    surface = Color(0xFFF3EFEB),
    textAndIcons = Color(0xFF3D2B1F),
    textAndIconsSecondary = Color(0xFF96817E),
    outline = Color(0xFFF1B19D),
)

val DarkColors = WritableColors(
    primary = Color(0xFFE8C99A),
    accent = Color(0xFFFFB74D),
    background = Color(0xFF150F07),
    surface = Color(0xFF1F160D),
    textAndIcons = Color(0xFFE8C99A),
    textAndIconsSecondary = Color(0xFF887C7C),
    outline = Color(0xFFA17B6B),
)
