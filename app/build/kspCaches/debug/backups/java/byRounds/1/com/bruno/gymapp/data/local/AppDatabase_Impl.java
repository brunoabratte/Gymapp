package com.bruno.gymapp.data.local;

import androidx.annotation.NonNull;
import androidx.room.DatabaseConfiguration;
import androidx.room.InvalidationTracker;
import androidx.room.RoomDatabase;
import androidx.room.RoomOpenHelper;
import androidx.room.migration.AutoMigrationSpec;
import androidx.room.migration.Migration;
import androidx.room.util.DBUtil;
import androidx.room.util.TableInfo;
import androidx.sqlite.db.SupportSQLiteDatabase;
import androidx.sqlite.db.SupportSQLiteOpenHelper;
import com.bruno.gymapp.data.local.dao.EjercicioDao;
import com.bruno.gymapp.data.local.dao.EjercicioDao_Impl;
import com.bruno.gymapp.data.local.dao.SerieDao;
import com.bruno.gymapp.data.local.dao.SerieDao_Impl;
import com.bruno.gymapp.data.local.dao.SesionDao;
import com.bruno.gymapp.data.local.dao.SesionDao_Impl;
import java.lang.Class;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.processing.Generated;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class AppDatabase_Impl extends AppDatabase {
  private volatile EjercicioDao _ejercicioDao;

  private volatile SesionDao _sesionDao;

  private volatile SerieDao _serieDao;

  @Override
  @NonNull
  protected SupportSQLiteOpenHelper createOpenHelper(@NonNull final DatabaseConfiguration config) {
    final SupportSQLiteOpenHelper.Callback _openCallback = new RoomOpenHelper(config, new RoomOpenHelper.Delegate(1) {
      @Override
      public void createAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS `ejercicios` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `nombre` TEXT NOT NULL, `grupoMuscular` TEXT NOT NULL, `esPersonalizado` INTEGER NOT NULL)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `sesiones` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `fechaEpochMillis` INTEGER NOT NULL, `duracionMinutos` INTEGER, `notas` TEXT)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `series_registradas` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `sesionId` INTEGER NOT NULL, `ejercicioId` INTEGER NOT NULL, `pesoKg` REAL NOT NULL, `repeticiones` INTEGER NOT NULL, `orden` INTEGER NOT NULL, `descansoSegundos` INTEGER, FOREIGN KEY(`sesionId`) REFERENCES `sesiones`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE , FOREIGN KEY(`ejercicioId`) REFERENCES `ejercicios`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE )");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_series_registradas_sesionId` ON `series_registradas` (`sesionId`)");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_series_registradas_ejercicioId` ON `series_registradas` (`ejercicioId`)");
        db.execSQL("CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY,identity_hash TEXT)");
        db.execSQL("INSERT OR REPLACE INTO room_master_table (id,identity_hash) VALUES(42, '10f052981be5d81849bb167612b483d6')");
      }

      @Override
      public void dropAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS `ejercicios`");
        db.execSQL("DROP TABLE IF EXISTS `sesiones`");
        db.execSQL("DROP TABLE IF EXISTS `series_registradas`");
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onDestructiveMigration(db);
          }
        }
      }

      @Override
      public void onCreate(@NonNull final SupportSQLiteDatabase db) {
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onCreate(db);
          }
        }
      }

      @Override
      public void onOpen(@NonNull final SupportSQLiteDatabase db) {
        mDatabase = db;
        db.execSQL("PRAGMA foreign_keys = ON");
        internalInitInvalidationTracker(db);
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onOpen(db);
          }
        }
      }

      @Override
      public void onPreMigrate(@NonNull final SupportSQLiteDatabase db) {
        DBUtil.dropFtsSyncTriggers(db);
      }

      @Override
      public void onPostMigrate(@NonNull final SupportSQLiteDatabase db) {
      }

      @Override
      @NonNull
      public RoomOpenHelper.ValidationResult onValidateSchema(
          @NonNull final SupportSQLiteDatabase db) {
        final HashMap<String, TableInfo.Column> _columnsEjercicios = new HashMap<String, TableInfo.Column>(4);
        _columnsEjercicios.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsEjercicios.put("nombre", new TableInfo.Column("nombre", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsEjercicios.put("grupoMuscular", new TableInfo.Column("grupoMuscular", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsEjercicios.put("esPersonalizado", new TableInfo.Column("esPersonalizado", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysEjercicios = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesEjercicios = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoEjercicios = new TableInfo("ejercicios", _columnsEjercicios, _foreignKeysEjercicios, _indicesEjercicios);
        final TableInfo _existingEjercicios = TableInfo.read(db, "ejercicios");
        if (!_infoEjercicios.equals(_existingEjercicios)) {
          return new RoomOpenHelper.ValidationResult(false, "ejercicios(com.bruno.gymapp.data.local.entity.Ejercicio).\n"
                  + " Expected:\n" + _infoEjercicios + "\n"
                  + " Found:\n" + _existingEjercicios);
        }
        final HashMap<String, TableInfo.Column> _columnsSesiones = new HashMap<String, TableInfo.Column>(4);
        _columnsSesiones.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSesiones.put("fechaEpochMillis", new TableInfo.Column("fechaEpochMillis", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSesiones.put("duracionMinutos", new TableInfo.Column("duracionMinutos", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSesiones.put("notas", new TableInfo.Column("notas", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysSesiones = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesSesiones = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoSesiones = new TableInfo("sesiones", _columnsSesiones, _foreignKeysSesiones, _indicesSesiones);
        final TableInfo _existingSesiones = TableInfo.read(db, "sesiones");
        if (!_infoSesiones.equals(_existingSesiones)) {
          return new RoomOpenHelper.ValidationResult(false, "sesiones(com.bruno.gymapp.data.local.entity.Sesion).\n"
                  + " Expected:\n" + _infoSesiones + "\n"
                  + " Found:\n" + _existingSesiones);
        }
        final HashMap<String, TableInfo.Column> _columnsSeriesRegistradas = new HashMap<String, TableInfo.Column>(7);
        _columnsSeriesRegistradas.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSeriesRegistradas.put("sesionId", new TableInfo.Column("sesionId", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSeriesRegistradas.put("ejercicioId", new TableInfo.Column("ejercicioId", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSeriesRegistradas.put("pesoKg", new TableInfo.Column("pesoKg", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSeriesRegistradas.put("repeticiones", new TableInfo.Column("repeticiones", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSeriesRegistradas.put("orden", new TableInfo.Column("orden", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSeriesRegistradas.put("descansoSegundos", new TableInfo.Column("descansoSegundos", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysSeriesRegistradas = new HashSet<TableInfo.ForeignKey>(2);
        _foreignKeysSeriesRegistradas.add(new TableInfo.ForeignKey("sesiones", "CASCADE", "NO ACTION", Arrays.asList("sesionId"), Arrays.asList("id")));
        _foreignKeysSeriesRegistradas.add(new TableInfo.ForeignKey("ejercicios", "CASCADE", "NO ACTION", Arrays.asList("ejercicioId"), Arrays.asList("id")));
        final HashSet<TableInfo.Index> _indicesSeriesRegistradas = new HashSet<TableInfo.Index>(2);
        _indicesSeriesRegistradas.add(new TableInfo.Index("index_series_registradas_sesionId", false, Arrays.asList("sesionId"), Arrays.asList("ASC")));
        _indicesSeriesRegistradas.add(new TableInfo.Index("index_series_registradas_ejercicioId", false, Arrays.asList("ejercicioId"), Arrays.asList("ASC")));
        final TableInfo _infoSeriesRegistradas = new TableInfo("series_registradas", _columnsSeriesRegistradas, _foreignKeysSeriesRegistradas, _indicesSeriesRegistradas);
        final TableInfo _existingSeriesRegistradas = TableInfo.read(db, "series_registradas");
        if (!_infoSeriesRegistradas.equals(_existingSeriesRegistradas)) {
          return new RoomOpenHelper.ValidationResult(false, "series_registradas(com.bruno.gymapp.data.local.entity.SerieRegistrada).\n"
                  + " Expected:\n" + _infoSeriesRegistradas + "\n"
                  + " Found:\n" + _existingSeriesRegistradas);
        }
        return new RoomOpenHelper.ValidationResult(true, null);
      }
    }, "10f052981be5d81849bb167612b483d6", "2461728be7867d3b50f089ca97375068");
    final SupportSQLiteOpenHelper.Configuration _sqliteConfig = SupportSQLiteOpenHelper.Configuration.builder(config.context).name(config.name).callback(_openCallback).build();
    final SupportSQLiteOpenHelper _helper = config.sqliteOpenHelperFactory.create(_sqliteConfig);
    return _helper;
  }

  @Override
  @NonNull
  protected InvalidationTracker createInvalidationTracker() {
    final HashMap<String, String> _shadowTablesMap = new HashMap<String, String>(0);
    final HashMap<String, Set<String>> _viewTables = new HashMap<String, Set<String>>(0);
    return new InvalidationTracker(this, _shadowTablesMap, _viewTables, "ejercicios","sesiones","series_registradas");
  }

  @Override
  public void clearAllTables() {
    super.assertNotMainThread();
    final SupportSQLiteDatabase _db = super.getOpenHelper().getWritableDatabase();
    final boolean _supportsDeferForeignKeys = android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP;
    try {
      if (!_supportsDeferForeignKeys) {
        _db.execSQL("PRAGMA foreign_keys = FALSE");
      }
      super.beginTransaction();
      if (_supportsDeferForeignKeys) {
        _db.execSQL("PRAGMA defer_foreign_keys = TRUE");
      }
      _db.execSQL("DELETE FROM `ejercicios`");
      _db.execSQL("DELETE FROM `sesiones`");
      _db.execSQL("DELETE FROM `series_registradas`");
      super.setTransactionSuccessful();
    } finally {
      super.endTransaction();
      if (!_supportsDeferForeignKeys) {
        _db.execSQL("PRAGMA foreign_keys = TRUE");
      }
      _db.query("PRAGMA wal_checkpoint(FULL)").close();
      if (!_db.inTransaction()) {
        _db.execSQL("VACUUM");
      }
    }
  }

  @Override
  @NonNull
  protected Map<Class<?>, List<Class<?>>> getRequiredTypeConverters() {
    final HashMap<Class<?>, List<Class<?>>> _typeConvertersMap = new HashMap<Class<?>, List<Class<?>>>();
    _typeConvertersMap.put(EjercicioDao.class, EjercicioDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(SesionDao.class, SesionDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(SerieDao.class, SerieDao_Impl.getRequiredConverters());
    return _typeConvertersMap;
  }

  @Override
  @NonNull
  public Set<Class<? extends AutoMigrationSpec>> getRequiredAutoMigrationSpecs() {
    final HashSet<Class<? extends AutoMigrationSpec>> _autoMigrationSpecsSet = new HashSet<Class<? extends AutoMigrationSpec>>();
    return _autoMigrationSpecsSet;
  }

  @Override
  @NonNull
  public List<Migration> getAutoMigrations(
      @NonNull final Map<Class<? extends AutoMigrationSpec>, AutoMigrationSpec> autoMigrationSpecs) {
    final List<Migration> _autoMigrations = new ArrayList<Migration>();
    return _autoMigrations;
  }

  @Override
  public EjercicioDao ejercicioDao() {
    if (_ejercicioDao != null) {
      return _ejercicioDao;
    } else {
      synchronized(this) {
        if(_ejercicioDao == null) {
          _ejercicioDao = new EjercicioDao_Impl(this);
        }
        return _ejercicioDao;
      }
    }
  }

  @Override
  public SesionDao sesionDao() {
    if (_sesionDao != null) {
      return _sesionDao;
    } else {
      synchronized(this) {
        if(_sesionDao == null) {
          _sesionDao = new SesionDao_Impl(this);
        }
        return _sesionDao;
      }
    }
  }

  @Override
  public SerieDao serieDao() {
    if (_serieDao != null) {
      return _serieDao;
    } else {
      synchronized(this) {
        if(_serieDao == null) {
          _serieDao = new SerieDao_Impl(this);
        }
        return _serieDao;
      }
    }
  }
}
