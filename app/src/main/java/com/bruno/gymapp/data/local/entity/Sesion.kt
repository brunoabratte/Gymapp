package com.bruno.gymapp.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class EstadoSesion { DRAFT, IN_PROGRESS, COMPLETED }

@Entity(tableName = "sesiones")
data class Sesion(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val fechaEpochMillis: Long,     // System.currentTimeMillis() al iniciar
    val duracionMinutos: Int? = null,
    val notas: String? = null,
    val nombrePlan: String = "Entrenamiento libre",
    val planId: String = "libre",
    val estado: EstadoSesion = EstadoSesion.IN_PROGRESS,
    val creadoEnEpochMillis: Long = fechaEpochMillis,
    val actualizadoEnEpochMillis: Long = fechaEpochMillis
)
