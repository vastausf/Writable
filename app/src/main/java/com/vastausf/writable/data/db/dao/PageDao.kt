package com.vastausf.writable.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.vastausf.writable.data.db.entry.PageEntity

@Dao
interface PageDao {
    @Query("SELECT * FROM pages WHERE id IN (:ids)")
    suspend fun getByIds(ids: List<Long>): List<PageEntity>

    @Insert
    suspend fun insert(page: PageEntity): Long

    @Query("DELETE FROM pages WHERE id = :id")
    suspend fun deleteById(id: Long)
}
