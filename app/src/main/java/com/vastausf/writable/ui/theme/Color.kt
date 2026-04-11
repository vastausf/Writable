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

    val palette: List<Color> = listOf(
        Color(0xFFFFB3B3), Color(0xFFFF8FA3), Color(0xFFFF6B9D),
        Color(0xFFC9B8F5), Color(0xFFB39DDB), Color(0xFF9C7FD4),
        Color(0xFF81C8F5), Color(0xFF5BA8E5), Color(0xFF4090D4),
        Color(0xFF7DDFC3), Color(0xFF4ECBA8), Color(0xFF2EB891),
        Color(0xFFFFD166), Color(0xFFFFBF40), Color(0xFFFFAA1A),
        Color(0xFFFFAD8A), Color(0xFFFF8C61), Color(0xFFFF6B3D),
        Color(0xFF98D68E), Color(0xFF72C264), Color(0xFF4DAD40),
        Color(0xFF5C6BC0), Color(0xFF7B4FA6), Color(0xFF2E7D9E),
    )
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
    background = Color(0xFFFCFAF9),
    surface = Color(0xFFF6F2F0),
    textAndIcons = Color(0xFF3D2B1F),
    textAndIconsSecondary = Color(0xFF96817E),
    outline = Color(0xFFF6ECE7),
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
