package com.bruno.gymapp.data.repository

import com.bruno.gymapp.data.local.AppDatabase
import com.bruno.gymapp.data.local.dao.PuntoProgreso
import com.bruno.gymapp.data.local.entity.Ejercicio
import com.bruno.gymapp.data.local.entity.Sesion
import com.bruno.gymapp.data.local.entity.SerieRegistrada
import kotlinx.coroutines.flow.Flow

class GymRepository(db: AppDatabase) {

    private val ejercicioDao = db.ejercicioDao()
    private val sesionDao = db.sesionDao()
    private val serieDao = db.serieDao()

    fun observarEjercicios(): Flow<List<Ejercicio>> = ejercicioDao.observarTodos()

    suspend fun crearEjercicioPersonalizado(ejercicio: Ejercicio): Long =
        ejercicioDao.insertar(ejercicio)

    fun observarSesiones(): Flow<List<Sesion>> = sesionDao.observarTodas()

    suspend fun iniciarSesion(fechaEpochMillis: Long): Long =
        sesionDao.insertar(Sesion(fechaEpochMillis = fechaEpochMillis))

    suspend fun finalizarSesion(sesion: Sesion) = sesionDao.actualizar(sesion)

    suspend fun registrarSerie(serie: SerieRegistrada): Long = serieDao.insertar(serie)

    fun observarSeriesDeSesion(sesionId: Long): Flow<List<SerieRegistrada>> =
        serieDao.observarPorSesion(sesionId)

    fun observarProgreso(ejercicioId: Long): Flow<List<PuntoProgreso>> =
        serieDao.observarProgreso(ejercicioId)
}
