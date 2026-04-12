package com.vastausf.writable.data.repository

import com.vastausf.writable.data.db.dao.DocumentDao
import com.vastausf.writable.data.db.entry.DocumentEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlin.collections.plus

class FakeDocumentDao: DocumentDao {
    private val _documents = MutableStateFlow<List<DocumentEntity>>(emptyList())
    val documents get() = _documents.value

    private var nextId = 1L

    override fun getAll(): Flow<List<DocumentEntity>> = _documents

    override suspend fun getById(id: Long): DocumentEntity? =
        documents.find { it.id == id }

    override fun getByIdFlow(id: Long): Flow<DocumentEntity?> =
        _documents.map { list -> list.find { it.id == id } }

    override suspend fun insert(document: DocumentEntity): Long {
        val id = nextId++

        _documents.value += document.copy(id = id)

        return id
    }

    override suspend fun update(document: DocumentEntity) {
        val newDocuments = _documents.value.toMutableList()

        val index = newDocuments.indexOfFirst { it.id == document.id }

        if (index != -1) {
            newDocuments[index] = document
        }

        _documents.value = newDocuments
    }

    override suspend fun delete(document: DocumentEntity) {
        val newDocuments = _documents.value.toMutableList()

        newDocuments.removeIf { it.id == document.id }

        _documents.value = newDocuments
    }
}
