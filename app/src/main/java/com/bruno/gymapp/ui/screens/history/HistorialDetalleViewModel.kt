package com.bruno.gymapp.ui.screens.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bruno.gymapp.data.repository.GymRepository
import com.bruno.gymapp.ui.screens.session.SerieUi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

class HistorialDetalleViewModel(
    repo: GymRepository,
    sesionId: Long
) : ViewModel() {

    val series: StateFlow<List<SerieUi>> = combine(
        repo.observarSeriesDeSesion(sesionId),
        repo.observarEjercicios()
    ) { series, ejercicios ->
        series.map { s ->
            SerieUi(
                nombreEjercicio = ejercicios.find { it.id == s.ejercicioId }?.nombre ?: "Ejercicio",
                pesoKg = s.pesoKg,
                repeticiones = s.repeticiones,
                orden = s.orden
            )
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
}
