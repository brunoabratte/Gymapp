package com.bruno.gymapp.domain

import com.bruno.gymapp.data.local.entity.DiaEntrenamiento
import com.bruno.gymapp.data.local.entity.Ejercicio
import com.bruno.gymapp.data.local.entity.EjercicioPlan
import com.bruno.gymapp.data.local.entity.FaseEntrenamiento
import com.bruno.gymapp.data.local.entity.PlanEntrenamiento
import com.bruno.gymapp.data.local.entity.PlanPreconfigurado
import com.bruno.gymapp.data.local.entity.TipoEjercicio
import com.bruno.gymapp.data.local.entity.prescripcionPara

class PlanificadorEntrenamiento {
    fun construir(planBase: PlanPreconfigurado, diasSemana: List<Int>, ejercicios: List<Ejercicio>): PlanDefinido {
        require(diasSemana.size in 1..7)
        val ejerciciosPorNombre = ejercicios.associateBy { it.nombre }
        val seleccionados = planBase.ejercicios.mapNotNull { ejerciciosPorNombre[it] }
        val bloques = distribuirBloques(seleccionados, diasSemana.size)
        val plan = PlanEntrenamiento(
            plantillaId = planBase.id,
            nombre = planBase.nombre,
            objetivo = planBase.descripcion,
            frecuenciaSemanal = diasSemana.size,
            personalizado = false
        )
        val dias = diasSemana.mapIndexed { indice, dia ->
            DiaEntrenamiento(planId = 0, diaSemana = dia, orden = indice, nombre = nombreSesion(planBase, indice, bloques.size))
        }
        val prescripciones = bloques.map { bloque ->
            bloque.mapIndexed { posicion, ejercicio ->
                val recomendacion = planBase.prescripcionPara(ejercicio.tipo)
                EjercicioPlan(
                    diaId = 0,
                    ejercicioId = ejercicio.id,
                    posicion = posicion,
                    fase = faseDe(ejercicio, posicion, bloque.size),
                    seriesObjetivo = recomendacion.series,
                    repeticionesMin = recomendacion.repeticiones.substringBefore('-').toIntOrNull() ?: 0,
                    repeticionesMax = recomendacion.repeticiones.substringAfter('-', recomendacion.repeticiones).filter { it.isDigit() }.toIntOrNull() ?: 0,
                    tiempoSegundos = if (ejercicio.tipo == TipoEjercicio.CARDIO) 1200 else null,
                    rirObjetivo = if (planBase.id == "fuerza") 2 else 1,
                    descansoSegundos = ejercicio.descansoSegundos
                )
            }
        }
        return PlanDefinido(plan, dias, prescripciones)
    }

    private fun distribuirBloques(ejercicios: List<Ejercicio>, cantidadDias: Int): List<List<Ejercicio>> =
        List(cantidadDias) { indice -> ejercicios.filterIndexed { ejercicioIndex, _ -> ejercicioIndex % cantidadDias == indice } }

    private fun faseDe(ejercicio: Ejercicio, posicion: Int, total: Int): FaseEntrenamiento = when {
        posicion == 0 && ejercicio.tipo == TipoEjercicio.CARDIO -> FaseEntrenamiento.CALENTAMIENTO
        posicion == 0 -> FaseEntrenamiento.COMPUESTO
        posicion == total - 1 && ejercicio.grupoMuscular.name == "CORE" -> FaseEntrenamiento.CORE
        ejercicio.tipo == TipoEjercicio.AISLAMIENTO -> FaseEntrenamiento.AISLAMIENTO
        else -> FaseEntrenamiento.ACCESORIO
    }

    private fun nombreSesion(plan: PlanPreconfigurado, indice: Int, total: Int): String =
        if (plan.id == "cardio") "Cardio ${indice + 1}" else "${plan.nombre} ${indice + 1}/$total"
}

data class PlanDefinido(
    val plan: PlanEntrenamiento,
    val dias: List<DiaEntrenamiento>,
    val ejerciciosPorDia: List<List<EjercicioPlan>>
)
