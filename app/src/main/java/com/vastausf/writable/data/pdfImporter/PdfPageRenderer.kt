package com.vastausf.writable.data.pdfImporter

import android.content.Context
import android.net.Uri
import javax.inject.Inject

interface PdfPageRenderer {
    fun openDocument(uri: Uri): PdfDocument?
}

class AndroidPdfPageRenderer @Inject constructor(
    private val context: Context,
): PdfPageRenderer {
    override fun openDocument(uri: Uri): PdfDocument? {
        val fileDescriptor = context.contentResolver.openFileDescriptor(uri, "r") ?: return null

        return try {
            AndroidPdfDocument(fileDescriptor)
        } catch (e: Exception) {
            fileDescriptor.close()
            null
        }
    }
}
