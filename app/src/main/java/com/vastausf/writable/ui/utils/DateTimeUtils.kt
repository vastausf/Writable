package com.vastausf.writable.ui.utils

import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

fun Long.toFormattedDateTime(
    pattern: String,
): String {
    val dateTime = LocalDateTime.ofInstant(
        Instant.ofEpochMilli(this),
        ZoneId.systemDefault(),
    )

    return DateTimeFormatter.ofPattern(pattern).format(dateTime)
}
