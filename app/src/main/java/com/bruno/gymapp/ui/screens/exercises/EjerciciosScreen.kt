package com.bruno.gymapp.ui.screens.exercises

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
    onVerHistorial: () -> Unit,
    onBack: () -> Unit
) {

    val ejercicios by viewModel.ejercicios.collectAsState()

    var categoriaSeleccionada by remember {
        mutableStateOf<CategoriaEjercicio?>(null)
    }

    var mostrarCrear by remember {
        mutableStateOf(false)
    }

    val ejerciciosFiltrados =
        if (categoriaSeleccionada == null) {
            ejercicios
        } else {
            ejercicios.filter {
                it.categoria == categoriaSeleccionada
            }
        }

    Scaffold(

        topBar = {

            TopAppBar(

                title = {
                    Text("Ejercicios")
                },

                navigationIcon = {

                    IconButton(
                        onClick = onBack
                    ) {

                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Volver"
                        )
                    }
                },

                actions = {

                    IconButton(
                        onClick = onVerHistorial
                    ) {

                        Icon(
                            imageVector = Icons.Default.History,
                            contentDescription = "Historial"
                        )
                    }
                }
            )
        },

        floatingActionButton = {

            FloatingActionButton(
                onClick = {
                    mostrarCrear = true
                }
            ) {

                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Crear ejercicio"
                )
            }
        }

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
                    text = "Biblioteca",
                    style = MaterialTheme.typography.headlineSmall
                )

                Spacer(
                    modifier = Modifier.height(4.dp)
                )

                Text(
                    text = "Los ejercicios se guardan en tu base local. Los que crees son tuyos y luego podremos sincronizarlos con Firebase.",
                    style = MaterialTheme.typography.bodyMedium
                )

                Spacer(
                    modifier = Modifier.height(10.dp)
                )

                Row(

                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(
                            rememberScrollState()
                        ),

                    horizontalArrangement =
                        Arrangement.spacedBy(8.dp)
                ) {

                    FilterChip(

                        selected =
                            categoriaSeleccionada == null,

                        onClick = {
                            categoriaSeleccionada = null
                        },

                        label = {
                            Text("Todos")
                        }
                    )

                    CategoriaEjercicio.values().forEach { categoria ->

                        FilterChip(

                            selected =
                                categoriaSeleccionada == categoria,

                            onClick = {
                                categoriaSeleccionada = categoria
                            },

                            label = {
                                Text(
                                    categoria.nombreVisible()
                                )
                            }
                        )
                    }
                }

                Spacer(
                    modifier = Modifier.height(8.dp)
                )
            }

            items(

                items = ejerciciosFiltrados,

                key = {
                    it.id
                }

            ) { ejercicio ->

                Card(

                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            onEjercicioClick(ejercicio)
                        }
                ) {

                    Column(
                        modifier = Modifier.padding(14.dp)
                    ) {

                        Row(

                            modifier = Modifier.fillMaxWidth(),

                            horizontalArrangement =
                                Arrangement.SpaceBetween
                        ) {

                            Text(

                                text = ejercicio.nombre,

                                style =
                                    MaterialTheme.typography.titleMedium,

                                modifier =
                                    Modifier.weight(1f)
                            )

                            if (ejercicio.esPersonalizado) {

                                Text(

                                    text = "Personalizado",

                                    style =
                                        MaterialTheme.typography.labelSmall,

                                    modifier =
                                        Modifier.padding(start = 8.dp)
                                )
                            }
                        }

                        Spacer(
                            modifier = Modifier.height(4.dp)
                        )

                        Text(

                            text =
                                "${ejercicio.grupoMuscular.nombreVisible()} · " +
                                        "${ejercicio.categoria.nombreVisible()} · " +
                                        ejercicio.tipo.nombreVisible(),

                            style =
                                MaterialTheme.typography.bodySmall
                        )

                        Spacer(
                            modifier = Modifier.height(2.dp)
                        )

                        Text(

                            text =
                                "Descanso base: ${ejercicio.descansoSegundos}s",

                            style =
                                MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
        }
    }

    if (mostrarCrear) {

        CrearEjercicioDialog(

            onDismiss = {
                mostrarCrear = false
            },

            onCrear = {
                    nombre,
                    grupo,
                    categoria,
                    tipo,
                    descanso ->

                viewModel.agregarEjercicio(
                    nombre,
                    grupo,
                    categoria,
                    tipo,
                    descanso
                )

                mostrarCrear = false
            }
        )
    }
}


