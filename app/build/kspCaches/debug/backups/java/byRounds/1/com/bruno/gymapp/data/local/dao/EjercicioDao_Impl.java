package com.bruno.gymapp.data.local.dao;

import android.database.Cursor;
import androidx.annotation.NonNull;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityDeletionOrUpdateAdapter;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.bruno.gymapp.data.local.Converters;
import com.bruno.gymapp.data.local.entity.Ejercicio;
import com.bruno.gymapp.data.local.entity.GrupoMuscular;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Long;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import javax.annotation.processing.Generated;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlinx.coroutines.flow.Flow;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class EjercicioDao_Impl implements EjercicioDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<Ejercicio> __insertionAdapterOfEjercicio;

  private final Converters __converters = new Converters();

  private final EntityDeletionOrUpdateAdapter<Ejercicio> __deletionAdapterOfEjercicio;

  public EjercicioDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfEjercicio = new EntityInsertionAdapter<Ejercicio>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR IGNORE INTO `ejercicios` (`id`,`nombre`,`grupoMuscular`,`esPersonalizado`) VALUES (nullif(?, 0),?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final Ejercicio entity) {
        statement.bindLong(1, entity.getId());
        statement.bindString(2, entity.getNombre());
        final String _tmp = __converters.fromGrupoMuscular(entity.getGrupoMuscular());
        statement.bindString(3, _tmp);
        final int _tmp_1 = entity.getEsPersonalizado() ? 1 : 0;
        statement.bindLong(4, _tmp_1);
      }
    };
    this.__deletionAdapterOfEjercicio = new EntityDeletionOrUpdateAdapter<Ejercicio>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `ejercicios` WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final Ejercicio entity) {
        statement.bindLong(1, entity.getId());
      }
    };
  }

  @Override
  public Object insertar(final Ejercicio ejercicio, final Continuation<? super Long> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Long>() {
      @Override
      @NonNull
      public Long call() throws Exception {
        __db.beginTransaction();
        try {
          final Long _result = __insertionAdapterOfEjercicio.insertAndReturnId(ejercicio);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object insertarVarios(final List<Ejercicio> ejercicios,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfEjercicio.insert(ejercicios);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object eliminar(final Ejercicio ejercicio, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __deletionAdapterOfEjercicio.handle(ejercicio);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<Ejercicio>> observarTodos() {
    final String _sql = "SELECT * FROM ejercicios ORDER BY nombre ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"ejercicios"}, new Callable<List<Ejercicio>>() {
      @Override
      @NonNull
      public List<Ejercicio> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfNombre = CursorUtil.getColumnIndexOrThrow(_cursor, "nombre");
          final int _cursorIndexOfGrupoMuscular = CursorUtil.getColumnIndexOrThrow(_cursor, "grupoMuscular");
          final int _cursorIndexOfEsPersonalizado = CursorUtil.getColumnIndexOrThrow(_cursor, "esPersonalizado");
          final List<Ejercicio> _result = new ArrayList<Ejercicio>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final Ejercicio _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpNombre;
            _tmpNombre = _cursor.getString(_cursorIndexOfNombre);
            final GrupoMuscular _tmpGrupoMuscular;
            final String _tmp;
            _tmp = _cursor.getString(_cursorIndexOfGrupoMuscular);
            _tmpGrupoMuscular = __converters.toGrupoMuscular(_tmp);
            final boolean _tmpEsPersonalizado;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfEsPersonalizado);
            _tmpEsPersonalizado = _tmp_1 != 0;
            _item = new Ejercicio(_tmpId,_tmpNombre,_tmpGrupoMuscular,_tmpEsPersonalizado);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
