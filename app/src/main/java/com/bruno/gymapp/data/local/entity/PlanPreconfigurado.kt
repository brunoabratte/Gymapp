package com.bruno.gymapp.data.local.entity

data class PlanPreconfigurado(
    val id: String,
    val nombre: String,
    val descripcion: String,
    val descansoCompuesto: Int,
    val descansoBasico: Int,
    val descansoAislamiento: Int,
    val ejercicios: List<String>
)

object PlanesPreconfigurados {
    val todos = listOf(
        PlanPreconfigurado(
            id = "full_body",
            nombre = "Full Body",
            descripcion = "Todo el cuerpo en una sola sesión, con énfasis en movimientos grandes.",
            descansoCompuesto = 150,
            descansoBasico = 90,
            descansoAislamiento = 60,
            ejercicios = listOf("Sentadilla", "Press de banca", "Remo con barra", "Press militar", "Curl de bíceps", "Extensión de tríceps", "Plancha")
        ),
        PlanPreconfigurado(
            id = "hipertrofia",
            nombre = "Hipertrofia",
            descripcion = "Volumen moderado y descansos más cortos para trabajar cerca del fallo.",
            descansoCompuesto = 120,
            descansoBasico = 90,
            descansoAislamiento = 60,
            ejercicios = listOf("Press de banca", "Press inclinado", "Remo con barra", "Jalón al pecho", "Press militar", "Curl de bíceps", "Extensión de tríceps")
        ),
        PlanPreconfigurado(
            id = "fuerza",
            nombre = "Fuerza",
            descripcion = "Prioriza movimientos compuestos y descansos largos.",
            descansoCompuesto = 180,
            descansoBasico = 120,
            descansoAislamiento = 90,
            ejercicios = listOf("Sentadilla", "Press de banca", "Peso muerto", "Press militar", "Dominadas")
        ),
        PlanPreconfigurado(
            id = "torso_pierna",
            nombre = "Torso / Pierna",
            descripcion = "Alternativa simple para dividir el entrenamiento por zonas.",
            descansoCompuesto = 150,
            descansoBasico = 90,
            descansoAislamiento = 60,
            ejercicios = listOf("Press de banca", "Dominadas", "Remo con barra", "Press militar", "Sentadilla", "Peso muerto", "Curl de bíceps", "Extensión de tríceps")
        )
    )

    fun porId(id: String): PlanPreconfigurado = todos.firstOrNull { it.id == id } ?: todos.first()
}
