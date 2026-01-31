package com.example.remeducp2.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.remeducp2.repositori.RepositoriPerpustakaan
import com.example.remeducp2.room.Buku
import com.example.remeducp2.room.Kategori
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class BukuEntryViewModel(private val repositori: RepositoriPerpustakaan) : ViewModel() {
    var uiState by mutableStateOf(BukuUiState())
        private set

    val kategoriList: StateFlow<List<Kategori>> = repositori.getAllKategori()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun updateUiState(details: BukuDetails) {
        uiState = BukuUiState(bukuDetails = details, isEntryValid = validateInput(details))
    }

    suspend fun saveBuku() {
        if (validateInput(uiState.bukuDetails)) {
            repositori.insertBukuWithPenulis(
                uiState.bukuDetails.toBuku(),
                uiState.bukuDetails.namaPenulis,
                uiState.bukuDetails.kategoriId
            )
        }
    }

    private fun validateInput(uiState: BukuDetails = this.uiState.bukuDetails): Boolean {
        // Strict Validation: Check types and content
        return with(uiState) {
            judul.isNotBlank() &&
            isbn.isNotBlank() &&
            penerbit.isNotBlank() &&
            tahun.isNotBlank() && tahun.toIntOrNull() != null && // Type validation
            kategoriId != null &&
            namaPenulis.isNotBlank()
        }
    }
}

data class BukuUiState(
    val bukuDetails: BukuDetails = BukuDetails(),
    val isEntryValid: Boolean = false
)

data class BukuDetails(
    val id: Int = 0,
    val judul: String = "",
    val isbn: String = "",
    val penerbit: String = "",
    val tahun: String = "",
    val kategoriId: Int? = null,
    val namaPenulis: String = ""
)

fun BukuDetails.toBuku(): Buku = Buku(
    idBuku = id,
    judul = judul,
    isbn = isbn,
    penerbit = penerbit,
    tahunTerbit = tahun.toIntOrNull() ?: 0,
    idKategori = kategoriId
)
