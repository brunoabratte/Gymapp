package com.bruno.gymapp.ui.screens.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.bruno.gymapp.data.ConfiguracionEntrenamiento
import com.bruno.gymapp.data.local.entity.PlanesPreconfigurados

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConfiguracionScreen(
    config: ConfiguracionEntrenamiento,
    planInicial: String? = null,
    onBack: () -> Unit
) {
    var frecuencia by remember { mutableIntStateOf((1..7).count { config.planParaDia(it).isNotBlank() }.coerceIn(3, 5)) }
    val seleccion = remember(planInicial) {
        mutableStateMapOf<Int, String>().apply {
            for (i in 1..7) put(i, planInicial ?: config.planParaDia(i))
        }
    }
    LaunchedEffect(frecuencia) {
        for (dia in (frecuencia + 1)..7) seleccion[dia] = ""
    }
    Scaffold(topBar = { TopAppBar(title = { Text("Mi planificación") }, navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, contentDescription = "Volver") } }) }) { padding ->
        LazyColumn(Modifier.fillMaxSize().padding(padding).padding(16.dp).imePadding(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            item { Text("Elegí qué plan querés hacer cada día.", style = MaterialTheme.typography.titleMedium) }
            item {
                Text("Frecuencia semanal", style = MaterialTheme.typography.labelLarge)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf(3, 4, 5).forEach { cantidad ->
                        FilterChip(
                            selected = frecuencia == cantidad,
                            onClick = { frecuencia = cantidad },
                            label = { Text("$cantidad días") }
                        )
                    }
                }
            }
            if (planInicial != null) {
                item {
                    Text(
                        "Plan seleccionado: ${ConfiguracionEntrenamiento.nombrePlan(planInicial)}. " +
                                "Asignalo a los días que quieras.",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
            items(7) { index ->
                var expanded by remember { mutableStateOf(false) }
                val actual = seleccion[index + 1] ?: ""
                ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = it }) {
                    OutlinedTextField(value = ConfiguracionEntrenamiento.dias[index] + " — " + ConfiguracionEntrenamiento.nombrePlan(actual), onValueChange = {}, readOnly = true, label = { Text(if (index < frecuencia) "Entrenamiento" else "Descanso") }, modifier = Modifier.fillMaxWidth().menuAnchor())
                    ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                        DropdownMenuItem(text = { Text("Descanso / libre") }, onClick = { seleccion[index + 1] = ""; expanded = false })
                        PlanesPreconfigurados.todos.forEach { plan -> DropdownMenuItem(text = { Text(plan.nombre) }, onClick = { seleccion[index + 1] = plan.id; expanded = false }) }
                    }
                }
            }
            item {
                Spacer(Modifier.height(8.dp))
                Button(onClick = {
                    for (i in 1..7) config.guardarPlanDia(i, seleccion[i] ?: "")
                    config.guardarConfigurado()
                    onBack()
                }, modifier = Modifier.fillMaxWidth()) { Text("Guardar planificación") }
            }
        }
    }
}
