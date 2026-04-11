package com.vastausf.writable.ui.widgets

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path

@Composable
fun DocumentCover(
    cover: Color,
    spine: Color,
    bookmark: Color,
    modifier: Modifier = Modifier,
) {
    Canvas(
        modifier = modifier,
    ) {
        drawRoundRect(
            color = cover,
        )

        drawRoundRect(
            color = spine,
            size = Size(size.width * 0.2f, size.height),
        )

        val bookmarkWidth = size.width / 5f
        val bookmarkHeight = size.height / 4f
        val bookmarkX = size.width * 0.7f

        drawPath(
            path = Path().apply {
                moveTo(bookmarkX, 0f)
                lineTo(bookmarkX + bookmarkWidth, 0f)
                lineTo(bookmarkX + bookmarkWidth, bookmarkHeight)
                lineTo(bookmarkX + bookmarkWidth / 2, bookmarkHeight * 0.7f)
                lineTo(bookmarkX, bookmarkHeight)
                close()
            },
            color = bookmark,
        )
    }
}
