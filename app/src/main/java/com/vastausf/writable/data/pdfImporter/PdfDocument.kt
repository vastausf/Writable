package com.vastausf.writable.data.pdfImporter

import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import android.os.ParcelFileDescriptor
import androidx.core.graphics.createBitmap

interface PdfDocument {
    val pageCount: Int

    fun renderPage(index: Int): Bitmap

    fun close()
}

class AndroidPdfDocument(
    private val fileDescriptor: ParcelFileDescriptor,
): PdfDocument {
    private val renderer = PdfRenderer(fileDescriptor)

    override val pageCount: Int
        get() = renderer.pageCount

    override fun renderPage(index: Int): Bitmap {
        val page = renderer.openPage(index)

        val bitmap = createBitmap(
            width = page.width * 2,
            height = page.height * 2,
        )

        page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
        page.close()

        return bitmap
    }

    override fun close() {
        renderer.close()
        fileDescriptor.close()
    }
}
