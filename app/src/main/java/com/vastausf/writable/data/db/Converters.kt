package com.vastausf.writable.data.db

import androidx.room.TypeConverter
class Converters {
    @TypeConverter
    fun fromLongList(value: List<Long>): String = value.joinToString(",")

    @TypeConverter
    fun toLongList(value: String): List<Long> {
        return if (value.isEmpty()) {
            emptyList()
        } else {
            value.split(",").map { it.toLong() }
        }
    }
}
