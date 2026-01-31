package com.example.remeducp2.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        Buku::class,
        Kategori::class,
        Penulis::class,
        BukuPenulisCrossRef::class,
        FisikBuku::class,
        AuditLog::class
    ],
    version = 1,
    exportSchema = false
)
abstract class PerpustakaanDatabase : RoomDatabase() {
    abstract fun bukuDao(): BukuDao
    abstract fun kategoriDao(): KategoriDao
    abstract fun penulisDao(): PenulisDao
    abstract fun fisikBukuDao(): FisikBukuDao
    abstract fun auditDao(): AuditDao

    companion object {
        @Volatile
        private var Instance: PerpustakaanDatabase? = null

        fun getDatabase(context: Context): PerpustakaanDatabase {
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(context, PerpustakaanDatabase::class.java, "perpustakaan_db")
                    .build()
                    .also { Instance = it }
            }
        }
    }
}
