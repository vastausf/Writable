package com.vastausf.writable.data.repository

import com.vastausf.writable.data.db.dao.PageDao
import com.vastausf.writable.data.db.entry.PageEntity
import kotlinx.coroutines.flow.MutableStateFlow

class FakePageDao : PageDao {
    private val _pages = MutableStateFlow<List<PageEntity>>(emptyList())
    val pages get() = _pages.value

    private var nextId = 1L

    override suspend fun getByIds(ids: List<Long>): List<PageEntity> =
        ids.mapNotNull { id -> pages.firstOrNull { it.id == id } }

    override suspend fun insert(page: PageEntity): Long {
        val newPages = _pages.value.toMutableList()

        val id = nextId++

        newPages.add(page.copy(id = id))

        _pages.value = newPages

        return id
    }

    override suspend fun insertList(pages: List<PageEntity>): List<Long> {
        val newPages = _pages.value.toMutableList()

        val ids = pages.map { page ->
            val id = nextId++

            newPages.add(page.copy(id = id))

            id
        }

        _pages.value = newPages

        return ids
    }

    override suspend fun updateList(pages: List<PageEntity>) {
        val newPages = _pages.value.toMutableList()

        pages.forEach { page ->
            val index = newPages.indexOfFirst { it.id == page.id }

            if (index != -1) {
                newPages[index] = page
            }
        }

        _pages.value = newPages
    }

    override suspend fun deleteById(id: Long) {
        val newPages = _pages.value.toMutableList()

        newPages.removeIf { it.id == id }

        _pages.value = newPages
    }

    override suspend fun deleteByIds(ids: List<Long>) {
        val newPages = _pages.value.toMutableList()

        ids.forEach { id ->
            val index = newPages.firstOrNull { it.id == id }

            newPages.remove(index)
        }

        _pages.value = newPages
    }
}
