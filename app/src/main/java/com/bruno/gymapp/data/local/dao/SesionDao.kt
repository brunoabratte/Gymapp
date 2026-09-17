package com.bruno.gymapp.data.local.dao

import androidx.room.*
import com.bruno.gymapp.data.local.entity.Sesion
import kotlinx.coroutines.flow.Flow

@Dao
interface SesionDao {

    @Query("SELECT * FROM sesiones ORDER BY fechaEpochMillis DESC")
    fun observarTodas(): Flow<List<Sesion>>

    @Insert
    suspend fun insertar(sesion: Sesion): Long

    @Query("SELECT * FROM sesiones WHERE duracionMinutos IS NULL ORDER BY fechaEpochMillis DESC LIMIT 1")
    fun observarSesionEnCurso(): Flow<Sesion?>

    @Update
    suspend fun actualizar(sesion: Sesion)
}
