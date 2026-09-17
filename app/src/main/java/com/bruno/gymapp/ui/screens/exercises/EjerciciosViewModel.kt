package com.bruno.gymapp.ui.screens.exercises

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bruno.gymapp.data.local.entity.CategoriaEjercicio
import com.bruno.gymapp.data.local.entity.Ejercicio
import com.bruno.gymapp.data.local.entity.GrupoMuscular
import com.bruno.gymapp.data.local.entity.TipoEjercicio
import com.bruno.gymapp.data.repository.GymRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class EjerciciosViewModel(private val repo: GymRepository) : ViewModel() {
    val ejercicios: StateFlow<List<Ejercicio>> = repo.observarEjercicios()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun agregarEjercicio(
        nombre: String,
        grupo: GrupoMuscular,
        categoria: CategoriaEjercicio,
        tipo: TipoEjercicio,
        descansoSegundos: Int
    ) {
        viewModelScope.launch {
            repo.crearEjercicioPersonalizado(
                Ejercicio(
                    nombre = nombre,
                    grupoMuscular = grupo,
                    categoria = categoria,
                    tipo = tipo,
                    descansoSegundos = descansoSegundos,
                    esPersonalizado = true
                )
            )
        }
    }
}
