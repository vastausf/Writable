package com.vastausf.writable.ui.screens.editor

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vastausf.writable.data.db.entry.DocumentEntity
import com.vastausf.writable.data.db.entry.PageEntity
import com.vastausf.writable.data.repository.DocumentRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditorViewModel @Inject constructor(
    private val repository: DocumentRepository,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {
    private val documentId: Long = checkNotNull(savedStateHandle[EditorHandles.DOCUMENT_ID])

    private val _document = MutableStateFlow<DocumentEntity?>(null)
    val document = _document.asStateFlow()

    private val _pages = MutableStateFlow<Map<Int, PageEntity>>(emptyMap())
    val pages = _pages.asStateFlow()

    private val visibleIndices = MutableStateFlow<List<Int>>(emptyList())

    init {
        viewModelScope.launch {
            repository.updateLastOpened(documentId)
        }

        viewModelScope.launch {
            repository.getDocument(documentId)
                .collect { document ->
                    _document.value = document
                }
        }

        viewModelScope.launch {
            visibleIndices
                .collect { indices ->
                    if (indices.isEmpty()) return@collect

                    loadVisiblePages(indices)
                }
        }
    }

    fun onVisiblePagesChanged(indices: List<Int>) {
        visibleIndices.value = indices
    }

    fun addNewPage(index: Int) {
        viewModelScope.launch {
            repository.addPage(documentId, index, PageEntity.blackPage())
        }
    }

    private suspend fun loadVisiblePages(visiblePositions: List<Int>) {
        val document = _document.value ?: return
        val pagesIds = document.pagesIds

        val extendedStart = (visiblePositions.first() - 2).coerceAtLeast(0)
        val extendedEnd = (visiblePositions.last()  + 2).coerceAtMost(pagesIds.lastIndex)

        val alreadyLoaded = _pages.value.keys

        val positionsToLoad = (extendedStart..extendedEnd).filter { it !in alreadyLoaded }
        if (positionsToLoad.isEmpty()) return

        val remap = positionsToLoad.map { pagesIds[it] }

        val loaded = repository.getPages(remap).associateBy { it.id }

        val newEntries = positionsToLoad.mapNotNull { position ->
            val pageId = pagesIds[position]
            loaded[pageId]?.let { position to it }
        }.toMap()

        _pages.update { it + newEntries }
    }
}
