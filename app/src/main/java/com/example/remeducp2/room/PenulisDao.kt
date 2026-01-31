package com.example.remeducp2.room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface PenulisDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertPenulis(penulis: Penulis): Long

    @Update
    suspend fun updatePenulis(penulis: Penulis)

    @Delete
    suspend fun deletePenulis(penulis: Penulis)

    @Query("SELECT * FROM penulis ORDER BY nama ASC")
    fun getAllPenulis(): Flow<List<Penulis>>
    
    @Query("SELECT * FROM penulis WHERE idPenulis = :id")
    fun getPenulisById(id: Int): Flow<Penulis>

    @Query("SELECT * FROM penulis WHERE nama = :name LIMIT 1")
    suspend fun getPenulisByName(name: String): Penulis?
}
