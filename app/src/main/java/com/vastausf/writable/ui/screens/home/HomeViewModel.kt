package com.vastausf.writable.ui.screens.home

import android.net.Uri
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vastausf.writable.data.db.entry.DocumentEntity
import com.vastausf.writable.data.db.entry.PageEntity
import com.vastausf.writable.data.db.entry.PageType
import com.vastausf.writable.data.pdfImporter.PdfImporter
import com.vastausf.writable.data.repository.DocumentRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: DocumentRepository,
    private val pdfImporter: PdfImporter,
) : ViewModel() {
    val documents = repository
        .getAllDocuments()
        .map { list -> list.sortedByDescending { it.openedAt } }
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    fun createDocument(
        title: String,
        coverColor: Int,
        spineColor: Int,
        bookmarkColor: Int,
    ) = viewModelScope.launch {
        repository.createDocument(
            title = title,
            coverColor = coverColor,
            spineColor = spineColor,
            bookmarkColor = bookmarkColor,
        )
    }

    fun importDocument(
        uri: Uri,
        coverColor: Int,
        spineColor: Int,
        bookmarkColor: Int,
    ) = viewModelScope.launch {
        val pdfHandle = pdfImporter.parse(uri) ?: return@launch

        val documentId = repository.createDocument(
            title = pdfHandle.fileName,
            coverColor = coverColor,
            spineColor = spineColor,
            bookmarkColor = bookmarkColor,
        )

        val pageIds = repository
            .addPages(documentId, 0, (0 until pdfHandle.pageCount).map {
                PageEntity(
                    type = PageType.Image,
                )
            })

        val pagesUri = pdfImporter
            .import(pdfHandle, pageIds)
            .map { it.toUri().toString() }

        repository.updatePagesUri(pageIds, pagesUri)
    }

    fun deleteDocument(document: DocumentEntity) = viewModelScope.launch {
        repository.deleteDocument(document)

        pdfImporter.deleteAll(document.pagesIds)
    }

    fun updateDocument(document: DocumentEntity) = viewModelScope.launch {
        repository.updateDocument(document)
    }
}