@Composable
private fun CrearEjercicioDialog(

    onDismiss: () -> Unit,

    onCrear: (
        String,
        GrupoMuscular,
        CategoriaEjercicio,
        TipoEjercicio,
        Int
    ) -> Unit

) {

    var nombre by remember {
        mutableStateOf("")
    }

    var grupo by remember {
        mutableStateOf(
            GrupoMuscular.PECHO
        )
    }

    var categoria by remember {
        mutableStateOf(
            CategoriaEjercicio.EMPUJE
        )
    }

    var tipo by remember {
        mutableStateOf(
            TipoEjercicio.BASICO
        )
    }

    var descanso by remember {
        mutableStateOf("60")
    }

    var grupoOpen by remember {
        mutableStateOf(false)
    }

    var categoriaOpen by remember {
        mutableStateOf(false)
    }

    var tipoOpen by remember {
        mutableStateOf(false)
    }


    AlertDialog(

        onDismissRequest = onDismiss,

        title = {
            Text("Crear ejercicio")
        },

        text = {

            Column(

                modifier = Modifier
                    .heightIn(max = 420.dp)
                    .verticalScroll(
                        rememberScrollState()
                    ),

                verticalArrangement =
                    Arrangement.spacedBy(8.dp)
            ) {

                OutlinedTextField(

                    value = nombre,

                    onValueChange = {
                        nombre = it
                    },

                    label = {
                        Text("Nombre")
                    },

                    singleLine = true,

                    modifier =
                        Modifier.fillMaxWidth()
                )


                // -------------------------------------------------
                // GRUPO MUSCULAR
                // -------------------------------------------------

                Box {

                    OutlinedTextField(

                        value =
                            grupo.nombreVisible(),

                        onValueChange = {},

                        readOnly = true,

                        label = {
                            Text("Grupo muscular")
                        },

                        modifier =
                            Modifier.fillMaxWidth()
                    )

                    DropdownMenu(

                        expanded = grupoOpen,

                        onDismissRequest = {
                            grupoOpen = false
                        }

                    ) {

                        GrupoMuscular.values()
                            .forEach { opcion ->

                                DropdownMenuItem(

                                    text = {
                                        Text(
                                            opcion.nombreVisible()
                                        )
                                    },

                                    onClick = {

                                        grupo = opcion
                                        grupoOpen = false
                                    }
                                )
                            }
                    }

                    Spacer(

                        modifier = Modifier
                            .matchParentSize()
                            .clickable {
                                grupoOpen = true
                            }
                    )
                }


                // -------------------------------------------------
                // CATEGORIA
                // -------------------------------------------------

                Box {

                    OutlinedTextField(

                        value =
                            categoria.nombreVisible(),

                        onValueChange = {},

                        readOnly = true,

                        label = {
                            Text("Categoría")
                        },

                        modifier =
                            Modifier.fillMaxWidth()
                    )

                    DropdownMenu(

                        expanded = categoriaOpen,

                        onDismissRequest = {
                            categoriaOpen = false
                        }

                    ) {

                        CategoriaEjercicio.values()
                            .forEach { opcion ->

                                DropdownMenuItem(

                                    text = {
                                        Text(
                                            opcion.nombreVisible()
                                        )
                                    },

                                    onClick = {

                                        categoria = opcion
                                        categoriaOpen = false
                                    }
                                )
                            }
                    }

                    Spacer(

                        modifier = Modifier
                            .matchParentSize()
                            .clickable {
                                categoriaOpen = true
                            }
                    )
                }


                // -------------------------------------------------
                // TIPO
                // -------------------------------------------------

                Box {

                    OutlinedTextField(

                        value =
                            tipo.nombreVisible(),

                        onValueChange = {},

                        readOnly = true,

                        label = {
                            Text("Tipo")
                        },

                        modifier =
                            Modifier.fillMaxWidth()
                    )

                    DropdownMenu(

                        expanded = tipoOpen,

                        onDismissRequest = {
                            tipoOpen = false
                        }

                    ) {

                        TipoEjercicio.values()
                            .forEach { opcion ->

                                DropdownMenuItem(

                                    text = {
                                        Text(
                                            opcion.nombreVisible()
                                        )
                                    },

                                    onClick = {

                                        tipo = opcion
                                        tipoOpen = false
                                    }
                                )
                            }
                    }

                    Spacer(

                        modifier = Modifier
                            .matchParentSize()
                            .clickable {
                                tipoOpen = true
                            }
                    )
                }


                // -------------------------------------------------
                // DESCANSO
                // -------------------------------------------------

                OutlinedTextField(

                    value = descanso,

                    onValueChange = {

                        descanso =
                            it.filter(
                                Char::isDigit
                            )
                    },

                    label = {
                        Text(
                            "Descanso base (segundos)"
                        )
                    },

                    singleLine = true,

                    modifier =
                        Modifier.fillMaxWidth()
                )
            }
        },

        confirmButton = {

            Button(

                onClick = {

                    val segundos =
                        descanso.toIntOrNull()
                            ?: 60

                    if (nombre.isNotBlank()) {

                        onCrear(

                            nombre.trim(),

                            grupo,

                            categoria,

                            tipo,

                            segundos.coerceAtLeast(0)
                        )
                    }
                },

                enabled =
                    nombre.isNotBlank()

            ) {

                Text("Crear")
            }
        },

        dismissButton = {

            TextButton(
                onClick = onDismiss
            ) {

                Text("Cancelar")
            }
        }
    )
}


// -------------------------------------------------------------
// FUNCIONES PARA MOSTRAR LOS ENUMS DE FORMA BONITA
// -------------------------------------------------------------

private fun CategoriaEjercicio.nombreVisible(): String =
    name
        .lowercase()
        .replace('_', ' ')
        .replaceFirstChar {
            it.uppercase()
        }


private fun GrupoMuscular.nombreVisible(): String =
    name
        .lowercase()
        .replace('_', ' ')
        .replaceFirstChar {
            it.uppercase()
        }


private fun TipoEjercicio.nombreVisible(): String =
    name
        .lowercase()
        .replace('_', ' ')
        .replaceFirstChar {
            it.uppercase()
        }