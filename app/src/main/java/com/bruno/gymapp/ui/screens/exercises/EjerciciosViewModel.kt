package com.bruno.gymapp.ui.screens.exercises

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bruno.gymapp.data.local.dao.PuntoProgreso
import com.bruno.gymapp.data.local.entity.CategoriaEjercicio
import com.bruno.gymapp.data.local.entity.Ejercicio
import com.bruno.gymapp.data.local.entity.GrupoMuscular
import com.bruno.gymapp.data.local.entity.TipoEjercicio
import com.bruno.gymapp.data.local.entity.MovementCategory
import com.bruno.gymapp.data.repository.GymRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class EjerciciosViewModel(private val repo: GymRepository) : ViewModel() {
    val ejercicios: StateFlow<List<Ejercicio>> = repo.observarEjercicios()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun agruparPorSubcategoria(ejercicios: List<Ejercicio>, categoria: MovementCategory?): Map<com.bruno.gymapp.data.local.entity.MuscleGroup, List<Ejercicio>> =
        ejercicios
            .asSequence()
            .filter { categoria == null || it.categoria == categoria }
            .groupBy { it.subCategory }

    fun observarProgreso(ejercicioId: Long): Flow<List<PuntoProgreso>> =
        repo.observarProgreso(ejercicioId)

    fun agregarEjercicio(
        nombre: String,
        grupo: GrupoMuscular,
        categoria: CategoriaEjercicio,
        tipo: TipoEjercicio,
        descansoSegundos: Int
    ) {
        viewModelScope.launch(Dispatchers.IO) {
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
