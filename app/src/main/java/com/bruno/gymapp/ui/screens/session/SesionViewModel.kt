package com.bruno.gymapp.ui.screens.session

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bruno.gymapp.data.local.entity.Ejercicio
import com.bruno.gymapp.data.local.entity.SerieRegistrada
import com.bruno.gymapp.data.local.entity.PlanesPreconfigurados
import com.bruno.gymapp.data.local.entity.TipoEjercicio
import com.bruno.gymapp.data.repository.GymRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

// Fila ya lista para mostrar en la lista de series cargadas (con el nombre del ejercicio resuelto)
data class SerieUi(
    val id: Long,
    val nombreEjercicio: String,
    val pesoKg: Double,
    val repeticiones: Int,
    val orden: Int
)

class SesionViewModel(
    private val repo: GymRepository,
    private val sesionId: Long,
    private val planId: String
) : ViewModel() {

    private val plan = if (planId == "libre") {
        PlanesPreconfigurados.porId("hipertrofia").copy(
            nombre = "Entrenamiento libre",
            ejercicios = emptyList(),
            descansoCompuesto = DESCANSO_POR_DEFECTO_SEGUNDOS,
            descansoBasico = 90,
            descansoAislamiento = 60
        )
    } else PlanesPreconfigurados.porId(planId)
    val nombrePlan: String = plan.nombre

    val ejercicios: StateFlow<List<Ejercicio>> = repo.observarEjercicios()
        .combine(kotlinx.coroutines.flow.flowOf(plan)) { ejercicios, plan ->
            if (planId == "libre") ejercicios
            else plan.ejercicios.mapNotNull { nombre -> ejercicios.firstOrNull { it.nombre == nombre } }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val seriesCrudas: StateFlow<List<SerieRegistrada>> = repo.observarSeriesDeSesion(sesionId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val series: StateFlow<List<SerieUi>> = combine(seriesCrudas, ejercicios) { series, ejercicios ->
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

    companion object {
        const val DESCANSO_POR_DEFECTO_SEGUNDOS = 90
    }

    private val _segundosDescanso = MutableStateFlow(0)
    val segundosDescanso: StateFlow<Int> = _segundosDescanso

    private val _timerPausado = MutableStateFlow(false)
    val timerPausado: StateFlow<Boolean> = _timerPausado

    private var timerJob: kotlinx.coroutines.Job? = null

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

            iniciarDescanso(restFor(ejercicioId))
        }
    }

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

    fun descansoPara(ejercicio: Ejercicio): String = "${restFor(ejercicio.id) / 60}:${(restFor(ejercicio.id) % 60).toString().padStart(2, '0')}"

    private fun restFor(ejercicioId: Long): Int {
        val ejercicio = ejercicios.value.firstOrNull { it.id == ejercicioId } ?: return DESCANSO_POR_DEFECTO_SEGUNDOS
        return when (ejercicio.tipo) {
            TipoEjercicio.COMPUESTO -> plan.descansoCompuesto
            TipoEjercicio.BASICO -> plan.descansoBasico
            TipoEjercicio.AISLAMIENTO -> plan.descansoAislamiento
            TipoEjercicio.CARDIO -> 45
        }
    }

    fun iniciarDescanso(segundos: Int = DESCANSO_POR_DEFECTO_SEGUNDOS) {
        timerJob?.cancel()
        _segundosDescanso.value = segundos.coerceAtLeast(0)
        _timerPausado.value = false

        if (_segundosDescanso.value == 0) return

        timerJob = viewModelScope.launch {
            while (_segundosDescanso.value > 0) {
                delay(1000)
                _segundosDescanso.value = (_segundosDescanso.value - 1).coerceAtLeast(0)
            }
        }
    }

    fun pausarDescanso() {
        if (_segundosDescanso.value <= 0) return
        timerJob?.cancel()
        timerJob = null
        _timerPausado.value = true
    }

    fun continuarDescanso() {
        if (_segundosDescanso.value > 0 && timerJob?.isActive != true) {
            _timerPausado.value = false
            timerJob = viewModelScope.launch {
                while (_segundosDescanso.value > 0) {
                    delay(1000)
                    _segundosDescanso.value = (_segundosDescanso.value - 1).coerceAtLeast(0)
                }
            }
        }
    }

    fun reiniciarDescanso() {
        val ultimoEjercicioId = seriesCrudas.value.lastOrNull()?.ejercicioId
        iniciarDescanso(ultimoEjercicioId?.let(::restFor) ?: DESCANSO_POR_DEFECTO_SEGUNDOS)
    }

    fun cerrarDescanso() {
        timerJob?.cancel()
        timerJob = null
        _timerPausado.value = false
        _segundosDescanso.value = 0
    }

    override fun onCleared() {
        timerJob?.cancel()
        super.onCleared()
    }

    // Busca la sesión actual (para no pisar su fecha original) y calcula cuántos minutos pasaron desde que arrancó.
    fun finalizarSesion() {
        viewModelScope.launch {
            val actual = repo.observarSesiones().first().find { it.id == sesionId } ?: return@launch
            val minutos = ((System.currentTimeMillis() - actual.fechaEpochMillis) / 60000).toInt().coerceAtLeast(0)
            repo.finalizarSesion(actual.copy(duracionMinutos = minutos))
        }
    }
}
