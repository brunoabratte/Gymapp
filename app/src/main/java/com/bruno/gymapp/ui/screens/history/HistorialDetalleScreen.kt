package com.bruno.gymapp.ui.screens.history

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistorialDetalleScreen(viewModel: HistorialDetalleViewModel) {
    val series by viewModel.series.collectAsState()

    Scaffold(
        topBar = { TopAppBar(title = { Text("Detalle de sesión") }) }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(series) { serie ->
                Card(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("${serie.nombreEjercicio} · serie ${serie.orden}")
                        Text("${serie.pesoKg} kg × ${serie.repeticiones}")
                    }
                }
            }
        }
    }
}
