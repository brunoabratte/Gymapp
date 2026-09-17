package com.bruno.gymapp.ui.screens.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.bruno.gymapp.data.ConfiguracionEntrenamiento
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(viewModel: HomeViewModel, onEntrenar: (String) -> Unit, onContinuar: (Long, String) -> Unit, onConfig: () -> Unit, onEjercicios: () -> Unit, onHistorial: () -> Unit, onProgreso: () -> Unit) {
    val sesiones = viewModel.sesiones.value
    val activa = viewModel.sesionEnCurso.collectAsState().value
    val totalSeries by viewModel.cantidadTotalSeries.collectAsState()
    val planDeHoy by viewModel.planDeHoy.collectAsState()
    val configurado by viewModel.configurado.collectAsState()
    val nombrePlanHoy by viewModel.nombrePlanHoy.collectAsState()
    val ejerciciosDeHoy by viewModel.ejerciciosDeHoy.collectAsState()
    val fecha = rememberHoy()
    Scaffold(topBar = { TopAppBar(title = { Text("GymApp") }, actions = { IconButton(onClick = onConfig) { Icon(Icons.Default.Settings, "Configuración") } }) }, bottomBar = {
        NavigationBar { NavigationBarItem(true, { onConfig() }, { Icon(Icons.Default.Settings, null) }, label = { Text("Config") }); NavigationBarItem(false, { onEjercicios() }, { Icon(Icons.Default.FitnessCenter, null) }, label = { Text("Ejercicios") }); NavigationBarItem(false, { onHistorial() }, { Icon(Icons.Default.History, null) }, label = { Text("Historial") }) }
    }) { padding ->
        Column(Modifier.fillMaxSize().padding(padding).padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Text("Hola 👋", style = MaterialTheme.typography.headlineMedium)
            Text(fecha, style = MaterialTheme.typography.bodyMedium)
            if (activa != null) {
                Card(Modifier.fillMaxWidth()) { Column(Modifier.padding(18.dp)) { Text("Entrenamiento en curso", style = MaterialTheme.typography.titleLarge); Text(activa.nombrePlan); Spacer(Modifier.height(10.dp)); Button(onClick = { onContinuar(activa.id, activa.planId) }, Modifier.fillMaxWidth()) { Text("Continuar entrenamiento") } } }
            } else if (!configurado || planDeHoy.isBlank()) {
                Card(Modifier.fillMaxWidth()) { Column(Modifier.padding(18.dp)) { Text("Configurá tu semana", style = MaterialTheme.typography.titleLarge); Text("Elegí qué plan querés hacer cada día y GymApp te mostrará automáticamente tu entrenamiento de hoy."); Spacer(Modifier.height(10.dp)); Button(onClick = onConfig) { Text("Configurar") } } }
            } else {
                Card(Modifier.fillMaxWidth()) { Column(Modifier.padding(18.dp)) { Text("Tu entrenamiento de hoy", style = MaterialTheme.typography.titleLarge); Text(nombrePlanHoy, style = MaterialTheme.typography.headlineSmall); Text("${ejerciciosDeHoy} ejercicios"); Spacer(Modifier.height(10.dp)); Button(onClick = { onEntrenar(planDeHoy) }, Modifier.fillMaxWidth()) { Icon(Icons.Default.PlayArrow, null); Spacer(Modifier.width(6.dp)); Text("Comenzar entrenamiento") } } }
            }
            Text("Resumen", style = MaterialTheme.typography.titleLarge)
            LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                item { StatCard("Sesiones", sesiones.size.toString()) }
                item { StatCard("Series", totalSeries.toString()) }
                item { StatCard("Plan", if (configurado) "Activo" else "Pendiente") }
            }
            if (sesiones.isNotEmpty()) {
                Text("Último entrenamiento", style = MaterialTheme.typography.titleLarge)
                val ultima = sesiones.first()
                Card(Modifier.fillMaxWidth()) { Column(Modifier.padding(16.dp)) { Text(ultima.nombrePlan, style = MaterialTheme.typography.titleMedium); Text(SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date(ultima.fechaEpochMillis))); Text(ultima.duracionMinutos?.let { "$it min" } ?: "En curso") } }
            }
        }
    }
}

@Composable private fun StatCard(titulo: String, valor: String) { Card { Column(Modifier.padding(16.dp)) { Text(valor, style = MaterialTheme.typography.headlineSmall); Text(titulo) } } }
@Composable private fun rememberHoy(): String { return java.text.SimpleDateFormat("EEEE d 'de' MMMM", Locale("es", "AR")).format(Date()).replaceFirstChar { it.uppercase() } }
