package com.example.remeducp2.room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface FisikBukuDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertFisikBuku(fisikBuku: FisikBuku)

    @Update
    suspend fun updateFisikBuku(fisikBuku: FisikBuku)

    @Delete
    suspend fun deleteFisikBuku(fisikBuku: FisikBuku)

    @Query("SELECT * FROM fisik_buku WHERE idBukuInduk = :bukuId")
    fun getFisikBukuByBukuId(bukuId: Int): Flow<List<FisikBuku>>

    @Query("UPDATE fisik_buku SET status = :newStatus WHERE idFisik = :id")
    suspend fun updateStatus(id: Int, newStatus: String)
}
