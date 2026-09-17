package com.bruno.gymapp.ui.screens.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bruno.gymapp.data.local.entity.SerieRegistrada
import com.bruno.gymapp.data.repository.GymRepository
import com.bruno.gymapp.ui.screens.session.SerieUi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class HistorialDetalleViewModel(
    private val repo: GymRepository,
    sesionId: Long
) : ViewModel() {

    private val seriesCrudas: StateFlow<List<SerieRegistrada>> = repo.observarSeriesDeSesion(sesionId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val series: StateFlow<List<SerieUi>> = combine(
        seriesCrudas,
        repo.observarEjercicios()
    ) { series, ejercicios ->
        series.map { s ->
            SerieUi(
                id = s.id,
                nombreEjercicio = ejercicios.find { it.id == s.ejercicioId }?.nombre ?: "Ejercicio",
                pesoKg = s.pesoKg,
                repeticiones = s.repeticiones,
                orden = s.orden
            )
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // La sesión ya haya sido finalizada o no, siempre se puede corregir una serie cargada por error.
    fun editarSerie(serieId: Long, pesoKg: Double, repeticiones: Int) {
        viewModelScope.launch {
            val original = seriesCrudas.value.find { it.id == serieId } ?: return@launch
            repo.actualizarSerie(original.copy(pesoKg = pesoKg, repeticiones = repeticiones))
        }
    }

    fun eliminarSerie(serieId: Long) {
        viewModelScope.launch {
            val original = seriesCrudas.value.find { it.id == serieId } ?: return@launch
            repo.eliminarSerie(original)
        }
    }
}
