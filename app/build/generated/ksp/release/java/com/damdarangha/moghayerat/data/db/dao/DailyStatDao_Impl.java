package com.damdarangha.moghayerat.data.db.dao;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityDeletionOrUpdateAdapter;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.damdarangha.moghayerat.data.db.entity.DailyStatEntity;
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
public final class DailyStatDao_Impl implements DailyStatDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<DailyStatEntity> __insertionAdapterOfDailyStatEntity;

  private final EntityDeletionOrUpdateAdapter<DailyStatEntity> __updateAdapterOfDailyStatEntity;

  private final SharedSQLiteStatement __preparedStmtOfFinalize;

  private final SharedSQLiteStatement __preparedStmtOfUnfinalize;

  private final SharedSQLiteStatement __preparedStmtOfDelete;

  public DailyStatDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfDailyStatEntity = new EntityInsertionAdapter<DailyStatEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `daily_stats` (`id`,`productId`,`isoDate`,`jalaliDate`,`openingStockTotal`,`morningLoadingTotal`,`actualMorningStockTotal`,`receiptTotal`,`returnsTotal`,`giftsTotal`,`wasteTotal`,`laboratoryTotal`,`midDayLoadingTotal`,`dayReceiptTotal`,`dayReturnsTotal`,`dayGiftsTotal`,`dayWasteTotal`,`dayLaboratoryTotal`,`dayMidDayLoadingTotal`,`nightLoadingTotal`,`currentStockTotal`,`expectedMorningStockTotal`,`morningVariance`,`expectedClosingStockTotal`,`closingVariance`,`startVariance`,`endCumulativeVariance`,`nightShiftVariance`,`isFinalized`,`notes`,`createdAt`,`updatedAt`) VALUES (nullif(?, 0),?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final DailyStatEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindLong(2, entity.getProductId());
        statement.bindString(3, entity.getIsoDate());
        statement.bindString(4, entity.getJalaliDate());
        statement.bindLong(5, entity.getOpeningStockTotal());
        statement.bindLong(6, entity.getMorningLoadingTotal());
        if (entity.getActualMorningStockTotal() == null) {
          statement.bindNull(7);
        } else {
          statement.bindLong(7, entity.getActualMorningStockTotal());
        }
        statement.bindLong(8, entity.getReceiptTotal());
        statement.bindLong(9, entity.getReturnsTotal());
        statement.bindLong(10, entity.getGiftsTotal());
        statement.bindLong(11, entity.getWasteTotal());
        statement.bindLong(12, entity.getLaboratoryTotal());
        statement.bindLong(13, entity.getMidDayLoadingTotal());
        statement.bindLong(14, entity.getDayReceiptTotal());
        statement.bindLong(15, entity.getDayReturnsTotal());
        statement.bindLong(16, entity.getDayGiftsTotal());
        statement.bindLong(17, entity.getDayWasteTotal());
        statement.bindLong(18, entity.getDayLaboratoryTotal());
        statement.bindLong(19, entity.getDayMidDayLoadingTotal());
        statement.bindLong(20, entity.getNightLoadingTotal());
        if (entity.getCurrentStockTotal() == null) {
          statement.bindNull(21);
        } else {
          statement.bindLong(21, entity.getCurrentStockTotal());
        }
        statement.bindLong(22, entity.getExpectedMorningStockTotal());
        if (entity.getMorningVariance() == null) {
          statement.bindNull(23);
        } else {
          statement.bindLong(23, entity.getMorningVariance());
        }
        statement.bindLong(24, entity.getExpectedClosingStockTotal());
        statement.bindLong(25, entity.getClosingVariance());
        if (entity.getStartVariance() == null) {
          statement.bindNull(26);
        } else {
          statement.bindLong(26, entity.getStartVariance());
        }
        if (entity.getEndCumulativeVariance() == null) {
          statement.bindNull(27);
        } else {
          statement.bindLong(27, entity.getEndCumulativeVariance());
        }
        if (entity.getNightShiftVariance() == null) {
          statement.bindNull(28);
        } else {
          statement.bindLong(28, entity.getNightShiftVariance());
        }
        final int _tmp = entity.isFinalized() ? 1 : 0;
        statement.bindLong(29, _tmp);
        statement.bindString(30, entity.getNotes());
        statement.bindLong(31, entity.getCreatedAt());
        statement.bindLong(32, entity.getUpdatedAt());
      }
    };
    this.__updateAdapterOfDailyStatEntity = new EntityDeletionOrUpdateAdapter<DailyStatEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `daily_stats` SET `id` = ?,`productId` = ?,`isoDate` = ?,`jalaliDate` = ?,`openingStockTotal` = ?,`morningLoadingTotal` = ?,`actualMorningStockTotal` = ?,`receiptTotal` = ?,`returnsTotal` = ?,`giftsTotal` = ?,`wasteTotal` = ?,`laboratoryTotal` = ?,`midDayLoadingTotal` = ?,`dayReceiptTotal` = ?,`dayReturnsTotal` = ?,`dayGiftsTotal` = ?,`dayWasteTotal` = ?,`dayLaboratoryTotal` = ?,`dayMidDayLoadingTotal` = ?,`nightLoadingTotal` = ?,`currentStockTotal` = ?,`expectedMorningStockTotal` = ?,`morningVariance` = ?,`expectedClosingStockTotal` = ?,`closingVariance` = ?,`startVariance` = ?,`endCumulativeVariance` = ?,`nightShiftVariance` = ?,`isFinalized` = ?,`notes` = ?,`createdAt` = ?,`updatedAt` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final DailyStatEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindLong(2, entity.getProductId());
        statement.bindString(3, entity.getIsoDate());
        statement.bindString(4, entity.getJalaliDate());
        statement.bindLong(5, entity.getOpeningStockTotal());
        statement.bindLong(6, entity.getMorningLoadingTotal());
        if (entity.getActualMorningStockTotal() == null) {
          statement.bindNull(7);
        } else {
          statement.bindLong(7, entity.getActualMorningStockTotal());
        }
        statement.bindLong(8, entity.getReceiptTotal());
        statement.bindLong(9, entity.getReturnsTotal());
        statement.bindLong(10, entity.getGiftsTotal());
        statement.bindLong(11, entity.getWasteTotal());
        statement.bindLong(12, entity.getLaboratoryTotal());
        statement.bindLong(13, entity.getMidDayLoadingTotal());
        statement.bindLong(14, entity.getDayReceiptTotal());
        statement.bindLong(15, entity.getDayReturnsTotal());
        statement.bindLong(16, entity.getDayGiftsTotal());
        statement.bindLong(17, entity.getDayWasteTotal());
        statement.bindLong(18, entity.getDayLaboratoryTotal());
        statement.bindLong(19, entity.getDayMidDayLoadingTotal());
        statement.bindLong(20, entity.getNightLoadingTotal());
        if (entity.getCurrentStockTotal() == null) {
          statement.bindNull(21);
        } else {
          statement.bindLong(21, entity.getCurrentStockTotal());
        }
        statement.bindLong(22, entity.getExpectedMorningStockTotal());
        if (entity.getMorningVariance() == null) {
          statement.bindNull(23);
        } else {
          statement.bindLong(23, entity.getMorningVariance());
        }
        statement.bindLong(24, entity.getExpectedClosingStockTotal());
        statement.bindLong(25, entity.getClosingVariance());
        if (entity.getStartVariance() == null) {
          statement.bindNull(26);
        } else {
          statement.bindLong(26, entity.getStartVariance());
        }
        if (entity.getEndCumulativeVariance() == null) {
          statement.bindNull(27);
        } else {
          statement.bindLong(27, entity.getEndCumulativeVariance());
        }
        if (entity.getNightShiftVariance() == null) {
          statement.bindNull(28);
        } else {
          statement.bindLong(28, entity.getNightShiftVariance());
        }
        final int _tmp = entity.isFinalized() ? 1 : 0;
        statement.bindLong(29, _tmp);
        statement.bindString(30, entity.getNotes());
        statement.bindLong(31, entity.getCreatedAt());
        statement.bindLong(32, entity.getUpdatedAt());
        statement.bindLong(33, entity.getId());
      }
    };
    this.__preparedStmtOfFinalize = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE daily_stats SET isFinalized = 1, updatedAt = ? WHERE id = ?";
        return _query;
      }
    };
    this.__preparedStmtOfUnfinalize = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE daily_stats SET isFinalized = 0, updatedAt = ? WHERE id = ?";
        return _query;
      }
    };
    this.__preparedStmtOfDelete = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM daily_stats WHERE id = ?";
        return _query;
      }
    };
  }

  @Override
  public Object insert(final DailyStatEntity stat, final Continuation<? super Long> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Long>() {
      @Override
      @NonNull
      public Long call() throws Exception {
        __db.beginTransaction();
        try {
          final Long _result = __insertionAdapterOfDailyStatEntity.insertAndReturnId(stat);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object update(final DailyStatEntity stat, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __updateAdapterOfDailyStatEntity.handle(stat);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object finalize(final long id, final long timestamp,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfFinalize.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, timestamp);
        _argIndex = 2;
        _stmt.bindLong(_argIndex, id);
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
          __preparedStmtOfFinalize.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object unfinalize(final long id, final long timestamp,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfUnfinalize.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, timestamp);
        _argIndex = 2;
        _stmt.bindLong(_argIndex, id);
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
          __preparedStmtOfUnfinalize.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object delete(final long id, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDelete.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, id);
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
          __preparedStmtOfDelete.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object getStat(final long productId, final String isoDate,
      final Continuation<? super DailyStatEntity> $completion) {
    final String _sql = "SELECT * FROM daily_stats WHERE productId = ? AND isoDate = ? LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, productId);
    _argIndex = 2;
    _statement.bindString(_argIndex, isoDate);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<DailyStatEntity>() {
      @Override
      @Nullable
      public DailyStatEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfProductId = CursorUtil.getColumnIndexOrThrow(_cursor, "productId");
          final int _cursorIndexOfIsoDate = CursorUtil.getColumnIndexOrThrow(_cursor, "isoDate");
          final int _cursorIndexOfJalaliDate = CursorUtil.getColumnIndexOrThrow(_cursor, "jalaliDate");
          final int _cursorIndexOfOpeningStockTotal = CursorUtil.getColumnIndexOrThrow(_cursor, "openingStockTotal");
          final int _cursorIndexOfMorningLoadingTotal = CursorUtil.getColumnIndexOrThrow(_cursor, "morningLoadingTotal");
          final int _cursorIndexOfActualMorningStockTotal = CursorUtil.getColumnIndexOrThrow(_cursor, "actualMorningStockTotal");
          final int _cursorIndexOfReceiptTotal = CursorUtil.getColumnIndexOrThrow(_cursor, "receiptTotal");
          final int _cursorIndexOfReturnsTotal = CursorUtil.getColumnIndexOrThrow(_cursor, "returnsTotal");
          final int _cursorIndexOfGiftsTotal = CursorUtil.getColumnIndexOrThrow(_cursor, "giftsTotal");
          final int _cursorIndexOfWasteTotal = CursorUtil.getColumnIndexOrThrow(_cursor, "wasteTotal");
          final int _cursorIndexOfLaboratoryTotal = CursorUtil.getColumnIndexOrThrow(_cursor, "laboratoryTotal");
          final int _cursorIndexOfMidDayLoadingTotal = CursorUtil.getColumnIndexOrThrow(_cursor, "midDayLoadingTotal");
          final int _cursorIndexOfDayReceiptTotal = CursorUtil.getColumnIndexOrThrow(_cursor, "dayReceiptTotal");
          final int _cursorIndexOfDayReturnsTotal = CursorUtil.getColumnIndexOrThrow(_cursor, "dayReturnsTotal");
          final int _cursorIndexOfDayGiftsTotal = CursorUtil.getColumnIndexOrThrow(_cursor, "dayGiftsTotal");
          final int _cursorIndexOfDayWasteTotal = CursorUtil.getColumnIndexOrThrow(_cursor, "dayWasteTotal");
          final int _cursorIndexOfDayLaboratoryTotal = CursorUtil.getColumnIndexOrThrow(_cursor, "dayLaboratoryTotal");
          final int _cursorIndexOfDayMidDayLoadingTotal = CursorUtil.getColumnIndexOrThrow(_cursor, "dayMidDayLoadingTotal");
          final int _cursorIndexOfNightLoadingTotal = CursorUtil.getColumnIndexOrThrow(_cursor, "nightLoadingTotal");
          final int _cursorIndexOfCurrentStockTotal = CursorUtil.getColumnIndexOrThrow(_cursor, "currentStockTotal");
          final int _cursorIndexOfExpectedMorningStockTotal = CursorUtil.getColumnIndexOrThrow(_cursor, "expectedMorningStockTotal");
          final int _cursorIndexOfMorningVariance = CursorUtil.getColumnIndexOrThrow(_cursor, "morningVariance");
          final int _cursorIndexOfExpectedClosingStockTotal = CursorUtil.getColumnIndexOrThrow(_cursor, "expectedClosingStockTotal");
          final int _cursorIndexOfClosingVariance = CursorUtil.getColumnIndexOrThrow(_cursor, "closingVariance");
          final int _cursorIndexOfStartVariance = CursorUtil.getColumnIndexOrThrow(_cursor, "startVariance");
          final int _cursorIndexOfEndCumulativeVariance = CursorUtil.getColumnIndexOrThrow(_cursor, "endCumulativeVariance");
          final int _cursorIndexOfNightShiftVariance = CursorUtil.getColumnIndexOrThrow(_cursor, "nightShiftVariance");
          final int _cursorIndexOfIsFinalized = CursorUtil.getColumnIndexOrThrow(_cursor, "isFinalized");
          final int _cursorIndexOfNotes = CursorUtil.getColumnIndexOrThrow(_cursor, "notes");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updatedAt");
          final DailyStatEntity _result;
          if (_cursor.moveToFirst()) {
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpProductId;
            _tmpProductId = _cursor.getLong(_cursorIndexOfProductId);
            final String _tmpIsoDate;
            _tmpIsoDate = _cursor.getString(_cursorIndexOfIsoDate);
            final String _tmpJalaliDate;
            _tmpJalaliDate = _cursor.getString(_cursorIndexOfJalaliDate);
            final int _tmpOpeningStockTotal;
            _tmpOpeningStockTotal = _cursor.getInt(_cursorIndexOfOpeningStockTotal);
            final int _tmpMorningLoadingTotal;
            _tmpMorningLoadingTotal = _cursor.getInt(_cursorIndexOfMorningLoadingTotal);
            final Integer _tmpActualMorningStockTotal;
            if (_cursor.isNull(_cursorIndexOfActualMorningStockTotal)) {
              _tmpActualMorningStockTotal = null;
            } else {
              _tmpActualMorningStockTotal = _cursor.getInt(_cursorIndexOfActualMorningStockTotal);
            }
            final int _tmpReceiptTotal;
            _tmpReceiptTotal = _cursor.getInt(_cursorIndexOfReceiptTotal);
            final int _tmpReturnsTotal;
            _tmpReturnsTotal = _cursor.getInt(_cursorIndexOfReturnsTotal);
            final int _tmpGiftsTotal;
            _tmpGiftsTotal = _cursor.getInt(_cursorIndexOfGiftsTotal);
            final int _tmpWasteTotal;
            _tmpWasteTotal = _cursor.getInt(_cursorIndexOfWasteTotal);
            final int _tmpLaboratoryTotal;
            _tmpLaboratoryTotal = _cursor.getInt(_cursorIndexOfLaboratoryTotal);
            final int _tmpMidDayLoadingTotal;
            _tmpMidDayLoadingTotal = _cursor.getInt(_cursorIndexOfMidDayLoadingTotal);
            final int _tmpDayReceiptTotal;
            _tmpDayReceiptTotal = _cursor.getInt(_cursorIndexOfDayReceiptTotal);
            final int _tmpDayReturnsTotal;
            _tmpDayReturnsTotal = _cursor.getInt(_cursorIndexOfDayReturnsTotal);
            final int _tmpDayGiftsTotal;
            _tmpDayGiftsTotal = _cursor.getInt(_cursorIndexOfDayGiftsTotal);
            final int _tmpDayWasteTotal;
            _tmpDayWasteTotal = _cursor.getInt(_cursorIndexOfDayWasteTotal);
            final int _tmpDayLaboratoryTotal;
            _tmpDayLaboratoryTotal = _cursor.getInt(_cursorIndexOfDayLaboratoryTotal);
            final int _tmpDayMidDayLoadingTotal;
            _tmpDayMidDayLoadingTotal = _cursor.getInt(_cursorIndexOfDayMidDayLoadingTotal);
            final int _tmpNightLoadingTotal;
            _tmpNightLoadingTotal = _cursor.getInt(_cursorIndexOfNightLoadingTotal);
            final Integer _tmpCurrentStockTotal;
            if (_cursor.isNull(_cursorIndexOfCurrentStockTotal)) {
              _tmpCurrentStockTotal = null;
            } else {
              _tmpCurrentStockTotal = _cursor.getInt(_cursorIndexOfCurrentStockTotal);
            }
            final int _tmpExpectedMorningStockTotal;
            _tmpExpectedMorningStockTotal = _cursor.getInt(_cursorIndexOfExpectedMorningStockTotal);
            final Integer _tmpMorningVariance;
            if (_cursor.isNull(_cursorIndexOfMorningVariance)) {
              _tmpMorningVariance = null;
            } else {
              _tmpMorningVariance = _cursor.getInt(_cursorIndexOfMorningVariance);
            }
            final int _tmpExpectedClosingStockTotal;
            _tmpExpectedClosingStockTotal = _cursor.getInt(_cursorIndexOfExpectedClosingStockTotal);
            final int _tmpClosingVariance;
            _tmpClosingVariance = _cursor.getInt(_cursorIndexOfClosingVariance);
            final Integer _tmpStartVariance;
            if (_cursor.isNull(_cursorIndexOfStartVariance)) {
              _tmpStartVariance = null;
            } else {
              _tmpStartVariance = _cursor.getInt(_cursorIndexOfStartVariance);
            }
            final Integer _tmpEndCumulativeVariance;
            if (_cursor.isNull(_cursorIndexOfEndCumulativeVariance)) {
              _tmpEndCumulativeVariance = null;
            } else {
              _tmpEndCumulativeVariance = _cursor.getInt(_cursorIndexOfEndCumulativeVariance);
            }
            final Integer _tmpNightShiftVariance;
            if (_cursor.isNull(_cursorIndexOfNightShiftVariance)) {
              _tmpNightShiftVariance = null;
            } else {
              _tmpNightShiftVariance = _cursor.getInt(_cursorIndexOfNightShiftVariance);
            }
            final boolean _tmpIsFinalized;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsFinalized);
            _tmpIsFinalized = _tmp != 0;
            final String _tmpNotes;
            _tmpNotes = _cursor.getString(_cursorIndexOfNotes);
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final long _tmpUpdatedAt;
            _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
            _result = new DailyStatEntity(_tmpId,_tmpProductId,_tmpIsoDate,_tmpJalaliDate,_tmpOpeningStockTotal,_tmpMorningLoadingTotal,_tmpActualMorningStockTotal,_tmpReceiptTotal,_tmpReturnsTotal,_tmpGiftsTotal,_tmpWasteTotal,_tmpLaboratoryTotal,_tmpMidDayLoadingTotal,_tmpDayReceiptTotal,_tmpDayReturnsTotal,_tmpDayGiftsTotal,_tmpDayWasteTotal,_tmpDayLaboratoryTotal,_tmpDayMidDayLoadingTotal,_tmpNightLoadingTotal,_tmpCurrentStockTotal,_tmpExpectedMorningStockTotal,_tmpMorningVariance,_tmpExpectedClosingStockTotal,_tmpClosingVariance,_tmpStartVariance,_tmpEndCumulativeVariance,_tmpNightShiftVariance,_tmpIsFinalized,_tmpNotes,_tmpCreatedAt,_tmpUpdatedAt);
          } else {
            _result = null;
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
  public Flow<DailyStatEntity> observeStat(final long productId, final String isoDate) {
    final String _sql = "SELECT * FROM daily_stats WHERE productId = ? AND isoDate = ? LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, productId);
    _argIndex = 2;
    _statement.bindString(_argIndex, isoDate);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"daily_stats"}, new Callable<DailyStatEntity>() {
      @Override
      @Nullable
      public DailyStatEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfProductId = CursorUtil.getColumnIndexOrThrow(_cursor, "productId");
          final int _cursorIndexOfIsoDate = CursorUtil.getColumnIndexOrThrow(_cursor, "isoDate");
          final int _cursorIndexOfJalaliDate = CursorUtil.getColumnIndexOrThrow(_cursor, "jalaliDate");
          final int _cursorIndexOfOpeningStockTotal = CursorUtil.getColumnIndexOrThrow(_cursor, "openingStockTotal");
          final int _cursorIndexOfMorningLoadingTotal = CursorUtil.getColumnIndexOrThrow(_cursor, "morningLoadingTotal");
          final int _cursorIndexOfActualMorningStockTotal = CursorUtil.getColumnIndexOrThrow(_cursor, "actualMorningStockTotal");
          final int _cursorIndexOfReceiptTotal = CursorUtil.getColumnIndexOrThrow(_cursor, "receiptTotal");
          final int _cursorIndexOfReturnsTotal = CursorUtil.getColumnIndexOrThrow(_cursor, "returnsTotal");
          final int _cursorIndexOfGiftsTotal = CursorUtil.getColumnIndexOrThrow(_cursor, "giftsTotal");
          final int _cursorIndexOfWasteTotal = CursorUtil.getColumnIndexOrThrow(_cursor, "wasteTotal");
          final int _cursorIndexOfLaboratoryTotal = CursorUtil.getColumnIndexOrThrow(_cursor, "laboratoryTotal");
          final int _cursorIndexOfMidDayLoadingTotal = CursorUtil.getColumnIndexOrThrow(_cursor, "midDayLoadingTotal");
          final int _cursorIndexOfDayReceiptTotal = CursorUtil.getColumnIndexOrThrow(_cursor, "dayReceiptTotal");
          final int _cursorIndexOfDayReturnsTotal = CursorUtil.getColumnIndexOrThrow(_cursor, "dayReturnsTotal");
          final int _cursorIndexOfDayGiftsTotal = CursorUtil.getColumnIndexOrThrow(_cursor, "dayGiftsTotal");
          final int _cursorIndexOfDayWasteTotal = CursorUtil.getColumnIndexOrThrow(_cursor, "dayWasteTotal");
          final int _cursorIndexOfDayLaboratoryTotal = CursorUtil.getColumnIndexOrThrow(_cursor, "dayLaboratoryTotal");
          final int _cursorIndexOfDayMidDayLoadingTotal = CursorUtil.getColumnIndexOrThrow(_cursor, "dayMidDayLoadingTotal");
          final int _cursorIndexOfNightLoadingTotal = CursorUtil.getColumnIndexOrThrow(_cursor, "nightLoadingTotal");
          final int _cursorIndexOfCurrentStockTotal = CursorUtil.getColumnIndexOrThrow(_cursor, "currentStockTotal");
          final int _cursorIndexOfExpectedMorningStockTotal = CursorUtil.getColumnIndexOrThrow(_cursor, "expectedMorningStockTotal");
          final int _cursorIndexOfMorningVariance = CursorUtil.getColumnIndexOrThrow(_cursor, "morningVariance");
          final int _cursorIndexOfExpectedClosingStockTotal = CursorUtil.getColumnIndexOrThrow(_cursor, "expectedClosingStockTotal");
          final int _cursorIndexOfClosingVariance = CursorUtil.getColumnIndexOrThrow(_cursor, "closingVariance");
          final int _cursorIndexOfStartVariance = CursorUtil.getColumnIndexOrThrow(_cursor, "startVariance");
          final int _cursorIndexOfEndCumulativeVariance = CursorUtil.getColumnIndexOrThrow(_cursor, "endCumulativeVariance");
          final int _cursorIndexOfNightShiftVariance = CursorUtil.getColumnIndexOrThrow(_cursor, "nightShiftVariance");
          final int _cursorIndexOfIsFinalized = CursorUtil.getColumnIndexOrThrow(_cursor, "isFinalized");
          final int _cursorIndexOfNotes = CursorUtil.getColumnIndexOrThrow(_cursor, "notes");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updatedAt");
          final DailyStatEntity _result;
          if (_cursor.moveToFirst()) {
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpProductId;
            _tmpProductId = _cursor.getLong(_cursorIndexOfProductId);
            final String _tmpIsoDate;
            _tmpIsoDate = _cursor.getString(_cursorIndexOfIsoDate);
            final String _tmpJalaliDate;
            _tmpJalaliDate = _cursor.getString(_cursorIndexOfJalaliDate);
            final int _tmpOpeningStockTotal;
            _tmpOpeningStockTotal = _cursor.getInt(_cursorIndexOfOpeningStockTotal);
            final int _tmpMorningLoadingTotal;
            _tmpMorningLoadingTotal = _cursor.getInt(_cursorIndexOfMorningLoadingTotal);
            final Integer _tmpActualMorningStockTotal;
            if (_cursor.isNull(_cursorIndexOfActualMorningStockTotal)) {
              _tmpActualMorningStockTotal = null;
            } else {
              _tmpActualMorningStockTotal = _cursor.getInt(_cursorIndexOfActualMorningStockTotal);
            }
            final int _tmpReceiptTotal;
            _tmpReceiptTotal = _cursor.getInt(_cursorIndexOfReceiptTotal);
            final int _tmpReturnsTotal;
            _tmpReturnsTotal = _cursor.getInt(_cursorIndexOfReturnsTotal);
            final int _tmpGiftsTotal;
            _tmpGiftsTotal = _cursor.getInt(_cursorIndexOfGiftsTotal);
            final int _tmpWasteTotal;
            _tmpWasteTotal = _cursor.getInt(_cursorIndexOfWasteTotal);
            final int _tmpLaboratoryTotal;
            _tmpLaboratoryTotal = _cursor.getInt(_cursorIndexOfLaboratoryTotal);
            final int _tmpMidDayLoadingTotal;
            _tmpMidDayLoadingTotal = _cursor.getInt(_cursorIndexOfMidDayLoadingTotal);
            final int _tmpDayReceiptTotal;
            _tmpDayReceiptTotal = _cursor.getInt(_cursorIndexOfDayReceiptTotal);
            final int _tmpDayReturnsTotal;
            _tmpDayReturnsTotal = _cursor.getInt(_cursorIndexOfDayReturnsTotal);
            final int _tmpDayGiftsTotal;
            _tmpDayGiftsTotal = _cursor.getInt(_cursorIndexOfDayGiftsTotal);
            final int _tmpDayWasteTotal;
            _tmpDayWasteTotal = _cursor.getInt(_cursorIndexOfDayWasteTotal);
            final int _tmpDayLaboratoryTotal;
            _tmpDayLaboratoryTotal = _cursor.getInt(_cursorIndexOfDayLaboratoryTotal);
            final int _tmpDayMidDayLoadingTotal;
            _tmpDayMidDayLoadingTotal = _cursor.getInt(_cursorIndexOfDayMidDayLoadingTotal);
            final int _tmpNightLoadingTotal;
            _tmpNightLoadingTotal = _cursor.getInt(_cursorIndexOfNightLoadingTotal);
            final Integer _tmpCurrentStockTotal;
            if (_cursor.isNull(_cursorIndexOfCurrentStockTotal)) {
              _tmpCurrentStockTotal = null;
            } else {
              _tmpCurrentStockTotal = _cursor.getInt(_cursorIndexOfCurrentStockTotal);
            }
            final int _tmpExpectedMorningStockTotal;
            _tmpExpectedMorningStockTotal = _cursor.getInt(_cursorIndexOfExpectedMorningStockTotal);
            final Integer _tmpMorningVariance;
            if (_cursor.isNull(_cursorIndexOfMorningVariance)) {
              _tmpMorningVariance = null;
            } else {
              _tmpMorningVariance = _cursor.getInt(_cursorIndexOfMorningVariance);
            }
            final int _tmpExpectedClosingStockTotal;
            _tmpExpectedClosingStockTotal = _cursor.getInt(_cursorIndexOfExpectedClosingStockTotal);
            final int _tmpClosingVariance;
            _tmpClosingVariance = _cursor.getInt(_cursorIndexOfClosingVariance);
            final Integer _tmpStartVariance;
            if (_cursor.isNull(_cursorIndexOfStartVariance)) {
              _tmpStartVariance = null;
            } else {
              _tmpStartVariance = _cursor.getInt(_cursorIndexOfStartVariance);
            }
            final Integer _tmpEndCumulativeVariance;
            if (_cursor.isNull(_cursorIndexOfEndCumulativeVariance)) {
              _tmpEndCumulativeVariance = null;
            } else {
              _tmpEndCumulativeVariance = _cursor.getInt(_cursorIndexOfEndCumulativeVariance);
            }
            final Integer _tmpNightShiftVariance;
            if (_cursor.isNull(_cursorIndexOfNightShiftVariance)) {
              _tmpNightShiftVariance = null;
            } else {
              _tmpNightShiftVariance = _cursor.getInt(_cursorIndexOfNightShiftVariance);
            }
            final boolean _tmpIsFinalized;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsFinalized);
            _tmpIsFinalized = _tmp != 0;
            final String _tmpNotes;
            _tmpNotes = _cursor.getString(_cursorIndexOfNotes);
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final long _tmpUpdatedAt;
            _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
            _result = new DailyStatEntity(_tmpId,_tmpProductId,_tmpIsoDate,_tmpJalaliDate,_tmpOpeningStockTotal,_tmpMorningLoadingTotal,_tmpActualMorningStockTotal,_tmpReceiptTotal,_tmpReturnsTotal,_tmpGiftsTotal,_tmpWasteTotal,_tmpLaboratoryTotal,_tmpMidDayLoadingTotal,_tmpDayReceiptTotal,_tmpDayReturnsTotal,_tmpDayGiftsTotal,_tmpDayWasteTotal,_tmpDayLaboratoryTotal,_tmpDayMidDayLoadingTotal,_tmpNightLoadingTotal,_tmpCurrentStockTotal,_tmpExpectedMorningStockTotal,_tmpMorningVariance,_tmpExpectedClosingStockTotal,_tmpClosingVariance,_tmpStartVariance,_tmpEndCumulativeVariance,_tmpNightShiftVariance,_tmpIsFinalized,_tmpNotes,_tmpCreatedAt,_tmpUpdatedAt);
          } else {
            _result = null;
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
  public Flow<List<DailyStatEntity>> observeStatsForDate(final String isoDate) {
    final String _sql = "SELECT * FROM daily_stats WHERE isoDate = ? ORDER BY productId ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, isoDate);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"daily_stats"}, new Callable<List<DailyStatEntity>>() {
      @Override
      @NonNull
      public List<DailyStatEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfProductId = CursorUtil.getColumnIndexOrThrow(_cursor, "productId");
          final int _cursorIndexOfIsoDate = CursorUtil.getColumnIndexOrThrow(_cursor, "isoDate");
          final int _cursorIndexOfJalaliDate = CursorUtil.getColumnIndexOrThrow(_cursor, "jalaliDate");
          final int _cursorIndexOfOpeningStockTotal = CursorUtil.getColumnIndexOrThrow(_cursor, "openingStockTotal");
          final int _cursorIndexOfMorningLoadingTotal = CursorUtil.getColumnIndexOrThrow(_cursor, "morningLoadingTotal");
          final int _cursorIndexOfActualMorningStockTotal = CursorUtil.getColumnIndexOrThrow(_cursor, "actualMorningStockTotal");
          final int _cursorIndexOfReceiptTotal = CursorUtil.getColumnIndexOrThrow(_cursor, "receiptTotal");
          final int _cursorIndexOfReturnsTotal = CursorUtil.getColumnIndexOrThrow(_cursor, "returnsTotal");
          final int _cursorIndexOfGiftsTotal = CursorUtil.getColumnIndexOrThrow(_cursor, "giftsTotal");
          final int _cursorIndexOfWasteTotal = CursorUtil.getColumnIndexOrThrow(_cursor, "wasteTotal");
          final int _cursorIndexOfLaboratoryTotal = CursorUtil.getColumnIndexOrThrow(_cursor, "laboratoryTotal");
          final int _cursorIndexOfMidDayLoadingTotal = CursorUtil.getColumnIndexOrThrow(_cursor, "midDayLoadingTotal");
          final int _cursorIndexOfDayReceiptTotal = CursorUtil.getColumnIndexOrThrow(_cursor, "dayReceiptTotal");
          final int _cursorIndexOfDayReturnsTotal = CursorUtil.getColumnIndexOrThrow(_cursor, "dayReturnsTotal");
          final int _cursorIndexOfDayGiftsTotal = CursorUtil.getColumnIndexOrThrow(_cursor, "dayGiftsTotal");
          final int _cursorIndexOfDayWasteTotal = CursorUtil.getColumnIndexOrThrow(_cursor, "dayWasteTotal");
          final int _cursorIndexOfDayLaboratoryTotal = CursorUtil.getColumnIndexOrThrow(_cursor, "dayLaboratoryTotal");
          final int _cursorIndexOfDayMidDayLoadingTotal = CursorUtil.getColumnIndexOrThrow(_cursor, "dayMidDayLoadingTotal");
          final int _cursorIndexOfNightLoadingTotal = CursorUtil.getColumnIndexOrThrow(_cursor, "nightLoadingTotal");
          final int _cursorIndexOfCurrentStockTotal = CursorUtil.getColumnIndexOrThrow(_cursor, "currentStockTotal");
          final int _cursorIndexOfExpectedMorningStockTotal = CursorUtil.getColumnIndexOrThrow(_cursor, "expectedMorningStockTotal");
          final int _cursorIndexOfMorningVariance = CursorUtil.getColumnIndexOrThrow(_cursor, "morningVariance");
          final int _cursorIndexOfExpectedClosingStockTotal = CursorUtil.getColumnIndexOrThrow(_cursor, "expectedClosingStockTotal");
          final int _cursorIndexOfClosingVariance = CursorUtil.getColumnIndexOrThrow(_cursor, "closingVariance");
          final int _cursorIndexOfStartVariance = CursorUtil.getColumnIndexOrThrow(_cursor, "startVariance");
          final int _cursorIndexOfEndCumulativeVariance = CursorUtil.getColumnIndexOrThrow(_cursor, "endCumulativeVariance");
          final int _cursorIndexOfNightShiftVariance = CursorUtil.getColumnIndexOrThrow(_cursor, "nightShiftVariance");
          final int _cursorIndexOfIsFinalized = CursorUtil.getColumnIndexOrThrow(_cursor, "isFinalized");
          final int _cursorIndexOfNotes = CursorUtil.getColumnIndexOrThrow(_cursor, "notes");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updatedAt");
          final List<DailyStatEntity> _result = new ArrayList<DailyStatEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final DailyStatEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpProductId;
            _tmpProductId = _cursor.getLong(_cursorIndexOfProductId);
            final String _tmpIsoDate;
            _tmpIsoDate = _cursor.getString(_cursorIndexOfIsoDate);
            final String _tmpJalaliDate;
            _tmpJalaliDate = _cursor.getString(_cursorIndexOfJalaliDate);
            final int _tmpOpeningStockTotal;
            _tmpOpeningStockTotal = _cursor.getInt(_cursorIndexOfOpeningStockTotal);
            final int _tmpMorningLoadingTotal;
            _tmpMorningLoadingTotal = _cursor.getInt(_cursorIndexOfMorningLoadingTotal);
            final Integer _tmpActualMorningStockTotal;
            if (_cursor.isNull(_cursorIndexOfActualMorningStockTotal)) {
              _tmpActualMorningStockTotal = null;
            } else {
              _tmpActualMorningStockTotal = _cursor.getInt(_cursorIndexOfActualMorningStockTotal);
            }
            final int _tmpReceiptTotal;
            _tmpReceiptTotal = _cursor.getInt(_cursorIndexOfReceiptTotal);
            final int _tmpReturnsTotal;
            _tmpReturnsTotal = _cursor.getInt(_cursorIndexOfReturnsTotal);
            final int _tmpGiftsTotal;
            _tmpGiftsTotal = _cursor.getInt(_cursorIndexOfGiftsTotal);
            final int _tmpWasteTotal;
            _tmpWasteTotal = _cursor.getInt(_cursorIndexOfWasteTotal);
            final int _tmpLaboratoryTotal;
            _tmpLaboratoryTotal = _cursor.getInt(_cursorIndexOfLaboratoryTotal);
            final int _tmpMidDayLoadingTotal;
            _tmpMidDayLoadingTotal = _cursor.getInt(_cursorIndexOfMidDayLoadingTotal);
            final int _tmpDayReceiptTotal;
            _tmpDayReceiptTotal = _cursor.getInt(_cursorIndexOfDayReceiptTotal);
            final int _tmpDayReturnsTotal;
            _tmpDayReturnsTotal = _cursor.getInt(_cursorIndexOfDayReturnsTotal);
            final int _tmpDayGiftsTotal;
            _tmpDayGiftsTotal = _cursor.getInt(_cursorIndexOfDayGiftsTotal);
            final int _tmpDayWasteTotal;
            _tmpDayWasteTotal = _cursor.getInt(_cursorIndexOfDayWasteTotal);
            final int _tmpDayLaboratoryTotal;
            _tmpDayLaboratoryTotal = _cursor.getInt(_cursorIndexOfDayLaboratoryTotal);
            final int _tmpDayMidDayLoadingTotal;
            _tmpDayMidDayLoadingTotal = _cursor.getInt(_cursorIndexOfDayMidDayLoadingTotal);
            final int _tmpNightLoadingTotal;
            _tmpNightLoadingTotal = _cursor.getInt(_cursorIndexOfNightLoadingTotal);
            final Integer _tmpCurrentStockTotal;
            if (_cursor.isNull(_cursorIndexOfCurrentStockTotal)) {
              _tmpCurrentStockTotal = null;
            } else {
              _tmpCurrentStockTotal = _cursor.getInt(_cursorIndexOfCurrentStockTotal);
            }
            final int _tmpExpectedMorningStockTotal;
            _tmpExpectedMorningStockTotal = _cursor.getInt(_cursorIndexOfExpectedMorningStockTotal);
            final Integer _tmpMorningVariance;
            if (_cursor.isNull(_cursorIndexOfMorningVariance)) {
              _tmpMorningVariance = null;
            } else {
              _tmpMorningVariance = _cursor.getInt(_cursorIndexOfMorningVariance);
            }
            final int _tmpExpectedClosingStockTotal;
            _tmpExpectedClosingStockTotal = _cursor.getInt(_cursorIndexOfExpectedClosingStockTotal);
            final int _tmpClosingVariance;
            _tmpClosingVariance = _cursor.getInt(_cursorIndexOfClosingVariance);
            final Integer _tmpStartVariance;
            if (_cursor.isNull(_cursorIndexOfStartVariance)) {
              _tmpStartVariance = null;
            } else {
              _tmpStartVariance = _cursor.getInt(_cursorIndexOfStartVariance);
            }
            final Integer _tmpEndCumulativeVariance;
            if (_cursor.isNull(_cursorIndexOfEndCumulativeVariance)) {
              _tmpEndCumulativeVariance = null;
            } else {
              _tmpEndCumulativeVariance = _cursor.getInt(_cursorIndexOfEndCumulativeVariance);
            }
            final Integer _tmpNightShiftVariance;
            if (_cursor.isNull(_cursorIndexOfNightShiftVariance)) {
              _tmpNightShiftVariance = null;
            } else {
              _tmpNightShiftVariance = _cursor.getInt(_cursorIndexOfNightShiftVariance);
            }
            final boolean _tmpIsFinalized;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsFinalized);
            _tmpIsFinalized = _tmp != 0;
            final String _tmpNotes;
            _tmpNotes = _cursor.getString(_cursorIndexOfNotes);
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final long _tmpUpdatedAt;
            _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
            _item = new DailyStatEntity(_tmpId,_tmpProductId,_tmpIsoDate,_tmpJalaliDate,_tmpOpeningStockTotal,_tmpMorningLoadingTotal,_tmpActualMorningStockTotal,_tmpReceiptTotal,_tmpReturnsTotal,_tmpGiftsTotal,_tmpWasteTotal,_tmpLaboratoryTotal,_tmpMidDayLoadingTotal,_tmpDayReceiptTotal,_tmpDayReturnsTotal,_tmpDayGiftsTotal,_tmpDayWasteTotal,_tmpDayLaboratoryTotal,_tmpDayMidDayLoadingTotal,_tmpNightLoadingTotal,_tmpCurrentStockTotal,_tmpExpectedMorningStockTotal,_tmpMorningVariance,_tmpExpectedClosingStockTotal,_tmpClosingVariance,_tmpStartVariance,_tmpEndCumulativeVariance,_tmpNightShiftVariance,_tmpIsFinalized,_tmpNotes,_tmpCreatedAt,_tmpUpdatedAt);
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
  public Object getStatsForDate(final String isoDate,
      final Continuation<? super List<DailyStatEntity>> $completion) {
    final String _sql = "SELECT * FROM daily_stats WHERE isoDate = ? ORDER BY productId ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, isoDate);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<DailyStatEntity>>() {
      @Override
      @NonNull
      public List<DailyStatEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfProductId = CursorUtil.getColumnIndexOrThrow(_cursor, "productId");
          final int _cursorIndexOfIsoDate = CursorUtil.getColumnIndexOrThrow(_cursor, "isoDate");
          final int _cursorIndexOfJalaliDate = CursorUtil.getColumnIndexOrThrow(_cursor, "jalaliDate");
          final int _cursorIndexOfOpeningStockTotal = CursorUtil.getColumnIndexOrThrow(_cursor, "openingStockTotal");
          final int _cursorIndexOfMorningLoadingTotal = CursorUtil.getColumnIndexOrThrow(_cursor, "morningLoadingTotal");
          final int _cursorIndexOfActualMorningStockTotal = CursorUtil.getColumnIndexOrThrow(_cursor, "actualMorningStockTotal");
          final int _cursorIndexOfReceiptTotal = CursorUtil.getColumnIndexOrThrow(_cursor, "receiptTotal");
          final int _cursorIndexOfReturnsTotal = CursorUtil.getColumnIndexOrThrow(_cursor, "returnsTotal");
          final int _cursorIndexOfGiftsTotal = CursorUtil.getColumnIndexOrThrow(_cursor, "giftsTotal");
          final int _cursorIndexOfWasteTotal = CursorUtil.getColumnIndexOrThrow(_cursor, "wasteTotal");
          final int _cursorIndexOfLaboratoryTotal = CursorUtil.getColumnIndexOrThrow(_cursor, "laboratoryTotal");
          final int _cursorIndexOfMidDayLoadingTotal = CursorUtil.getColumnIndexOrThrow(_cursor, "midDayLoadingTotal");
          final int _cursorIndexOfDayReceiptTotal = CursorUtil.getColumnIndexOrThrow(_cursor, "dayReceiptTotal");
          final int _cursorIndexOfDayReturnsTotal = CursorUtil.getColumnIndexOrThrow(_cursor, "dayReturnsTotal");
          final int _cursorIndexOfDayGiftsTotal = CursorUtil.getColumnIndexOrThrow(_cursor, "dayGiftsTotal");
          final int _cursorIndexOfDayWasteTotal = CursorUtil.getColumnIndexOrThrow(_cursor, "dayWasteTotal");
          final int _cursorIndexOfDayLaboratoryTotal = CursorUtil.getColumnIndexOrThrow(_cursor, "dayLaboratoryTotal");
          final int _cursorIndexOfDayMidDayLoadingTotal = CursorUtil.getColumnIndexOrThrow(_cursor, "dayMidDayLoadingTotal");
          final int _cursorIndexOfNightLoadingTotal = CursorUtil.getColumnIndexOrThrow(_cursor, "nightLoadingTotal");
          final int _cursorIndexOfCurrentStockTotal = CursorUtil.getColumnIndexOrThrow(_cursor, "currentStockTotal");
          final int _cursorIndexOfExpectedMorningStockTotal = CursorUtil.getColumnIndexOrThrow(_cursor, "expectedMorningStockTotal");
          final int _cursorIndexOfMorningVariance = CursorUtil.getColumnIndexOrThrow(_cursor, "morningVariance");
          final int _cursorIndexOfExpectedClosingStockTotal = CursorUtil.getColumnIndexOrThrow(_cursor, "expectedClosingStockTotal");
          final int _cursorIndexOfClosingVariance = CursorUtil.getColumnIndexOrThrow(_cursor, "closingVariance");
          final int _cursorIndexOfStartVariance = CursorUtil.getColumnIndexOrThrow(_cursor, "startVariance");
          final int _cursorIndexOfEndCumulativeVariance = CursorUtil.getColumnIndexOrThrow(_cursor, "endCumulativeVariance");
          final int _cursorIndexOfNightShiftVariance = CursorUtil.getColumnIndexOrThrow(_cursor, "nightShiftVariance");
          final int _cursorIndexOfIsFinalized = CursorUtil.getColumnIndexOrThrow(_cursor, "isFinalized");
          final int _cursorIndexOfNotes = CursorUtil.getColumnIndexOrThrow(_cursor, "notes");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updatedAt");
          final List<DailyStatEntity> _result = new ArrayList<DailyStatEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final DailyStatEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpProductId;
            _tmpProductId = _cursor.getLong(_cursorIndexOfProductId);
            final String _tmpIsoDate;
            _tmpIsoDate = _cursor.getString(_cursorIndexOfIsoDate);
            final String _tmpJalaliDate;
            _tmpJalaliDate = _cursor.getString(_cursorIndexOfJalaliDate);
            final int _tmpOpeningStockTotal;
            _tmpOpeningStockTotal = _cursor.getInt(_cursorIndexOfOpeningStockTotal);
            final int _tmpMorningLoadingTotal;
            _tmpMorningLoadingTotal = _cursor.getInt(_cursorIndexOfMorningLoadingTotal);
            final Integer _tmpActualMorningStockTotal;
            if (_cursor.isNull(_cursorIndexOfActualMorningStockTotal)) {
              _tmpActualMorningStockTotal = null;
            } else {
              _tmpActualMorningStockTotal = _cursor.getInt(_cursorIndexOfActualMorningStockTotal);
            }
            final int _tmpReceiptTotal;
            _tmpReceiptTotal = _cursor.getInt(_cursorIndexOfReceiptTotal);
            final int _tmpReturnsTotal;
            _tmpReturnsTotal = _cursor.getInt(_cursorIndexOfReturnsTotal);
            final int _tmpGiftsTotal;
            _tmpGiftsTotal = _cursor.getInt(_cursorIndexOfGiftsTotal);
            final int _tmpWasteTotal;
            _tmpWasteTotal = _cursor.getInt(_cursorIndexOfWasteTotal);
            final int _tmpLaboratoryTotal;
            _tmpLaboratoryTotal = _cursor.getInt(_cursorIndexOfLaboratoryTotal);
            final int _tmpMidDayLoadingTotal;
            _tmpMidDayLoadingTotal = _cursor.getInt(_cursorIndexOfMidDayLoadingTotal);
            final int _tmpDayReceiptTotal;
            _tmpDayReceiptTotal = _cursor.getInt(_cursorIndexOfDayReceiptTotal);
            final int _tmpDayReturnsTotal;
            _tmpDayReturnsTotal = _cursor.getInt(_cursorIndexOfDayReturnsTotal);
            final int _tmpDayGiftsTotal;
            _tmpDayGiftsTotal = _cursor.getInt(_cursorIndexOfDayGiftsTotal);
            final int _tmpDayWasteTotal;
            _tmpDayWasteTotal = _cursor.getInt(_cursorIndexOfDayWasteTotal);
            final int _tmpDayLaboratoryTotal;
            _tmpDayLaboratoryTotal = _cursor.getInt(_cursorIndexOfDayLaboratoryTotal);
            final int _tmpDayMidDayLoadingTotal;
            _tmpDayMidDayLoadingTotal = _cursor.getInt(_cursorIndexOfDayMidDayLoadingTotal);
            final int _tmpNightLoadingTotal;
            _tmpNightLoadingTotal = _cursor.getInt(_cursorIndexOfNightLoadingTotal);
            final Integer _tmpCurrentStockTotal;
            if (_cursor.isNull(_cursorIndexOfCurrentStockTotal)) {
              _tmpCurrentStockTotal = null;
            } else {
              _tmpCurrentStockTotal = _cursor.getInt(_cursorIndexOfCurrentStockTotal);
            }
            final int _tmpExpectedMorningStockTotal;
            _tmpExpectedMorningStockTotal = _cursor.getInt(_cursorIndexOfExpectedMorningStockTotal);
            final Integer _tmpMorningVariance;
            if (_cursor.isNull(_cursorIndexOfMorningVariance)) {
              _tmpMorningVariance = null;
            } else {
              _tmpMorningVariance = _cursor.getInt(_cursorIndexOfMorningVariance);
            }
            final int _tmpExpectedClosingStockTotal;
            _tmpExpectedClosingStockTotal = _cursor.getInt(_cursorIndexOfExpectedClosingStockTotal);
            final int _tmpClosingVariance;
            _tmpClosingVariance = _cursor.getInt(_cursorIndexOfClosingVariance);
            final Integer _tmpStartVariance;
            if (_cursor.isNull(_cursorIndexOfStartVariance)) {
              _tmpStartVariance = null;
            } else {
              _tmpStartVariance = _cursor.getInt(_cursorIndexOfStartVariance);
            }
            final Integer _tmpEndCumulativeVariance;
            if (_cursor.isNull(_cursorIndexOfEndCumulativeVariance)) {
              _tmpEndCumulativeVariance = null;
            } else {
              _tmpEndCumulativeVariance = _cursor.getInt(_cursorIndexOfEndCumulativeVariance);
            }
            final Integer _tmpNightShiftVariance;
            if (_cursor.isNull(_cursorIndexOfNightShiftVariance)) {
              _tmpNightShiftVariance = null;
            } else {
              _tmpNightShiftVariance = _cursor.getInt(_cursorIndexOfNightShiftVariance);
            }
            final boolean _tmpIsFinalized;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsFinalized);
            _tmpIsFinalized = _tmp != 0;
            final String _tmpNotes;
            _tmpNotes = _cursor.getString(_cursorIndexOfNotes);
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final long _tmpUpdatedAt;
            _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
            _item = new DailyStatEntity(_tmpId,_tmpProductId,_tmpIsoDate,_tmpJalaliDate,_tmpOpeningStockTotal,_tmpMorningLoadingTotal,_tmpActualMorningStockTotal,_tmpReceiptTotal,_tmpReturnsTotal,_tmpGiftsTotal,_tmpWasteTotal,_tmpLaboratoryTotal,_tmpMidDayLoadingTotal,_tmpDayReceiptTotal,_tmpDayReturnsTotal,_tmpDayGiftsTotal,_tmpDayWasteTotal,_tmpDayLaboratoryTotal,_tmpDayMidDayLoadingTotal,_tmpNightLoadingTotal,_tmpCurrentStockTotal,_tmpExpectedMorningStockTotal,_tmpMorningVariance,_tmpExpectedClosingStockTotal,_tmpClosingVariance,_tmpStartVariance,_tmpEndCumulativeVariance,_tmpNightShiftVariance,_tmpIsFinalized,_tmpNotes,_tmpCreatedAt,_tmpUpdatedAt);
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
  public Object getPreviousDayStat(final long productId, final String isoDate,
      final Continuation<? super DailyStatEntity> $completion) {
    final String _sql = "\n"
            + "        SELECT * FROM daily_stats\n"
            + "        WHERE productId = ?\n"
            + "        AND isoDate < ?\n"
            + "        ORDER BY isoDate DESC\n"
            + "        LIMIT 1\n"
            + "    ";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, productId);
    _argIndex = 2;
    _statement.bindString(_argIndex, isoDate);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<DailyStatEntity>() {
      @Override
      @Nullable
      public DailyStatEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfProductId = CursorUtil.getColumnIndexOrThrow(_cursor, "productId");
          final int _cursorIndexOfIsoDate = CursorUtil.getColumnIndexOrThrow(_cursor, "isoDate");
          final int _cursorIndexOfJalaliDate = CursorUtil.getColumnIndexOrThrow(_cursor, "jalaliDate");
          final int _cursorIndexOfOpeningStockTotal = CursorUtil.getColumnIndexOrThrow(_cursor, "openingStockTotal");
          final int _cursorIndexOfMorningLoadingTotal = CursorUtil.getColumnIndexOrThrow(_cursor, "morningLoadingTotal");
          final int _cursorIndexOfActualMorningStockTotal = CursorUtil.getColumnIndexOrThrow(_cursor, "actualMorningStockTotal");
          final int _cursorIndexOfReceiptTotal = CursorUtil.getColumnIndexOrThrow(_cursor, "receiptTotal");
          final int _cursorIndexOfReturnsTotal = CursorUtil.getColumnIndexOrThrow(_cursor, "returnsTotal");
          final int _cursorIndexOfGiftsTotal = CursorUtil.getColumnIndexOrThrow(_cursor, "giftsTotal");
          final int _cursorIndexOfWasteTotal = CursorUtil.getColumnIndexOrThrow(_cursor, "wasteTotal");
          final int _cursorIndexOfLaboratoryTotal = CursorUtil.getColumnIndexOrThrow(_cursor, "laboratoryTotal");
          final int _cursorIndexOfMidDayLoadingTotal = CursorUtil.getColumnIndexOrThrow(_cursor, "midDayLoadingTotal");
          final int _cursorIndexOfDayReceiptTotal = CursorUtil.getColumnIndexOrThrow(_cursor, "dayReceiptTotal");
          final int _cursorIndexOfDayReturnsTotal = CursorUtil.getColumnIndexOrThrow(_cursor, "dayReturnsTotal");
          final int _cursorIndexOfDayGiftsTotal = CursorUtil.getColumnIndexOrThrow(_cursor, "dayGiftsTotal");
          final int _cursorIndexOfDayWasteTotal = CursorUtil.getColumnIndexOrThrow(_cursor, "dayWasteTotal");
          final int _cursorIndexOfDayLaboratoryTotal = CursorUtil.getColumnIndexOrThrow(_cursor, "dayLaboratoryTotal");
          final int _cursorIndexOfDayMidDayLoadingTotal = CursorUtil.getColumnIndexOrThrow(_cursor, "dayMidDayLoadingTotal");
          final int _cursorIndexOfNightLoadingTotal = CursorUtil.getColumnIndexOrThrow(_cursor, "nightLoadingTotal");
          final int _cursorIndexOfCurrentStockTotal = CursorUtil.getColumnIndexOrThrow(_cursor, "currentStockTotal");
          final int _cursorIndexOfExpectedMorningStockTotal = CursorUtil.getColumnIndexOrThrow(_cursor, "expectedMorningStockTotal");
          final int _cursorIndexOfMorningVariance = CursorUtil.getColumnIndexOrThrow(_cursor, "morningVariance");
          final int _cursorIndexOfExpectedClosingStockTotal = CursorUtil.getColumnIndexOrThrow(_cursor, "expectedClosingStockTotal");
          final int _cursorIndexOfClosingVariance = CursorUtil.getColumnIndexOrThrow(_cursor, "closingVariance");
          final int _cursorIndexOfStartVariance = CursorUtil.getColumnIndexOrThrow(_cursor, "startVariance");
          final int _cursorIndexOfEndCumulativeVariance = CursorUtil.getColumnIndexOrThrow(_cursor, "endCumulativeVariance");
          final int _cursorIndexOfNightShiftVariance = CursorUtil.getColumnIndexOrThrow(_cursor, "nightShiftVariance");
          final int _cursorIndexOfIsFinalized = CursorUtil.getColumnIndexOrThrow(_cursor, "isFinalized");
          final int _cursorIndexOfNotes = CursorUtil.getColumnIndexOrThrow(_cursor, "notes");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updatedAt");
          final DailyStatEntity _result;
          if (_cursor.moveToFirst()) {
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpProductId;
            _tmpProductId = _cursor.getLong(_cursorIndexOfProductId);
            final String _tmpIsoDate;
            _tmpIsoDate = _cursor.getString(_cursorIndexOfIsoDate);
            final String _tmpJalaliDate;
            _tmpJalaliDate = _cursor.getString(_cursorIndexOfJalaliDate);
            final int _tmpOpeningStockTotal;
            _tmpOpeningStockTotal = _cursor.getInt(_cursorIndexOfOpeningStockTotal);
            final int _tmpMorningLoadingTotal;
            _tmpMorningLoadingTotal = _cursor.getInt(_cursorIndexOfMorningLoadingTotal);
            final Integer _tmpActualMorningStockTotal;
            if (_cursor.isNull(_cursorIndexOfActualMorningStockTotal)) {
              _tmpActualMorningStockTotal = null;
            } else {
              _tmpActualMorningStockTotal = _cursor.getInt(_cursorIndexOfActualMorningStockTotal);
            }
            final int _tmpReceiptTotal;
            _tmpReceiptTotal = _cursor.getInt(_cursorIndexOfReceiptTotal);
            final int _tmpReturnsTotal;
            _tmpReturnsTotal = _cursor.getInt(_cursorIndexOfReturnsTotal);
            final int _tmpGiftsTotal;
            _tmpGiftsTotal = _cursor.getInt(_cursorIndexOfGiftsTotal);
            final int _tmpWasteTotal;
            _tmpWasteTotal = _cursor.getInt(_cursorIndexOfWasteTotal);
            final int _tmpLaboratoryTotal;
            _tmpLaboratoryTotal = _cursor.getInt(_cursorIndexOfLaboratoryTotal);
            final int _tmpMidDayLoadingTotal;
            _tmpMidDayLoadingTotal = _cursor.getInt(_cursorIndexOfMidDayLoadingTotal);
            final int _tmpDayReceiptTotal;
            _tmpDayReceiptTotal = _cursor.getInt(_cursorIndexOfDayReceiptTotal);
            final int _tmpDayReturnsTotal;
            _tmpDayReturnsTotal = _cursor.getInt(_cursorIndexOfDayReturnsTotal);
            final int _tmpDayGiftsTotal;
            _tmpDayGiftsTotal = _cursor.getInt(_cursorIndexOfDayGiftsTotal);
            final int _tmpDayWasteTotal;
            _tmpDayWasteTotal = _cursor.getInt(_cursorIndexOfDayWasteTotal);
            final int _tmpDayLaboratoryTotal;
            _tmpDayLaboratoryTotal = _cursor.getInt(_cursorIndexOfDayLaboratoryTotal);
            final int _tmpDayMidDayLoadingTotal;
            _tmpDayMidDayLoadingTotal = _cursor.getInt(_cursorIndexOfDayMidDayLoadingTotal);
            final int _tmpNightLoadingTotal;
            _tmpNightLoadingTotal = _cursor.getInt(_cursorIndexOfNightLoadingTotal);
            final Integer _tmpCurrentStockTotal;
            if (_cursor.isNull(_cursorIndexOfCurrentStockTotal)) {
              _tmpCurrentStockTotal = null;
            } else {
              _tmpCurrentStockTotal = _cursor.getInt(_cursorIndexOfCurrentStockTotal);
            }
            final int _tmpExpectedMorningStockTotal;
            _tmpExpectedMorningStockTotal = _cursor.getInt(_cursorIndexOfExpectedMorningStockTotal);
            final Integer _tmpMorningVariance;
            if (_cursor.isNull(_cursorIndexOfMorningVariance)) {
              _tmpMorningVariance = null;
            } else {
              _tmpMorningVariance = _cursor.getInt(_cursorIndexOfMorningVariance);
            }
            final int _tmpExpectedClosingStockTotal;
            _tmpExpectedClosingStockTotal = _cursor.getInt(_cursorIndexOfExpectedClosingStockTotal);
            final int _tmpClosingVariance;
            _tmpClosingVariance = _cursor.getInt(_cursorIndexOfClosingVariance);
            final Integer _tmpStartVariance;
            if (_cursor.isNull(_cursorIndexOfStartVariance)) {
              _tmpStartVariance = null;
            } else {
              _tmpStartVariance = _cursor.getInt(_cursorIndexOfStartVariance);
            }
            final Integer _tmpEndCumulativeVariance;
            if (_cursor.isNull(_cursorIndexOfEndCumulativeVariance)) {
              _tmpEndCumulativeVariance = null;
            } else {
              _tmpEndCumulativeVariance = _cursor.getInt(_cursorIndexOfEndCumulativeVariance);
            }
            final Integer _tmpNightShiftVariance;
            if (_cursor.isNull(_cursorIndexOfNightShiftVariance)) {
              _tmpNightShiftVariance = null;
            } else {
              _tmpNightShiftVariance = _cursor.getInt(_cursorIndexOfNightShiftVariance);
            }
            final boolean _tmpIsFinalized;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsFinalized);
            _tmpIsFinalized = _tmp != 0;
            final String _tmpNotes;
            _tmpNotes = _cursor.getString(_cursorIndexOfNotes);
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final long _tmpUpdatedAt;
            _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
            _result = new DailyStatEntity(_tmpId,_tmpProductId,_tmpIsoDate,_tmpJalaliDate,_tmpOpeningStockTotal,_tmpMorningLoadingTotal,_tmpActualMorningStockTotal,_tmpReceiptTotal,_tmpReturnsTotal,_tmpGiftsTotal,_tmpWasteTotal,_tmpLaboratoryTotal,_tmpMidDayLoadingTotal,_tmpDayReceiptTotal,_tmpDayReturnsTotal,_tmpDayGiftsTotal,_tmpDayWasteTotal,_tmpDayLaboratoryTotal,_tmpDayMidDayLoadingTotal,_tmpNightLoadingTotal,_tmpCurrentStockTotal,_tmpExpectedMorningStockTotal,_tmpMorningVariance,_tmpExpectedClosingStockTotal,_tmpClosingVariance,_tmpStartVariance,_tmpEndCumulativeVariance,_tmpNightShiftVariance,_tmpIsFinalized,_tmpNotes,_tmpCreatedAt,_tmpUpdatedAt);
          } else {
            _result = null;
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
  public Object getLatestStatBeforeDate(final long productId, final String referenceDate,
      final Continuation<? super DailyStatEntity> $completion) {
    final String _sql = "\n"
            + "        SELECT * FROM daily_stats\n"
            + "        WHERE productId = ?\n"
            + "        AND isoDate < ?\n"
            + "        ORDER BY isoDate DESC\n"
            + "        LIMIT 1\n"
            + "    ";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, productId);
    _argIndex = 2;
    _statement.bindString(_argIndex, referenceDate);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<DailyStatEntity>() {
      @Override
      @Nullable
      public DailyStatEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfProductId = CursorUtil.getColumnIndexOrThrow(_cursor, "productId");
          final int _cursorIndexOfIsoDate = CursorUtil.getColumnIndexOrThrow(_cursor, "isoDate");
          final int _cursorIndexOfJalaliDate = CursorUtil.getColumnIndexOrThrow(_cursor, "jalaliDate");
          final int _cursorIndexOfOpeningStockTotal = CursorUtil.getColumnIndexOrThrow(_cursor, "openingStockTotal");
          final int _cursorIndexOfMorningLoadingTotal = CursorUtil.getColumnIndexOrThrow(_cursor, "morningLoadingTotal");
          final int _cursorIndexOfActualMorningStockTotal = CursorUtil.getColumnIndexOrThrow(_cursor, "actualMorningStockTotal");
          final int _cursorIndexOfReceiptTotal = CursorUtil.getColumnIndexOrThrow(_cursor, "receiptTotal");
          final int _cursorIndexOfReturnsTotal = CursorUtil.getColumnIndexOrThrow(_cursor, "returnsTotal");
          final int _cursorIndexOfGiftsTotal = CursorUtil.getColumnIndexOrThrow(_cursor, "giftsTotal");
          final int _cursorIndexOfWasteTotal = CursorUtil.getColumnIndexOrThrow(_cursor, "wasteTotal");
          final int _cursorIndexOfLaboratoryTotal = CursorUtil.getColumnIndexOrThrow(_cursor, "laboratoryTotal");
          final int _cursorIndexOfMidDayLoadingTotal = CursorUtil.getColumnIndexOrThrow(_cursor, "midDayLoadingTotal");
          final int _cursorIndexOfDayReceiptTotal = CursorUtil.getColumnIndexOrThrow(_cursor, "dayReceiptTotal");
          final int _cursorIndexOfDayReturnsTotal = CursorUtil.getColumnIndexOrThrow(_cursor, "dayReturnsTotal");
          final int _cursorIndexOfDayGiftsTotal = CursorUtil.getColumnIndexOrThrow(_cursor, "dayGiftsTotal");
          final int _cursorIndexOfDayWasteTotal = CursorUtil.getColumnIndexOrThrow(_cursor, "dayWasteTotal");
          final int _cursorIndexOfDayLaboratoryTotal = CursorUtil.getColumnIndexOrThrow(_cursor, "dayLaboratoryTotal");
          final int _cursorIndexOfDayMidDayLoadingTotal = CursorUtil.getColumnIndexOrThrow(_cursor, "dayMidDayLoadingTotal");
          final int _cursorIndexOfNightLoadingTotal = CursorUtil.getColumnIndexOrThrow(_cursor, "nightLoadingTotal");
          final int _cursorIndexOfCurrentStockTotal = CursorUtil.getColumnIndexOrThrow(_cursor, "currentStockTotal");
          final int _cursorIndexOfExpectedMorningStockTotal = CursorUtil.getColumnIndexOrThrow(_cursor, "expectedMorningStockTotal");
          final int _cursorIndexOfMorningVariance = CursorUtil.getColumnIndexOrThrow(_cursor, "morningVariance");
          final int _cursorIndexOfExpectedClosingStockTotal = CursorUtil.getColumnIndexOrThrow(_cursor, "expectedClosingStockTotal");
          final int _cursorIndexOfClosingVariance = CursorUtil.getColumnIndexOrThrow(_cursor, "closingVariance");
          final int _cursorIndexOfStartVariance = CursorUtil.getColumnIndexOrThrow(_cursor, "startVariance");
          final int _cursorIndexOfEndCumulativeVariance = CursorUtil.getColumnIndexOrThrow(_cursor, "endCumulativeVariance");
          final int _cursorIndexOfNightShiftVariance = CursorUtil.getColumnIndexOrThrow(_cursor, "nightShiftVariance");
          final int _cursorIndexOfIsFinalized = CursorUtil.getColumnIndexOrThrow(_cursor, "isFinalized");
          final int _cursorIndexOfNotes = CursorUtil.getColumnIndexOrThrow(_cursor, "notes");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updatedAt");
          final DailyStatEntity _result;
          if (_cursor.moveToFirst()) {
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpProductId;
            _tmpProductId = _cursor.getLong(_cursorIndexOfProductId);
            final String _tmpIsoDate;
            _tmpIsoDate = _cursor.getString(_cursorIndexOfIsoDate);
            final String _tmpJalaliDate;
            _tmpJalaliDate = _cursor.getString(_cursorIndexOfJalaliDate);
            final int _tmpOpeningStockTotal;
            _tmpOpeningStockTotal = _cursor.getInt(_cursorIndexOfOpeningStockTotal);
            final int _tmpMorningLoadingTotal;
            _tmpMorningLoadingTotal = _cursor.getInt(_cursorIndexOfMorningLoadingTotal);
            final Integer _tmpActualMorningStockTotal;
            if (_cursor.isNull(_cursorIndexOfActualMorningStockTotal)) {
              _tmpActualMorningStockTotal = null;
            } else {
              _tmpActualMorningStockTotal = _cursor.getInt(_cursorIndexOfActualMorningStockTotal);
            }
            final int _tmpReceiptTotal;
            _tmpReceiptTotal = _cursor.getInt(_cursorIndexOfReceiptTotal);
            final int _tmpReturnsTotal;
            _tmpReturnsTotal = _cursor.getInt(_cursorIndexOfReturnsTotal);
            final int _tmpGiftsTotal;
            _tmpGiftsTotal = _cursor.getInt(_cursorIndexOfGiftsTotal);
            final int _tmpWasteTotal;
            _tmpWasteTotal = _cursor.getInt(_cursorIndexOfWasteTotal);
            final int _tmpLaboratoryTotal;
            _tmpLaboratoryTotal = _cursor.getInt(_cursorIndexOfLaboratoryTotal);
            final int _tmpMidDayLoadingTotal;
            _tmpMidDayLoadingTotal = _cursor.getInt(_cursorIndexOfMidDayLoadingTotal);
            final int _tmpDayReceiptTotal;
            _tmpDayReceiptTotal = _cursor.getInt(_cursorIndexOfDayReceiptTotal);
            final int _tmpDayReturnsTotal;
            _tmpDayReturnsTotal = _cursor.getInt(_cursorIndexOfDayReturnsTotal);
            final int _tmpDayGiftsTotal;
            _tmpDayGiftsTotal = _cursor.getInt(_cursorIndexOfDayGiftsTotal);
            final int _tmpDayWasteTotal;
            _tmpDayWasteTotal = _cursor.getInt(_cursorIndexOfDayWasteTotal);
            final int _tmpDayLaboratoryTotal;
            _tmpDayLaboratoryTotal = _cursor.getInt(_cursorIndexOfDayLaboratoryTotal);
            final int _tmpDayMidDayLoadingTotal;
            _tmpDayMidDayLoadingTotal = _cursor.getInt(_cursorIndexOfDayMidDayLoadingTotal);
            final int _tmpNightLoadingTotal;
            _tmpNightLoadingTotal = _cursor.getInt(_cursorIndexOfNightLoadingTotal);
            final Integer _tmpCurrentStockTotal;
            if (_cursor.isNull(_cursorIndexOfCurrentStockTotal)) {
              _tmpCurrentStockTotal = null;
            } else {
              _tmpCurrentStockTotal = _cursor.getInt(_cursorIndexOfCurrentStockTotal);
            }
            final int _tmpExpectedMorningStockTotal;
            _tmpExpectedMorningStockTotal = _cursor.getInt(_cursorIndexOfExpectedMorningStockTotal);
            final Integer _tmpMorningVariance;
            if (_cursor.isNull(_cursorIndexOfMorningVariance)) {
              _tmpMorningVariance = null;
            } else {
              _tmpMorningVariance = _cursor.getInt(_cursorIndexOfMorningVariance);
            }
            final int _tmpExpectedClosingStockTotal;
            _tmpExpectedClosingStockTotal = _cursor.getInt(_cursorIndexOfExpectedClosingStockTotal);
            final int _tmpClosingVariance;
            _tmpClosingVariance = _cursor.getInt(_cursorIndexOfClosingVariance);
            final Integer _tmpStartVariance;
            if (_cursor.isNull(_cursorIndexOfStartVariance)) {
              _tmpStartVariance = null;
            } else {
              _tmpStartVariance = _cursor.getInt(_cursorIndexOfStartVariance);
            }
            final Integer _tmpEndCumulativeVariance;
            if (_cursor.isNull(_cursorIndexOfEndCumulativeVariance)) {
              _tmpEndCumulativeVariance = null;
            } else {
              _tmpEndCumulativeVariance = _cursor.getInt(_cursorIndexOfEndCumulativeVariance);
            }
            final Integer _tmpNightShiftVariance;
            if (_cursor.isNull(_cursorIndexOfNightShiftVariance)) {
              _tmpNightShiftVariance = null;
            } else {
              _tmpNightShiftVariance = _cursor.getInt(_cursorIndexOfNightShiftVariance);
            }
            final boolean _tmpIsFinalized;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsFinalized);
            _tmpIsFinalized = _tmp != 0;
            final String _tmpNotes;
            _tmpNotes = _cursor.getString(_cursorIndexOfNotes);
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final long _tmpUpdatedAt;
            _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
            _result = new DailyStatEntity(_tmpId,_tmpProductId,_tmpIsoDate,_tmpJalaliDate,_tmpOpeningStockTotal,_tmpMorningLoadingTotal,_tmpActualMorningStockTotal,_tmpReceiptTotal,_tmpReturnsTotal,_tmpGiftsTotal,_tmpWasteTotal,_tmpLaboratoryTotal,_tmpMidDayLoadingTotal,_tmpDayReceiptTotal,_tmpDayReturnsTotal,_tmpDayGiftsTotal,_tmpDayWasteTotal,_tmpDayLaboratoryTotal,_tmpDayMidDayLoadingTotal,_tmpNightLoadingTotal,_tmpCurrentStockTotal,_tmpExpectedMorningStockTotal,_tmpMorningVariance,_tmpExpectedClosingStockTotal,_tmpClosingVariance,_tmpStartVariance,_tmpEndCumulativeVariance,_tmpNightShiftVariance,_tmpIsFinalized,_tmpNotes,_tmpCreatedAt,_tmpUpdatedAt);
          } else {
            _result = null;
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
  public Flow<List<DailyStatEntity>> observeStatsForDateRange(final String startDate,
      final String endDate) {
    final String _sql = "\n"
            + "        SELECT * FROM daily_stats\n"
            + "        WHERE isoDate BETWEEN ? AND ?\n"
            + "        ORDER BY isoDate DESC, productId ASC\n"
            + "    ";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindString(_argIndex, startDate);
    _argIndex = 2;
    _statement.bindString(_argIndex, endDate);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"daily_stats"}, new Callable<List<DailyStatEntity>>() {
      @Override
      @NonNull
      public List<DailyStatEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfProductId = CursorUtil.getColumnIndexOrThrow(_cursor, "productId");
          final int _cursorIndexOfIsoDate = CursorUtil.getColumnIndexOrThrow(_cursor, "isoDate");
          final int _cursorIndexOfJalaliDate = CursorUtil.getColumnIndexOrThrow(_cursor, "jalaliDate");
          final int _cursorIndexOfOpeningStockTotal = CursorUtil.getColumnIndexOrThrow(_cursor, "openingStockTotal");
          final int _cursorIndexOfMorningLoadingTotal = CursorUtil.getColumnIndexOrThrow(_cursor, "morningLoadingTotal");
          final int _cursorIndexOfActualMorningStockTotal = CursorUtil.getColumnIndexOrThrow(_cursor, "actualMorningStockTotal");
          final int _cursorIndexOfReceiptTotal = CursorUtil.getColumnIndexOrThrow(_cursor, "receiptTotal");
          final int _cursorIndexOfReturnsTotal = CursorUtil.getColumnIndexOrThrow(_cursor, "returnsTotal");
          final int _cursorIndexOfGiftsTotal = CursorUtil.getColumnIndexOrThrow(_cursor, "giftsTotal");
          final int _cursorIndexOfWasteTotal = CursorUtil.getColumnIndexOrThrow(_cursor, "wasteTotal");
          final int _cursorIndexOfLaboratoryTotal = CursorUtil.getColumnIndexOrThrow(_cursor, "laboratoryTotal");
          final int _cursorIndexOfMidDayLoadingTotal = CursorUtil.getColumnIndexOrThrow(_cursor, "midDayLoadingTotal");
          final int _cursorIndexOfDayReceiptTotal = CursorUtil.getColumnIndexOrThrow(_cursor, "dayReceiptTotal");
          final int _cursorIndexOfDayReturnsTotal = CursorUtil.getColumnIndexOrThrow(_cursor, "dayReturnsTotal");
          final int _cursorIndexOfDayGiftsTotal = CursorUtil.getColumnIndexOrThrow(_cursor, "dayGiftsTotal");
          final int _cursorIndexOfDayWasteTotal = CursorUtil.getColumnIndexOrThrow(_cursor, "dayWasteTotal");
          final int _cursorIndexOfDayLaboratoryTotal = CursorUtil.getColumnIndexOrThrow(_cursor, "dayLaboratoryTotal");
          final int _cursorIndexOfDayMidDayLoadingTotal = CursorUtil.getColumnIndexOrThrow(_cursor, "dayMidDayLoadingTotal");
          final int _cursorIndexOfNightLoadingTotal = CursorUtil.getColumnIndexOrThrow(_cursor, "nightLoadingTotal");
          final int _cursorIndexOfCurrentStockTotal = CursorUtil.getColumnIndexOrThrow(_cursor, "currentStockTotal");
          final int _cursorIndexOfExpectedMorningStockTotal = CursorUtil.getColumnIndexOrThrow(_cursor, "expectedMorningStockTotal");
          final int _cursorIndexOfMorningVariance = CursorUtil.getColumnIndexOrThrow(_cursor, "morningVariance");
          final int _cursorIndexOfExpectedClosingStockTotal = CursorUtil.getColumnIndexOrThrow(_cursor, "expectedClosingStockTotal");
          final int _cursorIndexOfClosingVariance = CursorUtil.getColumnIndexOrThrow(_cursor, "closingVariance");
          final int _cursorIndexOfStartVariance = CursorUtil.getColumnIndexOrThrow(_cursor, "startVariance");
          final int _cursorIndexOfEndCumulativeVariance = CursorUtil.getColumnIndexOrThrow(_cursor, "endCumulativeVariance");
          final int _cursorIndexOfNightShiftVariance = CursorUtil.getColumnIndexOrThrow(_cursor, "nightShiftVariance");
          final int _cursorIndexOfIsFinalized = CursorUtil.getColumnIndexOrThrow(_cursor, "isFinalized");
          final int _cursorIndexOfNotes = CursorUtil.getColumnIndexOrThrow(_cursor, "notes");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updatedAt");
          final List<DailyStatEntity> _result = new ArrayList<DailyStatEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final DailyStatEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpProductId;
            _tmpProductId = _cursor.getLong(_cursorIndexOfProductId);
            final String _tmpIsoDate;
            _tmpIsoDate = _cursor.getString(_cursorIndexOfIsoDate);
            final String _tmpJalaliDate;
            _tmpJalaliDate = _cursor.getString(_cursorIndexOfJalaliDate);
            final int _tmpOpeningStockTotal;
            _tmpOpeningStockTotal = _cursor.getInt(_cursorIndexOfOpeningStockTotal);
            final int _tmpMorningLoadingTotal;
            _tmpMorningLoadingTotal = _cursor.getInt(_cursorIndexOfMorningLoadingTotal);
            final Integer _tmpActualMorningStockTotal;
            if (_cursor.isNull(_cursorIndexOfActualMorningStockTotal)) {
              _tmpActualMorningStockTotal = null;
            } else {
              _tmpActualMorningStockTotal = _cursor.getInt(_cursorIndexOfActualMorningStockTotal);
            }
            final int _tmpReceiptTotal;
            _tmpReceiptTotal = _cursor.getInt(_cursorIndexOfReceiptTotal);
            final int _tmpReturnsTotal;
            _tmpReturnsTotal = _cursor.getInt(_cursorIndexOfReturnsTotal);
            final int _tmpGiftsTotal;
            _tmpGiftsTotal = _cursor.getInt(_cursorIndexOfGiftsTotal);
            final int _tmpWasteTotal;
            _tmpWasteTotal = _cursor.getInt(_cursorIndexOfWasteTotal);
            final int _tmpLaboratoryTotal;
            _tmpLaboratoryTotal = _cursor.getInt(_cursorIndexOfLaboratoryTotal);
            final int _tmpMidDayLoadingTotal;
            _tmpMidDayLoadingTotal = _cursor.getInt(_cursorIndexOfMidDayLoadingTotal);
            final int _tmpDayReceiptTotal;
            _tmpDayReceiptTotal = _cursor.getInt(_cursorIndexOfDayReceiptTotal);
            final int _tmpDayReturnsTotal;
            _tmpDayReturnsTotal = _cursor.getInt(_cursorIndexOfDayReturnsTotal);
            final int _tmpDayGiftsTotal;
            _tmpDayGiftsTotal = _cursor.getInt(_cursorIndexOfDayGiftsTotal);
            final int _tmpDayWasteTotal;
            _tmpDayWasteTotal = _cursor.getInt(_cursorIndexOfDayWasteTotal);
            final int _tmpDayLaboratoryTotal;
            _tmpDayLaboratoryTotal = _cursor.getInt(_cursorIndexOfDayLaboratoryTotal);
            final int _tmpDayMidDayLoadingTotal;
            _tmpDayMidDayLoadingTotal = _cursor.getInt(_cursorIndexOfDayMidDayLoadingTotal);
            final int _tmpNightLoadingTotal;
            _tmpNightLoadingTotal = _cursor.getInt(_cursorIndexOfNightLoadingTotal);
            final Integer _tmpCurrentStockTotal;
            if (_cursor.isNull(_cursorIndexOfCurrentStockTotal)) {
              _tmpCurrentStockTotal = null;
            } else {
              _tmpCurrentStockTotal = _cursor.getInt(_cursorIndexOfCurrentStockTotal);
            }
            final int _tmpExpectedMorningStockTotal;
            _tmpExpectedMorningStockTotal = _cursor.getInt(_cursorIndexOfExpectedMorningStockTotal);
            final Integer _tmpMorningVariance;
            if (_cursor.isNull(_cursorIndexOfMorningVariance)) {
              _tmpMorningVariance = null;
            } else {
              _tmpMorningVariance = _cursor.getInt(_cursorIndexOfMorningVariance);
            }
            final int _tmpExpectedClosingStockTotal;
            _tmpExpectedClosingStockTotal = _cursor.getInt(_cursorIndexOfExpectedClosingStockTotal);
            final int _tmpClosingVariance;
            _tmpClosingVariance = _cursor.getInt(_cursorIndexOfClosingVariance);
            final Integer _tmpStartVariance;
            if (_cursor.isNull(_cursorIndexOfStartVariance)) {
              _tmpStartVariance = null;
            } else {
              _tmpStartVariance = _cursor.getInt(_cursorIndexOfStartVariance);
            }
            final Integer _tmpEndCumulativeVariance;
            if (_cursor.isNull(_cursorIndexOfEndCumulativeVariance)) {
              _tmpEndCumulativeVariance = null;
            } else {
              _tmpEndCumulativeVariance = _cursor.getInt(_cursorIndexOfEndCumulativeVariance);
            }
            final Integer _tmpNightShiftVariance;
            if (_cursor.isNull(_cursorIndexOfNightShiftVariance)) {
              _tmpNightShiftVariance = null;
            } else {
              _tmpNightShiftVariance = _cursor.getInt(_cursorIndexOfNightShiftVariance);
            }
            final boolean _tmpIsFinalized;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsFinalized);
            _tmpIsFinalized = _tmp != 0;
            final String _tmpNotes;
            _tmpNotes = _cursor.getString(_cursorIndexOfNotes);
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final long _tmpUpdatedAt;
            _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
            _item = new DailyStatEntity(_tmpId,_tmpProductId,_tmpIsoDate,_tmpJalaliDate,_tmpOpeningStockTotal,_tmpMorningLoadingTotal,_tmpActualMorningStockTotal,_tmpReceiptTotal,_tmpReturnsTotal,_tmpGiftsTotal,_tmpWasteTotal,_tmpLaboratoryTotal,_tmpMidDayLoadingTotal,_tmpDayReceiptTotal,_tmpDayReturnsTotal,_tmpDayGiftsTotal,_tmpDayWasteTotal,_tmpDayLaboratoryTotal,_tmpDayMidDayLoadingTotal,_tmpNightLoadingTotal,_tmpCurrentStockTotal,_tmpExpectedMorningStockTotal,_tmpMorningVariance,_tmpExpectedClosingStockTotal,_tmpClosingVariance,_tmpStartVariance,_tmpEndCumulativeVariance,_tmpNightShiftVariance,_tmpIsFinalized,_tmpNotes,_tmpCreatedAt,_tmpUpdatedAt);
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
  public Flow<List<DailyStatEntity>> observeProductHistory(final long productId) {
    final String _sql = "\n"
            + "        SELECT * FROM daily_stats\n"
            + "        WHERE productId = ?\n"
            + "        ORDER BY isoDate DESC\n"
            + "    ";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, productId);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"daily_stats"}, new Callable<List<DailyStatEntity>>() {
      @Override
      @NonNull
      public List<DailyStatEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfProductId = CursorUtil.getColumnIndexOrThrow(_cursor, "productId");
          final int _cursorIndexOfIsoDate = CursorUtil.getColumnIndexOrThrow(_cursor, "isoDate");
          final int _cursorIndexOfJalaliDate = CursorUtil.getColumnIndexOrThrow(_cursor, "jalaliDate");
          final int _cursorIndexOfOpeningStockTotal = CursorUtil.getColumnIndexOrThrow(_cursor, "openingStockTotal");
          final int _cursorIndexOfMorningLoadingTotal = CursorUtil.getColumnIndexOrThrow(_cursor, "morningLoadingTotal");
          final int _cursorIndexOfActualMorningStockTotal = CursorUtil.getColumnIndexOrThrow(_cursor, "actualMorningStockTotal");
          final int _cursorIndexOfReceiptTotal = CursorUtil.getColumnIndexOrThrow(_cursor, "receiptTotal");
          final int _cursorIndexOfReturnsTotal = CursorUtil.getColumnIndexOrThrow(_cursor, "returnsTotal");
          final int _cursorIndexOfGiftsTotal = CursorUtil.getColumnIndexOrThrow(_cursor, "giftsTotal");
          final int _cursorIndexOfWasteTotal = CursorUtil.getColumnIndexOrThrow(_cursor, "wasteTotal");
          final int _cursorIndexOfLaboratoryTotal = CursorUtil.getColumnIndexOrThrow(_cursor, "laboratoryTotal");
          final int _cursorIndexOfMidDayLoadingTotal = CursorUtil.getColumnIndexOrThrow(_cursor, "midDayLoadingTotal");
          final int _cursorIndexOfDayReceiptTotal = CursorUtil.getColumnIndexOrThrow(_cursor, "dayReceiptTotal");
          final int _cursorIndexOfDayReturnsTotal = CursorUtil.getColumnIndexOrThrow(_cursor, "dayReturnsTotal");
          final int _cursorIndexOfDayGiftsTotal = CursorUtil.getColumnIndexOrThrow(_cursor, "dayGiftsTotal");
          final int _cursorIndexOfDayWasteTotal = CursorUtil.getColumnIndexOrThrow(_cursor, "dayWasteTotal");
          final int _cursorIndexOfDayLaboratoryTotal = CursorUtil.getColumnIndexOrThrow(_cursor, "dayLaboratoryTotal");
          final int _cursorIndexOfDayMidDayLoadingTotal = CursorUtil.getColumnIndexOrThrow(_cursor, "dayMidDayLoadingTotal");
          final int _cursorIndexOfNightLoadingTotal = CursorUtil.getColumnIndexOrThrow(_cursor, "nightLoadingTotal");
          final int _cursorIndexOfCurrentStockTotal = CursorUtil.getColumnIndexOrThrow(_cursor, "currentStockTotal");
          final int _cursorIndexOfExpectedMorningStockTotal = CursorUtil.getColumnIndexOrThrow(_cursor, "expectedMorningStockTotal");
          final int _cursorIndexOfMorningVariance = CursorUtil.getColumnIndexOrThrow(_cursor, "morningVariance");
          final int _cursorIndexOfExpectedClosingStockTotal = CursorUtil.getColumnIndexOrThrow(_cursor, "expectedClosingStockTotal");
          final int _cursorIndexOfClosingVariance = CursorUtil.getColumnIndexOrThrow(_cursor, "closingVariance");
          final int _cursorIndexOfStartVariance = CursorUtil.getColumnIndexOrThrow(_cursor, "startVariance");
          final int _cursorIndexOfEndCumulativeVariance = CursorUtil.getColumnIndexOrThrow(_cursor, "endCumulativeVariance");
          final int _cursorIndexOfNightShiftVariance = CursorUtil.getColumnIndexOrThrow(_cursor, "nightShiftVariance");
          final int _cursorIndexOfIsFinalized = CursorUtil.getColumnIndexOrThrow(_cursor, "isFinalized");
          final int _cursorIndexOfNotes = CursorUtil.getColumnIndexOrThrow(_cursor, "notes");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updatedAt");
          final List<DailyStatEntity> _result = new ArrayList<DailyStatEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final DailyStatEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpProductId;
            _tmpProductId = _cursor.getLong(_cursorIndexOfProductId);
            final String _tmpIsoDate;
            _tmpIsoDate = _cursor.getString(_cursorIndexOfIsoDate);
            final String _tmpJalaliDate;
            _tmpJalaliDate = _cursor.getString(_cursorIndexOfJalaliDate);
            final int _tmpOpeningStockTotal;
            _tmpOpeningStockTotal = _cursor.getInt(_cursorIndexOfOpeningStockTotal);
            final int _tmpMorningLoadingTotal;
            _tmpMorningLoadingTotal = _cursor.getInt(_cursorIndexOfMorningLoadingTotal);
            final Integer _tmpActualMorningStockTotal;
            if (_cursor.isNull(_cursorIndexOfActualMorningStockTotal)) {
              _tmpActualMorningStockTotal = null;
            } else {
              _tmpActualMorningStockTotal = _cursor.getInt(_cursorIndexOfActualMorningStockTotal);
            }
            final int _tmpReceiptTotal;
            _tmpReceiptTotal = _cursor.getInt(_cursorIndexOfReceiptTotal);
            final int _tmpReturnsTotal;
            _tmpReturnsTotal = _cursor.getInt(_cursorIndexOfReturnsTotal);
            final int _tmpGiftsTotal;
            _tmpGiftsTotal = _cursor.getInt(_cursorIndexOfGiftsTotal);
            final int _tmpWasteTotal;
            _tmpWasteTotal = _cursor.getInt(_cursorIndexOfWasteTotal);
            final int _tmpLaboratoryTotal;
            _tmpLaboratoryTotal = _cursor.getInt(_cursorIndexOfLaboratoryTotal);
            final int _tmpMidDayLoadingTotal;
            _tmpMidDayLoadingTotal = _cursor.getInt(_cursorIndexOfMidDayLoadingTotal);
            final int _tmpDayReceiptTotal;
            _tmpDayReceiptTotal = _cursor.getInt(_cursorIndexOfDayReceiptTotal);
            final int _tmpDayReturnsTotal;
            _tmpDayReturnsTotal = _cursor.getInt(_cursorIndexOfDayReturnsTotal);
            final int _tmpDayGiftsTotal;
            _tmpDayGiftsTotal = _cursor.getInt(_cursorIndexOfDayGiftsTotal);
            final int _tmpDayWasteTotal;
            _tmpDayWasteTotal = _cursor.getInt(_cursorIndexOfDayWasteTotal);
            final int _tmpDayLaboratoryTotal;
            _tmpDayLaboratoryTotal = _cursor.getInt(_cursorIndexOfDayLaboratoryTotal);
            final int _tmpDayMidDayLoadingTotal;
            _tmpDayMidDayLoadingTotal = _cursor.getInt(_cursorIndexOfDayMidDayLoadingTotal);
            final int _tmpNightLoadingTotal;
            _tmpNightLoadingTotal = _cursor.getInt(_cursorIndexOfNightLoadingTotal);
            final Integer _tmpCurrentStockTotal;
            if (_cursor.isNull(_cursorIndexOfCurrentStockTotal)) {
              _tmpCurrentStockTotal = null;
            } else {
              _tmpCurrentStockTotal = _cursor.getInt(_cursorIndexOfCurrentStockTotal);
            }
            final int _tmpExpectedMorningStockTotal;
            _tmpExpectedMorningStockTotal = _cursor.getInt(_cursorIndexOfExpectedMorningStockTotal);
            final Integer _tmpMorningVariance;
            if (_cursor.isNull(_cursorIndexOfMorningVariance)) {
              _tmpMorningVariance = null;
            } else {
              _tmpMorningVariance = _cursor.getInt(_cursorIndexOfMorningVariance);
            }
            final int _tmpExpectedClosingStockTotal;
            _tmpExpectedClosingStockTotal = _cursor.getInt(_cursorIndexOfExpectedClosingStockTotal);
            final int _tmpClosingVariance;
            _tmpClosingVariance = _cursor.getInt(_cursorIndexOfClosingVariance);
            final Integer _tmpStartVariance;
            if (_cursor.isNull(_cursorIndexOfStartVariance)) {
              _tmpStartVariance = null;
            } else {
              _tmpStartVariance = _cursor.getInt(_cursorIndexOfStartVariance);
            }
            final Integer _tmpEndCumulativeVariance;
            if (_cursor.isNull(_cursorIndexOfEndCumulativeVariance)) {
              _tmpEndCumulativeVariance = null;
            } else {
              _tmpEndCumulativeVariance = _cursor.getInt(_cursorIndexOfEndCumulativeVariance);
            }
            final Integer _tmpNightShiftVariance;
            if (_cursor.isNull(_cursorIndexOfNightShiftVariance)) {
              _tmpNightShiftVariance = null;
            } else {
              _tmpNightShiftVariance = _cursor.getInt(_cursorIndexOfNightShiftVariance);
            }
            final boolean _tmpIsFinalized;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsFinalized);
            _tmpIsFinalized = _tmp != 0;
            final String _tmpNotes;
            _tmpNotes = _cursor.getString(_cursorIndexOfNotes);
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final long _tmpUpdatedAt;
            _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
            _item = new DailyStatEntity(_tmpId,_tmpProductId,_tmpIsoDate,_tmpJalaliDate,_tmpOpeningStockTotal,_tmpMorningLoadingTotal,_tmpActualMorningStockTotal,_tmpReceiptTotal,_tmpReturnsTotal,_tmpGiftsTotal,_tmpWasteTotal,_tmpLaboratoryTotal,_tmpMidDayLoadingTotal,_tmpDayReceiptTotal,_tmpDayReturnsTotal,_tmpDayGiftsTotal,_tmpDayWasteTotal,_tmpDayLaboratoryTotal,_tmpDayMidDayLoadingTotal,_tmpNightLoadingTotal,_tmpCurrentStockTotal,_tmpExpectedMorningStockTotal,_tmpMorningVariance,_tmpExpectedClosingStockTotal,_tmpClosingVariance,_tmpStartVariance,_tmpEndCumulativeVariance,_tmpNightShiftVariance,_tmpIsFinalized,_tmpNotes,_tmpCreatedAt,_tmpUpdatedAt);
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
  public Flow<List<DailyStatEntity>> observeZeroVarianceStats(final String isoDate) {
    final String _sql = "SELECT * FROM daily_stats WHERE isoDate = ? AND currentStockTotal IS NOT NULL AND closingVariance = 0";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, isoDate);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"daily_stats"}, new Callable<List<DailyStatEntity>>() {
      @Override
      @NonNull
      public List<DailyStatEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfProductId = CursorUtil.getColumnIndexOrThrow(_cursor, "productId");
          final int _cursorIndexOfIsoDate = CursorUtil.getColumnIndexOrThrow(_cursor, "isoDate");
          final int _cursorIndexOfJalaliDate = CursorUtil.getColumnIndexOrThrow(_cursor, "jalaliDate");
          final int _cursorIndexOfOpeningStockTotal = CursorUtil.getColumnIndexOrThrow(_cursor, "openingStockTotal");
          final int _cursorIndexOfMorningLoadingTotal = CursorUtil.getColumnIndexOrThrow(_cursor, "morningLoadingTotal");
          final int _cursorIndexOfActualMorningStockTotal = CursorUtil.getColumnIndexOrThrow(_cursor, "actualMorningStockTotal");
          final int _cursorIndexOfReceiptTotal = CursorUtil.getColumnIndexOrThrow(_cursor, "receiptTotal");
          final int _cursorIndexOfReturnsTotal = CursorUtil.getColumnIndexOrThrow(_cursor, "returnsTotal");
          final int _cursorIndexOfGiftsTotal = CursorUtil.getColumnIndexOrThrow(_cursor, "giftsTotal");
          final int _cursorIndexOfWasteTotal = CursorUtil.getColumnIndexOrThrow(_cursor, "wasteTotal");
          final int _cursorIndexOfLaboratoryTotal = CursorUtil.getColumnIndexOrThrow(_cursor, "laboratoryTotal");
          final int _cursorIndexOfMidDayLoadingTotal = CursorUtil.getColumnIndexOrThrow(_cursor, "midDayLoadingTotal");
          final int _cursorIndexOfDayReceiptTotal = CursorUtil.getColumnIndexOrThrow(_cursor, "dayReceiptTotal");
          final int _cursorIndexOfDayReturnsTotal = CursorUtil.getColumnIndexOrThrow(_cursor, "dayReturnsTotal");
          final int _cursorIndexOfDayGiftsTotal = CursorUtil.getColumnIndexOrThrow(_cursor, "dayGiftsTotal");
          final int _cursorIndexOfDayWasteTotal = CursorUtil.getColumnIndexOrThrow(_cursor, "dayWasteTotal");
          final int _cursorIndexOfDayLaboratoryTotal = CursorUtil.getColumnIndexOrThrow(_cursor, "dayLaboratoryTotal");
          final int _cursorIndexOfDayMidDayLoadingTotal = CursorUtil.getColumnIndexOrThrow(_cursor, "dayMidDayLoadingTotal");
          final int _cursorIndexOfNightLoadingTotal = CursorUtil.getColumnIndexOrThrow(_cursor, "nightLoadingTotal");
          final int _cursorIndexOfCurrentStockTotal = CursorUtil.getColumnIndexOrThrow(_cursor, "currentStockTotal");
          final int _cursorIndexOfExpectedMorningStockTotal = CursorUtil.getColumnIndexOrThrow(_cursor, "expectedMorningStockTotal");
          final int _cursorIndexOfMorningVariance = CursorUtil.getColumnIndexOrThrow(_cursor, "morningVariance");
          final int _cursorIndexOfExpectedClosingStockTotal = CursorUtil.getColumnIndexOrThrow(_cursor, "expectedClosingStockTotal");
          final int _cursorIndexOfClosingVariance = CursorUtil.getColumnIndexOrThrow(_cursor, "closingVariance");
          final int _cursorIndexOfStartVariance = CursorUtil.getColumnIndexOrThrow(_cursor, "startVariance");
          final int _cursorIndexOfEndCumulativeVariance = CursorUtil.getColumnIndexOrThrow(_cursor, "endCumulativeVariance");
          final int _cursorIndexOfNightShiftVariance = CursorUtil.getColumnIndexOrThrow(_cursor, "nightShiftVariance");
          final int _cursorIndexOfIsFinalized = CursorUtil.getColumnIndexOrThrow(_cursor, "isFinalized");
          final int _cursorIndexOfNotes = CursorUtil.getColumnIndexOrThrow(_cursor, "notes");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updatedAt");
          final List<DailyStatEntity> _result = new ArrayList<DailyStatEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final DailyStatEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpProductId;
            _tmpProductId = _cursor.getLong(_cursorIndexOfProductId);
            final String _tmpIsoDate;
            _tmpIsoDate = _cursor.getString(_cursorIndexOfIsoDate);
            final String _tmpJalaliDate;
            _tmpJalaliDate = _cursor.getString(_cursorIndexOfJalaliDate);
            final int _tmpOpeningStockTotal;
            _tmpOpeningStockTotal = _cursor.getInt(_cursorIndexOfOpeningStockTotal);
            final int _tmpMorningLoadingTotal;
            _tmpMorningLoadingTotal = _cursor.getInt(_cursorIndexOfMorningLoadingTotal);
            final Integer _tmpActualMorningStockTotal;
            if (_cursor.isNull(_cursorIndexOfActualMorningStockTotal)) {
              _tmpActualMorningStockTotal = null;
            } else {
              _tmpActualMorningStockTotal = _cursor.getInt(_cursorIndexOfActualMorningStockTotal);
            }
            final int _tmpReceiptTotal;
            _tmpReceiptTotal = _cursor.getInt(_cursorIndexOfReceiptTotal);
            final int _tmpReturnsTotal;
            _tmpReturnsTotal = _cursor.getInt(_cursorIndexOfReturnsTotal);
            final int _tmpGiftsTotal;
            _tmpGiftsTotal = _cursor.getInt(_cursorIndexOfGiftsTotal);
            final int _tmpWasteTotal;
            _tmpWasteTotal = _cursor.getInt(_cursorIndexOfWasteTotal);
            final int _tmpLaboratoryTotal;
            _tmpLaboratoryTotal = _cursor.getInt(_cursorIndexOfLaboratoryTotal);
            final int _tmpMidDayLoadingTotal;
            _tmpMidDayLoadingTotal = _cursor.getInt(_cursorIndexOfMidDayLoadingTotal);
            final int _tmpDayReceiptTotal;
            _tmpDayReceiptTotal = _cursor.getInt(_cursorIndexOfDayReceiptTotal);
            final int _tmpDayReturnsTotal;
            _tmpDayReturnsTotal = _cursor.getInt(_cursorIndexOfDayReturnsTotal);
            final int _tmpDayGiftsTotal;
            _tmpDayGiftsTotal = _cursor.getInt(_cursorIndexOfDayGiftsTotal);
            final int _tmpDayWasteTotal;
            _tmpDayWasteTotal = _cursor.getInt(_cursorIndexOfDayWasteTotal);
            final int _tmpDayLaboratoryTotal;
            _tmpDayLaboratoryTotal = _cursor.getInt(_cursorIndexOfDayLaboratoryTotal);
            final int _tmpDayMidDayLoadingTotal;
            _tmpDayMidDayLoadingTotal = _cursor.getInt(_cursorIndexOfDayMidDayLoadingTotal);
            final int _tmpNightLoadingTotal;
            _tmpNightLoadingTotal = _cursor.getInt(_cursorIndexOfNightLoadingTotal);
            final Integer _tmpCurrentStockTotal;
            if (_cursor.isNull(_cursorIndexOfCurrentStockTotal)) {
              _tmpCurrentStockTotal = null;
            } else {
              _tmpCurrentStockTotal = _cursor.getInt(_cursorIndexOfCurrentStockTotal);
            }
            final int _tmpExpectedMorningStockTotal;
            _tmpExpectedMorningStockTotal = _cursor.getInt(_cursorIndexOfExpectedMorningStockTotal);
            final Integer _tmpMorningVariance;
            if (_cursor.isNull(_cursorIndexOfMorningVariance)) {
              _tmpMorningVariance = null;
            } else {
              _tmpMorningVariance = _cursor.getInt(_cursorIndexOfMorningVariance);
            }
            final int _tmpExpectedClosingStockTotal;
            _tmpExpectedClosingStockTotal = _cursor.getInt(_cursorIndexOfExpectedClosingStockTotal);
            final int _tmpClosingVariance;
            _tmpClosingVariance = _cursor.getInt(_cursorIndexOfClosingVariance);
            final Integer _tmpStartVariance;
            if (_cursor.isNull(_cursorIndexOfStartVariance)) {
              _tmpStartVariance = null;
            } else {
              _tmpStartVariance = _cursor.getInt(_cursorIndexOfStartVariance);
            }
            final Integer _tmpEndCumulativeVariance;
            if (_cursor.isNull(_cursorIndexOfEndCumulativeVariance)) {
              _tmpEndCumulativeVariance = null;
            } else {
              _tmpEndCumulativeVariance = _cursor.getInt(_cursorIndexOfEndCumulativeVariance);
            }
            final Integer _tmpNightShiftVariance;
            if (_cursor.isNull(_cursorIndexOfNightShiftVariance)) {
              _tmpNightShiftVariance = null;
            } else {
              _tmpNightShiftVariance = _cursor.getInt(_cursorIndexOfNightShiftVariance);
            }
            final boolean _tmpIsFinalized;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsFinalized);
            _tmpIsFinalized = _tmp != 0;
            final String _tmpNotes;
            _tmpNotes = _cursor.getString(_cursorIndexOfNotes);
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final long _tmpUpdatedAt;
            _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
            _item = new DailyStatEntity(_tmpId,_tmpProductId,_tmpIsoDate,_tmpJalaliDate,_tmpOpeningStockTotal,_tmpMorningLoadingTotal,_tmpActualMorningStockTotal,_tmpReceiptTotal,_tmpReturnsTotal,_tmpGiftsTotal,_tmpWasteTotal,_tmpLaboratoryTotal,_tmpMidDayLoadingTotal,_tmpDayReceiptTotal,_tmpDayReturnsTotal,_tmpDayGiftsTotal,_tmpDayWasteTotal,_tmpDayLaboratoryTotal,_tmpDayMidDayLoadingTotal,_tmpNightLoadingTotal,_tmpCurrentStockTotal,_tmpExpectedMorningStockTotal,_tmpMorningVariance,_tmpExpectedClosingStockTotal,_tmpClosingVariance,_tmpStartVariance,_tmpEndCumulativeVariance,_tmpNightShiftVariance,_tmpIsFinalized,_tmpNotes,_tmpCreatedAt,_tmpUpdatedAt);
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
  public Flow<List<DailyStatEntity>> observeDeficitStats(final String isoDate) {
    final String _sql = "SELECT * FROM daily_stats WHERE isoDate = ? AND currentStockTotal IS NOT NULL AND closingVariance > 0";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, isoDate);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"daily_stats"}, new Callable<List<DailyStatEntity>>() {
      @Override
      @NonNull
      public List<DailyStatEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfProductId = CursorUtil.getColumnIndexOrThrow(_cursor, "productId");
          final int _cursorIndexOfIsoDate = CursorUtil.getColumnIndexOrThrow(_cursor, "isoDate");
          final int _cursorIndexOfJalaliDate = CursorUtil.getColumnIndexOrThrow(_cursor, "jalaliDate");
          final int _cursorIndexOfOpeningStockTotal = CursorUtil.getColumnIndexOrThrow(_cursor, "openingStockTotal");
          final int _cursorIndexOfMorningLoadingTotal = CursorUtil.getColumnIndexOrThrow(_cursor, "morningLoadingTotal");
          final int _cursorIndexOfActualMorningStockTotal = CursorUtil.getColumnIndexOrThrow(_cursor, "actualMorningStockTotal");
          final int _cursorIndexOfReceiptTotal = CursorUtil.getColumnIndexOrThrow(_cursor, "receiptTotal");
          final int _cursorIndexOfReturnsTotal = CursorUtil.getColumnIndexOrThrow(_cursor, "returnsTotal");
          final int _cursorIndexOfGiftsTotal = CursorUtil.getColumnIndexOrThrow(_cursor, "giftsTotal");
          final int _cursorIndexOfWasteTotal = CursorUtil.getColumnIndexOrThrow(_cursor, "wasteTotal");
          final int _cursorIndexOfLaboratoryTotal = CursorUtil.getColumnIndexOrThrow(_cursor, "laboratoryTotal");
          final int _cursorIndexOfMidDayLoadingTotal = CursorUtil.getColumnIndexOrThrow(_cursor, "midDayLoadingTotal");
          final int _cursorIndexOfDayReceiptTotal = CursorUtil.getColumnIndexOrThrow(_cursor, "dayReceiptTotal");
          final int _cursorIndexOfDayReturnsTotal = CursorUtil.getColumnIndexOrThrow(_cursor, "dayReturnsTotal");
          final int _cursorIndexOfDayGiftsTotal = CursorUtil.getColumnIndexOrThrow(_cursor, "dayGiftsTotal");
          final int _cursorIndexOfDayWasteTotal = CursorUtil.getColumnIndexOrThrow(_cursor, "dayWasteTotal");
          final int _cursorIndexOfDayLaboratoryTotal = CursorUtil.getColumnIndexOrThrow(_cursor, "dayLaboratoryTotal");
          final int _cursorIndexOfDayMidDayLoadingTotal = CursorUtil.getColumnIndexOrThrow(_cursor, "dayMidDayLoadingTotal");
          final int _cursorIndexOfNightLoadingTotal = CursorUtil.getColumnIndexOrThrow(_cursor, "nightLoadingTotal");
          final int _cursorIndexOfCurrentStockTotal = CursorUtil.getColumnIndexOrThrow(_cursor, "currentStockTotal");
          final int _cursorIndexOfExpectedMorningStockTotal = CursorUtil.getColumnIndexOrThrow(_cursor, "expectedMorningStockTotal");
          final int _cursorIndexOfMorningVariance = CursorUtil.getColumnIndexOrThrow(_cursor, "morningVariance");
          final int _cursorIndexOfExpectedClosingStockTotal = CursorUtil.getColumnIndexOrThrow(_cursor, "expectedClosingStockTotal");
          final int _cursorIndexOfClosingVariance = CursorUtil.getColumnIndexOrThrow(_cursor, "closingVariance");
          final int _cursorIndexOfStartVariance = CursorUtil.getColumnIndexOrThrow(_cursor, "startVariance");
          final int _cursorIndexOfEndCumulativeVariance = CursorUtil.getColumnIndexOrThrow(_cursor, "endCumulativeVariance");
          final int _cursorIndexOfNightShiftVariance = CursorUtil.getColumnIndexOrThrow(_cursor, "nightShiftVariance");
          final int _cursorIndexOfIsFinalized = CursorUtil.getColumnIndexOrThrow(_cursor, "isFinalized");
          final int _cursorIndexOfNotes = CursorUtil.getColumnIndexOrThrow(_cursor, "notes");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updatedAt");
          final List<DailyStatEntity> _result = new ArrayList<DailyStatEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final DailyStatEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpProductId;
            _tmpProductId = _cursor.getLong(_cursorIndexOfProductId);
            final String _tmpIsoDate;
            _tmpIsoDate = _cursor.getString(_cursorIndexOfIsoDate);
            final String _tmpJalaliDate;
            _tmpJalaliDate = _cursor.getString(_cursorIndexOfJalaliDate);
            final int _tmpOpeningStockTotal;
            _tmpOpeningStockTotal = _cursor.getInt(_cursorIndexOfOpeningStockTotal);
            final int _tmpMorningLoadingTotal;
            _tmpMorningLoadingTotal = _cursor.getInt(_cursorIndexOfMorningLoadingTotal);
            final Integer _tmpActualMorningStockTotal;
            if (_cursor.isNull(_cursorIndexOfActualMorningStockTotal)) {
              _tmpActualMorningStockTotal = null;
            } else {
              _tmpActualMorningStockTotal = _cursor.getInt(_cursorIndexOfActualMorningStockTotal);
            }
            final int _tmpReceiptTotal;
            _tmpReceiptTotal = _cursor.getInt(_cursorIndexOfReceiptTotal);
            final int _tmpReturnsTotal;
            _tmpReturnsTotal = _cursor.getInt(_cursorIndexOfReturnsTotal);
            final int _tmpGiftsTotal;
            _tmpGiftsTotal = _cursor.getInt(_cursorIndexOfGiftsTotal);
            final int _tmpWasteTotal;
            _tmpWasteTotal = _cursor.getInt(_cursorIndexOfWasteTotal);
            final int _tmpLaboratoryTotal;
            _tmpLaboratoryTotal = _cursor.getInt(_cursorIndexOfLaboratoryTotal);
            final int _tmpMidDayLoadingTotal;
            _tmpMidDayLoadingTotal = _cursor.getInt(_cursorIndexOfMidDayLoadingTotal);
            final int _tmpDayReceiptTotal;
            _tmpDayReceiptTotal = _cursor.getInt(_cursorIndexOfDayReceiptTotal);
            final int _tmpDayReturnsTotal;
            _tmpDayReturnsTotal = _cursor.getInt(_cursorIndexOfDayReturnsTotal);
            final int _tmpDayGiftsTotal;
            _tmpDayGiftsTotal = _cursor.getInt(_cursorIndexOfDayGiftsTotal);
            final int _tmpDayWasteTotal;
            _tmpDayWasteTotal = _cursor.getInt(_cursorIndexOfDayWasteTotal);
            final int _tmpDayLaboratoryTotal;
            _tmpDayLaboratoryTotal = _cursor.getInt(_cursorIndexOfDayLaboratoryTotal);
            final int _tmpDayMidDayLoadingTotal;
            _tmpDayMidDayLoadingTotal = _cursor.getInt(_cursorIndexOfDayMidDayLoadingTotal);
            final int _tmpNightLoadingTotal;
            _tmpNightLoadingTotal = _cursor.getInt(_cursorIndexOfNightLoadingTotal);
            final Integer _tmpCurrentStockTotal;
            if (_cursor.isNull(_cursorIndexOfCurrentStockTotal)) {
              _tmpCurrentStockTotal = null;
            } else {
              _tmpCurrentStockTotal = _cursor.getInt(_cursorIndexOfCurrentStockTotal);
            }
            final int _tmpExpectedMorningStockTotal;
            _tmpExpectedMorningStockTotal = _cursor.getInt(_cursorIndexOfExpectedMorningStockTotal);
            final Integer _tmpMorningVariance;
            if (_cursor.isNull(_cursorIndexOfMorningVariance)) {
              _tmpMorningVariance = null;
            } else {
              _tmpMorningVariance = _cursor.getInt(_cursorIndexOfMorningVariance);
            }
            final int _tmpExpectedClosingStockTotal;
            _tmpExpectedClosingStockTotal = _cursor.getInt(_cursorIndexOfExpectedClosingStockTotal);
            final int _tmpClosingVariance;
            _tmpClosingVariance = _cursor.getInt(_cursorIndexOfClosingVariance);
            final Integer _tmpStartVariance;
            if (_cursor.isNull(_cursorIndexOfStartVariance)) {
              _tmpStartVariance = null;
            } else {
              _tmpStartVariance = _cursor.getInt(_cursorIndexOfStartVariance);
            }
            final Integer _tmpEndCumulativeVariance;
            if (_cursor.isNull(_cursorIndexOfEndCumulativeVariance)) {
              _tmpEndCumulativeVariance = null;
            } else {
              _tmpEndCumulativeVariance = _cursor.getInt(_cursorIndexOfEndCumulativeVariance);
            }
            final Integer _tmpNightShiftVariance;
            if (_cursor.isNull(_cursorIndexOfNightShiftVariance)) {
              _tmpNightShiftVariance = null;
            } else {
              _tmpNightShiftVariance = _cursor.getInt(_cursorIndexOfNightShiftVariance);
            }
            final boolean _tmpIsFinalized;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsFinalized);
            _tmpIsFinalized = _tmp != 0;
            final String _tmpNotes;
            _tmpNotes = _cursor.getString(_cursorIndexOfNotes);
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final long _tmpUpdatedAt;
            _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
            _item = new DailyStatEntity(_tmpId,_tmpProductId,_tmpIsoDate,_tmpJalaliDate,_tmpOpeningStockTotal,_tmpMorningLoadingTotal,_tmpActualMorningStockTotal,_tmpReceiptTotal,_tmpReturnsTotal,_tmpGiftsTotal,_tmpWasteTotal,_tmpLaboratoryTotal,_tmpMidDayLoadingTotal,_tmpDayReceiptTotal,_tmpDayReturnsTotal,_tmpDayGiftsTotal,_tmpDayWasteTotal,_tmpDayLaboratoryTotal,_tmpDayMidDayLoadingTotal,_tmpNightLoadingTotal,_tmpCurrentStockTotal,_tmpExpectedMorningStockTotal,_tmpMorningVariance,_tmpExpectedClosingStockTotal,_tmpClosingVariance,_tmpStartVariance,_tmpEndCumulativeVariance,_tmpNightShiftVariance,_tmpIsFinalized,_tmpNotes,_tmpCreatedAt,_tmpUpdatedAt);
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
  public Flow<List<DailyStatEntity>> observeSurplusStats(final String isoDate) {
    final String _sql = "SELECT * FROM daily_stats WHERE isoDate = ? AND currentStockTotal IS NOT NULL AND closingVariance < 0";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, isoDate);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"daily_stats"}, new Callable<List<DailyStatEntity>>() {
      @Override
      @NonNull
      public List<DailyStatEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfProductId = CursorUtil.getColumnIndexOrThrow(_cursor, "productId");
          final int _cursorIndexOfIsoDate = CursorUtil.getColumnIndexOrThrow(_cursor, "isoDate");
          final int _cursorIndexOfJalaliDate = CursorUtil.getColumnIndexOrThrow(_cursor, "jalaliDate");
          final int _cursorIndexOfOpeningStockTotal = CursorUtil.getColumnIndexOrThrow(_cursor, "openingStockTotal");
          final int _cursorIndexOfMorningLoadingTotal = CursorUtil.getColumnIndexOrThrow(_cursor, "morningLoadingTotal");
          final int _cursorIndexOfActualMorningStockTotal = CursorUtil.getColumnIndexOrThrow(_cursor, "actualMorningStockTotal");
          final int _cursorIndexOfReceiptTotal = CursorUtil.getColumnIndexOrThrow(_cursor, "receiptTotal");
          final int _cursorIndexOfReturnsTotal = CursorUtil.getColumnIndexOrThrow(_cursor, "returnsTotal");
          final int _cursorIndexOfGiftsTotal = CursorUtil.getColumnIndexOrThrow(_cursor, "giftsTotal");
          final int _cursorIndexOfWasteTotal = CursorUtil.getColumnIndexOrThrow(_cursor, "wasteTotal");
          final int _cursorIndexOfLaboratoryTotal = CursorUtil.getColumnIndexOrThrow(_cursor, "laboratoryTotal");
          final int _cursorIndexOfMidDayLoadingTotal = CursorUtil.getColumnIndexOrThrow(_cursor, "midDayLoadingTotal");
          final int _cursorIndexOfDayReceiptTotal = CursorUtil.getColumnIndexOrThrow(_cursor, "dayReceiptTotal");
          final int _cursorIndexOfDayReturnsTotal = CursorUtil.getColumnIndexOrThrow(_cursor, "dayReturnsTotal");
          final int _cursorIndexOfDayGiftsTotal = CursorUtil.getColumnIndexOrThrow(_cursor, "dayGiftsTotal");
          final int _cursorIndexOfDayWasteTotal = CursorUtil.getColumnIndexOrThrow(_cursor, "dayWasteTotal");
          final int _cursorIndexOfDayLaboratoryTotal = CursorUtil.getColumnIndexOrThrow(_cursor, "dayLaboratoryTotal");
          final int _cursorIndexOfDayMidDayLoadingTotal = CursorUtil.getColumnIndexOrThrow(_cursor, "dayMidDayLoadingTotal");
          final int _cursorIndexOfNightLoadingTotal = CursorUtil.getColumnIndexOrThrow(_cursor, "nightLoadingTotal");
          final int _cursorIndexOfCurrentStockTotal = CursorUtil.getColumnIndexOrThrow(_cursor, "currentStockTotal");
          final int _cursorIndexOfExpectedMorningStockTotal = CursorUtil.getColumnIndexOrThrow(_cursor, "expectedMorningStockTotal");
          final int _cursorIndexOfMorningVariance = CursorUtil.getColumnIndexOrThrow(_cursor, "morningVariance");
          final int _cursorIndexOfExpectedClosingStockTotal = CursorUtil.getColumnIndexOrThrow(_cursor, "expectedClosingStockTotal");
          final int _cursorIndexOfClosingVariance = CursorUtil.getColumnIndexOrThrow(_cursor, "closingVariance");
          final int _cursorIndexOfStartVariance = CursorUtil.getColumnIndexOrThrow(_cursor, "startVariance");
          final int _cursorIndexOfEndCumulativeVariance = CursorUtil.getColumnIndexOrThrow(_cursor, "endCumulativeVariance");
          final int _cursorIndexOfNightShiftVariance = CursorUtil.getColumnIndexOrThrow(_cursor, "nightShiftVariance");
          final int _cursorIndexOfIsFinalized = CursorUtil.getColumnIndexOrThrow(_cursor, "isFinalized");
          final int _cursorIndexOfNotes = CursorUtil.getColumnIndexOrThrow(_cursor, "notes");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updatedAt");
          final List<DailyStatEntity> _result = new ArrayList<DailyStatEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final DailyStatEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpProductId;
            _tmpProductId = _cursor.getLong(_cursorIndexOfProductId);
            final String _tmpIsoDate;
            _tmpIsoDate = _cursor.getString(_cursorIndexOfIsoDate);
            final String _tmpJalaliDate;
            _tmpJalaliDate = _cursor.getString(_cursorIndexOfJalaliDate);
            final int _tmpOpeningStockTotal;
            _tmpOpeningStockTotal = _cursor.getInt(_cursorIndexOfOpeningStockTotal);
            final int _tmpMorningLoadingTotal;
            _tmpMorningLoadingTotal = _cursor.getInt(_cursorIndexOfMorningLoadingTotal);
            final Integer _tmpActualMorningStockTotal;
            if (_cursor.isNull(_cursorIndexOfActualMorningStockTotal)) {
              _tmpActualMorningStockTotal = null;
            } else {
              _tmpActualMorningStockTotal = _cursor.getInt(_cursorIndexOfActualMorningStockTotal);
            }
            final int _tmpReceiptTotal;
            _tmpReceiptTotal = _cursor.getInt(_cursorIndexOfReceiptTotal);
            final int _tmpReturnsTotal;
            _tmpReturnsTotal = _cursor.getInt(_cursorIndexOfReturnsTotal);
            final int _tmpGiftsTotal;
            _tmpGiftsTotal = _cursor.getInt(_cursorIndexOfGiftsTotal);
            final int _tmpWasteTotal;
            _tmpWasteTotal = _cursor.getInt(_cursorIndexOfWasteTotal);
            final int _tmpLaboratoryTotal;
            _tmpLaboratoryTotal = _cursor.getInt(_cursorIndexOfLaboratoryTotal);
            final int _tmpMidDayLoadingTotal;
            _tmpMidDayLoadingTotal = _cursor.getInt(_cursorIndexOfMidDayLoadingTotal);
            final int _tmpDayReceiptTotal;
            _tmpDayReceiptTotal = _cursor.getInt(_cursorIndexOfDayReceiptTotal);
            final int _tmpDayReturnsTotal;
            _tmpDayReturnsTotal = _cursor.getInt(_cursorIndexOfDayReturnsTotal);
            final int _tmpDayGiftsTotal;
            _tmpDayGiftsTotal = _cursor.getInt(_cursorIndexOfDayGiftsTotal);
            final int _tmpDayWasteTotal;
            _tmpDayWasteTotal = _cursor.getInt(_cursorIndexOfDayWasteTotal);
            final int _tmpDayLaboratoryTotal;
            _tmpDayLaboratoryTotal = _cursor.getInt(_cursorIndexOfDayLaboratoryTotal);
            final int _tmpDayMidDayLoadingTotal;
            _tmpDayMidDayLoadingTotal = _cursor.getInt(_cursorIndexOfDayMidDayLoadingTotal);
            final int _tmpNightLoadingTotal;
            _tmpNightLoadingTotal = _cursor.getInt(_cursorIndexOfNightLoadingTotal);
            final Integer _tmpCurrentStockTotal;
            if (_cursor.isNull(_cursorIndexOfCurrentStockTotal)) {
              _tmpCurrentStockTotal = null;
            } else {
              _tmpCurrentStockTotal = _cursor.getInt(_cursorIndexOfCurrentStockTotal);
            }
            final int _tmpExpectedMorningStockTotal;
            _tmpExpectedMorningStockTotal = _cursor.getInt(_cursorIndexOfExpectedMorningStockTotal);
            final Integer _tmpMorningVariance;
            if (_cursor.isNull(_cursorIndexOfMorningVariance)) {
              _tmpMorningVariance = null;
            } else {
              _tmpMorningVariance = _cursor.getInt(_cursorIndexOfMorningVariance);
            }
            final int _tmpExpectedClosingStockTotal;
            _tmpExpectedClosingStockTotal = _cursor.getInt(_cursorIndexOfExpectedClosingStockTotal);
            final int _tmpClosingVariance;
            _tmpClosingVariance = _cursor.getInt(_cursorIndexOfClosingVariance);
            final Integer _tmpStartVariance;
            if (_cursor.isNull(_cursorIndexOfStartVariance)) {
              _tmpStartVariance = null;
            } else {
              _tmpStartVariance = _cursor.getInt(_cursorIndexOfStartVariance);
            }
            final Integer _tmpEndCumulativeVariance;
            if (_cursor.isNull(_cursorIndexOfEndCumulativeVariance)) {
              _tmpEndCumulativeVariance = null;
            } else {
              _tmpEndCumulativeVariance = _cursor.getInt(_cursorIndexOfEndCumulativeVariance);
            }
            final Integer _tmpNightShiftVariance;
            if (_cursor.isNull(_cursorIndexOfNightShiftVariance)) {
              _tmpNightShiftVariance = null;
            } else {
              _tmpNightShiftVariance = _cursor.getInt(_cursorIndexOfNightShiftVariance);
            }
            final boolean _tmpIsFinalized;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsFinalized);
            _tmpIsFinalized = _tmp != 0;
            final String _tmpNotes;
            _tmpNotes = _cursor.getString(_cursorIndexOfNotes);
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final long _tmpUpdatedAt;
            _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
            _item = new DailyStatEntity(_tmpId,_tmpProductId,_tmpIsoDate,_tmpJalaliDate,_tmpOpeningStockTotal,_tmpMorningLoadingTotal,_tmpActualMorningStockTotal,_tmpReceiptTotal,_tmpReturnsTotal,_tmpGiftsTotal,_tmpWasteTotal,_tmpLaboratoryTotal,_tmpMidDayLoadingTotal,_tmpDayReceiptTotal,_tmpDayReturnsTotal,_tmpDayGiftsTotal,_tmpDayWasteTotal,_tmpDayLaboratoryTotal,_tmpDayMidDayLoadingTotal,_tmpNightLoadingTotal,_tmpCurrentStockTotal,_tmpExpectedMorningStockTotal,_tmpMorningVariance,_tmpExpectedClosingStockTotal,_tmpClosingVariance,_tmpStartVariance,_tmpEndCumulativeVariance,_tmpNightShiftVariance,_tmpIsFinalized,_tmpNotes,_tmpCreatedAt,_tmpUpdatedAt);
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
  public Object countStatsForDate(final String isoDate,
      final Continuation<? super Integer> $completion) {
    final String _sql = "SELECT COUNT(*) FROM daily_stats WHERE isoDate = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, isoDate);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<Integer>() {
      @Override
      @NonNull
      public Integer call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final Integer _result;
          if (_cursor.moveToFirst()) {
            final int _tmp;
            _tmp = _cursor.getInt(0);
            _result = _tmp;
          } else {
            _result = 0;
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
  public Object countVarianceStatsForDate(final String isoDate,
      final Continuation<? super Integer> $completion) {
    final String _sql = "SELECT COUNT(*) FROM daily_stats WHERE isoDate = ? AND currentStockTotal IS NOT NULL AND closingVariance != 0";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, isoDate);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<Integer>() {
      @Override
      @NonNull
      public Integer call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final Integer _result;
          if (_cursor.moveToFirst()) {
            final int _tmp;
            _tmp = _cursor.getInt(0);
            _result = _tmp;
          } else {
            _result = 0;
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
  public Object getAll(final Continuation<? super List<DailyStatEntity>> $completion) {
    final String _sql = "SELECT * FROM daily_stats";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<DailyStatEntity>>() {
      @Override
      @NonNull
      public List<DailyStatEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfProductId = CursorUtil.getColumnIndexOrThrow(_cursor, "productId");
          final int _cursorIndexOfIsoDate = CursorUtil.getColumnIndexOrThrow(_cursor, "isoDate");
          final int _cursorIndexOfJalaliDate = CursorUtil.getColumnIndexOrThrow(_cursor, "jalaliDate");
          final int _cursorIndexOfOpeningStockTotal = CursorUtil.getColumnIndexOrThrow(_cursor, "openingStockTotal");
          final int _cursorIndexOfMorningLoadingTotal = CursorUtil.getColumnIndexOrThrow(_cursor, "morningLoadingTotal");
          final int _cursorIndexOfActualMorningStockTotal = CursorUtil.getColumnIndexOrThrow(_cursor, "actualMorningStockTotal");
          final int _cursorIndexOfReceiptTotal = CursorUtil.getColumnIndexOrThrow(_cursor, "receiptTotal");
          final int _cursorIndexOfReturnsTotal = CursorUtil.getColumnIndexOrThrow(_cursor, "returnsTotal");
          final int _cursorIndexOfGiftsTotal = CursorUtil.getColumnIndexOrThrow(_cursor, "giftsTotal");
          final int _cursorIndexOfWasteTotal = CursorUtil.getColumnIndexOrThrow(_cursor, "wasteTotal");
          final int _cursorIndexOfLaboratoryTotal = CursorUtil.getColumnIndexOrThrow(_cursor, "laboratoryTotal");
          final int _cursorIndexOfMidDayLoadingTotal = CursorUtil.getColumnIndexOrThrow(_cursor, "midDayLoadingTotal");
          final int _cursorIndexOfDayReceiptTotal = CursorUtil.getColumnIndexOrThrow(_cursor, "dayReceiptTotal");
          final int _cursorIndexOfDayReturnsTotal = CursorUtil.getColumnIndexOrThrow(_cursor, "dayReturnsTotal");
          final int _cursorIndexOfDayGiftsTotal = CursorUtil.getColumnIndexOrThrow(_cursor, "dayGiftsTotal");
          final int _cursorIndexOfDayWasteTotal = CursorUtil.getColumnIndexOrThrow(_cursor, "dayWasteTotal");
          final int _cursorIndexOfDayLaboratoryTotal = CursorUtil.getColumnIndexOrThrow(_cursor, "dayLaboratoryTotal");
          final int _cursorIndexOfDayMidDayLoadingTotal = CursorUtil.getColumnIndexOrThrow(_cursor, "dayMidDayLoadingTotal");
          final int _cursorIndexOfNightLoadingTotal = CursorUtil.getColumnIndexOrThrow(_cursor, "nightLoadingTotal");
          final int _cursorIndexOfCurrentStockTotal = CursorUtil.getColumnIndexOrThrow(_cursor, "currentStockTotal");
          final int _cursorIndexOfExpectedMorningStockTotal = CursorUtil.getColumnIndexOrThrow(_cursor, "expectedMorningStockTotal");
          final int _cursorIndexOfMorningVariance = CursorUtil.getColumnIndexOrThrow(_cursor, "morningVariance");
          final int _cursorIndexOfExpectedClosingStockTotal = CursorUtil.getColumnIndexOrThrow(_cursor, "expectedClosingStockTotal");
          final int _cursorIndexOfClosingVariance = CursorUtil.getColumnIndexOrThrow(_cursor, "closingVariance");
          final int _cursorIndexOfStartVariance = CursorUtil.getColumnIndexOrThrow(_cursor, "startVariance");
          final int _cursorIndexOfEndCumulativeVariance = CursorUtil.getColumnIndexOrThrow(_cursor, "endCumulativeVariance");
          final int _cursorIndexOfNightShiftVariance = CursorUtil.getColumnIndexOrThrow(_cursor, "nightShiftVariance");
          final int _cursorIndexOfIsFinalized = CursorUtil.getColumnIndexOrThrow(_cursor, "isFinalized");
          final int _cursorIndexOfNotes = CursorUtil.getColumnIndexOrThrow(_cursor, "notes");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updatedAt");
          final List<DailyStatEntity> _result = new ArrayList<DailyStatEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final DailyStatEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpProductId;
            _tmpProductId = _cursor.getLong(_cursorIndexOfProductId);
            final String _tmpIsoDate;
            _tmpIsoDate = _cursor.getString(_cursorIndexOfIsoDate);
            final String _tmpJalaliDate;
            _tmpJalaliDate = _cursor.getString(_cursorIndexOfJalaliDate);
            final int _tmpOpeningStockTotal;
            _tmpOpeningStockTotal = _cursor.getInt(_cursorIndexOfOpeningStockTotal);
            final int _tmpMorningLoadingTotal;
            _tmpMorningLoadingTotal = _cursor.getInt(_cursorIndexOfMorningLoadingTotal);
            final Integer _tmpActualMorningStockTotal;
            if (_cursor.isNull(_cursorIndexOfActualMorningStockTotal)) {
              _tmpActualMorningStockTotal = null;
            } else {
              _tmpActualMorningStockTotal = _cursor.getInt(_cursorIndexOfActualMorningStockTotal);
            }
            final int _tmpReceiptTotal;
            _tmpReceiptTotal = _cursor.getInt(_cursorIndexOfReceiptTotal);
            final int _tmpReturnsTotal;
            _tmpReturnsTotal = _cursor.getInt(_cursorIndexOfReturnsTotal);
            final int _tmpGiftsTotal;
            _tmpGiftsTotal = _cursor.getInt(_cursorIndexOfGiftsTotal);
            final int _tmpWasteTotal;
            _tmpWasteTotal = _cursor.getInt(_cursorIndexOfWasteTotal);
            final int _tmpLaboratoryTotal;
            _tmpLaboratoryTotal = _cursor.getInt(_cursorIndexOfLaboratoryTotal);
            final int _tmpMidDayLoadingTotal;
            _tmpMidDayLoadingTotal = _cursor.getInt(_cursorIndexOfMidDayLoadingTotal);
            final int _tmpDayReceiptTotal;
            _tmpDayReceiptTotal = _cursor.getInt(_cursorIndexOfDayReceiptTotal);
            final int _tmpDayReturnsTotal;
            _tmpDayReturnsTotal = _cursor.getInt(_cursorIndexOfDayReturnsTotal);
            final int _tmpDayGiftsTotal;
            _tmpDayGiftsTotal = _cursor.getInt(_cursorIndexOfDayGiftsTotal);
            final int _tmpDayWasteTotal;
            _tmpDayWasteTotal = _cursor.getInt(_cursorIndexOfDayWasteTotal);
            final int _tmpDayLaboratoryTotal;
            _tmpDayLaboratoryTotal = _cursor.getInt(_cursorIndexOfDayLaboratoryTotal);
            final int _tmpDayMidDayLoadingTotal;
            _tmpDayMidDayLoadingTotal = _cursor.getInt(_cursorIndexOfDayMidDayLoadingTotal);
            final int _tmpNightLoadingTotal;
            _tmpNightLoadingTotal = _cursor.getInt(_cursorIndexOfNightLoadingTotal);
            final Integer _tmpCurrentStockTotal;
            if (_cursor.isNull(_cursorIndexOfCurrentStockTotal)) {
              _tmpCurrentStockTotal = null;
            } else {
              _tmpCurrentStockTotal = _cursor.getInt(_cursorIndexOfCurrentStockTotal);
            }
            final int _tmpExpectedMorningStockTotal;
            _tmpExpectedMorningStockTotal = _cursor.getInt(_cursorIndexOfExpectedMorningStockTotal);
            final Integer _tmpMorningVariance;
            if (_cursor.isNull(_cursorIndexOfMorningVariance)) {
              _tmpMorningVariance = null;
            } else {
              _tmpMorningVariance = _cursor.getInt(_cursorIndexOfMorningVariance);
            }
            final int _tmpExpectedClosingStockTotal;
            _tmpExpectedClosingStockTotal = _cursor.getInt(_cursorIndexOfExpectedClosingStockTotal);
            final int _tmpClosingVariance;
            _tmpClosingVariance = _cursor.getInt(_cursorIndexOfClosingVariance);
            final Integer _tmpStartVariance;
            if (_cursor.isNull(_cursorIndexOfStartVariance)) {
              _tmpStartVariance = null;
            } else {
              _tmpStartVariance = _cursor.getInt(_cursorIndexOfStartVariance);
            }
            final Integer _tmpEndCumulativeVariance;
            if (_cursor.isNull(_cursorIndexOfEndCumulativeVariance)) {
              _tmpEndCumulativeVariance = null;
            } else {
              _tmpEndCumulativeVariance = _cursor.getInt(_cursorIndexOfEndCumulativeVariance);
            }
            final Integer _tmpNightShiftVariance;
            if (_cursor.isNull(_cursorIndexOfNightShiftVariance)) {
              _tmpNightShiftVariance = null;
            } else {
              _tmpNightShiftVariance = _cursor.getInt(_cursorIndexOfNightShiftVariance);
            }
            final boolean _tmpIsFinalized;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsFinalized);
            _tmpIsFinalized = _tmp != 0;
            final String _tmpNotes;
            _tmpNotes = _cursor.getString(_cursorIndexOfNotes);
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final long _tmpUpdatedAt;
            _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
            _item = new DailyStatEntity(_tmpId,_tmpProductId,_tmpIsoDate,_tmpJalaliDate,_tmpOpeningStockTotal,_tmpMorningLoadingTotal,_tmpActualMorningStockTotal,_tmpReceiptTotal,_tmpReturnsTotal,_tmpGiftsTotal,_tmpWasteTotal,_tmpLaboratoryTotal,_tmpMidDayLoadingTotal,_tmpDayReceiptTotal,_tmpDayReturnsTotal,_tmpDayGiftsTotal,_tmpDayWasteTotal,_tmpDayLaboratoryTotal,_tmpDayMidDayLoadingTotal,_tmpNightLoadingTotal,_tmpCurrentStockTotal,_tmpExpectedMorningStockTotal,_tmpMorningVariance,_tmpExpectedClosingStockTotal,_tmpClosingVariance,_tmpStartVariance,_tmpEndCumulativeVariance,_tmpNightShiftVariance,_tmpIsFinalized,_tmpNotes,_tmpCreatedAt,_tmpUpdatedAt);
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
  public Object getLatestUnfinalizedDate(final Continuation<? super String> $completion) {
    final String _sql = "\n"
            + "        SELECT isoDate FROM daily_stats\n"
            + "        WHERE isFinalized = 0\n"
            + "        ORDER BY isoDate DESC\n"
            + "        LIMIT 1\n"
            + "    ";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<String>() {
      @Override
      @Nullable
      public String call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final String _result;
          if (_cursor.moveToFirst()) {
            if (_cursor.isNull(0)) {
              _result = null;
            } else {
              _result = _cursor.getString(0);
            }
          } else {
            _result = null;
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
  public Object getLatestStatDate(final Continuation<? super String> $completion) {
    final String _sql = "\n"
            + "        SELECT isoDate FROM daily_stats\n"
            + "        ORDER BY isoDate DESC\n"
            + "        LIMIT 1\n"
            + "    ";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<String>() {
      @Override
      @Nullable
      public String call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final String _result;
          if (_cursor.moveToFirst()) {
            if (_cursor.isNull(0)) {
              _result = null;
            } else {
              _result = _cursor.getString(0);
            }
          } else {
            _result = null;
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
  public Object getLatestStatDateForProduct(final long productId,
      final Continuation<? super String> $completion) {
    final String _sql = "\n"
            + "        SELECT isoDate FROM daily_stats\n"
            + "        WHERE productId = ?\n"
            + "        ORDER BY isoDate DESC\n"
            + "        LIMIT 1\n"
            + "    ";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, productId);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<String>() {
      @Override
      @Nullable
      public String call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final String _result;
          if (_cursor.moveToFirst()) {
            if (_cursor.isNull(0)) {
              _result = null;
            } else {
              _result = _cursor.getString(0);
            }
          } else {
            _result = null;
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
  public Object getLatestUnfinalizedStatForProduct(final long productId,
      final Continuation<? super DailyStatEntity> $completion) {
    final String _sql = "\n"
            + "        SELECT * FROM daily_stats\n"
            + "        WHERE productId = ? AND isFinalized = 0\n"
            + "        ORDER BY isoDate DESC\n"
            + "        LIMIT 1\n"
            + "    ";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, productId);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<DailyStatEntity>() {
      @Override
      @Nullable
      public DailyStatEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfProductId = CursorUtil.getColumnIndexOrThrow(_cursor, "productId");
          final int _cursorIndexOfIsoDate = CursorUtil.getColumnIndexOrThrow(_cursor, "isoDate");
          final int _cursorIndexOfJalaliDate = CursorUtil.getColumnIndexOrThrow(_cursor, "jalaliDate");
          final int _cursorIndexOfOpeningStockTotal = CursorUtil.getColumnIndexOrThrow(_cursor, "openingStockTotal");
          final int _cursorIndexOfMorningLoadingTotal = CursorUtil.getColumnIndexOrThrow(_cursor, "morningLoadingTotal");
          final int _cursorIndexOfActualMorningStockTotal = CursorUtil.getColumnIndexOrThrow(_cursor, "actualMorningStockTotal");
          final int _cursorIndexOfReceiptTotal = CursorUtil.getColumnIndexOrThrow(_cursor, "receiptTotal");
          final int _cursorIndexOfReturnsTotal = CursorUtil.getColumnIndexOrThrow(_cursor, "returnsTotal");
          final int _cursorIndexOfGiftsTotal = CursorUtil.getColumnIndexOrThrow(_cursor, "giftsTotal");
          final int _cursorIndexOfWasteTotal = CursorUtil.getColumnIndexOrThrow(_cursor, "wasteTotal");
          final int _cursorIndexOfLaboratoryTotal = CursorUtil.getColumnIndexOrThrow(_cursor, "laboratoryTotal");
          final int _cursorIndexOfMidDayLoadingTotal = CursorUtil.getColumnIndexOrThrow(_cursor, "midDayLoadingTotal");
          final int _cursorIndexOfDayReceiptTotal = CursorUtil.getColumnIndexOrThrow(_cursor, "dayReceiptTotal");
          final int _cursorIndexOfDayReturnsTotal = CursorUtil.getColumnIndexOrThrow(_cursor, "dayReturnsTotal");
          final int _cursorIndexOfDayGiftsTotal = CursorUtil.getColumnIndexOrThrow(_cursor, "dayGiftsTotal");
          final int _cursorIndexOfDayWasteTotal = CursorUtil.getColumnIndexOrThrow(_cursor, "dayWasteTotal");
          final int _cursorIndexOfDayLaboratoryTotal = CursorUtil.getColumnIndexOrThrow(_cursor, "dayLaboratoryTotal");
          final int _cursorIndexOfDayMidDayLoadingTotal = CursorUtil.getColumnIndexOrThrow(_cursor, "dayMidDayLoadingTotal");
          final int _cursorIndexOfNightLoadingTotal = CursorUtil.getColumnIndexOrThrow(_cursor, "nightLoadingTotal");
          final int _cursorIndexOfCurrentStockTotal = CursorUtil.getColumnIndexOrThrow(_cursor, "currentStockTotal");
          final int _cursorIndexOfExpectedMorningStockTotal = CursorUtil.getColumnIndexOrThrow(_cursor, "expectedMorningStockTotal");
          final int _cursorIndexOfMorningVariance = CursorUtil.getColumnIndexOrThrow(_cursor, "morningVariance");
          final int _cursorIndexOfExpectedClosingStockTotal = CursorUtil.getColumnIndexOrThrow(_cursor, "expectedClosingStockTotal");
          final int _cursorIndexOfClosingVariance = CursorUtil.getColumnIndexOrThrow(_cursor, "closingVariance");
          final int _cursorIndexOfStartVariance = CursorUtil.getColumnIndexOrThrow(_cursor, "startVariance");
          final int _cursorIndexOfEndCumulativeVariance = CursorUtil.getColumnIndexOrThrow(_cursor, "endCumulativeVariance");
          final int _cursorIndexOfNightShiftVariance = CursorUtil.getColumnIndexOrThrow(_cursor, "nightShiftVariance");
          final int _cursorIndexOfIsFinalized = CursorUtil.getColumnIndexOrThrow(_cursor, "isFinalized");
          final int _cursorIndexOfNotes = CursorUtil.getColumnIndexOrThrow(_cursor, "notes");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updatedAt");
          final DailyStatEntity _result;
          if (_cursor.moveToFirst()) {
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpProductId;
            _tmpProductId = _cursor.getLong(_cursorIndexOfProductId);
            final String _tmpIsoDate;
            _tmpIsoDate = _cursor.getString(_cursorIndexOfIsoDate);
            final String _tmpJalaliDate;
            _tmpJalaliDate = _cursor.getString(_cursorIndexOfJalaliDate);
            final int _tmpOpeningStockTotal;
            _tmpOpeningStockTotal = _cursor.getInt(_cursorIndexOfOpeningStockTotal);
            final int _tmpMorningLoadingTotal;
            _tmpMorningLoadingTotal = _cursor.getInt(_cursorIndexOfMorningLoadingTotal);
            final Integer _tmpActualMorningStockTotal;
            if (_cursor.isNull(_cursorIndexOfActualMorningStockTotal)) {
              _tmpActualMorningStockTotal = null;
            } else {
              _tmpActualMorningStockTotal = _cursor.getInt(_cursorIndexOfActualMorningStockTotal);
            }
            final int _tmpReceiptTotal;
            _tmpReceiptTotal = _cursor.getInt(_cursorIndexOfReceiptTotal);
            final int _tmpReturnsTotal;
            _tmpReturnsTotal = _cursor.getInt(_cursorIndexOfReturnsTotal);
            final int _tmpGiftsTotal;
            _tmpGiftsTotal = _cursor.getInt(_cursorIndexOfGiftsTotal);
            final int _tmpWasteTotal;
            _tmpWasteTotal = _cursor.getInt(_cursorIndexOfWasteTotal);
            final int _tmpLaboratoryTotal;
            _tmpLaboratoryTotal = _cursor.getInt(_cursorIndexOfLaboratoryTotal);
            final int _tmpMidDayLoadingTotal;
            _tmpMidDayLoadingTotal = _cursor.getInt(_cursorIndexOfMidDayLoadingTotal);
            final int _tmpDayReceiptTotal;
            _tmpDayReceiptTotal = _cursor.getInt(_cursorIndexOfDayReceiptTotal);
            final int _tmpDayReturnsTotal;
            _tmpDayReturnsTotal = _cursor.getInt(_cursorIndexOfDayReturnsTotal);
            final int _tmpDayGiftsTotal;
            _tmpDayGiftsTotal = _cursor.getInt(_cursorIndexOfDayGiftsTotal);
            final int _tmpDayWasteTotal;
            _tmpDayWasteTotal = _cursor.getInt(_cursorIndexOfDayWasteTotal);
            final int _tmpDayLaboratoryTotal;
            _tmpDayLaboratoryTotal = _cursor.getInt(_cursorIndexOfDayLaboratoryTotal);
            final int _tmpDayMidDayLoadingTotal;
            _tmpDayMidDayLoadingTotal = _cursor.getInt(_cursorIndexOfDayMidDayLoadingTotal);
            final int _tmpNightLoadingTotal;
            _tmpNightLoadingTotal = _cursor.getInt(_cursorIndexOfNightLoadingTotal);
            final Integer _tmpCurrentStockTotal;
            if (_cursor.isNull(_cursorIndexOfCurrentStockTotal)) {
              _tmpCurrentStockTotal = null;
            } else {
              _tmpCurrentStockTotal = _cursor.getInt(_cursorIndexOfCurrentStockTotal);
            }
            final int _tmpExpectedMorningStockTotal;
            _tmpExpectedMorningStockTotal = _cursor.getInt(_cursorIndexOfExpectedMorningStockTotal);
            final Integer _tmpMorningVariance;
            if (_cursor.isNull(_cursorIndexOfMorningVariance)) {
              _tmpMorningVariance = null;
            } else {
              _tmpMorningVariance = _cursor.getInt(_cursorIndexOfMorningVariance);
            }
            final int _tmpExpectedClosingStockTotal;
            _tmpExpectedClosingStockTotal = _cursor.getInt(_cursorIndexOfExpectedClosingStockTotal);
            final int _tmpClosingVariance;
            _tmpClosingVariance = _cursor.getInt(_cursorIndexOfClosingVariance);
            final Integer _tmpStartVariance;
            if (_cursor.isNull(_cursorIndexOfStartVariance)) {
              _tmpStartVariance = null;
            } else {
              _tmpStartVariance = _cursor.getInt(_cursorIndexOfStartVariance);
            }
            final Integer _tmpEndCumulativeVariance;
            if (_cursor.isNull(_cursorIndexOfEndCumulativeVariance)) {
              _tmpEndCumulativeVariance = null;
            } else {
              _tmpEndCumulativeVariance = _cursor.getInt(_cursorIndexOfEndCumulativeVariance);
            }
            final Integer _tmpNightShiftVariance;
            if (_cursor.isNull(_cursorIndexOfNightShiftVariance)) {
              _tmpNightShiftVariance = null;
            } else {
              _tmpNightShiftVariance = _cursor.getInt(_cursorIndexOfNightShiftVariance);
            }
            final boolean _tmpIsFinalized;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsFinalized);
            _tmpIsFinalized = _tmp != 0;
            final String _tmpNotes;
            _tmpNotes = _cursor.getString(_cursorIndexOfNotes);
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final long _tmpUpdatedAt;
            _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
            _result = new DailyStatEntity(_tmpId,_tmpProductId,_tmpIsoDate,_tmpJalaliDate,_tmpOpeningStockTotal,_tmpMorningLoadingTotal,_tmpActualMorningStockTotal,_tmpReceiptTotal,_tmpReturnsTotal,_tmpGiftsTotal,_tmpWasteTotal,_tmpLaboratoryTotal,_tmpMidDayLoadingTotal,_tmpDayReceiptTotal,_tmpDayReturnsTotal,_tmpDayGiftsTotal,_tmpDayWasteTotal,_tmpDayLaboratoryTotal,_tmpDayMidDayLoadingTotal,_tmpNightLoadingTotal,_tmpCurrentStockTotal,_tmpExpectedMorningStockTotal,_tmpMorningVariance,_tmpExpectedClosingStockTotal,_tmpClosingVariance,_tmpStartVariance,_tmpEndCumulativeVariance,_tmpNightShiftVariance,_tmpIsFinalized,_tmpNotes,_tmpCreatedAt,_tmpUpdatedAt);
          } else {
            _result = null;
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
