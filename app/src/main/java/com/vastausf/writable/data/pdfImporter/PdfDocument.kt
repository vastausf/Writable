package com.vastausf.writable.data.pdfImporter

import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import android.os.ParcelFileDescriptor
import androidx.core.graphics.createBitmap

interface PdfDocument {
    val pageCount: Int

    fun renderPage(index: Int): PdfPage

    fun close()
}

class AndroidPdfDocument(
    private val fileDescriptor: ParcelFileDescriptor,
): PdfDocument {
    private val renderer = PdfRenderer(fileDescriptor)

    override val pageCount: Int
        get() = renderer.pageCount

    override fun renderPage(index: Int): PdfPage {
        val page = renderer.openPage(index)

        val width = page.width * 2
        val height = page.height * 2

        val bitmap = createBitmap(
            width = width,
            height = height,
        )

        page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
        page.close()

        return PdfPage(
            bitmap = bitmap,
            width = width,
            height = height,
        )
    }

    override fun close() {
        renderer.close()
        fileDescriptor.close()
    }
}

data class PdfPage(
    val bitmap: Bitmap,
    val width: Int,
    val height: Int,
) {
    fun ratio(): Float = this.width.toFloat() / this.height.toFloat()
}
