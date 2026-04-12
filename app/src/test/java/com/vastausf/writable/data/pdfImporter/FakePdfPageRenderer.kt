package com.vastausf.writable.data.pdfImporter

import android.net.Uri
import io.mockk.mockk

class FakePdfPageRenderer: PdfPageRenderer {
    var pageCount = 1
    var shouldFail = false

    override fun openDocument(uri: Uri): PdfDocument? {
        if (shouldFail) return null

        return FakePdfDocument(pageCount)
    }
}

class FakePdfDocument(
    override val pageCount: Int,
): PdfDocument {
    override fun renderPage(index: Int): PdfPage = mockk(relaxed = true)

    override fun close() { }
}
