package com.bruno.gymapp.data.local.dao

import androidx.room.*
import com.bruno.gymapp.data.local.entity.Ejercicio
import kotlinx.coroutines.flow.Flow

@Dao
interface EjercicioDao {

    @Query("SELECT * FROM ejercicios ORDER BY nombre ASC")
    fun observarTodos(): Flow<List<Ejercicio>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertar(ejercicio: Ejercicio): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertarVarios(ejercicios: List<Ejercicio>)

    @Delete
    suspend fun eliminar(ejercicio: Ejercicio)
}
