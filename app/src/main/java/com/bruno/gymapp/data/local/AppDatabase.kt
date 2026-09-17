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
import com.bruno.gymapp.data.local.entity.CategoriaEjercicio
import com.bruno.gymapp.data.local.entity.Ejercicio
import com.bruno.gymapp.data.local.entity.GrupoMuscular
import com.bruno.gymapp.data.local.entity.SerieRegistrada
import com.bruno.gymapp.data.local.entity.Sesion
import com.bruno.gymapp.data.local.entity.TipoEjercicio
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class Converters {
    @TypeConverter fun fromGrupoMuscular(valor: GrupoMuscular): String = valor.name
    @TypeConverter fun toGrupoMuscular(valor: String): GrupoMuscular = GrupoMuscular.valueOf(valor)
    @TypeConverter fun fromTipoEjercicio(valor: TipoEjercicio): String = valor.name
    @TypeConverter fun toTipoEjercicio(valor: String): TipoEjercicio = TipoEjercicio.valueOf(valor)
    @TypeConverter fun fromCategoriaEjercicio(valor: CategoriaEjercicio): String = valor.name
    @TypeConverter fun toCategoriaEjercicio(valor: String): CategoriaEjercicio = CategoriaEjercicio.valueOf(valor)
}

@Database(
    entities = [Ejercicio::class, Sesion::class, SerieRegistrada::class],
    version = 3,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun ejercicioDao(): EjercicioDao
    abstract fun sesionDao(): SesionDao
    abstract fun serieDao(): SerieDao

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase = INSTANCE ?: synchronized(this) {
            Room.databaseBuilder(context.applicationContext, AppDatabase::class.java, "gymapp.db")
                .addMigrations(MIGRATION_1_2, MIGRATION_2_3)
                .addCallback(object : Callback() {
                    override fun onCreate(db: androidx.sqlite.db.SupportSQLiteDatabase) {
                        super.onCreate(db)
                        CoroutineScope(Dispatchers.IO).launch {
                            INSTANCE?.ejercicioDao()?.insertarVarios(ejerciciosBase())
                        }
                    }
                })
                .build()
                .also { INSTANCE = it }
        }

        val MIGRATION_1_2 = object : androidx.room.migration.Migration(1, 2) {
            override fun migrate(db: androidx.sqlite.db.SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE ejercicios ADD COLUMN tipo TEXT NOT NULL DEFAULT 'BASICO'")
                db.execSQL("ALTER TABLE ejercicios ADD COLUMN descansoSegundos INTEGER NOT NULL DEFAULT 60")
                db.execSQL("ALTER TABLE sesiones ADD COLUMN nombrePlan TEXT NOT NULL DEFAULT 'Entrenamiento libre'")
            }
        }

        val MIGRATION_2_3 = object : androidx.room.migration.Migration(2, 3) {
            override fun migrate(db: androidx.sqlite.db.SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE ejercicios ADD COLUMN categoria TEXT NOT NULL DEFAULT 'OTRO'")
                db.execSQL("ALTER TABLE sesiones ADD COLUMN planId TEXT NOT NULL DEFAULT 'libre'")
            }
        }

        private fun ejerciciosBase(): List<Ejercicio> = listOf(
            Ejercicio(nombre="Press de banca", grupoMuscular=GrupoMuscular.PECHO, categoria=CategoriaEjercicio.EMPUJE, tipo=TipoEjercicio.COMPUESTO, descansoSegundos=120),
            Ejercicio(nombre="Press inclinado", grupoMuscular=GrupoMuscular.PECHO, categoria=CategoriaEjercicio.EMPUJE, tipo=TipoEjercicio.COMPUESTO, descansoSegundos=120),
            Ejercicio(nombre="Sentadilla", grupoMuscular=GrupoMuscular.PIERNA, categoria=CategoriaEjercicio.PIERNAS, tipo=TipoEjercicio.COMPUESTO, descansoSegundos=150),
            Ejercicio(nombre="Peso muerto", grupoMuscular=GrupoMuscular.ESPALDA, categoria=CategoriaEjercicio.PIERNAS, tipo=TipoEjercicio.COMPUESTO, descansoSegundos=180),
            Ejercicio(nombre="Dominadas", grupoMuscular=GrupoMuscular.ESPALDA, categoria=CategoriaEjercicio.TIRON, tipo=TipoEjercicio.COMPUESTO, descansoSegundos=120),
            Ejercicio(nombre="Remo con barra", grupoMuscular=GrupoMuscular.ESPALDA, categoria=CategoriaEjercicio.TIRON, tipo=TipoEjercicio.COMPUESTO, descansoSegundos=120),
            Ejercicio(nombre="Jalón al pecho", grupoMuscular=GrupoMuscular.ESPALDA, categoria=CategoriaEjercicio.TIRON, tipo=TipoEjercicio.BASICO, descansoSegundos=90),
            Ejercicio(nombre="Press militar", grupoMuscular=GrupoMuscular.HOMBRO, categoria=CategoriaEjercicio.EMPUJE, tipo=TipoEjercicio.COMPUESTO, descansoSegundos=120),
            Ejercicio(nombre="Elevaciones laterales", grupoMuscular=GrupoMuscular.HOMBRO, categoria=CategoriaEjercicio.EMPUJE, tipo=TipoEjercicio.AISLAMIENTO, descansoSegundos=60),
            Ejercicio(nombre="Curl de bíceps", grupoMuscular=GrupoMuscular.BICEPS, categoria=CategoriaEjercicio.TIRON, tipo=TipoEjercicio.BASICO, descansoSegundos=60),
            Ejercicio(nombre="Curl martillo", grupoMuscular=GrupoMuscular.BICEPS, categoria=CategoriaEjercicio.TIRON, tipo=TipoEjercicio.BASICO, descansoSegundos=60),
            Ejercicio(nombre="Extensión de tríceps", grupoMuscular=GrupoMuscular.TRICEPS, categoria=CategoriaEjercicio.EMPUJE, tipo=TipoEjercicio.AISLAMIENTO, descansoSegundos=60),
            Ejercicio(nombre="Plancha", grupoMuscular=GrupoMuscular.CORE, categoria=CategoriaEjercicio.CORE, tipo=TipoEjercicio.BASICO, descansoSegundos=45),
            Ejercicio(nombre="Crunch abdominal", grupoMuscular=GrupoMuscular.CORE, categoria=CategoriaEjercicio.CORE, tipo=TipoEjercicio.BASICO, descansoSegundos=45),
            Ejercicio(nombre="Zancadas", grupoMuscular=GrupoMuscular.PIERNA, categoria=CategoriaEjercicio.PIERNAS, tipo=TipoEjercicio.BASICO, descansoSegundos=90),
            Ejercicio(nombre="Prensa de piernas", grupoMuscular=GrupoMuscular.PIERNA, categoria=CategoriaEjercicio.PIERNAS, tipo=TipoEjercicio.COMPUESTO, descansoSegundos=120),
            Ejercicio(nombre="Curl femoral", grupoMuscular=GrupoMuscular.PIERNA, categoria=CategoriaEjercicio.PIERNAS, tipo=TipoEjercicio.AISLAMIENTO, descansoSegundos=60),
            Ejercicio(nombre="Extensión de cuádriceps", grupoMuscular=GrupoMuscular.PIERNA, categoria=CategoriaEjercicio.PIERNAS, tipo=TipoEjercicio.AISLAMIENTO, descansoSegundos=60),
            Ejercicio(nombre="Remo en máquina", grupoMuscular=GrupoMuscular.ESPALDA, categoria=CategoriaEjercicio.TIRON, tipo=TipoEjercicio.BASICO, descansoSegundos=90),
            Ejercicio(nombre="Aperturas en máquina", grupoMuscular=GrupoMuscular.PECHO, categoria=CategoriaEjercicio.EMPUJE, tipo=TipoEjercicio.AISLAMIENTO, descansoSegundos=60)
        )
    }
}
