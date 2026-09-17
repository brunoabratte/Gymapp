package com.bruno.gymapp.ui.screens.progress

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.unit.dp
import com.bruno.gymapp.data.local.dao.PuntoProgreso
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProgresoScreen(viewModel: ProgresoViewModel, onBack: () -> Unit) {
    val ejercicio by viewModel.ejercicio.collectAsState()
    val progreso by viewModel.progreso.collectAsState()
    var popupCerrado by remember { mutableStateOf(false) }

    val progresoActual = progreso
    if (progresoActual != null && progresoActual.isEmpty() && !popupCerrado) {
        AlertDialog(
            onDismissRequest = { popupCerrado = true },
            title = { Text("Sin datos todavía") },
            text = { Text("No hay series cargadas para ${ejercicio?.nombre ?: "este ejercicio"}. Registralas desde una sesión de entrenamiento.") },
            confirmButton = {
                TextButton(onClick = { popupCerrado = true }) { Text("Entendido") }
            }
        )
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text(ejercicio?.nombre ?: "Progreso") }, navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, contentDescription = "Volver") } }) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            if (progresoActual == null) {
                // Todavía no llegó el primer dato de la base: no mostramos nada para evitar el parpadeo.
            } else if (progresoActual.isEmpty()) {
                Box(
                    Modifier.fillMaxWidth().padding(top = 32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Todavía no cargaste series de este ejercicio.")
                }
            } else {
                Text("Peso (kg) por serie cargada", style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(12.dp))
                GraficoPeso(
                    progresoActual,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                )
                Spacer(Modifier.height(24.dp))
                Text("Detalle", style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(8.dp))

                val formato = remember { SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()) }

                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(bottom = 8.dp)
                ) {
                    items(progresoActual.reversed()) { punto ->
                        Card(modifier = Modifier.fillMaxWidth()) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(formato.format(Date(punto.fechaEpochMillis)))
                                Text("${punto.pesoKg} kg × ${punto.repeticiones}")
                            }
                        }
                    }
                }
            }
        }
    }
}

// Gráfico de línea simple dibujado a mano con Canvas, sin librerías externas.
@Composable
private fun GraficoPeso(puntos: List<PuntoProgreso>, modifier: Modifier = Modifier) {
    val colorLinea = MaterialTheme.colorScheme.primary
    val colorPunto = MaterialTheme.colorScheme.primary

    Canvas(modifier = modifier) {
        if (puntos.size < 2) {
            if (puntos.size == 1) {
                drawCircle(color = colorPunto, radius = 8f, center = Offset(size.width / 2, size.height / 2))
            }
            return@Canvas
        }

        val pesoMin = puntos.minOf { it.pesoKg }
        val pesoMax = puntos.maxOf { it.pesoKg }
        val rango = (pesoMax - pesoMin).takeIf { it > 0 } ?: 1.0

        val pasoX = size.width / (puntos.size - 1)

        fun yDe(peso: Double): Float {
            val proporcion = (peso - pesoMin) / rango
            return size.height - (proporcion * size.height).toFloat()
        }

        val offsets = puntos.mapIndexed { i, p ->
            Offset(x = i * pasoX, y = yDe(p.pesoKg))
        }

        for (i in 0 until offsets.size - 1) {
            drawLine(
                color = colorLinea,
                start = offsets[i],
                end = offsets[i + 1],
                strokeWidth = 6f,
                cap = StrokeCap.Round
            )
        }
        offsets.forEach { punto ->
            drawCircle(color = colorPunto, radius = 8f, center = punto)
        }
    }
}
