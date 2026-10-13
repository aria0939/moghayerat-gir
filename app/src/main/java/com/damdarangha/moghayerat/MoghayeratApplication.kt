package com.damdarangha.moghayerat

import android.app.Application
import androidx.room.Room
import com.damdarangha.moghayerat.data.db.AppDatabase
import com.damdarangha.moghayerat.data.datastore.SettingsDataStore
import com.damdarangha.moghayerat.data.backup.BackupManager
import com.damdarangha.moghayerat.data.repository.DailyStatRepository
import com.damdarangha.moghayerat.data.repository.ProductRepository
import com.damdarangha.moghayerat.data.repository.WorkflowRepository
import com.google.gson.Gson

/**
 * کلاس Application برنامه.
 */
class MoghayeratApplication : Application() {

    lateinit var database: AppDatabase
    lateinit var workflowRepository: WorkflowRepository
    lateinit var productRepository: ProductRepository
    lateinit var dailyStatRepository: DailyStatRepository
    lateinit var settingsDataStore: SettingsDataStore
    lateinit var backupManager: BackupManager

    override fun onCreate() {
        super.onCreate()
        instance = this

        database = Room.databaseBuilder(
            this,
            AppDatabase::class.java,
            AppDatabase.DATABASE_NAME
        )
            .addMigrations(AppDatabase.MIGRATION_6_7, AppDatabase.MIGRATION_7_8)
            // به‌عنوان safety net: اگر کاربر روی نسخه‌ی بسیار قدیمی بود (قبل از v6)،
            // به‌جای crash کردن، دیتابیس را بازسازی می‌کند.
            .fallbackToDestructiveMigration()
            .build()

        val gson = Gson()
        workflowRepository = WorkflowRepository(database.workflowDao())
        productRepository = ProductRepository(database.productDao())
        dailyStatRepository = DailyStatRepository(
            database.dailyStatDao(),
            database.statHistoryDao(),
            gson
        )
        settingsDataStore = SettingsDataStore(this)
        backupManager = BackupManager(this, database, gson)
    }

    companion object {
        lateinit var instance: MoghayeratApplication
            private set
    }
}
