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
    val canvasData: ByteArray? = null,
) {
    companion object {
        fun blackPage(): PageEntity {
            return PageEntity(
                type = PageType.Blank,
            )
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as PageEntity

        if (id != other.id) return false
        if (documentId != other.documentId) return false
        if (ratio != other.ratio) return false
        if (type != other.type) return false
        if (url != other.url) return false
        if (!canvasData.contentEquals(other.canvasData)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + documentId.hashCode()
        result = 31 * result + ratio.hashCode()
        result = 31 * result + type.hashCode()
        result = 31 * result + (url?.hashCode() ?: 0)
        result = 31 * result + (canvasData?.contentHashCode() ?: 0)
        return result
    }
}

enum class PageType { Blank, Image }
