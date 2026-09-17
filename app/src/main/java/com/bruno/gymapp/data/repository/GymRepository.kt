package com.bruno.gymapp.data.repository

import com.bruno.gymapp.data.local.AppDatabase
import com.bruno.gymapp.data.local.dao.ConteoPorSesion
import com.bruno.gymapp.data.local.dao.PuntoProgreso
import com.bruno.gymapp.data.local.dao.PlanEntrenamientoDao
import com.bruno.gymapp.data.local.entity.Ejercicio
import com.bruno.gymapp.data.local.entity.Sesion
import com.bruno.gymapp.data.local.entity.SerieRegistrada
import com.bruno.gymapp.data.local.entity.EstadoSesion
import kotlinx.coroutines.flow.Flow

class GymRepository(db: AppDatabase) {

    private val ejercicioDao = db.ejercicioDao()
    private val sesionDao = db.sesionDao()
    private val serieDao = db.serieDao()
    private val planDao: PlanEntrenamientoDao = db.planEntrenamientoDao()

    fun observarEjercicios(): Flow<List<Ejercicio>> = ejercicioDao.observarTodos()

    suspend fun crearEjercicioPersonalizado(ejercicio: Ejercicio): Long =
        ejercicioDao.insertar(ejercicio)

    fun observarSesiones(): Flow<List<Sesion>> = sesionDao.observarTodas()

    suspend fun iniciarSesion(fechaEpochMillis: Long, nombrePlan: String = "Entrenamiento libre", planId: String = "libre"): Long =
        sesionDao.insertar(Sesion(fechaEpochMillis = fechaEpochMillis, nombrePlan = nombrePlan, planId = planId, estado = EstadoSesion.IN_PROGRESS, creadoEnEpochMillis = fechaEpochMillis, actualizadoEnEpochMillis = fechaEpochMillis))

    suspend fun finalizarSesion(sesion: Sesion) = sesionDao.actualizar(sesion.copy(estado = EstadoSesion.COMPLETED, actualizadoEnEpochMillis = System.currentTimeMillis()))

    fun observarSesionEnCurso(): Flow<Sesion?> = sesionDao.observarSesionEnCurso()

    suspend fun registrarSerie(serie: SerieRegistrada): Long = serieDao.insertar(serie)

    suspend fun actualizarSerie(serie: SerieRegistrada) = serieDao.actualizar(serie)

    suspend fun eliminarSerie(serie: SerieRegistrada) = serieDao.eliminar(serie)

    fun observarSeriesDeSesion(sesionId: Long): Flow<List<SerieRegistrada>> =
        serieDao.observarPorSesion(sesionId)

    fun observarProgreso(ejercicioId: Long): Flow<List<PuntoProgreso>> =
        serieDao.observarProgreso(ejercicioId)

    fun observarUltimoRegistro(ejercicioId: Long): Flow<PuntoProgreso?> =
        serieDao.observarUltimoRegistro(ejercicioId)

    fun observarConteoPorSesion(): Flow<List<ConteoPorSesion>> =
        serieDao.observarConteoPorSesion()

    fun observarCantidadTotalSeries(): Flow<Int> = serieDao.observarCantidadTotal()

    fun observarPlanes() = planDao.observarPlanes()
    fun observarDiasDePlan(planId: Long) = planDao.observarDias(planId)
    fun observarEjerciciosDeDia(diaId: Long) = planDao.observarEjercicios(diaId)
}
