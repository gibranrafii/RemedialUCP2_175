package com.example.remeducp2.room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface KategoriDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertKategori(kategori: Kategori): Long

    @Update
    suspend fun updateKategori(kategori: Kategori)

    @Delete
    suspend fun deleteKategori(kategori: Kategori)

    @Query("SELECT * FROM kategori WHERE idKategori = :id")
    fun getKategoriById(id: Int): Flow<Kategori?>

    @Query("SELECT * FROM kategori")
    fun getAllKategori(): Flow<List<Kategori>>

    @Query("SELECT * FROM kategori WHERE parentId = :parentId")
    fun getSubKategoris(parentId: Int): Flow<List<Kategori>>
    
    // Non-Flow version for internal logic checks
    @Query("SELECT * FROM kategori WHERE idKategori = :id")
    suspend fun getKategoriByIdSync(id: Int): Kategori?

    @Query("SELECT * FROM kategori WHERE parentId = :parentId")
    suspend fun getSubKategorisSync(parentId: Int): List<Kategori>
}
