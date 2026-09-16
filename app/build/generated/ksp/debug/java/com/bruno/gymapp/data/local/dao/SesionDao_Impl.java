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
import com.bruno.gymapp.data.local.entity.Sesion;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Integer;
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
public final class SesionDao_Impl implements SesionDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<Sesion> __insertionAdapterOfSesion;

  private final EntityDeletionOrUpdateAdapter<Sesion> __updateAdapterOfSesion;

  public SesionDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfSesion = new EntityInsertionAdapter<Sesion>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR ABORT INTO `sesiones` (`id`,`fechaEpochMillis`,`duracionMinutos`,`notas`) VALUES (nullif(?, 0),?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final Sesion entity) {
        statement.bindLong(1, entity.getId());
        statement.bindLong(2, entity.getFechaEpochMillis());
        if (entity.getDuracionMinutos() == null) {
          statement.bindNull(3);
        } else {
          statement.bindLong(3, entity.getDuracionMinutos());
        }
        if (entity.getNotas() == null) {
          statement.bindNull(4);
        } else {
          statement.bindString(4, entity.getNotas());
        }
      }
    };
    this.__updateAdapterOfSesion = new EntityDeletionOrUpdateAdapter<Sesion>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `sesiones` SET `id` = ?,`fechaEpochMillis` = ?,`duracionMinutos` = ?,`notas` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final Sesion entity) {
        statement.bindLong(1, entity.getId());
        statement.bindLong(2, entity.getFechaEpochMillis());
        if (entity.getDuracionMinutos() == null) {
          statement.bindNull(3);
        } else {
          statement.bindLong(3, entity.getDuracionMinutos());
        }
        if (entity.getNotas() == null) {
          statement.bindNull(4);
        } else {
          statement.bindString(4, entity.getNotas());
        }
        statement.bindLong(5, entity.getId());
      }
    };
  }

  @Override
  public Object insertar(final Sesion sesion, final Continuation<? super Long> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Long>() {
      @Override
      @NonNull
      public Long call() throws Exception {
        __db.beginTransaction();
        try {
          final Long _result = __insertionAdapterOfSesion.insertAndReturnId(sesion);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object actualizar(final Sesion sesion, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __updateAdapterOfSesion.handle(sesion);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<Sesion>> observarTodas() {
    final String _sql = "SELECT * FROM sesiones ORDER BY fechaEpochMillis DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"sesiones"}, new Callable<List<Sesion>>() {
      @Override
      @NonNull
      public List<Sesion> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfFechaEpochMillis = CursorUtil.getColumnIndexOrThrow(_cursor, "fechaEpochMillis");
          final int _cursorIndexOfDuracionMinutos = CursorUtil.getColumnIndexOrThrow(_cursor, "duracionMinutos");
          final int _cursorIndexOfNotas = CursorUtil.getColumnIndexOrThrow(_cursor, "notas");
          final List<Sesion> _result = new ArrayList<Sesion>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final Sesion _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpFechaEpochMillis;
            _tmpFechaEpochMillis = _cursor.getLong(_cursorIndexOfFechaEpochMillis);
            final Integer _tmpDuracionMinutos;
            if (_cursor.isNull(_cursorIndexOfDuracionMinutos)) {
              _tmpDuracionMinutos = null;
            } else {
              _tmpDuracionMinutos = _cursor.getInt(_cursorIndexOfDuracionMinutos);
            }
            final String _tmpNotas;
            if (_cursor.isNull(_cursorIndexOfNotas)) {
              _tmpNotas = null;
            } else {
              _tmpNotas = _cursor.getString(_cursorIndexOfNotas);
            }
            _item = new Sesion(_tmpId,_tmpFechaEpochMillis,_tmpDuracionMinutos,_tmpNotas);
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
