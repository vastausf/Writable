package com.vastausf.writable.ui.widgets

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.unit.dp

@Composable
fun DocumentCover(
    modifier: Modifier = Modifier,
    cover: Color,
    edge: Color,
    bookmark: Color,
) {
    Canvas(
        modifier = modifier,
    ) {
        drawRoundRect(
            color = cover,
        )

        drawRoundRect(
            color = edge,
            size = Size(12.dp.toPx(), size.height),
        )

        val bookmarkWidth = 24.dp.toPx()
        val bookmarkHeight = 48.dp.toPx()
        val bookmarkX = size.width - 40.dp.toPx()

        drawPath(
            path = Path().apply {
                moveTo(bookmarkX, 0f)
                lineTo(bookmarkX + bookmarkWidth, 0f)
                lineTo(bookmarkX + bookmarkWidth, bookmarkHeight)
                lineTo(bookmarkX + bookmarkWidth / 2, bookmarkHeight - 10.dp.toPx())
                lineTo(bookmarkX, bookmarkHeight)
                close()
            },
            color = bookmark,
        )
    }
}
