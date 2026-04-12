package com.vastausf.writable.data.db.entry

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "pages")
data class PageEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val documentId: Long = -1,
    val type: PageType,
    val ratio: Float = 1f,
    val url: String? = null,
) {
    companion object {
        fun blackPage(): PageEntity {
            return PageEntity(
                type = PageType.Blank,
            )
        }
    }
}

enum class PageType { Blank, Image }
