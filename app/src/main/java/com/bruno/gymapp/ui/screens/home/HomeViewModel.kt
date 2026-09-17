package com.bruno.gymapp.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bruno.gymapp.data.ConfiguracionEntrenamiento
import com.bruno.gymapp.data.local.entity.PlanesPreconfigurados
import com.bruno.gymapp.data.local.entity.Sesion
import com.bruno.gymapp.data.repository.GymRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

class HomeViewModel(private val repo: GymRepository, private val config: ConfiguracionEntrenamiento) : ViewModel() {
    val sesiones: StateFlow<List<Sesion>> = repo.observarSesiones().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val sesionEnCurso: StateFlow<Sesion?> = repo.observarSesionEnCurso().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)
    private val refrescoConfig = config.cambios

    val planDeHoy: StateFlow<String> = refrescoConfig.combine(kotlinx.coroutines.flow.flowOf(Unit)) { _, _ -> config.planDelDia() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), config.planDelDia())
    val configurado: StateFlow<Boolean> = refrescoConfig.combine(kotlinx.coroutines.flow.flowOf(Unit)) { _, _ -> config.estaConfigurado() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), config.estaConfigurado())
    val cantidadTotalSeries: StateFlow<Int> = repo.observarCantidadTotalSeries().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val nombrePlanHoy: StateFlow<String> = planDeHoy.combine(refrescoConfig) { planId, _ ->
        ConfiguracionEntrenamiento.nombrePlan(planId)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ConfiguracionEntrenamiento.nombrePlan(config.planDelDia()))

    val ejerciciosDeHoy: StateFlow<Int> = planDeHoy.combine(refrescoConfig) { planId, _ ->
        if (planId.isBlank() || planId == "libre") 0 else PlanesPreconfigurados.porId(planId).ejercicios.size
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)
}
