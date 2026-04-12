package com.vastausf.writable.data.pdfImporter

import android.content.Context
import java.io.File
import javax.inject.Inject

interface FileStorage {
    fun getFile(name: String): File

    fun deleteFile(name: String): Boolean
}

class AppFileStorage @Inject constructor(
    private val context: Context
) : FileStorage {
    override fun getFile(name: String): File =
        File(context.filesDir, name)

    override fun deleteFile(name: String): Boolean =
        File(context.filesDir, name).delete()
}
