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
import com.bruno.gymapp.data.local.entity.EstadoSesion
import com.bruno.gymapp.data.local.entity.TipoEjercicio
import com.bruno.gymapp.data.local.entity.FaseEntrenamiento
import com.bruno.gymapp.data.local.entity.PlanEntrenamiento
import com.bruno.gymapp.data.local.entity.DiaEntrenamiento
import com.bruno.gymapp.data.local.entity.EjercicioPlan
import com.bruno.gymapp.data.local.dao.PlanEntrenamientoDao
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
    @TypeConverter fun fromFaseEntrenamiento(valor: FaseEntrenamiento): String = valor.name
    @TypeConverter fun toFaseEntrenamiento(valor: String): FaseEntrenamiento = FaseEntrenamiento.valueOf(valor)
    @TypeConverter fun fromEstadoSesion(valor: EstadoSesion): String = valor.name
    @TypeConverter fun toEstadoSesion(valor: String): EstadoSesion = EstadoSesion.valueOf(valor)
}

@Database(
    entities = [Ejercicio::class, Sesion::class, SerieRegistrada::class, PlanEntrenamiento::class, DiaEntrenamiento::class, EjercicioPlan::class],
    version = 5,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun ejercicioDao(): EjercicioDao
    abstract fun sesionDao(): SesionDao
    abstract fun serieDao(): SerieDao
    abstract fun planEntrenamientoDao(): PlanEntrenamientoDao

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase = INSTANCE ?: synchronized(this) {
            Room.databaseBuilder(context.applicationContext, AppDatabase::class.java, "gymapp.db")
                .addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4, MIGRATION_4_5)
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

        val MIGRATION_3_4 = object : androidx.room.migration.Migration(3, 4) {
            override fun migrate(db: androidx.sqlite.db.SupportSQLiteDatabase) {
                db.execSQL("CREATE TABLE IF NOT EXISTS planes_entrenamiento (id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, plantillaId TEXT NOT NULL, nombre TEXT NOT NULL, objetivo TEXT NOT NULL, frecuenciaSemanal INTEGER NOT NULL, personalizado INTEGER NOT NULL, notas TEXT NOT NULL)")
                db.execSQL("CREATE TABLE IF NOT EXISTS dias_entrenamiento (id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, planId INTEGER NOT NULL, diaSemana INTEGER NOT NULL, orden INTEGER NOT NULL, nombre TEXT NOT NULL, notas TEXT NOT NULL, FOREIGN KEY(planId) REFERENCES planes_entrenamiento(id) ON DELETE CASCADE)")
                db.execSQL("CREATE INDEX IF NOT EXISTS index_dias_entrenamiento_planId ON dias_entrenamiento(planId)")
                db.execSQL("CREATE TABLE IF NOT EXISTS ejercicios_plan (id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, diaId INTEGER NOT NULL, ejercicioId INTEGER NOT NULL, posicion INTEGER NOT NULL, fase TEXT NOT NULL, seriesObjetivo INTEGER NOT NULL, repeticionesMin INTEGER NOT NULL, repeticionesMax INTEGER NOT NULL, tiempoSegundos INTEGER, rirObjetivo INTEGER, descansoSegundos INTEGER NOT NULL, notas TEXT NOT NULL, FOREIGN KEY(diaId) REFERENCES dias_entrenamiento(id) ON DELETE CASCADE, FOREIGN KEY(ejercicioId) REFERENCES ejercicios(id) ON DELETE CASCADE)")
                db.execSQL("CREATE INDEX IF NOT EXISTS index_ejercicios_plan_diaId ON ejercicios_plan(diaId)")
                db.execSQL("CREATE INDEX IF NOT EXISTS index_ejercicios_plan_ejercicioId ON ejercicios_plan(ejercicioId)")
            }
        }

        val MIGRATION_4_5 = object : androidx.room.migration.Migration(4, 5) {
            override fun migrate(db: androidx.sqlite.db.SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE sesiones ADD COLUMN estado TEXT NOT NULL DEFAULT 'IN_PROGRESS'")
                db.execSQL("ALTER TABLE sesiones ADD COLUMN creadoEnEpochMillis INTEGER NOT NULL DEFAULT 0")
                db.execSQL("ALTER TABLE sesiones ADD COLUMN actualizadoEnEpochMillis INTEGER NOT NULL DEFAULT 0")
                db.execSQL("UPDATE sesiones SET creadoEnEpochMillis = fechaEpochMillis, actualizadoEnEpochMillis = fechaEpochMillis")
                db.execSQL("UPDATE sesiones SET estado = 'COMPLETED' WHERE duracionMinutos IS NOT NULL")
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
            Ejercicio(nombre="Aperturas en máquina", grupoMuscular=GrupoMuscular.PECHO, categoria=CategoriaEjercicio.EMPUJE, tipo=TipoEjercicio.AISLAMIENTO, descansoSegundos=60),
            Ejercicio(nombre="Caminata inclinada", grupoMuscular=GrupoMuscular.CARDIO, categoria=CategoriaEjercicio.OTRO, tipo=TipoEjercicio.CARDIO, descansoSegundos=60),
            Ejercicio(nombre="Bicicleta", grupoMuscular=GrupoMuscular.CARDIO, categoria=CategoriaEjercicio.OTRO, tipo=TipoEjercicio.CARDIO, descansoSegundos=60),
            Ejercicio(nombre="Remo ergómetro", grupoMuscular=GrupoMuscular.CARDIO, categoria=CategoriaEjercicio.OTRO, tipo=TipoEjercicio.CARDIO, descansoSegundos=60)
        )
    }
}
