package com.bruno.gymapp.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.compose.runtime.Immutable

enum class GrupoMuscular {
    PECHO, ESPALDA, PIERNA, HOMBRO, BICEPS, TRICEPS, CORE, CARDIO, OTRO
}

@Immutable
@Entity(tableName = "ejercicios")
data class Ejercicio(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val nombre: String,
    val grupoMuscular: GrupoMuscular,
    val categoria: CategoriaEjercicio = CategoriaEjercicio.OTRO,
    val tipo: TipoEjercicio = TipoEjercicio.BASICO,
    val descansoSegundos: Int = 60,
    val esPersonalizado: Boolean = false // false = precargado, true = creado por el usuario
) {
    val subCategory: MuscleGroup
        get() = MuscleGroup.fromExercise(nombre, grupoMuscular)
}

enum class MuscleGroup(val label: String) {
    PECHO("Pecho"),
    ESPALDA_ALTA("Espalda alta"),
    DORSALES("Dorsales"),
    HOMBRO("Hombro anterior/lateral"),
    DELTOIDES_POSTERIOR("Deltoides posterior"),
    BICEPS("Bíceps"),
    TRICEPS("Tríceps"),
    CUADRICEPS("Cuádriceps"),
    ISQUIOTIBIALES("Isquiotibiales"),
    GLUTEOS("Glúteos"),
    GEMELOS("Gemelos"),
    CORE("Core"),
    CARDIO("Cardio"),
    OTRO("Otros");

    companion object {
        fun fromExercise(nombre: String, grupo: GrupoMuscular): MuscleGroup {
            val nombreNormalizado = nombre.lowercase()
            return when (grupo) {
                GrupoMuscular.PIERNA -> when {
                    "femoral" in nombreNormalizado || "peso muerto" in nombreNormalizado -> ISQUIOTIBIALES
                    "gemelo" in nombreNormalizado || "pantorrilla" in nombreNormalizado -> GEMELOS
                    "glúteo" in nombreNormalizado || "hip thrust" in nombreNormalizado -> GLUTEOS
                    else -> CUADRICEPS
                }
                GrupoMuscular.ESPALDA -> when {
                    "jalón" in nombreNormalizado || "dominada" in nombreNormalizado -> DORSALES
                    "deltoide" in nombreNormalizado || "posterior" in nombreNormalizado -> DELTOIDES_POSTERIOR
                    else -> ESPALDA_ALTA
                }
                GrupoMuscular.HOMBRO -> when {
                    "posterior" in nombreNormalizado -> DELTOIDES_POSTERIOR
                    else -> HOMBRO
                }
                else -> fromGrupoMuscular(grupo)
            }
        }

        fun fromGrupoMuscular(grupo: GrupoMuscular): MuscleGroup = when (grupo) {
            GrupoMuscular.PECHO -> PECHO
            GrupoMuscular.ESPALDA -> DORSALES
            GrupoMuscular.HOMBRO -> HOMBRO
            GrupoMuscular.BICEPS -> BICEPS
            GrupoMuscular.TRICEPS -> TRICEPS
            GrupoMuscular.PIERNA -> CUADRICEPS
            GrupoMuscular.CORE -> CORE
            GrupoMuscular.CARDIO -> CARDIO
            GrupoMuscular.OTRO -> OTRO
        }
    }
}
