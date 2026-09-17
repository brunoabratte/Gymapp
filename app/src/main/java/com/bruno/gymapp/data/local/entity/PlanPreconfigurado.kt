package com.bruno.gymapp.data.local.entity

import androidx.compose.runtime.Immutable
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

@Immutable
data class PlanPreconfigurado(
    val id: String,
    val nombre: String,
    val descripcion: String,
    val descansoCompuesto: Int,
    val descansoBasico: Int,
    val descansoAislamiento: Int,
    val ejercicios: ImmutableList<String>
)

@Immutable
data class PrescripcionEjercicio(
    val series: Int,
    val repeticiones: String,
    val objetivo: String
)

fun PlanPreconfigurado.prescripcionPara(tipo: TipoEjercicio): PrescripcionEjercicio {
    return when (id) {
        "fuerza" -> when (tipo) {
            TipoEjercicio.COMPUESTO -> PrescripcionEjercicio(4, "3-6", "Fuerza y técnica")
            TipoEjercicio.BASICO -> PrescripcionEjercicio(3, "5-8", "Fuerza controlada")
            else -> PrescripcionEjercicio(3, "8-12", "Accesorio")
        }
        "full_body" -> when (tipo) {
            TipoEjercicio.COMPUESTO -> PrescripcionEjercicio(3, "6-10", "Fuerza e hipertrofia")
            else -> PrescripcionEjercicio(3, "10-15", "Volumen moderado")
        }
        "torso_pierna", "cardio" -> when (tipo) {
            TipoEjercicio.COMPUESTO -> PrescripcionEjercicio(4, "6-10", "Volumen por patrón")
            else -> PrescripcionEjercicio(3, "10-15", "Volumen por músculo")
        }
        else -> when (tipo) {
            TipoEjercicio.COMPUESTO -> PrescripcionEjercicio(4, "6-10", "Hipertrofia")
            TipoEjercicio.BASICO -> PrescripcionEjercicio(3, "8-12", "Hipertrofia")
            TipoEjercicio.AISLAMIENTO -> PrescripcionEjercicio(3, "10-15", "Hipertrofia")
            TipoEjercicio.CARDIO -> PrescripcionEjercicio(3, "10-20 min", "Condición")
        }
    }
}

object PlanesPreconfigurados {
    val todos = listOf(
        PlanPreconfigurado(
            id = "full_body",
            nombre = "Full Body",
            descripcion = "Todo el cuerpo en una sola sesión, con énfasis en movimientos grandes.",
            descansoCompuesto = 150,
            descansoBasico = 90,
            descansoAislamiento = 60,
            ejercicios = persistentListOf("Sentadilla", "Press de banca", "Remo con barra", "Press militar", "Curl de bíceps", "Extensión de tríceps", "Plancha")
        ),
        PlanPreconfigurado(
            id = "hipertrofia",
            nombre = "Hipertrofia",
            descripcion = "Volumen moderado y descansos más cortos para trabajar cerca del fallo.",
            descansoCompuesto = 120,
            descansoBasico = 90,
            descansoAislamiento = 60,
            ejercicios = persistentListOf("Press de banca", "Press inclinado", "Remo con barra", "Jalón al pecho", "Press militar", "Curl de bíceps", "Extensión de tríceps")
        ),
        PlanPreconfigurado(
            id = "fuerza",
            nombre = "Fuerza",
            descripcion = "Prioriza movimientos compuestos y descansos largos.",
            descansoCompuesto = 180,
            descansoBasico = 120,
            descansoAislamiento = 90,
            ejercicios = persistentListOf("Sentadilla", "Press de banca", "Peso muerto", "Press militar", "Dominadas")
        ),
        PlanPreconfigurado(
            id = "cardio",
            nombre = "Cardio",
            descripcion = "Plan progresivo de capacidad aeróbica y acondicionamiento general.",
            descansoCompuesto = 150,
            descansoBasico = 90,
            descansoAislamiento = 60,
            ejercicios = persistentListOf("Caminata inclinada", "Bicicleta", "Remo ergómetro", "Plancha")
        )
    )

    fun porId(id: String): PlanPreconfigurado = todos.firstOrNull { it.id == id }
        ?: if (id == "torso_pierna") todos.first { it.id == "cardio" } else todos.first()
}
