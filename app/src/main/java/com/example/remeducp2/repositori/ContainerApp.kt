package com.example.remeducp2.repositori

import android.content.Context
import androidx.room.Room
import com.example.remeducp2.room.PerpustakaanDatabase

interface AppContainer {
    val repositoriPerpustakaan: RepositoriPerpustakaan
}

class ContainerApp(private val context: Context) : AppContainer {
    override val repositoriPerpustakaan: RepositoriPerpustakaan by lazy {
        val db = PerpustakaanDatabase.getDatabase(context)
        
        RepositoriPerpustakaanImpl(
            bukuDao = db.bukuDao(),
            kategoriDao = db.kategoriDao(),
            auditDao = db.auditDao(),
            penulisDao = db.penulisDao(),
            fisikBukuDao = db.fisikBukuDao()
        )
    }
}
