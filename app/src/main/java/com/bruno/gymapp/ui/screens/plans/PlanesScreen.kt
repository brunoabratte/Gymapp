package com.bruno.gymapp.ui.screens.plans

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.bruno.gymapp.data.local.entity.PlanPreconfigurado
import com.bruno.gymapp.data.local.entity.PlanesPreconfigurados

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlanesScreen(onPlanClick: (String) -> Unit, onBack: () -> Unit) {
    Scaffold(topBar = { TopAppBar(title = { Text("Elegí tu plan") }, navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, contentDescription = "Volver") } }) }) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Text("¿Qué querés entrenar hoy?", style = MaterialTheme.typography.headlineSmall)
                Spacer(Modifier.height(4.dp))
                Text("Elegí una preconfiguración. Podrás personalizarla más adelante.")
            }
            items(PlanesPreconfigurados.todos) { plan ->
                PlanCard(plan = plan, onClick = { onPlanClick(plan.id) })
            }
        }
    }
}

@Composable
private fun PlanCard(plan: PlanPreconfigurado, onClick: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth().clickable(onClick = onClick)) {
        Column(Modifier.padding(16.dp)) {
            Text(plan.nombre, style = MaterialTheme.typography.titleLarge)
            Spacer(Modifier.height(4.dp))
            Text(plan.descripcion, style = MaterialTheme.typography.bodyMedium)
            Spacer(Modifier.height(8.dp))
            HorizontalDivider(modifier = Modifier.padding(vertical = 6.dp))
            Text("Descansos automáticos", style = MaterialTheme.typography.labelLarge)
            Text("Compuesto: ${plan.descansoCompuesto}s · Básico: ${plan.descansoBasico}s · Aislamiento: ${plan.descansoAislamiento}s", style = MaterialTheme.typography.bodySmall)
        }
    }
}
