package com.example.remeducp2.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.remeducp2.repositori.RepositoriPerpustakaan
import com.example.remeducp2.room.Buku
import com.example.remeducp2.room.Kategori
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class HomeUiState(
    val listBuku: List<Buku> = listOf(),
    val listKategori: List<Kategori> = listOf(),
    val selectedKategori: Kategori? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)

class HomeViewModel(
    private val repositori: RepositoriPerpustakaan
) : ViewModel() {

    private val _selectedKategoriId = MutableStateFlow<Int?>(null)

    // Combine flows to produce UI state
    val homeUiState: StateFlow<HomeUiState> = combine(
        repositori.getAllKategori(),
        _selectedKategoriId
    ) { categories, selectedId ->
        Pair(categories, selectedId)
    }.combine(repositori.getAllBuku()) { (categories, selectedId), allBooks ->
        // Note: Real implementation might use a separate flow for filtered books to avoid fetching ALL books first
        // But for simplicity/logic demonstration we use the logic here or switch flow.
        // Actually, let's switch flow based on selection to use the recursive query.
        HomeUiState(
            listKategori = categories,
            listBuku = allBooks, // Placeholder, will be updated by collect logic below
            selectedKategori = categories.find { it.idKategori == selectedId }
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = HomeUiState(isLoading = true)
    )

    // Separate flow for Books that reacts to selection
    val books: StateFlow<List<Buku>> = _selectedKategoriId
        .flatMapLatest { id ->
            if (id == null) {
                repositori.getAllBuku()
            } else {
                repositori.getBukuByKategoriRecursive(id)
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun selectKategori(id: Int?) {
        _selectedKategoriId.value = id
    }

    fun deleteBuku(buku: Buku) {
        viewModelScope.launch {
            repositori.deleteBuku(buku)
        }
    }

    // Deletion State
    var deleteConfirmationRequired by mutableStateOf<Kategori?>(null)
        private set
    var deleteConflictMessage by mutableStateOf<String?>(null)
        private set

    fun deleteKategori(kategori: Kategori) {
        viewModelScope.launch {
            repositori.deleteKategori(
                kategori = kategori,
                onConflict = { message ->
                    deleteConflictMessage = message
                },
                onConfirmationNeeded = {
                    deleteConfirmationRequired = kategori
                }
            )
        }
    }

    fun confirmDeleteKategori(deleteBooks: Boolean) {
        viewModelScope.launch {
            deleteConfirmationRequired?.let { kategori ->
                repositori.confirmDeleteKategori(kategori, deleteBooks)
                deleteConfirmationRequired = null
            }
        }
    }

    fun dismissDialogs() {
        deleteConfirmationRequired = null
        deleteConflictMessage = null
    }
}