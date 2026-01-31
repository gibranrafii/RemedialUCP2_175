package com.example.remeducp2.room

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "fisik_buku",
    foreignKeys = [
        ForeignKey(
            entity = Buku::class,
            parentColumns = ["idBuku"],
            childColumns = ["idBukuInduk"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["idBukuInduk"])]
)
data class FisikBuku(
    @PrimaryKey(autoGenerate = true)
    val idFisik: Int = 0,
    val idBukuInduk: Int,
    val kodeInventaris: String,
    val status: String,
    val kondisi: String
)
