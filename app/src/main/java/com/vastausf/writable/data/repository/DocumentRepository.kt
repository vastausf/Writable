package com.vastausf.writable.data.repository

import com.vastausf.writable.data.db.dao.DocumentDao
import com.vastausf.writable.data.db.dao.PageDao
import com.vastausf.writable.data.db.entry.DocumentEntity
import com.vastausf.writable.data.db.entry.PageEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DocumentRepository @Inject constructor(
    private val documentDao: DocumentDao,
    private val pageDao: PageDao,
) {
    fun getAllDocuments(): Flow<List<DocumentEntity>> =
        documentDao.getAll()

    suspend fun createDocument(title: String): Long =
        documentDao.insert(DocumentEntity(title = title))

    suspend fun deleteDocument(document: DocumentEntity) =
        documentDao.delete(document)

    fun getDocument(documentId: Long): Flow<DocumentEntity?> =
        documentDao.getByIdFlow(documentId)

    suspend fun updateLastOpened(documentId: Long) {
        val document = documentDao.getById(documentId) ?: return

        documentDao.update(document.copy(openedAt = System.currentTimeMillis()))
    }

    suspend fun getOrderedPages(documentId: Long): List<PageEntity> {
        val document = documentDao.getById(documentId) ?: return emptyList()
        val pages = pageDao.getByIds(document.pagesIds).associateBy { it.id }

        return document.pagesIds.mapNotNull { pages[it] }
    }

    suspend fun getPages(indices: List<Long>): List<PageEntity> {
        return pageDao.getByIds(indices)
    }

    suspend fun addPage(documentId: Long, index: Int, page: PageEntity): Long {
        val pageId = pageDao.insert(page.copy(documentId = documentId))

        documentDao.appendPageId(documentId, index, pageId)

        return pageId
    }

    suspend fun movePage(documentId: Long, index: Int, pageId: Long) {
        documentDao.movePageId(documentId, index, pageId)
    }

    suspend fun removePage(documentId: Long, pageId: Long) {
        pageDao.deleteById(pageId)
        documentDao.removePageId(documentId, pageId)
    }
}
