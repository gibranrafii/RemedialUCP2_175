package com.example.remeducp2.repositori

import com.example.remeducp2.room.Buku
import com.example.remeducp2.room.FisikBuku
import com.example.remeducp2.room.Kategori
import com.example.remeducp2.room.Penulis
import kotlinx.coroutines.flow.Flow

interface RepositoriPerpustakaan {
    // Kategori
    fun getAllKategori(): Flow<List<Kategori>>
    fun getKategoriById(id: Int): Flow<Kategori?>
    suspend fun insertKategori(kategori: Kategori)
    suspend fun updateKategori(kategori: Kategori)
    suspend fun deleteKategori(kategori: Kategori, onConflict: (String) -> Unit, onConfirmationNeeded: () -> Unit)
    suspend fun confirmDeleteKategori(kategori: Kategori, deleteBooks: Boolean)

    // Buku
    fun getAllBuku(): Flow<List<Buku>>
    fun getBukuById(id: Int): Flow<Buku?>
    fun getPenulisByBukuId(bukuId: Int): Flow<List<Penulis>>
    suspend fun insertBuku(buku: Buku)
    suspend fun insertBukuWithPenulis(buku: Buku, namaPenulis: String, kategoriId: Int?)
    suspend fun updateBuku(buku: Buku)
    suspend fun deleteBuku(buku: Buku)

    // Fisik Buku
    fun getFisikBukuByBukuId(bukuId: Int): Flow<List<FisikBuku>>
    suspend fun updateFisikBukuStatus(fisikBukuId: Int, newStatus: String)

    // Recursive Search
    fun getBukuByKategoriRecursive(kategoriId: Int): Flow<List<Buku>>
}
