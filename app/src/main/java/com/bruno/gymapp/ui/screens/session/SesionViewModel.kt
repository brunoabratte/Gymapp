package com.bruno.gymapp.ui.screens.session

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bruno.gymapp.data.local.entity.Ejercicio
import com.bruno.gymapp.data.local.entity.SerieRegistrada
import com.bruno.gymapp.data.repository.GymRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

// Fila ya lista para mostrar en la lista de series cargadas (con el nombre del ejercicio resuelto)
data class SerieUi(
    val nombreEjercicio: String,
    val pesoKg: Double,
    val repeticiones: Int,
    val orden: Int
)

class SesionViewModel(
    private val repo: GymRepository,
    private val sesionId: Long
) : ViewModel() {

    val ejercicios: StateFlow<List<Ejercicio>> = repo.observarEjercicios()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val seriesCrudas: StateFlow<List<SerieRegistrada>> = repo.observarSeriesDeSesion(sesionId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val series: StateFlow<List<SerieUi>> = combine(seriesCrudas, ejercicios) { series, ejercicios ->
        series.map { s ->
            SerieUi(
                nombreEjercicio = ejercicios.find { it.id == s.ejercicioId }?.nombre ?: "Ejercicio",
                pesoKg = s.pesoKg,
                repeticiones = s.repeticiones,
                orden = s.orden
            )
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun agregarSerie(ejercicioId: Long, pesoKg: Double, repeticiones: Int) {
        viewModelScope.launch {
            val ordenSiguiente = seriesCrudas.value.count { it.ejercicioId == ejercicioId } + 1
            repo.registrarSerie(
                SerieRegistrada(
                    sesionId = sesionId,
                    ejercicioId = ejercicioId,
                    pesoKg = pesoKg,
                    repeticiones = repeticiones,
                    orden = ordenSiguiente
                )
            )
        }
    }

    // Busca la sesión actual (para no pisar su fecha original) y le completa la duración al finalizar.
    fun finalizarSesion(duracionMinutos: Int?) {
        viewModelScope.launch {
            val actual = repo.observarSesiones().first().find { it.id == sesionId } ?: return@launch
            repo.finalizarSesion(actual.copy(duracionMinutos = duracionMinutos))
        }
    }
}
