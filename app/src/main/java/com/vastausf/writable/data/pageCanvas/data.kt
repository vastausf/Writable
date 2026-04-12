package com.vastausf.writable.data.pageCanvas

data class Stroke(
    val points: List<StrokePoint>,
    val color: Int,
    val width: Float,
)

data class StrokePoint(
    val x: Float,
    val y: Float,
    val pressure: Float,
)


