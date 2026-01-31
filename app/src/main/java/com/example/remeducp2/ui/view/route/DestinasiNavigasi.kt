package com.example.remeducp2.ui.view.route

interface DestinasiNavigasi {
    val route: String
    val titleRes: String
}

object DestinasiHome : DestinasiNavigasi {
    override val route = "home"
    override val titleRes = "Beranda"
}

object DestinasiEntryBuku : DestinasiNavigasi {
    override val route = "entry_buku"
    override val titleRes = "Tambah Buku"
}

object DestinasiEntryKategori : DestinasiNavigasi {
    override val route = "entry_kategori"
    override val titleRes = "Tambah Kategori"
}

object DestinasiDetailBuku : DestinasiNavigasi {
    override val route = "detail_buku"
    override val titleRes = "Detail Buku"
    const val idBukuArg = "idBuku"
    val routeWithArgs = "$route/{$idBukuArg}"
}

object DestinasiEditBuku : DestinasiNavigasi {
    override val route = "edit_buku"
    override val titleRes = "Edit Buku"
    const val idBukuArg = "idBuku"
    val routeWithArgs = "$route/{$idBukuArg}"
}
