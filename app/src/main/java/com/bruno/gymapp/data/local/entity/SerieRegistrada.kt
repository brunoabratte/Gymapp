package com.bruno.gymapp.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "series_registradas",
    foreignKeys = [
        ForeignKey(
            entity = Sesion::class,
            parentColumns = ["id"],
            childColumns = ["sesionId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Ejercicio::class,
            parentColumns = ["id"],
            childColumns = ["ejercicioId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("sesionId"), Index("ejercicioId")]
)
data class SerieRegistrada(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val sesionId: Long,
    val ejercicioId: Long,
    val pesoKg: Double,
    val repeticiones: Int,
    val orden: Int,          // orden de la serie dentro del ejercicio (1ra, 2da, 3ra...)
    val descansoSegundos: Int? = null
)
