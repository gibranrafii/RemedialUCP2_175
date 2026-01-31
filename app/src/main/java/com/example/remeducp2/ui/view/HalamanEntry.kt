package com.example.remeducp2.ui.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.remeducp2.room.Kategori
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import com.example.remeducp2.ui.view.route.DestinasiEntryBuku
import com.example.remeducp2.ui.viewmodel.BukuDetails
import com.example.remeducp2.ui.viewmodel.BukuEntryViewModel
import com.example.remeducp2.ui.viewmodel.BukuUiState
import com.example.remeducp2.ui.viewmodel.PenyediaViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HalamanEntryBuku(
    navigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: BukuEntryViewModel = viewModel(factory = PenyediaViewModel.Factory)
) {
    val coroutineScope = rememberCoroutineScope()
    val kategoriList by viewModel.kategoriList.collectAsState()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(DestinasiEntryBuku.titleRes) },
                navigationIcon = {
                    IconButton(onClick = navigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        EntryBody(
            bukuUiState = viewModel.uiState,
            kategoriList = kategoriList,
            onBukuValueChange = viewModel::updateUiState,
            onSaveClick = {
                coroutineScope.launch {
                    viewModel.saveBuku()
                    navigateBack()
                }
            },
            modifier = modifier
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .fillMaxWidth()
        )
    }
}

@Composable
fun EntryBody(
    bukuUiState: BukuUiState,
    kategoriList: List<Kategori>,
    onBukuValueChange: (BukuDetails) -> Unit,
    onSaveClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(20.dp),
        modifier = modifier.padding(16.dp)
    ) {
        FormInput(
            bukuDetails = bukuUiState.bukuDetails,
            kategoriList = kategoriList,
            onValueChange = onBukuValueChange,
            modifier = Modifier.fillMaxWidth()
        )
        Button(
            onClick = onSaveClick,
            enabled = bukuUiState.isEntryValid,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Simpan")
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormInput(
    bukuDetails: BukuDetails,
    kategoriList: List<Kategori>,
    onValueChange: (BukuDetails) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    var selectedCategoryName by remember { mutableStateOf("") }
    
    // Update selected name when ID changes or list loads
    if (bukuDetails.kategoriId != null && kategoriList.isNotEmpty()) {
        selectedCategoryName = kategoriList.find { it.idKategori == bukuDetails.kategoriId }?.nama ?: ""
    }

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        OutlinedTextField(
            value = bukuDetails.judul,
            onValueChange = { onValueChange(bukuDetails.copy(judul = it)) },
            label = { Text("Judul Buku") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        OutlinedTextField(
            value = bukuDetails.namaPenulis,
            onValueChange = { onValueChange(bukuDetails.copy(namaPenulis = it)) },
            label = { Text("Nama Penulis/Pengarang") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        OutlinedTextField(
            value = bukuDetails.isbn,
            onValueChange = { onValueChange(bukuDetails.copy(isbn = it)) },
            label = { Text("ISBN") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        OutlinedTextField(
            value = bukuDetails.penerbit,
            onValueChange = { onValueChange(bukuDetails.copy(penerbit = it)) },
            label = { Text("Penerbit") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        OutlinedTextField(
            value = bukuDetails.tahun,
            onValueChange = { onValueChange(bukuDetails.copy(tahun = it)) },
            label = { Text("Tahun Terbit") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        
        // Category Dropdown
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded },
            modifier = Modifier.fillMaxWidth()
        ) {
            OutlinedTextField(
                value = selectedCategoryName,
                onValueChange = {},
                readOnly = true,
                label = { Text("Kategori") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                modifier = Modifier.menuAnchor().fillMaxWidth()
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                if (kategoriList.isEmpty()) {
                    DropdownMenuItem(
                        text = { Text("Belum ada kategori") },
                        onClick = { expanded = false }
                    )
                }
                kategoriList.forEach { category ->
                    DropdownMenuItem(
                        text = { Text(category.nama) },
                        onClick = {
                            onValueChange(bukuDetails.copy(kategoriId = category.idKategori))
                            selectedCategoryName = category.nama
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}
