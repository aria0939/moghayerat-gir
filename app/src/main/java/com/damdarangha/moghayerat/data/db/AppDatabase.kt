package com.damdarangha.moghayerat.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.damdarangha.moghayerat.data.db.dao.DailyStatDao
import com.damdarangha.moghayerat.data.db.dao.ProductDao
import com.damdarangha.moghayerat.data.db.dao.StatHistoryDao
import com.damdarangha.moghayerat.data.db.entity.DailyStatEntity
import com.damdarangha.moghayerat.data.db.entity.ProductEntity
import com.damdarangha.moghayerat.data.db.entity.StatHistoryEntity

@Database(
    entities = [
        ProductEntity::class,
        DailyStatEntity::class,
        StatHistoryEntity::class
    ],
    version = 2,  // افزایش نسخه به دلیل اضافه شدن فیلدهای returns و midDayLoading
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun productDao(): ProductDao
    abstract fun dailyStatDao(): DailyStatDao
    abstract fun statHistoryDao(): StatHistoryDao

    companion object {
        const val DATABASE_NAME = "moghayerat.db"
    }
}
