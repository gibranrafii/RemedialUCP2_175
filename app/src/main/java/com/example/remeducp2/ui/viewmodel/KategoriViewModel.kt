package com.example.remeducp2.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.remeducp2.repositori.RepositoriPerpustakaan
import com.example.remeducp2.room.Kategori
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class KategoriViewModel(private val repositori: RepositoriPerpustakaan) : ViewModel() {
    var uiState by mutableStateOf(KategoriUiState())
        private set

    // For selecting Parent Category
    val existingCategories: StateFlow<List<Kategori>> = repositori.getAllKategori()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun updateUiState(details: KategoriDetails) {
        uiState = KategoriUiState(details = details, isEntryValid = validateInput(details))
    }

    suspend fun saveKategori() {
        if (validateInput(uiState.details)) {
            repositori.insertKategori(uiState.details.toKategori())
        }
    }

    private fun validateInput(details: KategoriDetails = this.uiState.details): Boolean {
        return details.nama.isNotBlank()
    }
}

data class KategoriUiState(
    val details: KategoriDetails = KategoriDetails(),
    val isEntryValid: Boolean = false
)

data class KategoriDetails(
    val id: Int = 0,
    val nama: String = "",
    val deskripsi: String = "",
    val parentId: Int? = null
)

fun KategoriDetails.toKategori(): Kategori = Kategori(
    idKategori = id,
    nama = nama,
    deskripsi = deskripsi,
    parentId = parentId
)
