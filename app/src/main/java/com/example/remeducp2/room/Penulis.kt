package com.example.remeducp2.room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "penulis")
data class Penulis(
    @PrimaryKey(autoGenerate = true)
    val idPenulis: Int = 0,
    val nama: String,
    val biografi: String = ""
)
