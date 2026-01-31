package com.example.remeducp2.room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface BukuDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertBuku(buku: Buku): Long

    @Update
    suspend fun updateBuku(buku: Buku)

    @Delete
    suspend fun deleteBuku(buku: Buku)

    @Query("SELECT * FROM buku WHERE idBuku = :id")
    fun getBukuById(id: Int): Flow<Buku?>

    @Query("""
        SELECT penulis.* FROM penulis 
        INNER JOIN buku_penulis_cross_ref ON penulis.idPenulis = buku_penulis_cross_ref.idPenulis
        WHERE buku_penulis_cross_ref.idBuku = :bukuId
    """)
    fun getPenulisByBukuId(bukuId: Int): Flow<List<Penulis>>

    @Query("SELECT * FROM buku")
    fun getAllBuku(): Flow<List<Buku>>

    @Query("SELECT * FROM buku WHERE idKategori = :kategoriId")
    fun getBukuByKategori(kategoriId: Int): Flow<List<Buku>>

    @Query("""
        SELECT COUNT(*) FROM fisik_buku 
        INNER JOIN buku ON fisik_buku.idBukuInduk = buku.idBuku 
        WHERE buku.idKategori = :kategoriId AND fisik_buku.status = 'Dipinjam'
    """)
    suspend fun countBorrowedBooksInCategory(kategoriId: Int): Int
    
    @Query("SELECT * FROM buku WHERE idKategori IN (:kategoriIds)")
    fun getBukuByKategoriList(kategoriIds: List<Int>): Flow<List<Buku>>

    @Query("DELETE FROM buku WHERE idKategori = :kategoriId")
    suspend fun deleteBooksByCategoryId(kategoriId: Int)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertBukuPenulisCrossRef(crossRef: BukuPenulisCrossRef)

    @Query("UPDATE buku SET idKategori = NULL WHERE idKategori = :kategoriId")
    suspend fun updateBooksCategoryToNull(kategoriId: Int)
}
