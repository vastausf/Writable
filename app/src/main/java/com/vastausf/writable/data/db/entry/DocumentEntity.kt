package com.vastausf.writable.data.db.entry

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "documents")
data class DocumentEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val createdAt: Long = System.currentTimeMillis(),
    val openedAt: Long = System.currentTimeMillis(),
    val pagesIds: List<Long> = emptyList(),
)
