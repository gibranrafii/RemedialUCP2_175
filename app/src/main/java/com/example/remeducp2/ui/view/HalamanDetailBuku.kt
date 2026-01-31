package com.example.remeducp2.ui.view

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.remeducp2.room.Buku
import com.example.remeducp2.room.PerpustakaanDatabase
import com.example.remeducp2.ui.view.route.DestinasiDetailBuku
import com.example.remeducp2.ui.viewmodel.BukuDetailUiState
import com.example.remeducp2.ui.viewmodel.BukuDetailViewModel
import com.example.remeducp2.ui.viewmodel.BukuDetails
import com.example.remeducp2.ui.viewmodel.PenyediaViewModel
import com.example.remeducp2.ui.viewmodel.toBuku
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HalamanDetailBuku(
    navigateToEditItem: (Int) -> Unit,
    navigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: BukuDetailViewModel = viewModel(factory = PenyediaViewModel.Factory)
) {
    val uiState = viewModel.uiState.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    
    // Layout and UI Setup
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(DestinasiDetailBuku.titleRes) },
                navigationIcon = {
                    IconButton(onClick = navigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navigateToEditItem(uiState.value.detailBuku.id) },
                shape = MaterialTheme.shapes.medium,
                modifier = Modifier.padding(18.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Edit Buku"
                )
            }
        }
    ) { innerPadding ->
        ItemDetailsBody(
            bukuDetailUiState = uiState.value,
            onDelete = {
                coroutineScope.launch {
                    viewModel.deleteBuku()
                    navigateBack()
                }
            },
            modifier = modifier.padding(innerPadding),
            onToggleStatus = { viewModel.toggleStatus() }
        )
    }
}

@Composable
fun ItemDetailsBody(
    bukuDetailUiState: BukuDetailUiState,
    onDelete: () -> Unit,
    onToggleStatus: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(16.dp)
    ) {
        var deleteConfirmationRequired by rememberSaveable { mutableStateOf(false) }

        ItemDetails(
            buku = bukuDetailUiState.detailBuku,
            statusFisik = bukuDetailUiState.statusFisik,
            onToggleStatus = onToggleStatus,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.padding(8.dp))
        OutlinedButton(
            onClick = { deleteConfirmationRequired = true },
            shape = MaterialTheme.shapes.small,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = MaterialTheme.colorScheme.error
            )
        ) {
            Text("Hapus Buku")
        }

        if (deleteConfirmationRequired) {
            DeleteConfirmationDialog(
                onDeleteConfirm = {
                    deleteConfirmationRequired = false
                    onDelete()
                },
                onDeleteCancel = { deleteConfirmationRequired = false }
            )
        }
    }
}

@Composable
fun ItemDetails(
    buku: BukuDetails,
    statusFisik: String,
    onToggleStatus: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
        ) {
            ItemDetailRow("Judul", buku.judul)
            ItemDetailRow("Penulis", buku.namaPenulis)
            ItemDetailRow("ISBN", buku.isbn)
            ItemDetailRow("Penerbit", buku.penerbit)
            ItemDetailRow("Tahun", buku.tahun)
            ItemDetailRow("ID Kategori", buku.kategoriId?.toString() ?: "Tanpa Kategori")
            
            Spacer(modifier = Modifier.padding(8.dp))
            Text(text = "Status Fisik", fontWeight = FontWeight.Bold)
            Text(text = statusFisik, color = if (statusFisik == "Tersedia") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error)
            
            Button(
                onClick = onToggleStatus,
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
            ) {
                Text(if (statusFisik == "Tersedia") "Pinjam Buku" else "Kembalikan Buku")
            }
        }
    }
}

@Composable
fun ItemDetailRow(
    label: String,
    value: String
) {
    Column(modifier = Modifier.padding(vertical = 4.dp)) {
        Text(text = label, fontWeight = FontWeight.SemiBold)
        Text(text = value)
    }
}

@Composable
fun DeleteConfirmationDialog(
    onDeleteConfirm: () -> Unit,
    onDeleteCancel: () -> Unit,
    modifier: Modifier = Modifier
) {
    AlertDialog(
        onDismissRequest = { /* Do nothing */ },
        title = { Text("Peringatan") },
        text = { Text("Apakah anda yakin ingin menghapus buku ini?") },
        modifier = modifier,
        dismissButton = {
            TextButton(onClick = onDeleteCancel) {
                Text("Batal")
            }
        },
        confirmButton = {
            TextButton(onClick = onDeleteConfirm) {
                Text("Ya")
            }
        }
    )
}
