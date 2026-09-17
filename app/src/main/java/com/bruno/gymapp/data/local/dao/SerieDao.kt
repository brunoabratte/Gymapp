package com.bruno.gymapp.data.local.dao

import androidx.room.*
import com.bruno.gymapp.data.local.entity.SerieRegistrada
import kotlinx.coroutines.flow.Flow

// Fila de resultado para el gráfico/tabla de progreso de un ejercicio en el tiempo
data class PuntoProgreso(
    val fechaEpochMillis: Long,
    val pesoKg: Double,
    val repeticiones: Int
)

// Cantidad de series cargadas por sesión, para mostrar en el historial sin abrir cada una
data class ConteoPorSesion(
    val sesionId: Long,
    val cantidad: Int
)

@Dao
interface SerieDao {

    @Insert
    suspend fun insertar(serie: SerieRegistrada): Long

    @Update
    suspend fun actualizar(serie: SerieRegistrada)

    @Delete
    suspend fun eliminar(serie: SerieRegistrada)

    @Query("SELECT * FROM series_registradas WHERE sesionId = :sesionId ORDER BY orden ASC")
    fun observarPorSesion(sesionId: Long): Flow<List<SerieRegistrada>>

    // Progreso histórico de UN ejercicio: peso y reps de cada serie, ordenado por fecha de sesión.
    // Esto es lo que vas a usar para el gráfico de progreso.
    @Query(
        """
        SELECT s.fechaEpochMillis AS fechaEpochMillis, sr.pesoKg AS pesoKg, sr.repeticiones AS repeticiones
        FROM series_registradas sr
        INNER JOIN sesiones s ON s.id = sr.sesionId
        WHERE sr.ejercicioId = :ejercicioId
        ORDER BY s.fechaEpochMillis ASC
        """
    )
    fun observarProgreso(ejercicioId: Long): Flow<List<PuntoProgreso>>

    // Para el historial: cuántas series se cargaron en cada sesión
    @Query(
        """
        SELECT sesionId, COUNT(*) AS cantidad
        FROM series_registradas
        GROUP BY sesionId
        """
    )
    fun observarConteoPorSesion(): Flow<List<ConteoPorSesion>>

    @Query("SELECT COUNT(*) FROM series_registradas")
    fun observarCantidadTotal(): Flow<Int>
}