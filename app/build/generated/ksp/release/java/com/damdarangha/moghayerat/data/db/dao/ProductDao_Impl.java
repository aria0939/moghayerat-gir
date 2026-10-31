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
import com.damdarangha.moghayerat.data.db.entity.ProductEntity;
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
public final class ProductDao_Impl implements ProductDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<ProductEntity> __insertionAdapterOfProductEntity;

  private final EntityDeletionOrUpdateAdapter<ProductEntity> __deletionAdapterOfProductEntity;

  private final EntityDeletionOrUpdateAdapter<ProductEntity> __updateAdapterOfProductEntity;

  private final SharedSQLiteStatement __preparedStmtOfDeactivate;

  private final SharedSQLiteStatement __preparedStmtOfActivate;

  private final SharedSQLiteStatement __preparedStmtOfUpdateDisplayOrder;

  public ProductDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfProductEntity = new EntityInsertionAdapter<ProductEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR IGNORE INTO `products` (`id`,`workflowId`,`name`,`group`,`packagingType`,`boxCapacity`,`displayOrder`,`isActive`,`createdAt`) VALUES (nullif(?, 0),?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final ProductEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindLong(2, entity.getWorkflowId());
        statement.bindString(3, entity.getName());
        statement.bindString(4, entity.getGroup());
        statement.bindString(5, entity.getPackagingType());
        statement.bindLong(6, entity.getBoxCapacity());
        statement.bindLong(7, entity.getDisplayOrder());
        final int _tmp = entity.isActive() ? 1 : 0;
        statement.bindLong(8, _tmp);
        statement.bindLong(9, entity.getCreatedAt());
      }
    };
    this.__deletionAdapterOfProductEntity = new EntityDeletionOrUpdateAdapter<ProductEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `products` WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final ProductEntity entity) {
        statement.bindLong(1, entity.getId());
      }
    };
    this.__updateAdapterOfProductEntity = new EntityDeletionOrUpdateAdapter<ProductEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `products` SET `id` = ?,`workflowId` = ?,`name` = ?,`group` = ?,`packagingType` = ?,`boxCapacity` = ?,`displayOrder` = ?,`isActive` = ?,`createdAt` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final ProductEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindLong(2, entity.getWorkflowId());
        statement.bindString(3, entity.getName());
        statement.bindString(4, entity.getGroup());
        statement.bindString(5, entity.getPackagingType());
        statement.bindLong(6, entity.getBoxCapacity());
        statement.bindLong(7, entity.getDisplayOrder());
        final int _tmp = entity.isActive() ? 1 : 0;
        statement.bindLong(8, _tmp);
        statement.bindLong(9, entity.getCreatedAt());
        statement.bindLong(10, entity.getId());
      }
    };
    this.__preparedStmtOfDeactivate = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE products SET isActive = 0 WHERE id = ?";
        return _query;
      }
    };
    this.__preparedStmtOfActivate = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE products SET isActive = 1 WHERE id = ?";
        return _query;
      }
    };
    this.__preparedStmtOfUpdateDisplayOrder = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE products SET displayOrder = ? WHERE id = ?";
        return _query;
      }
    };
  }

  @Override
  public Object insert(final ProductEntity product, final Continuation<? super Long> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Long>() {
      @Override
      @NonNull
      public Long call() throws Exception {
        __db.beginTransaction();
        try {
          final Long _result = __insertionAdapterOfProductEntity.insertAndReturnId(product);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object delete(final ProductEntity product, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __deletionAdapterOfProductEntity.handle(product);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object update(final ProductEntity product, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __updateAdapterOfProductEntity.handle(product);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object deactivate(final long id, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeactivate.acquire();
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
          __preparedStmtOfDeactivate.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object activate(final long id, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfActivate.acquire();
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
          __preparedStmtOfActivate.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object updateDisplayOrder(final long id, final int order,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfUpdateDisplayOrder.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, order);
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
          __preparedStmtOfUpdateDisplayOrder.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<ProductEntity>> observeActiveProducts() {
    final String _sql = "SELECT * FROM products WHERE isActive = 1 ORDER BY displayOrder ASC, name ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"products"}, new Callable<List<ProductEntity>>() {
      @Override
      @NonNull
      public List<ProductEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfWorkflowId = CursorUtil.getColumnIndexOrThrow(_cursor, "workflowId");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfGroup = CursorUtil.getColumnIndexOrThrow(_cursor, "group");
          final int _cursorIndexOfPackagingType = CursorUtil.getColumnIndexOrThrow(_cursor, "packagingType");
          final int _cursorIndexOfBoxCapacity = CursorUtil.getColumnIndexOrThrow(_cursor, "boxCapacity");
          final int _cursorIndexOfDisplayOrder = CursorUtil.getColumnIndexOrThrow(_cursor, "displayOrder");
          final int _cursorIndexOfIsActive = CursorUtil.getColumnIndexOrThrow(_cursor, "isActive");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final List<ProductEntity> _result = new ArrayList<ProductEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final ProductEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpWorkflowId;
            _tmpWorkflowId = _cursor.getLong(_cursorIndexOfWorkflowId);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final String _tmpGroup;
            _tmpGroup = _cursor.getString(_cursorIndexOfGroup);
            final String _tmpPackagingType;
            _tmpPackagingType = _cursor.getString(_cursorIndexOfPackagingType);
            final int _tmpBoxCapacity;
            _tmpBoxCapacity = _cursor.getInt(_cursorIndexOfBoxCapacity);
            final int _tmpDisplayOrder;
            _tmpDisplayOrder = _cursor.getInt(_cursorIndexOfDisplayOrder);
            final boolean _tmpIsActive;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsActive);
            _tmpIsActive = _tmp != 0;
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            _item = new ProductEntity(_tmpId,_tmpWorkflowId,_tmpName,_tmpGroup,_tmpPackagingType,_tmpBoxCapacity,_tmpDisplayOrder,_tmpIsActive,_tmpCreatedAt);
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
  public Flow<List<ProductEntity>> observeAllProducts() {
    final String _sql = "SELECT * FROM products ORDER BY displayOrder ASC, name ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"products"}, new Callable<List<ProductEntity>>() {
      @Override
      @NonNull
      public List<ProductEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfWorkflowId = CursorUtil.getColumnIndexOrThrow(_cursor, "workflowId");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfGroup = CursorUtil.getColumnIndexOrThrow(_cursor, "group");
          final int _cursorIndexOfPackagingType = CursorUtil.getColumnIndexOrThrow(_cursor, "packagingType");
          final int _cursorIndexOfBoxCapacity = CursorUtil.getColumnIndexOrThrow(_cursor, "boxCapacity");
          final int _cursorIndexOfDisplayOrder = CursorUtil.getColumnIndexOrThrow(_cursor, "displayOrder");
          final int _cursorIndexOfIsActive = CursorUtil.getColumnIndexOrThrow(_cursor, "isActive");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final List<ProductEntity> _result = new ArrayList<ProductEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final ProductEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpWorkflowId;
            _tmpWorkflowId = _cursor.getLong(_cursorIndexOfWorkflowId);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final String _tmpGroup;
            _tmpGroup = _cursor.getString(_cursorIndexOfGroup);
            final String _tmpPackagingType;
            _tmpPackagingType = _cursor.getString(_cursorIndexOfPackagingType);
            final int _tmpBoxCapacity;
            _tmpBoxCapacity = _cursor.getInt(_cursorIndexOfBoxCapacity);
            final int _tmpDisplayOrder;
            _tmpDisplayOrder = _cursor.getInt(_cursorIndexOfDisplayOrder);
            final boolean _tmpIsActive;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsActive);
            _tmpIsActive = _tmp != 0;
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            _item = new ProductEntity(_tmpId,_tmpWorkflowId,_tmpName,_tmpGroup,_tmpPackagingType,_tmpBoxCapacity,_tmpDisplayOrder,_tmpIsActive,_tmpCreatedAt);
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
  public Flow<List<ProductEntity>> observeActiveProductsByWorkflow(final long workflowId) {
    final String _sql = "SELECT * FROM products WHERE workflowId = ? AND isActive = 1 ORDER BY displayOrder ASC, name ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, workflowId);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"products"}, new Callable<List<ProductEntity>>() {
      @Override
      @NonNull
      public List<ProductEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfWorkflowId = CursorUtil.getColumnIndexOrThrow(_cursor, "workflowId");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfGroup = CursorUtil.getColumnIndexOrThrow(_cursor, "group");
          final int _cursorIndexOfPackagingType = CursorUtil.getColumnIndexOrThrow(_cursor, "packagingType");
          final int _cursorIndexOfBoxCapacity = CursorUtil.getColumnIndexOrThrow(_cursor, "boxCapacity");
          final int _cursorIndexOfDisplayOrder = CursorUtil.getColumnIndexOrThrow(_cursor, "displayOrder");
          final int _cursorIndexOfIsActive = CursorUtil.getColumnIndexOrThrow(_cursor, "isActive");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final List<ProductEntity> _result = new ArrayList<ProductEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final ProductEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpWorkflowId;
            _tmpWorkflowId = _cursor.getLong(_cursorIndexOfWorkflowId);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final String _tmpGroup;
            _tmpGroup = _cursor.getString(_cursorIndexOfGroup);
            final String _tmpPackagingType;
            _tmpPackagingType = _cursor.getString(_cursorIndexOfPackagingType);
            final int _tmpBoxCapacity;
            _tmpBoxCapacity = _cursor.getInt(_cursorIndexOfBoxCapacity);
            final int _tmpDisplayOrder;
            _tmpDisplayOrder = _cursor.getInt(_cursorIndexOfDisplayOrder);
            final boolean _tmpIsActive;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsActive);
            _tmpIsActive = _tmp != 0;
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            _item = new ProductEntity(_tmpId,_tmpWorkflowId,_tmpName,_tmpGroup,_tmpPackagingType,_tmpBoxCapacity,_tmpDisplayOrder,_tmpIsActive,_tmpCreatedAt);
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
  public Flow<List<ProductEntity>> observeAllProductsByWorkflow(final long workflowId) {
    final String _sql = "SELECT * FROM products WHERE workflowId = ? ORDER BY displayOrder ASC, name ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, workflowId);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"products"}, new Callable<List<ProductEntity>>() {
      @Override
      @NonNull
      public List<ProductEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfWorkflowId = CursorUtil.getColumnIndexOrThrow(_cursor, "workflowId");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfGroup = CursorUtil.getColumnIndexOrThrow(_cursor, "group");
          final int _cursorIndexOfPackagingType = CursorUtil.getColumnIndexOrThrow(_cursor, "packagingType");
          final int _cursorIndexOfBoxCapacity = CursorUtil.getColumnIndexOrThrow(_cursor, "boxCapacity");
          final int _cursorIndexOfDisplayOrder = CursorUtil.getColumnIndexOrThrow(_cursor, "displayOrder");
          final int _cursorIndexOfIsActive = CursorUtil.getColumnIndexOrThrow(_cursor, "isActive");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final List<ProductEntity> _result = new ArrayList<ProductEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final ProductEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpWorkflowId;
            _tmpWorkflowId = _cursor.getLong(_cursorIndexOfWorkflowId);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final String _tmpGroup;
            _tmpGroup = _cursor.getString(_cursorIndexOfGroup);
            final String _tmpPackagingType;
            _tmpPackagingType = _cursor.getString(_cursorIndexOfPackagingType);
            final int _tmpBoxCapacity;
            _tmpBoxCapacity = _cursor.getInt(_cursorIndexOfBoxCapacity);
            final int _tmpDisplayOrder;
            _tmpDisplayOrder = _cursor.getInt(_cursorIndexOfDisplayOrder);
            final boolean _tmpIsActive;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsActive);
            _tmpIsActive = _tmp != 0;
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            _item = new ProductEntity(_tmpId,_tmpWorkflowId,_tmpName,_tmpGroup,_tmpPackagingType,_tmpBoxCapacity,_tmpDisplayOrder,_tmpIsActive,_tmpCreatedAt);
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
  public Flow<List<ProductEntity>> observeActiveProductsByGroup(final String group) {
    final String _sql = "SELECT * FROM products WHERE `group` = ? AND isActive = 1 ORDER BY displayOrder ASC, name ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, group);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"products"}, new Callable<List<ProductEntity>>() {
      @Override
      @NonNull
      public List<ProductEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfWorkflowId = CursorUtil.getColumnIndexOrThrow(_cursor, "workflowId");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfGroup = CursorUtil.getColumnIndexOrThrow(_cursor, "group");
          final int _cursorIndexOfPackagingType = CursorUtil.getColumnIndexOrThrow(_cursor, "packagingType");
          final int _cursorIndexOfBoxCapacity = CursorUtil.getColumnIndexOrThrow(_cursor, "boxCapacity");
          final int _cursorIndexOfDisplayOrder = CursorUtil.getColumnIndexOrThrow(_cursor, "displayOrder");
          final int _cursorIndexOfIsActive = CursorUtil.getColumnIndexOrThrow(_cursor, "isActive");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final List<ProductEntity> _result = new ArrayList<ProductEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final ProductEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpWorkflowId;
            _tmpWorkflowId = _cursor.getLong(_cursorIndexOfWorkflowId);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final String _tmpGroup;
            _tmpGroup = _cursor.getString(_cursorIndexOfGroup);
            final String _tmpPackagingType;
            _tmpPackagingType = _cursor.getString(_cursorIndexOfPackagingType);
            final int _tmpBoxCapacity;
            _tmpBoxCapacity = _cursor.getInt(_cursorIndexOfBoxCapacity);
            final int _tmpDisplayOrder;
            _tmpDisplayOrder = _cursor.getInt(_cursorIndexOfDisplayOrder);
            final boolean _tmpIsActive;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsActive);
            _tmpIsActive = _tmp != 0;
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            _item = new ProductEntity(_tmpId,_tmpWorkflowId,_tmpName,_tmpGroup,_tmpPackagingType,_tmpBoxCapacity,_tmpDisplayOrder,_tmpIsActive,_tmpCreatedAt);
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
  public Object getById(final long id, final Continuation<? super ProductEntity> $completion) {
    final String _sql = "SELECT * FROM products WHERE id = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, id);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<ProductEntity>() {
      @Override
      @Nullable
      public ProductEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfWorkflowId = CursorUtil.getColumnIndexOrThrow(_cursor, "workflowId");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfGroup = CursorUtil.getColumnIndexOrThrow(_cursor, "group");
          final int _cursorIndexOfPackagingType = CursorUtil.getColumnIndexOrThrow(_cursor, "packagingType");
          final int _cursorIndexOfBoxCapacity = CursorUtil.getColumnIndexOrThrow(_cursor, "boxCapacity");
          final int _cursorIndexOfDisplayOrder = CursorUtil.getColumnIndexOrThrow(_cursor, "displayOrder");
          final int _cursorIndexOfIsActive = CursorUtil.getColumnIndexOrThrow(_cursor, "isActive");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final ProductEntity _result;
          if (_cursor.moveToFirst()) {
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpWorkflowId;
            _tmpWorkflowId = _cursor.getLong(_cursorIndexOfWorkflowId);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final String _tmpGroup;
            _tmpGroup = _cursor.getString(_cursorIndexOfGroup);
            final String _tmpPackagingType;
            _tmpPackagingType = _cursor.getString(_cursorIndexOfPackagingType);
            final int _tmpBoxCapacity;
            _tmpBoxCapacity = _cursor.getInt(_cursorIndexOfBoxCapacity);
            final int _tmpDisplayOrder;
            _tmpDisplayOrder = _cursor.getInt(_cursorIndexOfDisplayOrder);
            final boolean _tmpIsActive;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsActive);
            _tmpIsActive = _tmp != 0;
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            _result = new ProductEntity(_tmpId,_tmpWorkflowId,_tmpName,_tmpGroup,_tmpPackagingType,_tmpBoxCapacity,_tmpDisplayOrder,_tmpIsActive,_tmpCreatedAt);
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
  public Flow<ProductEntity> observeById(final long id) {
    final String _sql = "SELECT * FROM products WHERE id = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, id);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"products"}, new Callable<ProductEntity>() {
      @Override
      @Nullable
      public ProductEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfWorkflowId = CursorUtil.getColumnIndexOrThrow(_cursor, "workflowId");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfGroup = CursorUtil.getColumnIndexOrThrow(_cursor, "group");
          final int _cursorIndexOfPackagingType = CursorUtil.getColumnIndexOrThrow(_cursor, "packagingType");
          final int _cursorIndexOfBoxCapacity = CursorUtil.getColumnIndexOrThrow(_cursor, "boxCapacity");
          final int _cursorIndexOfDisplayOrder = CursorUtil.getColumnIndexOrThrow(_cursor, "displayOrder");
          final int _cursorIndexOfIsActive = CursorUtil.getColumnIndexOrThrow(_cursor, "isActive");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final ProductEntity _result;
          if (_cursor.moveToFirst()) {
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpWorkflowId;
            _tmpWorkflowId = _cursor.getLong(_cursorIndexOfWorkflowId);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final String _tmpGroup;
            _tmpGroup = _cursor.getString(_cursorIndexOfGroup);
            final String _tmpPackagingType;
            _tmpPackagingType = _cursor.getString(_cursorIndexOfPackagingType);
            final int _tmpBoxCapacity;
            _tmpBoxCapacity = _cursor.getInt(_cursorIndexOfBoxCapacity);
            final int _tmpDisplayOrder;
            _tmpDisplayOrder = _cursor.getInt(_cursorIndexOfDisplayOrder);
            final boolean _tmpIsActive;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsActive);
            _tmpIsActive = _tmp != 0;
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            _result = new ProductEntity(_tmpId,_tmpWorkflowId,_tmpName,_tmpGroup,_tmpPackagingType,_tmpBoxCapacity,_tmpDisplayOrder,_tmpIsActive,_tmpCreatedAt);
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
  public Flow<List<ProductEntity>> search(final String query) {
    final String _sql = "SELECT * FROM products WHERE name LIKE '%' || ? || '%' ORDER BY displayOrder ASC, name ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, query);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"products"}, new Callable<List<ProductEntity>>() {
      @Override
      @NonNull
      public List<ProductEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfWorkflowId = CursorUtil.getColumnIndexOrThrow(_cursor, "workflowId");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfGroup = CursorUtil.getColumnIndexOrThrow(_cursor, "group");
          final int _cursorIndexOfPackagingType = CursorUtil.getColumnIndexOrThrow(_cursor, "packagingType");
          final int _cursorIndexOfBoxCapacity = CursorUtil.getColumnIndexOrThrow(_cursor, "boxCapacity");
          final int _cursorIndexOfDisplayOrder = CursorUtil.getColumnIndexOrThrow(_cursor, "displayOrder");
          final int _cursorIndexOfIsActive = CursorUtil.getColumnIndexOrThrow(_cursor, "isActive");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final List<ProductEntity> _result = new ArrayList<ProductEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final ProductEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpWorkflowId;
            _tmpWorkflowId = _cursor.getLong(_cursorIndexOfWorkflowId);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final String _tmpGroup;
            _tmpGroup = _cursor.getString(_cursorIndexOfGroup);
            final String _tmpPackagingType;
            _tmpPackagingType = _cursor.getString(_cursorIndexOfPackagingType);
            final int _tmpBoxCapacity;
            _tmpBoxCapacity = _cursor.getInt(_cursorIndexOfBoxCapacity);
            final int _tmpDisplayOrder;
            _tmpDisplayOrder = _cursor.getInt(_cursorIndexOfDisplayOrder);
            final boolean _tmpIsActive;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsActive);
            _tmpIsActive = _tmp != 0;
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            _item = new ProductEntity(_tmpId,_tmpWorkflowId,_tmpName,_tmpGroup,_tmpPackagingType,_tmpBoxCapacity,_tmpDisplayOrder,_tmpIsActive,_tmpCreatedAt);
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
  public Object countActiveByWorkflow(final long workflowId,
      final Continuation<? super Integer> $completion) {
    final String _sql = "SELECT COUNT(*) FROM products WHERE isActive = 1 AND workflowId = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, workflowId);
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
  public Object countActiveByGroup(final String group,
      final Continuation<? super Integer> $completion) {
    final String _sql = "SELECT COUNT(*) FROM products WHERE isActive = 1 AND `group` = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, group);
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
  public Object countStatsForProduct(final long productId,
      final Continuation<? super Integer> $completion) {
    final String _sql = "SELECT COUNT(*) FROM daily_stats WHERE productId = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, productId);
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
  public Object getAllProducts(final Continuation<? super List<ProductEntity>> $completion) {
    final String _sql = "SELECT * FROM products ORDER BY displayOrder ASC, name ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<ProductEntity>>() {
      @Override
      @NonNull
      public List<ProductEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfWorkflowId = CursorUtil.getColumnIndexOrThrow(_cursor, "workflowId");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfGroup = CursorUtil.getColumnIndexOrThrow(_cursor, "group");
          final int _cursorIndexOfPackagingType = CursorUtil.getColumnIndexOrThrow(_cursor, "packagingType");
          final int _cursorIndexOfBoxCapacity = CursorUtil.getColumnIndexOrThrow(_cursor, "boxCapacity");
          final int _cursorIndexOfDisplayOrder = CursorUtil.getColumnIndexOrThrow(_cursor, "displayOrder");
          final int _cursorIndexOfIsActive = CursorUtil.getColumnIndexOrThrow(_cursor, "isActive");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final List<ProductEntity> _result = new ArrayList<ProductEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final ProductEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpWorkflowId;
            _tmpWorkflowId = _cursor.getLong(_cursorIndexOfWorkflowId);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final String _tmpGroup;
            _tmpGroup = _cursor.getString(_cursorIndexOfGroup);
            final String _tmpPackagingType;
            _tmpPackagingType = _cursor.getString(_cursorIndexOfPackagingType);
            final int _tmpBoxCapacity;
            _tmpBoxCapacity = _cursor.getInt(_cursorIndexOfBoxCapacity);
            final int _tmpDisplayOrder;
            _tmpDisplayOrder = _cursor.getInt(_cursorIndexOfDisplayOrder);
            final boolean _tmpIsActive;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsActive);
            _tmpIsActive = _tmp != 0;
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            _item = new ProductEntity(_tmpId,_tmpWorkflowId,_tmpName,_tmpGroup,_tmpPackagingType,_tmpBoxCapacity,_tmpDisplayOrder,_tmpIsActive,_tmpCreatedAt);
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
