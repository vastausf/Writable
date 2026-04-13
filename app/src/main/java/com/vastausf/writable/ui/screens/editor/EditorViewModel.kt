package com.vastausf.writable.ui.screens.editor

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vastausf.writable.data.db.entry.DocumentEntity
import com.vastausf.writable.data.db.entry.PageEntity
import com.vastausf.writable.data.pageCanvas.Stroke
import com.vastausf.writable.data.repository.DocumentRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.cbor.Cbor
import kotlinx.serialization.decodeFromByteArray
import javax.inject.Inject

@OptIn(FlowPreview::class)
@HiltViewModel
class EditorViewModel @Inject constructor(
    private val repository: DocumentRepository,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {
    private val documentId: Long = checkNotNull(savedStateHandle[EditorHandles.DOCUMENT_ID])

    private val _document = MutableStateFlow<DocumentEntity?>(null)
    val document = _document.asStateFlow()

    private val _pages = MutableStateFlow<Map<Int, PageState>>(emptyMap())
    val pages = _pages.asStateFlow()

    private val pendingSaves = MutableSharedFlow<Pair<Long, List<Stroke>>>(
        extraBufferCapacity = Channel.UNLIMITED,
    )

    private val visibleIndices = MutableStateFlow<List<Int>>(emptyList())

    init {
        viewModelScope.launch {
            repository.updateLastOpened(documentId)
        }

        viewModelScope.launch {
            repository.getDocument(documentId)
                .collect { document ->
                    _document.value = document

                    loadVisiblePages(visibleIndices.value)
                }
        }

        viewModelScope.launch {
            visibleIndices
                .collect { indices ->
                    if (indices.isEmpty()) return@collect

                    loadVisiblePages(indices)
                }
        }

        viewModelScope.launch {
            pendingSaves
                .debounce(500)
                .collect { (pageId, strokes) ->
                    repository.saveStrokes(pageId, strokes)
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

    fun addStroke(pageId: Long, stroke: Stroke) {
        val position = _pages.value.entries
            .firstOrNull { it.value.page.id == pageId }
            ?.key ?: return

        _pages.update { current ->
            val pageState = current[position] ?: return@update current

            current + (position to pageState.copy(
                strokes = pageState.strokes + stroke,
            ))
        }

        val updatedStrokes = _pages.value[position]?.strokes ?: return
        pendingSaves.tryEmit(pageId to updatedStrokes)
    }

    fun updateStylusStyle(stylusStyle: StylusStyle) {
        val document = document.value ?: return

        viewModelScope.launch {
            repository.updateDocument(document.copy(
                stylusColor = stylusStyle.color,
                stylusWidth = stylusStyle.width,
            ))
        }
    }

    @OptIn(ExperimentalSerializationApi::class)
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
            loaded[pageId]?.let { page ->
                position to PageState(
                    page = page,
                    strokes = page.canvasData?.let { Cbor.decodeFromByteArray(it) } ?: emptyList(),
                )
            }
        }.toMap()

        _pages.update { it + newEntries }
    }
}

data class PageState(
    val page: PageEntity,
    val strokes: List<Stroke> = emptyList(),
)
