package com.example.remeducp2.repositori

import com.example.remeducp2.room.AuditDao
import com.example.remeducp2.room.AuditLog
import com.example.remeducp2.room.Buku
import com.example.remeducp2.room.BukuDao
import com.example.remeducp2.room.BukuPenulisCrossRef
import com.example.remeducp2.room.FisikBuku
import com.example.remeducp2.room.FisikBukuDao
import com.example.remeducp2.room.Kategori
import com.example.remeducp2.room.KategoriDao
import com.example.remeducp2.room.Penulis
import com.example.remeducp2.room.PenulisDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import java.util.LinkedList
import java.util.Queue

class RepositoriPerpustakaanImpl(
    private val bukuDao: BukuDao,
    private val kategoriDao: KategoriDao,
    private val auditDao: AuditDao,
    private val penulisDao: PenulisDao,
    private val fisikBukuDao: FisikBukuDao
) : RepositoriPerpustakaan {

    // --- Kategori Logic ---

    override fun getAllKategori(): Flow<List<Kategori>> = kategoriDao.getAllKategori()

    override fun getKategoriById(id: Int): Flow<Kategori?> = kategoriDao.getKategoriById(id)

    override suspend fun insertKategori(kategori: Kategori) {
        val id = kategoriDao.insertKategori(kategori)
        auditDao.insertLog(AuditLog(
            tableName = "kategori",
            recordId = id.toString(),
            actionType = "INSERT",
            newData = kategori.nama
        ))
    }

    override suspend fun updateKategori(kategori: Kategori) {
        if (kategori.parentId != null) {
            if (isCyclic(kategori.idKategori, kategori.parentId)) {
                throw IllegalArgumentException("Terdeteksi Cyclic Reference pada struktur kategori!")
            }
        }

        kategoriDao.updateKategori(kategori)
        auditDao.insertLog(AuditLog(
            tableName = "kategori",
            recordId = kategori.idKategori.toString(),
            actionType = "UPDATE",
            newData = kategori.toString()
        ))
    }

    private suspend fun isCyclic(entityId: Int, targetParentId: Int): Boolean {
        if (entityId == targetParentId) return true
        
        val queue: Queue<Int> = LinkedList()
        queue.add(entityId)
        
        while(queue.isNotEmpty()) {
            val currentId = queue.poll() ?: continue
            val children = kategoriDao.getSubKategorisSync(currentId)
            for (child in children) {
                if (child.idKategori == targetParentId) return true
                queue.add(child.idKategori)
            }
        }
        return false
    }

    override suspend fun deleteKategori(kategori: Kategori, onConflict: (String) -> Unit, onConfirmationNeeded: () -> Unit) {
        val borrowedCount = bukuDao.countBorrowedBooksInCategory(kategori.idKategori)
        if (borrowedCount > 0) {
            onConflict("Tidak dapat menghapus kategori. Terdapat $borrowedCount buku yang sedang dipinjam dalam kategori ini.")
            return
        }
        onConfirmationNeeded()
    }

    override suspend fun confirmDeleteKategori(kategori: Kategori, deleteBooks: Boolean) {
        if (deleteBooks) {
            bukuDao.deleteBooksByCategoryId(kategori.idKategori)
        } else {
            bukuDao.updateBooksCategoryToNull(kategori.idKategori)
        }
        
        kategoriDao.deleteKategori(kategori)
        
        auditDao.insertLog(AuditLog(
            tableName = "kategori",
            recordId = kategori.idKategori.toString(),
            actionType = "DELETE"
        ))
    }


    // --- Buku Logic ---

    override fun getAllBuku(): Flow<List<Buku>> = bukuDao.getAllBuku()

    override fun getBukuById(id: Int): Flow<Buku?> = bukuDao.getBukuById(id)
    
    override fun getPenulisByBukuId(bukuId: Int): Flow<List<Penulis>> = bukuDao.getPenulisByBukuId(bukuId)

    override suspend fun insertBuku(buku: Buku) {
        bukuDao.insertBuku(buku)
    }

    override suspend fun insertBukuWithPenulis(buku: Buku, namaPenulis: String, kategoriId: Int?) {
        var penulisId: Int = 0
        val existingPenulisCheck = penulisDao.getPenulisByName(namaPenulis)
        if (existingPenulisCheck != null) {
            penulisId = existingPenulisCheck.idPenulis
        } else {
            val newPenulis = Penulis(nama = namaPenulis, biografi = "Generated auto")
            penulisId = penulisDao.insertPenulis(newPenulis).toInt()
        }

        val bukuId = bukuDao.insertBuku(buku).toInt()

        bukuDao.insertBukuPenulisCrossRef(
            BukuPenulisCrossRef(idBuku = bukuId, idPenulis = penulisId)
        )

        fisikBukuDao.insertFisikBuku(
            FisikBuku(
                idBukuInduk = bukuId,
                kodeInventaris = "INV-${System.currentTimeMillis()}",
                status = "Tersedia",
                kondisi = "Baik"
            )
        )

        auditDao.insertLog(AuditLog(
            tableName = "buku",
            recordId = buku.isbn,
            actionType = "INSERT",
            newData = "Judul: ${buku.judul}, Penulis: $namaPenulis"
        ))
    }

    override suspend fun updateBuku(buku: Buku) {
        bukuDao.updateBuku(buku)
        auditDao.insertLog(AuditLog(
            tableName = "buku",
            recordId = buku.idBuku.toString(),
            actionType = "UPDATE"
        ))
    }

    override suspend fun deleteBuku(buku: Buku) {
        bukuDao.deleteBuku(buku)
        auditDao.insertLog(AuditLog(
            tableName = "buku",
            recordId = buku.idBuku.toString(),
            actionType = "DELETE"
        ))
    }
    
    // --- Fisik Buku Logic ---
    override fun getFisikBukuByBukuId(bukuId: Int): Flow<List<FisikBuku>> = fisikBukuDao.getFisikBukuByBukuId(bukuId)
    
    override suspend fun updateFisikBukuStatus(fisikBukuId: Int, newStatus: String) {
        fisikBukuDao.updateStatus(fisikBukuId, newStatus)
        auditDao.insertLog(AuditLog(
            tableName = "fisik_buku",
            recordId = fisikBukuId.toString(),
            actionType = "UPDATE_STATUS",
            newData = "Status: $newStatus"
        ))
    }
    
    // --- Recursive Search ---
    
    override fun getBukuByKategoriRecursive(kategoriId: Int): Flow<List<Buku>> = flow {
       val allCategoryIds = getAllDescendants(kategoriId)
       allCategoryIds.add(kategoriId)
       bukuDao.getBukuByKategoriList(allCategoryIds).collect {
           emit(it)
       }
    }
    
    private suspend fun getAllDescendants(parentId: Int): MutableList<Int> {
        val descendants = mutableListOf<Int>()
        val queue: Queue<Int> = LinkedList()
        queue.add(parentId)
        
        while(queue.isNotEmpty()) {
            val current = queue.poll() ?: continue
            val children = kategoriDao.getSubKategorisSync(current)
            for(child in children) {
                descendants.add(child.idKategori)
                queue.add(child.idKategori)
            }
        }
        return descendants
    }
}
