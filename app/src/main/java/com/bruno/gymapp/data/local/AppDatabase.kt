package com.bruno.gymapp.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.bruno.gymapp.data.local.dao.EjercicioDao
import com.bruno.gymapp.data.local.dao.SerieDao
import com.bruno.gymapp.data.local.dao.SesionDao
import com.bruno.gymapp.data.local.entity.Ejercicio
import com.bruno.gymapp.data.local.entity.GrupoMuscular
import com.bruno.gymapp.data.local.entity.Sesion
import com.bruno.gymapp.data.local.entity.SerieRegistrada
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class Converters {
    @TypeConverter
    fun fromGrupoMuscular(valor: GrupoMuscular): String = valor.name

    @TypeConverter
    fun toGrupoMuscular(valor: String): GrupoMuscular = GrupoMuscular.valueOf(valor)
}

@Database(
    entities = [Ejercicio::class, Sesion::class, SerieRegistrada::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun ejercicioDao(): EjercicioDao
    abstract fun sesionDao(): SesionDao
    abstract fun serieDao(): SerieDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "gymapp.db"
                )
                    .addCallback(object : Callback() {
                        override fun onCreate(db: androidx.sqlite.db.SupportSQLiteDatabase) {
                            super.onCreate(db)
                            // Precarga de ejercicios comunes al crear la BD por primera vez
                            CoroutineScope(Dispatchers.IO).launch {
                                INSTANCE?.ejercicioDao()?.insertarVarios(ejerciciosBase())
                            }
                        }
                    })
                    .build()
                    .also { INSTANCE = it }
            }
        }

        private fun ejerciciosBase(): List<Ejercicio> = listOf(
            Ejercicio(nombre = "Press de banca", grupoMuscular = GrupoMuscular.PECHO),
            Ejercicio(nombre = "Sentadilla", grupoMuscular = GrupoMuscular.PIERNA),
            Ejercicio(nombre = "Peso muerto", grupoMuscular = GrupoMuscular.ESPALDA),
            Ejercicio(nombre = "Dominadas", grupoMuscular = GrupoMuscular.ESPALDA),
            Ejercicio(nombre = "Press militar", grupoMuscular = GrupoMuscular.HOMBRO),
            Ejercicio(nombre = "Curl de bíceps", grupoMuscular = GrupoMuscular.BICEPS),
            Ejercicio(nombre = "Extensión de tríceps", grupoMuscular = GrupoMuscular.TRICEPS),
            Ejercicio(nombre = "Plancha", grupoMuscular = GrupoMuscular.CORE)
        )
    }
}
