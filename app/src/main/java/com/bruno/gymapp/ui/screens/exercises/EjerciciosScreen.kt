package com.bruno.gymapp.ui.screens.exercises

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.bruno.gymapp.data.local.entity.CategoriaEjercicio
import com.bruno.gymapp.data.local.entity.Ejercicio
import com.bruno.gymapp.data.local.entity.GrupoMuscular
import com.bruno.gymapp.data.local.entity.TipoEjercicio

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EjerciciosScreen(
    viewModel: EjerciciosViewModel,
    onEjercicioClick: (Ejercicio) -> Unit,
    onIniciarSesion: () -> Unit,
    onVerHistorial: () -> Unit
) {
    val ejercicios by viewModel.ejercicios.collectAsState()
    var categoriaSeleccionada by remember { mutableStateOf<CategoriaEjercicio?>(null) }
    var mostrarCrear by remember { mutableStateOf(false) }

    val ejerciciosFiltrados = if (categoriaSeleccionada == null) ejercicios
    else ejercicios.filter { it.categoria == categoriaSeleccionada }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Ejercicios") },
                actions = {
                    IconButton(onClick = onVerHistorial) {
                        Icon(Icons.Default.History, contentDescription = "Historial")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { mostrarCrear = true }) {
                Icon(Icons.Default.Add, contentDescription = "Crear ejercicio")
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                Text("Biblioteca", style = MaterialTheme.typography.headlineSmall)
                Text("Los ejercicios se guardan en tu base local. Los que crees son tuyos y luego podremos sincronizarlos con Firebase.")
                Spacer(Modifier.height(10.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilterChip(
                        selected = categoriaSeleccionada == null,
                        onClick = { categoriaSeleccionada = null },
                        label = { Text("Todos") }
                    )
                    CategoriaEjercicio.values().forEach { categoria ->
                        FilterChip(
                            selected = categoriaSeleccionada == categoria,
                            onClick = { categoriaSeleccionada = categoria },
                            label = { Text(categoria.nombreVisible()) }
                        )
                    }
                }
                Spacer(Modifier.height(8.dp))
            }
            items(ejerciciosFiltrados, key = { it.id }) { ejercicio ->
                Card(modifier = Modifier.fillMaxWidth().clickable { onEjercicioClick(ejercicio) }) {
                    Column(Modifier.padding(14.dp)) {
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(ejercicio.nombre, style = MaterialTheme.typography.titleMedium)
                            if (ejercicio.esPersonalizado) Text("Personalizado", style = MaterialTheme.typography.labelSmall)
                        }
                        Text(
                            "${ejercicio.grupoMuscular.nombreVisible()} · ${ejercicio.categoria.nombreVisible()} · ${ejercicio.tipo.nombreVisible()}",
                            style = MaterialTheme.typography.bodySmall
                        )
                        Text("Descanso base: ${ejercicio.descansoSegundos}s", style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        }
    }

    if (mostrarCrear) {
        CrearEjercicioDialog(
            onDismiss = { mostrarCrear = false },
            onCrear = { nombre, grupo, categoria, tipo, descanso ->
                viewModel.agregarEjercicio(nombre, grupo, categoria, tipo, descanso)
                mostrarCrear = false
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CrearEjercicioDialog(
    onDismiss: () -> Unit,
    onCrear: (String, GrupoMuscular, CategoriaEjercicio, TipoEjercicio, Int) -> Unit
) {
    var nombre by remember { mutableStateOf("") }
    var grupo by remember { mutableStateOf(GrupoMuscular.PECHO) }
    var categoria by remember { mutableStateOf(CategoriaEjercicio.EMPUJE) }
    var tipo by remember { mutableStateOf(TipoEjercicio.BASICO) }
    var descanso by remember { mutableStateOf("60") }
    var grupoOpen by remember { mutableStateOf(false) }
    var categoriaOpen by remember { mutableStateOf(false) }
    var tipoOpen by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Crear ejercicio") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = nombre, onValueChange = { nombre = it }, label = { Text("Nombre") }, singleLine = true)
                ExposedDropdownMenuBox(expanded = grupoOpen, onExpandedChange = { grupoOpen = it }) {
                    OutlinedTextField(value = grupo.nombreVisible(), onValueChange = {}, readOnly = true, label = { Text("Grupo muscular") }, modifier = Modifier.fillMaxWidth().menuAnchor())
                    ExposedDropdownMenu(expanded = grupoOpen, onDismissRequest = { grupoOpen = false }) {
                        GrupoMuscular.values().forEach { opcion -> DropdownMenuItem(text = { Text(opcion.nombreVisible()) }, onClick = { grupo = opcion; grupoOpen = false }) }
                    }
                }
                ExposedDropdownMenuBox(expanded = categoriaOpen, onExpandedChange = { categoriaOpen = it }) {
                    OutlinedTextField(value = categoria.nombreVisible(), onValueChange = {}, readOnly = true, label = { Text("Categoría") }, modifier = Modifier.fillMaxWidth().menuAnchor())
                    ExposedDropdownMenu(expanded = categoriaOpen, onDismissRequest = { categoriaOpen = false }) {
                        CategoriaEjercicio.values().forEach { opcion -> DropdownMenuItem(text = { Text(opcion.nombreVisible()) }, onClick = { categoria = opcion; categoriaOpen = false }) }
                    }
                }
                ExposedDropdownMenuBox(expanded = tipoOpen, onExpandedChange = { tipoOpen = it }) {
                    OutlinedTextField(value = tipo.nombreVisible(), onValueChange = {}, readOnly = true, label = { Text("Tipo") }, modifier = Modifier.fillMaxWidth().menuAnchor())
                    ExposedDropdownMenu(expanded = tipoOpen, onDismissRequest = { tipoOpen = false }) {
                        TipoEjercicio.values().forEach { opcion -> DropdownMenuItem(text = { Text(opcion.nombreVisible()) }, onClick = { tipo = opcion; tipoOpen = false }) }
                    }
                }
                OutlinedTextField(value = descanso, onValueChange = { descanso = it.filter(Char::isDigit) }, label = { Text("Descanso base (segundos)") }, singleLine = true)
            }
        },
        confirmButton = {
            Button(onClick = { val segundos = descanso.toIntOrNull() ?: 60; if (nombre.isNotBlank()) onCrear(nombre.trim(), grupo, categoria, tipo, segundos.coerceAtLeast(0)) }, enabled = nombre.isNotBlank()) { Text("Crear") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar") } }
    )
}

private fun CategoriaEjercicio.nombreVisible() = name.lowercase().replace('_', ' ').replaceFirstChar { it.uppercase() }
private fun GrupoMuscular.nombreVisible() = name.lowercase().replaceFirstChar { it.uppercase() }
private fun TipoEjercicio.nombreVisible() = name.lowercase().replaceFirstChar { it.uppercase() }
