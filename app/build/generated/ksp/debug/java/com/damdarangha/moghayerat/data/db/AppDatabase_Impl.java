package com.damdarangha.moghayerat.data.db;

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
import com.damdarangha.moghayerat.data.db.dao.DailyStatDao;
import com.damdarangha.moghayerat.data.db.dao.DailyStatDao_Impl;
import com.damdarangha.moghayerat.data.db.dao.ProductDao;
import com.damdarangha.moghayerat.data.db.dao.ProductDao_Impl;
import com.damdarangha.moghayerat.data.db.dao.StatHistoryDao;
import com.damdarangha.moghayerat.data.db.dao.StatHistoryDao_Impl;
import com.damdarangha.moghayerat.data.db.dao.WorkflowDao;
import com.damdarangha.moghayerat.data.db.dao.WorkflowDao_Impl;
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
  private volatile WorkflowDao _workflowDao;

  private volatile ProductDao _productDao;

  private volatile DailyStatDao _dailyStatDao;

  private volatile StatHistoryDao _statHistoryDao;

  @Override
  @NonNull
  protected SupportSQLiteOpenHelper createOpenHelper(@NonNull final DatabaseConfiguration config) {
    final SupportSQLiteOpenHelper.Callback _openCallback = new RoomOpenHelper(config, new RoomOpenHelper.Delegate(8) {
      @Override
      public void createAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS `workflows` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `name` TEXT NOT NULL, `displayOrder` INTEGER NOT NULL, `isActive` INTEGER NOT NULL, `createdAt` INTEGER NOT NULL)");
        db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS `index_workflows_name` ON `workflows` (`name`)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `products` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `workflowId` INTEGER NOT NULL, `name` TEXT NOT NULL, `group` TEXT NOT NULL, `packagingType` TEXT NOT NULL, `boxCapacity` INTEGER NOT NULL, `displayOrder` INTEGER NOT NULL, `isActive` INTEGER NOT NULL, `createdAt` INTEGER NOT NULL, FOREIGN KEY(`workflowId`) REFERENCES `workflows`(`id`) ON UPDATE NO ACTION ON DELETE RESTRICT )");
        db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS `index_products_workflowId_name` ON `products` (`workflowId`, `name`)");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_products_workflowId` ON `products` (`workflowId`)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `daily_stats` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `productId` INTEGER NOT NULL, `isoDate` TEXT NOT NULL, `jalaliDate` TEXT NOT NULL, `openingStockTotal` INTEGER NOT NULL, `morningLoadingTotal` INTEGER NOT NULL, `actualMorningStockTotal` INTEGER, `receiptTotal` INTEGER NOT NULL, `returnsTotal` INTEGER NOT NULL, `giftsTotal` INTEGER NOT NULL, `wasteTotal` INTEGER NOT NULL, `laboratoryTotal` INTEGER NOT NULL, `midDayLoadingTotal` INTEGER NOT NULL, `dayReceiptTotal` INTEGER NOT NULL, `dayReturnsTotal` INTEGER NOT NULL, `dayGiftsTotal` INTEGER NOT NULL, `dayWasteTotal` INTEGER NOT NULL, `dayLaboratoryTotal` INTEGER NOT NULL, `dayMidDayLoadingTotal` INTEGER NOT NULL, `nightLoadingTotal` INTEGER NOT NULL, `currentStockTotal` INTEGER, `expectedMorningStockTotal` INTEGER NOT NULL, `morningVariance` INTEGER, `expectedClosingStockTotal` INTEGER NOT NULL, `closingVariance` INTEGER NOT NULL, `startVariance` INTEGER, `endCumulativeVariance` INTEGER, `nightShiftVariance` INTEGER, `isFinalized` INTEGER NOT NULL, `notes` TEXT NOT NULL, `createdAt` INTEGER NOT NULL, `updatedAt` INTEGER NOT NULL, FOREIGN KEY(`productId`) REFERENCES `products`(`id`) ON UPDATE NO ACTION ON DELETE RESTRICT )");
        db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS `index_daily_stats_productId_isoDate` ON `daily_stats` (`productId`, `isoDate`)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `stat_history` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `statId` INTEGER NOT NULL, `productId` INTEGER NOT NULL, `isoDate` TEXT NOT NULL, `changeType` TEXT NOT NULL, `previousValue` TEXT, `newValue` TEXT, `changedAt` INTEGER NOT NULL, `note` TEXT NOT NULL)");
        db.execSQL("CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY,identity_hash TEXT)");
        db.execSQL("INSERT OR REPLACE INTO room_master_table (id,identity_hash) VALUES(42, '2ae6e24a2bf20f5846bd582873304bd8')");
      }

      @Override
      public void dropAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS `workflows`");
        db.execSQL("DROP TABLE IF EXISTS `products`");
        db.execSQL("DROP TABLE IF EXISTS `daily_stats`");
        db.execSQL("DROP TABLE IF EXISTS `stat_history`");
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
        final HashMap<String, TableInfo.Column> _columnsWorkflows = new HashMap<String, TableInfo.Column>(5);
        _columnsWorkflows.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsWorkflows.put("name", new TableInfo.Column("name", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsWorkflows.put("displayOrder", new TableInfo.Column("displayOrder", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsWorkflows.put("isActive", new TableInfo.Column("isActive", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsWorkflows.put("createdAt", new TableInfo.Column("createdAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysWorkflows = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesWorkflows = new HashSet<TableInfo.Index>(1);
        _indicesWorkflows.add(new TableInfo.Index("index_workflows_name", true, Arrays.asList("name"), Arrays.asList("ASC")));
        final TableInfo _infoWorkflows = new TableInfo("workflows", _columnsWorkflows, _foreignKeysWorkflows, _indicesWorkflows);
        final TableInfo _existingWorkflows = TableInfo.read(db, "workflows");
        if (!_infoWorkflows.equals(_existingWorkflows)) {
          return new RoomOpenHelper.ValidationResult(false, "workflows(com.damdarangha.moghayerat.data.db.entity.WorkflowEntity).\n"
                  + " Expected:\n" + _infoWorkflows + "\n"
                  + " Found:\n" + _existingWorkflows);
        }
        final HashMap<String, TableInfo.Column> _columnsProducts = new HashMap<String, TableInfo.Column>(9);
        _columnsProducts.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsProducts.put("workflowId", new TableInfo.Column("workflowId", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsProducts.put("name", new TableInfo.Column("name", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsProducts.put("group", new TableInfo.Column("group", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsProducts.put("packagingType", new TableInfo.Column("packagingType", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsProducts.put("boxCapacity", new TableInfo.Column("boxCapacity", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsProducts.put("displayOrder", new TableInfo.Column("displayOrder", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsProducts.put("isActive", new TableInfo.Column("isActive", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsProducts.put("createdAt", new TableInfo.Column("createdAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysProducts = new HashSet<TableInfo.ForeignKey>(1);
        _foreignKeysProducts.add(new TableInfo.ForeignKey("workflows", "RESTRICT", "NO ACTION", Arrays.asList("workflowId"), Arrays.asList("id")));
        final HashSet<TableInfo.Index> _indicesProducts = new HashSet<TableInfo.Index>(2);
        _indicesProducts.add(new TableInfo.Index("index_products_workflowId_name", true, Arrays.asList("workflowId", "name"), Arrays.asList("ASC", "ASC")));
        _indicesProducts.add(new TableInfo.Index("index_products_workflowId", false, Arrays.asList("workflowId"), Arrays.asList("ASC")));
        final TableInfo _infoProducts = new TableInfo("products", _columnsProducts, _foreignKeysProducts, _indicesProducts);
        final TableInfo _existingProducts = TableInfo.read(db, "products");
        if (!_infoProducts.equals(_existingProducts)) {
          return new RoomOpenHelper.ValidationResult(false, "products(com.damdarangha.moghayerat.data.db.entity.ProductEntity).\n"
                  + " Expected:\n" + _infoProducts + "\n"
                  + " Found:\n" + _existingProducts);
        }
        final HashMap<String, TableInfo.Column> _columnsDailyStats = new HashMap<String, TableInfo.Column>(32);
        _columnsDailyStats.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDailyStats.put("productId", new TableInfo.Column("productId", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDailyStats.put("isoDate", new TableInfo.Column("isoDate", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDailyStats.put("jalaliDate", new TableInfo.Column("jalaliDate", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDailyStats.put("openingStockTotal", new TableInfo.Column("openingStockTotal", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDailyStats.put("morningLoadingTotal", new TableInfo.Column("morningLoadingTotal", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDailyStats.put("actualMorningStockTotal", new TableInfo.Column("actualMorningStockTotal", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDailyStats.put("receiptTotal", new TableInfo.Column("receiptTotal", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDailyStats.put("returnsTotal", new TableInfo.Column("returnsTotal", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDailyStats.put("giftsTotal", new TableInfo.Column("giftsTotal", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDailyStats.put("wasteTotal", new TableInfo.Column("wasteTotal", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDailyStats.put("laboratoryTotal", new TableInfo.Column("laboratoryTotal", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDailyStats.put("midDayLoadingTotal", new TableInfo.Column("midDayLoadingTotal", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDailyStats.put("dayReceiptTotal", new TableInfo.Column("dayReceiptTotal", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDailyStats.put("dayReturnsTotal", new TableInfo.Column("dayReturnsTotal", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDailyStats.put("dayGiftsTotal", new TableInfo.Column("dayGiftsTotal", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDailyStats.put("dayWasteTotal", new TableInfo.Column("dayWasteTotal", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDailyStats.put("dayLaboratoryTotal", new TableInfo.Column("dayLaboratoryTotal", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDailyStats.put("dayMidDayLoadingTotal", new TableInfo.Column("dayMidDayLoadingTotal", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDailyStats.put("nightLoadingTotal", new TableInfo.Column("nightLoadingTotal", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDailyStats.put("currentStockTotal", new TableInfo.Column("currentStockTotal", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDailyStats.put("expectedMorningStockTotal", new TableInfo.Column("expectedMorningStockTotal", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDailyStats.put("morningVariance", new TableInfo.Column("morningVariance", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDailyStats.put("expectedClosingStockTotal", new TableInfo.Column("expectedClosingStockTotal", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDailyStats.put("closingVariance", new TableInfo.Column("closingVariance", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDailyStats.put("startVariance", new TableInfo.Column("startVariance", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDailyStats.put("endCumulativeVariance", new TableInfo.Column("endCumulativeVariance", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDailyStats.put("nightShiftVariance", new TableInfo.Column("nightShiftVariance", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDailyStats.put("isFinalized", new TableInfo.Column("isFinalized", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDailyStats.put("notes", new TableInfo.Column("notes", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDailyStats.put("createdAt", new TableInfo.Column("createdAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDailyStats.put("updatedAt", new TableInfo.Column("updatedAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysDailyStats = new HashSet<TableInfo.ForeignKey>(1);
        _foreignKeysDailyStats.add(new TableInfo.ForeignKey("products", "RESTRICT", "NO ACTION", Arrays.asList("productId"), Arrays.asList("id")));
        final HashSet<TableInfo.Index> _indicesDailyStats = new HashSet<TableInfo.Index>(1);
        _indicesDailyStats.add(new TableInfo.Index("index_daily_stats_productId_isoDate", true, Arrays.asList("productId", "isoDate"), Arrays.asList("ASC", "ASC")));
        final TableInfo _infoDailyStats = new TableInfo("daily_stats", _columnsDailyStats, _foreignKeysDailyStats, _indicesDailyStats);
        final TableInfo _existingDailyStats = TableInfo.read(db, "daily_stats");
        if (!_infoDailyStats.equals(_existingDailyStats)) {
          return new RoomOpenHelper.ValidationResult(false, "daily_stats(com.damdarangha.moghayerat.data.db.entity.DailyStatEntity).\n"
                  + " Expected:\n" + _infoDailyStats + "\n"
                  + " Found:\n" + _existingDailyStats);
        }
        final HashMap<String, TableInfo.Column> _columnsStatHistory = new HashMap<String, TableInfo.Column>(9);
        _columnsStatHistory.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsStatHistory.put("statId", new TableInfo.Column("statId", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsStatHistory.put("productId", new TableInfo.Column("productId", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsStatHistory.put("isoDate", new TableInfo.Column("isoDate", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsStatHistory.put("changeType", new TableInfo.Column("changeType", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsStatHistory.put("previousValue", new TableInfo.Column("previousValue", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsStatHistory.put("newValue", new TableInfo.Column("newValue", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsStatHistory.put("changedAt", new TableInfo.Column("changedAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsStatHistory.put("note", new TableInfo.Column("note", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysStatHistory = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesStatHistory = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoStatHistory = new TableInfo("stat_history", _columnsStatHistory, _foreignKeysStatHistory, _indicesStatHistory);
        final TableInfo _existingStatHistory = TableInfo.read(db, "stat_history");
        if (!_infoStatHistory.equals(_existingStatHistory)) {
          return new RoomOpenHelper.ValidationResult(false, "stat_history(com.damdarangha.moghayerat.data.db.entity.StatHistoryEntity).\n"
                  + " Expected:\n" + _infoStatHistory + "\n"
                  + " Found:\n" + _existingStatHistory);
        }
        return new RoomOpenHelper.ValidationResult(true, null);
      }
    }, "2ae6e24a2bf20f5846bd582873304bd8", "22eeedf270c89d6d54323d5315b55dbd");
    final SupportSQLiteOpenHelper.Configuration _sqliteConfig = SupportSQLiteOpenHelper.Configuration.builder(config.context).name(config.name).callback(_openCallback).build();
    final SupportSQLiteOpenHelper _helper = config.sqliteOpenHelperFactory.create(_sqliteConfig);
    return _helper;
  }

  @Override
  @NonNull
  protected InvalidationTracker createInvalidationTracker() {
    final HashMap<String, String> _shadowTablesMap = new HashMap<String, String>(0);
    final HashMap<String, Set<String>> _viewTables = new HashMap<String, Set<String>>(0);
    return new InvalidationTracker(this, _shadowTablesMap, _viewTables, "workflows","products","daily_stats","stat_history");
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
      _db.execSQL("DELETE FROM `daily_stats`");
      _db.execSQL("DELETE FROM `products`");
      _db.execSQL("DELETE FROM `workflows`");
      _db.execSQL("DELETE FROM `stat_history`");
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
    _typeConvertersMap.put(WorkflowDao.class, WorkflowDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(ProductDao.class, ProductDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(DailyStatDao.class, DailyStatDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(StatHistoryDao.class, StatHistoryDao_Impl.getRequiredConverters());
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
  public WorkflowDao workflowDao() {
    if (_workflowDao != null) {
      return _workflowDao;
    } else {
      synchronized(this) {
        if(_workflowDao == null) {
          _workflowDao = new WorkflowDao_Impl(this);
        }
        return _workflowDao;
      }
    }
  }

  @Override
  public ProductDao productDao() {
    if (_productDao != null) {
      return _productDao;
    } else {
      synchronized(this) {
        if(_productDao == null) {
          _productDao = new ProductDao_Impl(this);
        }
        return _productDao;
      }
    }
  }

  @Override
  public DailyStatDao dailyStatDao() {
    if (_dailyStatDao != null) {
      return _dailyStatDao;
    } else {
      synchronized(this) {
        if(_dailyStatDao == null) {
          _dailyStatDao = new DailyStatDao_Impl(this);
        }
        return _dailyStatDao;
      }
    }
  }

  @Override
  public StatHistoryDao statHistoryDao() {
    if (_statHistoryDao != null) {
      return _statHistoryDao;
    } else {
      synchronized(this) {
        if(_statHistoryDao == null) {
          _statHistoryDao = new StatHistoryDao_Impl(this);
        }
        return _statHistoryDao;
      }
    }
  }
}
