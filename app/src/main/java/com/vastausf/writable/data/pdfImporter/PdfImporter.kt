package com.vastausf.writable.data.pdfImporter

import android.content.Context
import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import android.net.Uri
import android.provider.OpenableColumns
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject
import androidx.core.graphics.createBitmap

class PdfImporter @Inject constructor(
    private val context: Context,
) {
    suspend fun parse(
        uri: Uri,
    ): PdfHandle? = withContext(Dispatchers.IO) {
        val fileDescriptor = context.contentResolver
            .openFileDescriptor(uri, "r") ?: return@withContext null

        val fileName = context.contentResolver.query(
            uri,
            arrayOf(OpenableColumns.DISPLAY_NAME),
            null, null, null
        )?.use { cursor ->
            if (cursor.moveToFirst()) {
                cursor.getString(cursor.getColumnIndexOrThrow(OpenableColumns.DISPLAY_NAME))
            } else {
                null
            }
        } ?: return@withContext null

        PdfHandle(
            uri = uri,
            fileName = fileName,
            pageCount = fileDescriptor.use { PdfRenderer(it).pageCount },
        )
    }

    suspend fun import(
        pdfHandle: PdfHandle,
        indices: List<Long>,
    ): List<File> = withContext(Dispatchers.IO) {
        val pages = mutableListOf<File>()

        val fileDescriptor = context.contentResolver
            .openFileDescriptor(pdfHandle.uri, "r") ?: return@withContext emptyList()

        fileDescriptor.use { fd ->
            val renderer = PdfRenderer(fd)

            renderer.use {
                for (i in 0 until renderer.pageCount) {
                    val index = indices[i]

                    val page = renderer.openPage(i)

                    val bitmap = createBitmap(
                        width = page.width * 2,
                        height = page.height * 2,
                    )

                    page.render(
                        bitmap,
                        null,
                        null,
                        PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY,
                    )
                    page.close()

                    val file = File(
                        context.filesDir,
                        idToName(index),
                    )
                    file.outputStream().use { out ->
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
                    }
                    bitmap.recycle()

                    pages.add(file)
                }
            }
        }

        pages
    }

    fun deleteAll(pageIds: List<Long>) {
        pageIds
            .map { File(context.filesDir, idToName(it)) }
            .forEach { it.delete() }
    }

    private fun idToName(id: Long) = "page_$id.png"

    class PdfHandle(
        val uri: Uri,
        val fileName: String,
        val pageCount: Int,
    )
}
