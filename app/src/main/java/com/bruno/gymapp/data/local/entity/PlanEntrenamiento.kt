package com.bruno.gymapp.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "planes_entrenamiento")
data class PlanEntrenamiento(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val plantillaId: String,
    val nombre: String,
    val objetivo: String,
    val frecuenciaSemanal: Int,
    val personalizado: Boolean = false,
    val notas: String = ""
)

@Entity(
    tableName = "dias_entrenamiento",
    foreignKeys = [ForeignKey(entity = PlanEntrenamiento::class, parentColumns = ["id"], childColumns = ["planId"], onDelete = ForeignKey.CASCADE)],
    indices = [Index("planId")]
)
data class DiaEntrenamiento(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val planId: Long,
    val diaSemana: Int,
    val orden: Int,
    val nombre: String,
    val notas: String = ""
)

enum class FaseEntrenamiento { CALENTAMIENTO, ACTIVACION, COMPUESTO, ACCESORIO, AISLAMIENTO, CORE, FINISHER }

@Entity(
    tableName = "ejercicios_plan",
    foreignKeys = [
        ForeignKey(entity = DiaEntrenamiento::class, parentColumns = ["id"], childColumns = ["diaId"], onDelete = ForeignKey.CASCADE),
        ForeignKey(entity = Ejercicio::class, parentColumns = ["id"], childColumns = ["ejercicioId"], onDelete = ForeignKey.CASCADE)
    ],
    indices = [Index("diaId"), Index("ejercicioId")]
)
data class EjercicioPlan(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val diaId: Long,
    val ejercicioId: Long,
    val posicion: Int,
    val fase: FaseEntrenamiento,
    val seriesObjetivo: Int,
    val repeticionesMin: Int = 0,
    val repeticionesMax: Int = 0,
    val tiempoSegundos: Int? = null,
    val rirObjetivo: Int? = null,
    val descansoSegundos: Int,
    val notas: String = ""
)
