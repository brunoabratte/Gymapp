package com.bruno.gymapp.ui.screens.history

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.bruno.gymapp.ui.screens.session.DialogoEditarSerie

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistorialDetalleScreen(viewModel: HistorialDetalleViewModel, onBack: () -> Unit) {
    val series by viewModel.series.collectAsState()
    var serieEnEdicion by remember { mutableStateOf<com.bruno.gymapp.ui.screens.session.SerieUi?>(null) }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Detalle de sesión") }, navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, contentDescription = "Volver") } }) }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                Text(
                    "Tocá una serie para editarla o borrarla",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            items(series) { serie ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { serieEnEdicion = serie }
                ) {
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

    serieEnEdicion?.let { serie ->
        DialogoEditarSerie(
            serie = serie,
            onGuardar = { peso, reps ->
                viewModel.editarSerie(serie.id, peso, reps)
                serieEnEdicion = null
            },
            onEliminar = {
                viewModel.eliminarSerie(serie.id)
                serieEnEdicion = null
            },
            onCancelar = { serieEnEdicion = null }
        )
    }
}
