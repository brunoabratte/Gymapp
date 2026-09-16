package com.bruno.gymapp.ui.screens.session

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.bruno.gymapp.data.local.entity.Ejercicio

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SesionScreen(
    viewModel: SesionViewModel,
    onFinalizar: () -> Unit
) {
    val ejercicios by viewModel.ejercicios.collectAsState()
    val series by viewModel.series.collectAsState()

    var ejercicioSeleccionado by remember { mutableStateOf<Ejercicio?>(null) }
    var pesoTexto by remember { mutableStateOf("") }
    var repsTexto by remember { mutableStateOf("") }
    var menuExpandido by remember { mutableStateOf(false) }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Sesión en curso") }) },
        bottomBar = {
            Button(
                onClick = {
                    viewModel.finalizarSesion(duracionMinutos = null)
                    onFinalizar()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text("Finalizar sesión")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            // Selector de ejercicio
            ExposedDropdownMenuBox(
                expanded = menuExpandido,
                onExpandedChange = { menuExpandido = it }
            ) {
                OutlinedTextField(
                    value = ejercicioSeleccionado?.nombre ?: "Elegí un ejercicio",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Ejercicio") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor()
                )
                ExposedDropdownMenu(
                    expanded = menuExpandido,
                    onDismissRequest = { menuExpandido = false }
                ) {
                    ejercicios.forEach { ejercicio ->
                        DropdownMenuItem(
                            text = { Text(ejercicio.nombre) },
                            onClick = {
                                ejercicioSeleccionado = ejercicio
                                menuExpandido = false
                            }
                        )
                    }
                }
            }

            Spacer(Modifier.height(12.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = pesoTexto,
                    onValueChange = { pesoTexto = it },
                    label = { Text("Peso (kg)") },
                    keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    value = repsTexto,
                    onValueChange = { repsTexto = it },
                    label = { Text("Reps") },
                    keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(Modifier.height(12.dp))

            Button(
                onClick = {
                    val ejercicio = ejercicioSeleccionado ?: return@Button
                    val peso = pesoTexto.replace(",", ".").toDoubleOrNull() ?: return@Button
                    val reps = repsTexto.toIntOrNull() ?: return@Button

                    viewModel.agregarSerie(ejercicio.id, peso, reps)

                    // Limpio peso y reps para cargar la próxima serie rápido, dejo el ejercicio elegido
                    pesoTexto = ""
                    repsTexto = ""
                },
                enabled = ejercicioSeleccionado != null && pesoTexto.isNotBlank() && repsTexto.isNotBlank(),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Agregar serie")
            }

            Spacer(Modifier.height(16.dp))
            Text("Series de hoy", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(8.dp))

            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
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
}
