package com.bruno.gymapp.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sesiones")
data class Sesion(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val fechaEpochMillis: Long,     // System.currentTimeMillis() al iniciar
    val duracionMinutos: Int? = null,
    val notas: String? = null
)
