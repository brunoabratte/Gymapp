package com.bruno.gymapp.ui.screens.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bruno.gymapp.data.repository.GymRepository
import com.bruno.gymapp.data.local.entity.EstadoSesion
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import androidx.compose.runtime.Immutable

// Fila lista para mostrar en la lista de historial
@Immutable
data class SesionHistorialUi(
    val sesionId: Long,
    val fechaEpochMillis: Long,
    val duracionMinutos: Int?,
    val cantidadSeries: Int,
    val planId: String,
    val estado: EstadoSesion
)

class HistorialViewModel(repo: GymRepository) : ViewModel() {

    val sesiones: StateFlow<List<SesionHistorialUi>> = combine(
        repo.observarSesiones(),
        repo.observarConteoPorSesion()
    ) { sesiones, conteos ->
        sesiones.map { sesion ->
            SesionHistorialUi(
                sesionId = sesion.id,
                fechaEpochMillis = sesion.fechaEpochMillis,
                duracionMinutos = sesion.duracionMinutos,
                cantidadSeries = conteos.find { it.sesionId == sesion.id }?.cantidad ?: 0
                ,planId = sesion.planId
                ,estado = sesion.estado
            )
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
}
