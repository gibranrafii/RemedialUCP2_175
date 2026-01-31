package com.example.remeducp2.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.remeducp2.repositori.RepositoriPerpustakaan
import com.example.remeducp2.room.Kategori
import com.example.remeducp2.ui.view.route.DestinasiEditBuku
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class BukuEditViewModel(
    savedStateHandle: SavedStateHandle,
    private val repositori: RepositoriPerpustakaan
) : ViewModel() {

    var uiState by mutableStateOf(BukuUiState())
        private set

    private val bukuId: Int = checkNotNull(savedStateHandle[DestinasiEditBuku.idBukuArg])

    val kategoriList: StateFlow<List<Kategori>> = repositori.getAllKategori()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    init {
        viewModelScope.launch {
            val buku = repositori.getBukuById(bukuId)
                .filterNotNull()
                .first()
            uiState = BukuUiState(
                bukuDetails = buku.toDetailBuku(),
                isEntryValid = true
            )
        }
    }

    fun updateUiState(details: BukuDetails) {
        uiState = BukuUiState(
            bukuDetails = details,
            isEntryValid = validateInput(details)
        )
    }

    suspend fun updateBuku() {
        if (validateInput(uiState.bukuDetails)) {
            repositori.updateBuku(uiState.bukuDetails.toBuku())
        }
    }

    private fun validateInput(uiState: BukuDetails = this.uiState.bukuDetails): Boolean {
        return with(uiState) {
            judul.isNotBlank() &&
            isbn.isNotBlank() &&
            penerbit.isNotBlank() &&
            tahun.isNotBlank() && tahun.toIntOrNull() != null &&
            kategoriId != null
        }
    }
}
