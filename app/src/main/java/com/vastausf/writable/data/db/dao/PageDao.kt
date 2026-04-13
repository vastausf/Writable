package com.vastausf.writable.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.vastausf.writable.data.db.entry.PageEntity

@Dao
interface PageDao {
    @Query("SELECT * FROM pages WHERE id IN (:ids)")
    suspend fun getByIds(ids: List<Long>): List<PageEntity>

    @Insert
    suspend fun insert(page: PageEntity): Long

    @Insert
    suspend fun insertList(pages: List<PageEntity>): List<Long>

    @Update
    suspend fun updateList(pages: List<PageEntity>)

    @Query("DELETE FROM pages WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("DELETE FROM pages WHERE id IN (:ids)")
    suspend fun deleteByIds(ids: List<Long>)

    @Query("UPDATE pages SET canvasData = :canvasData WHERE id = :pageId")
    suspend fun updateCanvasData(pageId: Long, canvasData: ByteArray)
}
