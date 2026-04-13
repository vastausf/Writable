package com.vastausf.writable.ui.widgets

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.PointerType
import androidx.compose.ui.input.pointer.pointerInput
import com.vastausf.writable.data.pageCanvas.Stroke as StrokeData
import com.vastausf.writable.data.pageCanvas.StrokePoint
import com.vastausf.writable.ui.screens.editor.StylusStyle

@Composable
fun DrawingLayer(
    strokes: List<StrokeData>,
    onStrokeFinished: (StrokeData) -> Unit,
    modifier: Modifier = Modifier,
    stylusStyle: StylusStyle,
) {
    var currentPoints by remember { mutableStateOf<List<StrokePoint>>(emptyList()) }

    Canvas(
        modifier = modifier
            .pointerInput(stylusStyle) {
                awaitEachGesture {
                    val down = awaitFirstDown()

                    if (down.type != PointerType.Stylus) return@awaitEachGesture

                    currentPoints = listOf(down.toStrokePoint())

                    do {
                        val event = awaitPointerEvent()
                        val change = event.changes.firstOrNull() ?: break

                        if (change.type != PointerType.Stylus) break

                        change.consume()
                        currentPoints += change.toStrokePoint()
                    } while (event.changes.any { it.pressed })

                    if (currentPoints.size > 1) {
                        onStrokeFinished(
                            StrokeData(
                                points = currentPoints,
                                color = stylusStyle.color,
                                width = stylusStyle.width,
                            )
                        )
                    }

                    currentPoints = emptyList()
                }
            }
    ) {
        clipRect {
            strokes.forEach { stroke ->
                drawStroke(stroke)
            }

            if (currentPoints.size > 1) {
                drawStroke(
                    StrokeData(
                        points = currentPoints,
                        color = stylusStyle.color,
                        width = stylusStyle.width,
                    )
                )
            }
        }
    }
}

private fun DrawScope.drawStroke(stroke: StrokeData) {
    if (stroke.points.size < 2) return

    val path = Path()
    path.moveTo(stroke.points.first().x, stroke.points.first().y)

    for (i in 1 until stroke.points.size - 1) {
        val current = stroke.points[i]
        val next = stroke.points[i + 1]
        path.quadraticTo(
            current.x, current.y,
            (current.x + next.x) / 2f,
            (current.y + next.y) / 2f,
        )
    }
    path.lineTo(stroke.points.last().x, stroke.points.last().y)

    drawPath(
        path = path,
        color = Color(stroke.color),
        style = Stroke(
            width = stroke.width,
            cap = StrokeCap.Round,
            join = StrokeJoin.Round,
        )
    )
}

private fun PointerInputChange.toStrokePoint() = StrokePoint(
    x = position.x,
    y = position.y,
    pressure = pressure,
)
