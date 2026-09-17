package com.bruno.gymapp.ui.screens.progress

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bruno.gymapp.data.local.dao.PuntoProgreso
import com.bruno.gymapp.data.local.entity.Ejercicio
import com.bruno.gymapp.data.repository.GymRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

class ProgresoViewModel(
    private val repo: GymRepository,
    private val ejercicioId: Long
) : ViewModel() {

    // Nombre del ejercicio, resuelto buscándolo en la lista completa (no hay getEjercicioPorId todavía)
    val ejercicio: StateFlow<Ejercicio?> = repo.observarEjercicios()
        .combine(repo.observarProgreso(ejercicioId)) { ejercicios, _ ->
            ejercicios.find { it.id == ejercicioId }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val progreso: StateFlow<List<PuntoProgreso>> = repo.observarProgreso(ejercicioId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
}
