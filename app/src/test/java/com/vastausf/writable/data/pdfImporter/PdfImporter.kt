package com.vastausf.writable.data.pdfImporter

import android.net.Uri
import com.google.common.truth.Truth.assertThat
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Test

class PdfImporterTest {
    private val fakeStorage = FakeFileStorage()
    private val renderer = FakePdfPageRenderer()
    private val importer = PdfImporter(
        context = mockk(),
        fileStorage = fakeStorage,
        pageRenderer = renderer,
    )

    private val fakeUri = mockk<Uri>()
    private val handle = PdfImporter.PdfHandle(
        uri = fakeUri,
        fileName = "fake.pdf",
        pageCount = 42,
    )

    @Test
    fun import_create_file_for_each_page() = runTest {
        renderer.pageCount = 3

        val indices = listOf(10L, 11L, 30L)
        importer.import(handle, indices)

        assertThat(fakeStorage.files.keys).containsExactly(
            "page_10.png", "page_11.png", "page_30.png"
        )
    }

    @Test
    fun import_returns_files_in_order() = runTest {
        renderer.pageCount = 2

        val files = importer.import(handle, listOf(5L, 10L))

        assertThat(files).hasSize(2)
        assertThat(files[0].name).startsWith("page_5.png")
        assertThat(files[1].name).startsWith("page_10.png")
    }

    @Test
    fun import_returns_empty_when_document_not_opened() = runTest {
        renderer.shouldFail = true

        val files = importer.import(handle, listOf(1L))

        assertThat(files).isEmpty()
    }

    @Test
    fun delete_all_removes_files() = runTest {
        renderer.pageCount = 2

        importer.import(handle, listOf(1L, 2L))

        assertThat(fakeStorage.files).hasSize(2)

        importer.deleteAll(listOf(1L, 2L))

        assertThat(fakeStorage.files).isEmpty()
    }

    @Test
    fun delete_all_ignores_nonexistent_files() {
        importer.deleteAll(listOf(999L))
    }
}