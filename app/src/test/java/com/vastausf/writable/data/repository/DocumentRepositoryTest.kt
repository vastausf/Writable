package com.vastausf.writable.data.repository

import com.google.common.truth.Truth.assertThat
import com.vastausf.writable.data.db.entry.DocumentEntity
import com.vastausf.writable.data.db.entry.PageEntity
import com.vastausf.writable.data.db.entry.PageType
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class DocumentRepositoryTest {
    private val fakeDocumentDao = FakeDocumentDao()
    private val fakePageDao = FakePageDao()
    private val repository = DocumentRepository(fakeDocumentDao, fakePageDao)

    private val fakeDocument = DocumentEntity(
        title = "Test",
    )

    private val fakePage = PageEntity(
        type = PageType.Blank,
    )

    private var documentId = 0L

    @Before
    fun setup() = runTest {
        documentId = createDocument()
    }

    @Test
    fun create_document() = runTest {
        assertThat(documentId).isGreaterThan(0)
        assertThat(fakeDocumentDao.documents).hasSize(1)
        assertThat(fakeDocumentDao.documents.first().title).isEqualTo(fakeDocument.title)
    }

    @Test
    fun get_document_by_id_returns_correct_document() = runTest {
        val document = repository.getDocument(documentId).first()

        assertThat(document).isNotNull()
        assertThat(document?.id).isEqualTo(documentId)
    }

    @Test
    fun get_document_by_wrong_id_returns_null() = runTest {
        val document = repository.getDocument(999L).first()

        assertThat(document).isNull()
    }

    @Test
    fun update_last_opened_changes_openedAt() = runTest {
        val before = fakeDocumentDao.documents.first().openedAt

        repository.updateLastOpened(documentId)

        val after = fakeDocumentDao.documents.first().openedAt
        assertThat(after).isAtLeast(before)
    }

    @Test
    fun delete_document() = runTest {
        assertThat(fakeDocumentDao.documents).hasSize(1)

        val created = fakeDocumentDao.documents.first()
        repository.deleteDocument(created)

        assertThat(fakeDocumentDao.documents).hasSize(0)
        assertThat(fakePageDao.pages).isEmpty()
    }

    @Test
    fun delete_document_also_deletes_pages() = runTest {
        repository.addPage(documentId, 0, fakePage)
        repository.addPage(documentId, 1, fakePage)

        assertThat(fakePageDao.pages).hasSize(2)

        val document = requireNotNull(fakeDocumentDao.documents.first())

        repository.deleteDocument(document)

        assertThat(fakeDocumentDao.documents).isEmpty()
        assertThat(fakePageDao.pages).isEmpty()
    }

    @Test
    fun add_page_saves_to_document_pagesIds() = runTest {
        val documentBefore = fakeDocumentDao.documents.first()
        assertThat(documentBefore.pagesIds).hasSize(0)

        repository.addPage(documentId, 0, fakePage)

        val documentAfter = fakeDocumentDao.documents.first()
        assertThat(documentAfter.pagesIds).hasSize(1)
    }

    @Test
    fun add_multiple_pages_preserves_order() = runTest {
        val pageA = repository.addPage(documentId, 0, fakePage)
        val pageB = repository.addPage(documentId, 1, fakePage)
        val pageC = repository.addPage(documentId, 2, fakePage)

        val document = fakeDocumentDao.documents.first()
        assertThat(document.pagesIds).isEqualTo(listOf(pageA, pageB, pageC))
    }

    @Test
    fun add_page_at_index_inserts_at_correct_position() = runTest {
        val pageA = repository.addPage(documentId, 0, fakePage)
        val pageB = repository.addPage(documentId, 1, fakePage)
        val pageC = repository.addPage(documentId, 1, fakePage)

        val document = fakeDocumentDao.documents.first()
        assertThat(document.pagesIds).isEqualTo(listOf(pageA, pageC, pageB))
    }

    @Test
    fun remove_page_deletes_from_pagesIds() = runTest {
        val pageId = repository.addPage(documentId, 0, fakePage)

        val documentBefore = fakeDocumentDao.documents.first()
        assertThat(documentBefore.pagesIds).hasSize(1)

        repository.removePage(documentId, pageId)

        val documentAfter = fakeDocumentDao.documents.first()
        assertThat(documentAfter.pagesIds).isEmpty()
    }

    @Test
    fun remove_page_deletes_from_pageDao() = runTest {
        val pageId = repository.addPage(documentId, 0, fakePage)

        assertThat(fakePageDao.pages).hasSize(1)

        repository.removePage(documentId, pageId)

        assertThat(fakePageDao.pages).isEmpty()
    }

    @Test
    fun move_page_changes_order() = runTest {
        val pageA = repository.addPage(documentId, 0, fakePage)
        val pageB = repository.addPage(documentId, 1, fakePage)
        val pageC = repository.addPage(documentId, 2, fakePage)

        repository.movePage(documentId, 0, pageC)

        val document = fakeDocumentDao.documents.first()
        assertThat(document.pagesIds).isEqualTo(listOf(pageC, pageA, pageB))
    }

    @Test
    fun get_pages_returns_pages_in_pagesIds_order() = runTest {
        val pageA = repository.addPage(documentId, 0, fakePage)
        val pageB = repository.addPage(documentId, 1, fakePage)

        repository.movePage(documentId, 0, pageB)

        val document = requireNotNull(repository.getDocument(documentId).first())

        assertThat(document.pagesIds).isEqualTo(listOf(pageB, pageA))

        val pages = repository.getPages(document.pagesIds)
        assertThat(pages.map { it.id }).isEqualTo(listOf(pageB, pageA))
    }

    @Test
    fun add_pages_batch_saves_all_to_document() = runTest {
        val pages = listOf(fakePage, fakePage, fakePage)
        val ids = repository.addPages(documentId, 0, pages)

        assertThat(ids).hasSize(3)
        assertThat(fakePageDao.pages).hasSize(3)

        val document = fakeDocumentDao.documents.first()
        assertThat(document.pagesIds).isEqualTo(ids)
    }

    @Test
    fun add_pages_batch_at_offset_inserts_at_correct_position() = runTest {
        val pageA = repository.addPage(documentId, 0, fakePage)
        val pageB = repository.addPage(documentId, 1, fakePage)

        val newIds = repository.addPages(documentId, 1, listOf(fakePage, fakePage))

        val document = fakeDocumentDao.documents.first()
        assertThat(document.pagesIds).isEqualTo(listOf(pageA) + newIds + listOf(pageB))
    }

    private suspend fun createDocument(): Long {
        return repository.createDocument(
            title = fakeDocument.title,
            coverColor = fakeDocument.coverColor,
            spineColor = fakeDocument.spineColor,
            bookmarkColor = fakeDocument.bookmarkColor,
        )
    }
}
