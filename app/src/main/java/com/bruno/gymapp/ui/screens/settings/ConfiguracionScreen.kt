package com.bruno.gymapp.ui.screens.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.bruno.gymapp.data.ConfiguracionEntrenamiento
import com.bruno.gymapp.data.local.entity.PlanesPreconfigurados

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConfiguracionScreen(config: ConfiguracionEntrenamiento, onGuardar: () -> Unit) {
    val seleccion = remember { mutableStateMapOf<Int, String>().apply { for (i in 1..7) put(i, config.planParaDia(i)) } }
    Scaffold(topBar = { TopAppBar(title = { Text("Mi planificación") }) }) { padding ->
        LazyColumn(Modifier.fillMaxSize().padding(padding).padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            item { Text("Elegí qué plan querés hacer cada día.", style = MaterialTheme.typography.titleMedium) }
            items(7) { index ->
                var expanded by remember { mutableStateOf(false) }
                val actual = seleccion[index + 1] ?: ""
                ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = it }) {
                    OutlinedTextField(value = ConfiguracionEntrenamiento.dias[index] + " — " + ConfiguracionEntrenamiento.nombrePlan(actual), onValueChange = {}, readOnly = true, label = { Text("Entrenamiento") }, modifier = Modifier.fillMaxWidth().menuAnchor())
                    ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                        DropdownMenuItem(text = { Text("Descanso / libre") }, onClick = { seleccion[index + 1] = ""; expanded = false })
                        PlanesPreconfigurados.todos.forEach { plan -> DropdownMenuItem(text = { Text(plan.nombre) }, onClick = { seleccion[index + 1] = plan.id; expanded = false }) }
                    }
                }
            }
            item {
                Spacer(Modifier.height(8.dp))
                Button(onClick = { for (i in 1..7) config.guardarPlanDia(i, seleccion[i] ?: ""); config.guardarConfigurado(); onGuardar() }, modifier = Modifier.fillMaxWidth()) { Text("Guardar planificación") }
            }
        }
    }
}
