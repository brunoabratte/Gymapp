package com.bruno.gymapp.data.local.dao;

import android.database.Cursor;
import androidx.annotation.NonNull;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.bruno.gymapp.data.local.entity.SerieRegistrada;
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
import kotlin.coroutines.Continuation;
import kotlinx.coroutines.flow.Flow;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class SerieDao_Impl implements SerieDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<SerieRegistrada> __insertionAdapterOfSerieRegistrada;

  public SerieDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfSerieRegistrada = new EntityInsertionAdapter<SerieRegistrada>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR ABORT INTO `series_registradas` (`id`,`sesionId`,`ejercicioId`,`pesoKg`,`repeticiones`,`orden`,`descansoSegundos`) VALUES (nullif(?, 0),?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final SerieRegistrada entity) {
        statement.bindLong(1, entity.getId());
        statement.bindLong(2, entity.getSesionId());
        statement.bindLong(3, entity.getEjercicioId());
        statement.bindDouble(4, entity.getPesoKg());
        statement.bindLong(5, entity.getRepeticiones());
        statement.bindLong(6, entity.getOrden());
        if (entity.getDescansoSegundos() == null) {
          statement.bindNull(7);
        } else {
          statement.bindLong(7, entity.getDescansoSegundos());
        }
      }
    };
  }

  @Override
  public Object insertar(final SerieRegistrada serie,
      final Continuation<? super Long> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Long>() {
      @Override
      @NonNull
      public Long call() throws Exception {
        __db.beginTransaction();
        try {
          final Long _result = __insertionAdapterOfSerieRegistrada.insertAndReturnId(serie);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<SerieRegistrada>> observarPorSesion(final long sesionId) {
    final String _sql = "SELECT * FROM series_registradas WHERE sesionId = ? ORDER BY orden ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, sesionId);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"series_registradas"}, new Callable<List<SerieRegistrada>>() {
      @Override
      @NonNull
      public List<SerieRegistrada> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfSesionId = CursorUtil.getColumnIndexOrThrow(_cursor, "sesionId");
          final int _cursorIndexOfEjercicioId = CursorUtil.getColumnIndexOrThrow(_cursor, "ejercicioId");
          final int _cursorIndexOfPesoKg = CursorUtil.getColumnIndexOrThrow(_cursor, "pesoKg");
          final int _cursorIndexOfRepeticiones = CursorUtil.getColumnIndexOrThrow(_cursor, "repeticiones");
          final int _cursorIndexOfOrden = CursorUtil.getColumnIndexOrThrow(_cursor, "orden");
          final int _cursorIndexOfDescansoSegundos = CursorUtil.getColumnIndexOrThrow(_cursor, "descansoSegundos");
          final List<SerieRegistrada> _result = new ArrayList<SerieRegistrada>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final SerieRegistrada _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpSesionId;
            _tmpSesionId = _cursor.getLong(_cursorIndexOfSesionId);
            final long _tmpEjercicioId;
            _tmpEjercicioId = _cursor.getLong(_cursorIndexOfEjercicioId);
            final double _tmpPesoKg;
            _tmpPesoKg = _cursor.getDouble(_cursorIndexOfPesoKg);
            final int _tmpRepeticiones;
            _tmpRepeticiones = _cursor.getInt(_cursorIndexOfRepeticiones);
            final int _tmpOrden;
            _tmpOrden = _cursor.getInt(_cursorIndexOfOrden);
            final Integer _tmpDescansoSegundos;
            if (_cursor.isNull(_cursorIndexOfDescansoSegundos)) {
              _tmpDescansoSegundos = null;
            } else {
              _tmpDescansoSegundos = _cursor.getInt(_cursorIndexOfDescansoSegundos);
            }
            _item = new SerieRegistrada(_tmpId,_tmpSesionId,_tmpEjercicioId,_tmpPesoKg,_tmpRepeticiones,_tmpOrden,_tmpDescansoSegundos);
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

  @Override
  public Flow<List<PuntoProgreso>> observarProgreso(final long ejercicioId) {
    final String _sql = "\n"
            + "        SELECT s.fechaEpochMillis AS fechaEpochMillis, sr.pesoKg AS pesoKg, sr.repeticiones AS repeticiones\n"
            + "        FROM series_registradas sr\n"
            + "        INNER JOIN sesiones s ON s.id = sr.sesionId\n"
            + "        WHERE sr.ejercicioId = ?\n"
            + "        ORDER BY s.fechaEpochMillis ASC\n"
            + "        ";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, ejercicioId);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"series_registradas",
        "sesiones"}, new Callable<List<PuntoProgreso>>() {
      @Override
      @NonNull
      public List<PuntoProgreso> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfFechaEpochMillis = 0;
          final int _cursorIndexOfPesoKg = 1;
          final int _cursorIndexOfRepeticiones = 2;
          final List<PuntoProgreso> _result = new ArrayList<PuntoProgreso>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final PuntoProgreso _item;
            final long _tmpFechaEpochMillis;
            _tmpFechaEpochMillis = _cursor.getLong(_cursorIndexOfFechaEpochMillis);
            final double _tmpPesoKg;
            _tmpPesoKg = _cursor.getDouble(_cursorIndexOfPesoKg);
            final int _tmpRepeticiones;
            _tmpRepeticiones = _cursor.getInt(_cursorIndexOfRepeticiones);
            _item = new PuntoProgreso(_tmpFechaEpochMillis,_tmpPesoKg,_tmpRepeticiones);
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
