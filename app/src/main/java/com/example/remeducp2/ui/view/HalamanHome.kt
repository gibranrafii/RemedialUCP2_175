package com.example.remeducp2.ui.view

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.remeducp2.room.Buku
import com.example.remeducp2.room.Kategori
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import com.example.remeducp2.ui.view.route.DestinasiHome
import com.example.remeducp2.ui.viewmodel.HomeViewModel
import com.example.remeducp2.ui.viewmodel.PenyediaViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HalamanHome(
    navigateToItemEntry: () -> Unit,
    navigateToCategoryEntry: () -> Unit,
    navigateToDetail: (Int) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = viewModel(factory = PenyediaViewModel.Factory)
) {
    val homeUiState by viewModel.homeUiState.collectAsState()
    val books by viewModel.books.collectAsState()

    // Dialogs
    if (viewModel.deleteConflictMessage != null) {
        AlertDialog(
            onDismissRequest = viewModel::dismissDialogs,
            title = { Text("Peringatan") },
            text = { Text(viewModel.deleteConflictMessage ?: "") },
            confirmButton = {
                TextButton(onClick = viewModel::dismissDialogs) { Text("OK") }
            }
        )
    }

    if (viewModel.deleteConfirmationRequired != null) {
        AlertDialog(
            onDismissRequest = viewModel::dismissDialogs,
            title = { Text("Hapus Kategori") },
            text = { Text("Kategori ini memiliki buku yang tersedia. Hapus buku juga atau jadikan Tanpa Kategori?") },
            confirmButton = {
                TextButton(onClick = { viewModel.confirmDeleteKategori(deleteBooks = true) }) {
                    Text("Hapus Buku")
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.confirmDeleteKategori(deleteBooks = false) }) {
                    Text("Tanpa Kategori")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(DestinasiHome.titleRes) }
            )
        },
        floatingActionButton = {
            Column(horizontalAlignment = Alignment.End) {
                FloatingActionButton(
                    onClick = navigateToCategoryEntry,
                    modifier = Modifier.padding(bottom = 16.dp),
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                ) {
                    Text("+ Kat", modifier = Modifier.padding(8.dp))
                }
                FloatingActionButton(onClick = navigateToItemEntry) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = "Entry Buku")
                }
            }
        }
    ) { innerPadding ->
        HomeBody(
            bookList = books,
            categoryList = homeUiState.listKategori,
            selectedCategory = homeUiState.selectedKategori,
            onCategorySelected = viewModel::selectKategori,
            onCategoryLongPress = viewModel::deleteKategori,
            onBookClick = {
                navigateToDetail(it)
            },
            modifier = modifier.padding(innerPadding)
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HomeBody(
    bookList: List<Buku>,
    categoryList: List<Kategori>,
    selectedCategory: Kategori?,
    onCategorySelected: (Int?) -> Unit,
    onCategoryLongPress: (Kategori) -> Unit,
    onBookClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxSize()) {
        LazyRow(
            contentPadding = PaddingValues(16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                FilterChip(
                    selected = selectedCategory == null,
                    onClick = { onCategorySelected(null) },
                    label = { Text("Semua") }
                )
            }
            items(categoryList) { category ->
                FilterChip(
                    selected = selectedCategory?.idKategori == category.idKategori,
                    onClick = { onCategorySelected(category.idKategori) },
                    label = {
                         Text(category.nama)
                    },
                )
            }
        }
        
        // Alternative: Category Management List
        if (selectedCategory != null) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                 Text("Kategori terpilih: ${selectedCategory.nama}", style = MaterialTheme.typography.labelLarge)
                 Button(
                     onClick = { onCategoryLongPress(selectedCategory) },
                     colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                 ) {
                     Text("Hapus")
                 }
            }
        }

        if (bookList.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Tidak ada buku", style = MaterialTheme.typography.bodyLarge)
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(bookList) { buku ->
                    BukuCard(buku = buku, onClick = { onBookClick(buku.idBuku) })
                }
            }
        }
    }
}

@Composable
fun BukuCard(
    buku: Buku,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        onClick = onClick,
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = buku.judul,
                style = MaterialTheme.typography.titleLarge,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Penerbit: ${buku.penerbit}",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "Tahun: ${buku.tahunTerbit}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
