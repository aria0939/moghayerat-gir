package com.damdarangha.moghayerat.data.db.dao;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.damdarangha.moghayerat.data.db.entity.StatHistoryEntity;
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

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class StatHistoryDao_Impl implements StatHistoryDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<StatHistoryEntity> __insertionAdapterOfStatHistoryEntity;

  private final SharedSQLiteStatement __preparedStmtOfDeleteForStat;

  public StatHistoryDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfStatHistoryEntity = new EntityInsertionAdapter<StatHistoryEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR ABORT INTO `stat_history` (`id`,`statId`,`productId`,`isoDate`,`changeType`,`previousValue`,`newValue`,`changedAt`,`note`) VALUES (nullif(?, 0),?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final StatHistoryEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindLong(2, entity.getStatId());
        statement.bindLong(3, entity.getProductId());
        statement.bindString(4, entity.getIsoDate());
        statement.bindString(5, entity.getChangeType());
        if (entity.getPreviousValue() == null) {
          statement.bindNull(6);
        } else {
          statement.bindString(6, entity.getPreviousValue());
        }
        if (entity.getNewValue() == null) {
          statement.bindNull(7);
        } else {
          statement.bindString(7, entity.getNewValue());
        }
        statement.bindLong(8, entity.getChangedAt());
        statement.bindString(9, entity.getNote());
      }
    };
    this.__preparedStmtOfDeleteForStat = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM stat_history WHERE statId = ?";
        return _query;
      }
    };
  }

  @Override
  public Object insert(final StatHistoryEntity history,
      final Continuation<? super Long> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Long>() {
      @Override
      @NonNull
      public Long call() throws Exception {
        __db.beginTransaction();
        try {
          final Long _result = __insertionAdapterOfStatHistoryEntity.insertAndReturnId(history);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteForStat(final long statId, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteForStat.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, statId);
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfDeleteForStat.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object getHistoryForStat(final long statId,
      final Continuation<? super List<StatHistoryEntity>> $completion) {
    final String _sql = "SELECT * FROM stat_history WHERE statId = ? ORDER BY changedAt DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, statId);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<StatHistoryEntity>>() {
      @Override
      @NonNull
      public List<StatHistoryEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfStatId = CursorUtil.getColumnIndexOrThrow(_cursor, "statId");
          final int _cursorIndexOfProductId = CursorUtil.getColumnIndexOrThrow(_cursor, "productId");
          final int _cursorIndexOfIsoDate = CursorUtil.getColumnIndexOrThrow(_cursor, "isoDate");
          final int _cursorIndexOfChangeType = CursorUtil.getColumnIndexOrThrow(_cursor, "changeType");
          final int _cursorIndexOfPreviousValue = CursorUtil.getColumnIndexOrThrow(_cursor, "previousValue");
          final int _cursorIndexOfNewValue = CursorUtil.getColumnIndexOrThrow(_cursor, "newValue");
          final int _cursorIndexOfChangedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "changedAt");
          final int _cursorIndexOfNote = CursorUtil.getColumnIndexOrThrow(_cursor, "note");
          final List<StatHistoryEntity> _result = new ArrayList<StatHistoryEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final StatHistoryEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpStatId;
            _tmpStatId = _cursor.getLong(_cursorIndexOfStatId);
            final long _tmpProductId;
            _tmpProductId = _cursor.getLong(_cursorIndexOfProductId);
            final String _tmpIsoDate;
            _tmpIsoDate = _cursor.getString(_cursorIndexOfIsoDate);
            final String _tmpChangeType;
            _tmpChangeType = _cursor.getString(_cursorIndexOfChangeType);
            final String _tmpPreviousValue;
            if (_cursor.isNull(_cursorIndexOfPreviousValue)) {
              _tmpPreviousValue = null;
            } else {
              _tmpPreviousValue = _cursor.getString(_cursorIndexOfPreviousValue);
            }
            final String _tmpNewValue;
            if (_cursor.isNull(_cursorIndexOfNewValue)) {
              _tmpNewValue = null;
            } else {
              _tmpNewValue = _cursor.getString(_cursorIndexOfNewValue);
            }
            final long _tmpChangedAt;
            _tmpChangedAt = _cursor.getLong(_cursorIndexOfChangedAt);
            final String _tmpNote;
            _tmpNote = _cursor.getString(_cursorIndexOfNote);
            _item = new StatHistoryEntity(_tmpId,_tmpStatId,_tmpProductId,_tmpIsoDate,_tmpChangeType,_tmpPreviousValue,_tmpNewValue,_tmpChangedAt,_tmpNote);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Object getHistoryForProduct(final long productId,
      final Continuation<? super List<StatHistoryEntity>> $completion) {
    final String _sql = "SELECT * FROM stat_history WHERE productId = ? ORDER BY changedAt DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, productId);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<StatHistoryEntity>>() {
      @Override
      @NonNull
      public List<StatHistoryEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfStatId = CursorUtil.getColumnIndexOrThrow(_cursor, "statId");
          final int _cursorIndexOfProductId = CursorUtil.getColumnIndexOrThrow(_cursor, "productId");
          final int _cursorIndexOfIsoDate = CursorUtil.getColumnIndexOrThrow(_cursor, "isoDate");
          final int _cursorIndexOfChangeType = CursorUtil.getColumnIndexOrThrow(_cursor, "changeType");
          final int _cursorIndexOfPreviousValue = CursorUtil.getColumnIndexOrThrow(_cursor, "previousValue");
          final int _cursorIndexOfNewValue = CursorUtil.getColumnIndexOrThrow(_cursor, "newValue");
          final int _cursorIndexOfChangedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "changedAt");
          final int _cursorIndexOfNote = CursorUtil.getColumnIndexOrThrow(_cursor, "note");
          final List<StatHistoryEntity> _result = new ArrayList<StatHistoryEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final StatHistoryEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpStatId;
            _tmpStatId = _cursor.getLong(_cursorIndexOfStatId);
            final long _tmpProductId;
            _tmpProductId = _cursor.getLong(_cursorIndexOfProductId);
            final String _tmpIsoDate;
            _tmpIsoDate = _cursor.getString(_cursorIndexOfIsoDate);
            final String _tmpChangeType;
            _tmpChangeType = _cursor.getString(_cursorIndexOfChangeType);
            final String _tmpPreviousValue;
            if (_cursor.isNull(_cursorIndexOfPreviousValue)) {
              _tmpPreviousValue = null;
            } else {
              _tmpPreviousValue = _cursor.getString(_cursorIndexOfPreviousValue);
            }
            final String _tmpNewValue;
            if (_cursor.isNull(_cursorIndexOfNewValue)) {
              _tmpNewValue = null;
            } else {
              _tmpNewValue = _cursor.getString(_cursorIndexOfNewValue);
            }
            final long _tmpChangedAt;
            _tmpChangedAt = _cursor.getLong(_cursorIndexOfChangedAt);
            final String _tmpNote;
            _tmpNote = _cursor.getString(_cursorIndexOfNote);
            _item = new StatHistoryEntity(_tmpId,_tmpStatId,_tmpProductId,_tmpIsoDate,_tmpChangeType,_tmpPreviousValue,_tmpNewValue,_tmpChangedAt,_tmpNote);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
