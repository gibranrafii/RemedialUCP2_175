package com.example.remeducp2.ui.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.remeducp2.repositori.RepositoriPerpustakaanImpl
import com.example.remeducp2.room.PerpustakaanDatabase

object PenyediaViewModel {
    val Factory = viewModelFactory {
        initializer {
            HomeViewModel(aplikasiPerpustakaan())
        }
        initializer {
            BukuEntryViewModel(aplikasiPerpustakaan())
        }
        initializer {
            KategoriViewModel(aplikasiPerpustakaan())
        }
        initializer {
            BukuDetailViewModel(createSavedStateHandle(), aplikasiPerpustakaan())
        }
        initializer {
            BukuEditViewModel(createSavedStateHandle(), aplikasiPerpustakaan())
        }
    }
}

// Helper to get Repo directly from Context
fun CreationExtras.aplikasiPerpustakaan(): RepositoriPerpustakaanImpl {
    val application = (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as Application)
    val db = PerpustakaanDatabase.getDatabase(application)
    return RepositoriPerpustakaanImpl(
        db.bukuDao(),
        db.kategoriDao(),
        db.auditDao(),
        db.penulisDao(),
        db.fisikBukuDao()
    )
}
