package com.bruno.gymapp.ui.screens.exercises

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.bruno.gymapp.data.local.entity.Ejercicio

@Composable
fun EjerciciosScreen(
    viewModel: EjerciciosViewModel,
    onEjercicioClick: (Ejercicio) -> Unit,
    onIniciarSesion: () -> Unit
) {
    val ejercicios by viewModel.ejercicios.collectAsState()

    Scaffold(
        topBar = { TopAppBar(title = { Text("Ejercicios") }) },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                text = { Text("Iniciar sesión") },
                icon = { Icon(Icons.Default.Add, contentDescription = null) },
                onClick = onIniciarSesion
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(ejercicios) { ejercicio ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onEjercicioClick(ejercicio) }
                ) {
                    Column(Modifier.padding(12.dp)) {
                        Text(ejercicio.nombre, style = MaterialTheme.typography.titleMedium)
                        Text(
                            ejercicio.grupoMuscular.name,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
        }
    }
}
