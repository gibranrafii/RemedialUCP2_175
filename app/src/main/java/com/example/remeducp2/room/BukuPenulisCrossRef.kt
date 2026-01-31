package com.example.remeducp2.room

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "buku_penulis_cross_ref",
    primaryKeys = ["idBuku", "idPenulis"],
    foreignKeys = [
        ForeignKey(
            entity = Buku::class,
            parentColumns = ["idBuku"],
            childColumns = ["idBuku"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Penulis::class,
            parentColumns = ["idPenulis"],
            childColumns = ["idPenulis"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["idBuku"]), Index(value = ["idPenulis"])]
)
data class BukuPenulisCrossRef(
    val idBuku: Int,
    val idPenulis: Int
)
