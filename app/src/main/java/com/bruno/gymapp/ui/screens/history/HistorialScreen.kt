package com.bruno.gymapp.ui.screens.history

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistorialScreen(
    viewModel: HistorialViewModel,
    onSesionClick: (Long) -> Unit,
    onBack: () -> Unit
) {
    val sesiones by viewModel.sesiones.collectAsState()
    val formato = remember { SimpleDateFormat("dd/MM/yyyy - HH:mm", Locale.getDefault()) }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Historial") }, navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, contentDescription = "Volver") } }) }
    ) { padding ->
        if (sesiones.isEmpty()) {
            Box(
                Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text("Todavía no completaste ninguna sesión.")
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(sesiones) { sesion ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onSesionClick(sesion.sesionId) }
                    ) {
                        Column(Modifier.padding(12.dp)) {
                            Text(
                                formato.format(Date(sesion.fechaEpochMillis)),
                                style = MaterialTheme.typography.titleMedium
                            )
                            Spacer(Modifier.height(4.dp))
                            Text(
                                "${sesion.cantidadSeries} series" +
                                    (sesion.duracionMinutos?.let { " · $it min" } ?: ""),
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }
            }
        }
    }
}
