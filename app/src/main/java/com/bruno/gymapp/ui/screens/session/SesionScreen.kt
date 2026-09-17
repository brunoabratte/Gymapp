package com.bruno.gymapp.ui.screens.session

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.bruno.gymapp.data.local.entity.Ejercicio

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SesionScreen(
    viewModel: SesionViewModel,
    onFinalizar: () -> Unit,
    onBack: () -> Unit
) {
    val ejercicios by viewModel.ejercicios.collectAsState()
    val series by viewModel.series.collectAsState()
    val segundosDescanso by viewModel.segundosDescanso.collectAsState()
    val timerPausado by viewModel.timerPausado.collectAsState()
    val context = LocalContext.current

    var timerAnterior by remember { mutableIntStateOf(0) }
    var mostrarDescansoTerminado by remember { mutableStateOf(false) }
    LaunchedEffect(segundosDescanso) {
        if (segundosDescanso > 0) mostrarDescansoTerminado = false
        if (timerAnterior > 0 && segundosDescanso == 0) {
            mostrarDescansoTerminado = true
            val vibrator = context.getSystemService(Vibrator::class.java)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator?.vibrate(VibrationEffect.createOneShot(450, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                @Suppress("DEPRECATION")
                vibrator?.vibrate(450)
            }
        }
        timerAnterior = segundosDescanso
    }

    var ejercicioSeleccionado by remember { mutableStateOf<Ejercicio?>(null) }
    var pesoTexto by remember { mutableStateOf("") }
    var repsTexto by remember { mutableStateOf("") }
    var menuExpandido by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(viewModel.nombrePlan) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        },
        bottomBar = {
            Button(
                onClick = {
                    viewModel.finalizarSesion()
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
            if (segundosDescanso > 0) {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                        horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally
                    ) {
                        Text("Descanso", style = MaterialTheme.typography.titleMedium)
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = "%d:%02d".format(segundosDescanso / 60, segundosDescanso % 60),
                            style = MaterialTheme.typography.headlineLarge
                        )
                        Spacer(Modifier.height(8.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Button(
                                onClick = {
                                    if (timerPausado) viewModel.continuarDescanso()
                                    else viewModel.pausarDescanso()
                                }
                            ) {
                                Text(if (timerPausado) "Continuar" else "Pausar")
                            }
                            OutlinedButton(onClick = { viewModel.reiniciarDescanso() }) { Text("Reiniciar") }
                        }
                    }
                }
                Spacer(Modifier.height(12.dp))
            }
            if (mostrarDescansoTerminado) {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("¡Descanso terminado!", style = MaterialTheme.typography.titleMedium)
                        TextButton(onClick = { mostrarDescansoTerminado = false }) { Text("Cerrar") }
                    }
                }
                Spacer(Modifier.height(12.dp))
            }

            Text("Plan: ${viewModel.nombrePlan}", style = MaterialTheme.typography.labelLarge)
            Spacer(Modifier.height(8.dp))

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
            Text(
                "Tocá una serie para editarla o borrarla",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(8.dp))

            var serieEnEdicion by remember { mutableStateOf<SerieUi?>(null) }

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(bottom = 8.dp)
            ) {
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
    }
}

@Composable
fun DialogoEditarSerie(
    serie: SerieUi,
    onGuardar: (Double, Int) -> Unit,
    onEliminar: () -> Unit,
    onCancelar: () -> Unit
) {
    var peso by remember { mutableStateOf(serie.pesoKg.toString()) }
    var reps by remember { mutableStateOf(serie.repeticiones.toString()) }

    AlertDialog(
        onDismissRequest = onCancelar,
        title = { Text("${serie.nombreEjercicio} · serie ${serie.orden}") },
        text = {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = peso,
                    onValueChange = { peso = it },
                    label = { Text("Peso (kg)") },
                    keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    value = reps,
                    onValueChange = { reps = it },
                    label = { Text("Reps") },
                    keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f)
                )
            }
        },
        confirmButton = {
            TextButton(onClick = {
                val pesoNum = peso.replace(",", ".").toDoubleOrNull()
                val repsNum = reps.toIntOrNull()
                if (pesoNum != null && repsNum != null) onGuardar(pesoNum, repsNum)
            }) { Text("Guardar") }
        },
        dismissButton = {
            Row {
                TextButton(onClick = onEliminar) { Text("Eliminar") }
                TextButton(onClick = onCancelar) { Text("Cancelar") }
            }
        }
    )
}
