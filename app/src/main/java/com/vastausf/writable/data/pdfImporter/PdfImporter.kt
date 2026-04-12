package com.vastausf.writable.data.pdfImporter

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.provider.OpenableColumns
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject

class PdfImporter @Inject constructor(
    private val context: Context,
    private val fileStorage: FileStorage,
    private val pageRenderer: PdfPageRenderer,
) {
    suspend fun parse(
        uri: Uri,
    ): PdfHandle? = withContext(Dispatchers.IO) {
        val document = pageRenderer.openDocument(uri = uri) ?: return@withContext null
        val fileName = getNameFromUri(uri) ?: return@withContext null

        PdfHandle(
            uri = uri,
            fileName = fileName,
            pageCount = document.pageCount,
        )
    }

    suspend fun import(
        pdfHandle: PdfHandle,
        indices: List<Long>,
    ): List<Pair<File, Float>> = withContext(Dispatchers.IO) {
        val pages = mutableListOf<Pair<File, Float>>()

        val pdfDocument = pageRenderer.openDocument(uri = pdfHandle.uri)

        if (pdfDocument != null) {
            for (i in 0 until pdfDocument.pageCount) {
                val index = indices[i]

                val page = pdfDocument.renderPage(i)

                val file = fileStorage.getFile(idToName(index))
                file.outputStream().use { out ->
                    page.bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
                }
                page.bitmap.recycle()

                pages.add(file to page.ratio())
            }
        }

        pages
    }

    fun deleteAll(pageIds: List<Long>) {
        pageIds
            .forEach { fileStorage.deleteFile(idToName(it)) }
    }

    private fun idToName(id: Long) = "page_$id.png"

    private fun getNameFromUri(uri: Uri): String? {
        return context.contentResolver.query(
            uri,
            arrayOf(OpenableColumns.DISPLAY_NAME),
            null, null, null
        )?.use { cursor ->
            if (cursor.moveToFirst()) {
                cursor.getString(cursor.getColumnIndexOrThrow(OpenableColumns.DISPLAY_NAME))
            } else {
                null
            }
        }
    }

    class PdfHandle(
        val uri: Uri,
        val fileName: String,
        val pageCount: Int,
    )
}
