package com.example.remeducp2.ui.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import com.example.remeducp2.ui.view.route.DestinasiEntryKategori
import com.example.remeducp2.ui.viewmodel.KategoriViewModel
import com.example.remeducp2.ui.viewmodel.PenyediaViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HalamanEntryKategori(
    navigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: KategoriViewModel = viewModel(factory = PenyediaViewModel.Factory)
) {
    val coroutineScope = rememberCoroutineScope()
    val existingCategories by viewModel.existingCategories.collectAsState()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(DestinasiEntryKategori.titleRes) },
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
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = modifier
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            val details = viewModel.uiState.details
            
            OutlinedTextField(
                value = details.nama,
                onValueChange = { viewModel.updateUiState(details.copy(nama = it)) },
                label = { Text("Nama Kategori") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            
            OutlinedTextField(
                value = details.deskripsi,
                onValueChange = { viewModel.updateUiState(details.copy(deskripsi = it)) },
                label = { Text("Deskripsi") },
                modifier = Modifier.fillMaxWidth()
            )

            // Parent Category Dropdown
            var expanded by remember { mutableStateOf(false) }
            val parentName = existingCategories.find { it.idKategori == details.parentId }?.nama ?: "Tidak Ada (Root)"
            
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded },
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = parentName,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Induk Kategori (Opsional)") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                    modifier = Modifier.menuAnchor().fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("Tidak Ada (Root)") },
                        onClick = {
                            viewModel.updateUiState(details.copy(parentId = null))
                            expanded = false
                        }
                    )
                    existingCategories.forEach { cat ->
                        // Prevent selecting self as parent (simple check, full cyclic check is in Repo)
                        if (cat.idKategori != details.id) {
                            DropdownMenuItem(
                                text = { Text(cat.nama) },
                                onClick = {
                                    viewModel.updateUiState(details.copy(parentId = cat.idKategori))
                                    expanded = false
                                }
                            )
                        }
                    }
                }
            }

            Button(
                onClick = {
                    coroutineScope.launch {
                        viewModel.saveKategori()
                        navigateBack()
                    }
                },
                enabled = viewModel.uiState.isEntryValid,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Simpan Kategori")
            }
        }
    }
}
