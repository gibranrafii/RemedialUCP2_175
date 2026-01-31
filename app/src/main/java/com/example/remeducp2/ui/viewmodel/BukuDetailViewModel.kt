package com.example.remeducp2.ui.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.remeducp2.repositori.RepositoriPerpustakaan
import com.example.remeducp2.room.Buku
import com.example.remeducp2.room.Kategori
import com.example.remeducp2.ui.view.route.DestinasiDetailBuku
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class BukuDetailViewModel(
    savedStateHandle: SavedStateHandle,
    private val repositori: RepositoriPerpustakaan
) : ViewModel() {

    private val bukuId: Int = checkNotNull(savedStateHandle[DestinasiDetailBuku.idBukuArg])

    val uiState: StateFlow<BukuDetailUiState> = repositori.getBukuById(bukuId)
        .filterNotNull()
        .combine(repositori.getPenulisByBukuId(bukuId)) { buku, penulisList ->
            Pair(buku, penulisList)
        }
        .combine(repositori.getFisikBukuByBukuId(bukuId)) { (buku, penulisList), fisikList ->
            val namaPenulis = penulisList.joinToString(", ") { it.nama }
            // Assume 1 physical copy for simplicity in this remedial context
            val fisik = fisikList.firstOrNull()
            BukuDetailUiState(
                detailBuku = buku.toDetailBuku(namaPenulis),
                statusFisik = fisik?.status ?: "Tidak Diketahui",
                idFisik = fisik?.idFisik ?: 0,
                isLoading = false
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = BukuDetailUiState(isLoading = true)
        )

    suspend fun deleteBuku() {
        repositori.deleteBuku(uiState.value.detailBuku.toBuku())
    }

    fun toggleStatus() {
        viewModelScope.launch {
            val currentStatus = uiState.value.statusFisik
            val idFisik = uiState.value.idFisik
            if (idFisik != 0) {
                val newStatus = if (currentStatus == "Tersedia") "Dipinjam" else "Tersedia"
                repositori.updateFisikBukuStatus(idFisik, newStatus)
            }
        }
    }
}

data class BukuDetailUiState(
    val detailBuku: BukuDetails = BukuDetails(),
    val statusFisik: String = "Tersedia",
    val idFisik: Int = 0,
    val isLoading: Boolean = false
)

fun Buku.toDetailBuku(penulis: String = ""): BukuDetails = BukuDetails(
    id = idBuku,
    judul = judul,
    isbn = isbn,
    penerbit = penerbit,
    tahun = tahunTerbit.toString(),
    kategoriId = idKategori,
    namaPenulis = penulis
)
