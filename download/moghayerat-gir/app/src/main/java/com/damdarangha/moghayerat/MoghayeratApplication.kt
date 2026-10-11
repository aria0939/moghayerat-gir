package com.damdarangha.moghayerat

import android.app.Application
import androidx.room.Room
import com.damdarangha.moghayerat.data.db.AppDatabase
import com.damdarangha.moghayerat.data.datastore.SettingsDataStore
import com.damdarangha.moghayerat.data.repository.DailyStatRepository
import com.damdarangha.moghayerat.data.repository.ProductRepository
import com.damdarangha.moghayerat.data.backup.BackupManager
import com.google.gson.Gson

/**
 * کلاس Application برنامه.
 * مسئولیت: مقداردهی اولیه پایگاه داده و Repositoryها.
 */
class MoghayeratApplication : Application() {

    lateinit var database: AppDatabase
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
            .fallbackToDestructiveMigration()
            .build()

        val gson = Gson()
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
