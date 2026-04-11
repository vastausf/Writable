package com.vastausf.writable.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.vastausf.writable.data.db.entry.DocumentEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DocumentDao {
    @Query("SELECT * FROM documents ORDER BY createdAt DESC")
    fun getAll(): Flow<List<DocumentEntity>>

    @Query("SELECT * FROM documents WHERE id = :id")
    suspend fun getById(id: Long): DocumentEntity?

    @Query("SELECT * FROM documents WHERE id = :id")
    fun getByIdFlow(id: Long): Flow<DocumentEntity?>

    @Insert
    suspend fun insert(document: DocumentEntity): Long

    @Update
    suspend fun update(document: DocumentEntity)

    @Delete
    suspend fun delete(document: DocumentEntity)

    suspend fun appendPageId(documentId: Long, index: Int, pageId: Long) {
        val document = getById(documentId) ?: return

        val pageIds = document.pagesIds.toMutableList()
        pageIds.add(index, pageId)

        update(document.copy(pagesIds = pageIds))
    }

    suspend fun appendPageIds(documentId: Long, offset: Int, pageIds: List<Long>) {
        val document = getById(documentId) ?: return

        val newPageIds = document.pagesIds.toMutableList()
        newPageIds.addAll(offset, pageIds)

        update(document.copy(pagesIds = newPageIds))
    }

    suspend fun removePageId(documentId: Long, pageId: Long) {
        val document = getById(documentId) ?: return

        update(document.copy(pagesIds = document.pagesIds - pageId))
    }

    suspend fun movePageId(documentId: Long, index: Int, pageId: Long) {
        val document = getById(documentId) ?: return

        val pageIds = document.pagesIds.toMutableList()
        pageIds.remove(pageId)
        pageIds.add(index, pageId)

        update(document.copy(pagesIds = pageIds))
    }
}