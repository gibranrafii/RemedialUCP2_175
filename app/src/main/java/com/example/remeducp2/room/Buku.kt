package com.example.remeducp2.room

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "buku",
    foreignKeys = [
        ForeignKey(
            entity = Kategori::class,
            parentColumns = ["idKategori"],
            childColumns = ["idKategori"],
            onDelete = ForeignKey.RESTRICT
        )
    ],
    indices = [Index(value = ["idKategori"])]
)
data class Buku(
    @PrimaryKey(autoGenerate = true)
    val idBuku: Int = 0,
    val judul: String,
    val isbn: String,
    val penerbit: String,
    val tahunTerbit: Int,
    val idKategori: Int? = null
)
