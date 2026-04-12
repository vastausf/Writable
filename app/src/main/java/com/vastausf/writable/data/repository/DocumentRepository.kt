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

    suspend fun createDocument(
        title: String,
        coverColor: Int,
        spineColor: Int,
        bookmarkColor: Int,
    ): Long = documentDao.insert(
        DocumentEntity(
            title = title,
            coverColor = coverColor,
            spineColor = spineColor,
            bookmarkColor = bookmarkColor,
        )
    )

    suspend fun deleteDocument(document: DocumentEntity) {
        documentDao.delete(document)

        pageDao.deleteByIds(document.pagesIds)
    }

    suspend fun updateDocument(document: DocumentEntity) =
        documentDao.update(document)

    fun getDocument(documentId: Long): Flow<DocumentEntity?> =
        documentDao.getByIdFlow(documentId)

    suspend fun updateLastOpened(documentId: Long) {
        val document = documentDao.getById(documentId) ?: return

        documentDao.update(document.copy(openedAt = System.currentTimeMillis()))
    }

    suspend fun getPages(indices: List<Long>): List<PageEntity> {
        return pageDao.getByIds(indices)
    }

    suspend fun addPage(documentId: Long, index: Int, page: PageEntity): Long {
        val pageId = pageDao.insert(page.copy(documentId = documentId))

        documentDao.appendPageId(documentId, index, pageId)

        return pageId
    }

    suspend fun addPages(documentId: Long, offset: Int, pages: List<PageEntity>): List<Long> {
        val pageIds = pageDao.insertList(pages)

        documentDao.appendPageIds(documentId, offset, pageIds)

        return pageIds
    }

    suspend fun updatePages(values: List<PageEntity>) {
        pageDao.updateList(values)
    }

    suspend fun movePage(documentId: Long, index: Int, pageId: Long) {
        documentDao.movePageId(documentId, index, pageId)
    }

    suspend fun removePage(documentId: Long, pageId: Long) {
        pageDao.deleteById(pageId)
        documentDao.removePageId(documentId, pageId)
    }
}
