package com.bruno.gymapp.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class GrupoMuscular {
    PECHO, ESPALDA, PIERNA, HOMBRO, BICEPS, TRICEPS, CORE, CARDIO, OTRO
}

@Entity(tableName = "ejercicios")
data class Ejercicio(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val nombre: String,
    val grupoMuscular: GrupoMuscular,
    val esPersonalizado: Boolean = false // false = precargado, true = creado por el usuario
)
