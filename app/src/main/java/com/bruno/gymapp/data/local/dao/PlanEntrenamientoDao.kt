package com.bruno.gymapp.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import com.bruno.gymapp.data.local.entity.DiaEntrenamiento
import com.bruno.gymapp.data.local.entity.EjercicioPlan
import com.bruno.gymapp.data.local.entity.PlanEntrenamiento
import kotlinx.coroutines.flow.Flow

@Dao
abstract class PlanEntrenamientoDao {
    @Insert
    abstract suspend fun insertarPlan(plan: PlanEntrenamiento): Long

    @Insert
    abstract suspend fun insertarDias(dias: List<DiaEntrenamiento>): List<Long>

    @Insert
    abstract suspend fun insertarEjercicios(ejercicios: List<EjercicioPlan>)

    @Query("SELECT * FROM planes_entrenamiento ORDER BY id DESC")
    abstract fun observarPlanes(): Flow<List<PlanEntrenamiento>>

    @Query("SELECT * FROM dias_entrenamiento WHERE planId = :planId ORDER BY orden ASC")
    abstract fun observarDias(planId: Long): Flow<List<DiaEntrenamiento>>

    @Query("SELECT * FROM ejercicios_plan WHERE diaId = :diaId ORDER BY posicion ASC")
    abstract fun observarEjercicios(diaId: Long): Flow<List<EjercicioPlan>>

    @Query("UPDATE ejercicios_plan SET posicion = :posicion, seriesObjetivo = :series, repeticionesMin = :min, repeticionesMax = :max, rirObjetivo = :rir, descansoSegundos = :descanso, notas = :notas WHERE id = :id")
    abstract suspend fun actualizarEjercicio(id: Long, posicion: Int, series: Int, min: Int, max: Int, rir: Int?, descanso: Int, notas: String)

    @Query("UPDATE dias_entrenamiento SET orden = :orden, nombre = :nombre, notas = :notas WHERE id = :id")
    abstract suspend fun actualizarDia(id: Long, orden: Int, nombre: String, notas: String)

    @Transaction
    open suspend fun crearPlanCompleto(plan: PlanEntrenamiento, dias: List<DiaEntrenamiento>, ejerciciosPorDia: List<List<EjercicioPlan>>): Long {
        val planId = insertarPlan(plan)
        val diasConPlan = dias.map { it.copy(planId = planId) }
        val diaIds = insertarDias(diasConPlan)
        insertarEjercicios(ejerciciosPorDia.flatMapIndexed { index, ejercicios ->
            ejercicios.map { it.copy(diaId = diaIds[index]) }
        })
        return planId
    }
}
