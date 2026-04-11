package com.vastausf.writable.data.db.entry

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "pages")
data class PageEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val documentId: Long,
    val type: PageType,
    val url: String? = null,
    val pdfPage: Int? = null,
)

enum class PageType { Blank, Image, Pdf }
