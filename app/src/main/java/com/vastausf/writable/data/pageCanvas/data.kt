package com.vastausf.writable.data.pageCanvas

import kotlinx.serialization.Serializable

@Serializable
data class Stroke(
    val points: List<StrokePoint>,
    val color: Int,
    val width: Float,
)

@Serializable
data class StrokePoint(
    val x: Float,
    val y: Float,
    val pressure: Float,
)
