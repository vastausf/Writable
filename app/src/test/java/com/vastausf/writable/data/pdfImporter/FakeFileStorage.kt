package com.vastausf.writable.data.pdfImporter

import java.io.File

class FakeFileStorage: FileStorage {
    val files = mutableMapOf<String, ByteArray>()

    override fun getFile(name: String): File {
        val tempFile = File.createTempFile(name, null)

        files[name] = ByteArray(0)

        return tempFile.also { it.deleteOnExit() }
    }

    override fun deleteFile(name: String): Boolean {
        return files.remove(name) != null
    }
}
