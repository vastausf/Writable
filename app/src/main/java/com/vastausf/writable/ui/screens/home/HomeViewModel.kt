package com.vastausf.writable.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
) : ViewModel() {
    val documents = repository
        .getAllDocuments()
        .map { list -> list.sortedByDescending { it.openedAt } }
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    fun createDocument(title: String) = viewModelScope.launch {
        repository.createDocument(title)
    }
}