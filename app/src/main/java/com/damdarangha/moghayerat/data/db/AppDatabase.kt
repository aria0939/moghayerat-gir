package com.damdarangha.moghayerat.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.damdarangha.moghayerat.data.db.dao.DailyStatDao
import com.damdarangha.moghayerat.data.db.dao.ProductDao
import com.damdarangha.moghayerat.data.db.dao.StatHistoryDao
import com.damdarangha.moghayerat.data.db.dao.WorkflowDao
import com.damdarangha.moghayerat.data.db.entity.DailyStatEntity
import com.damdarangha.moghayerat.data.db.entity.ProductEntity
import com.damdarangha.moghayerat.data.db.entity.StatHistoryEntity
import com.damdarangha.moghayerat.data.db.entity.WorkflowEntity

@Database(
    entities = [
        WorkflowEntity::class,
        ProductEntity::class,
        DailyStatEntity::class,
        StatHistoryEntity::class
    ],
    version = 7,  // افزایش نسخه برای nullable شدن currentStockTotal
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun workflowDao(): WorkflowDao
    abstract fun productDao(): ProductDao
    abstract fun dailyStatDao(): DailyStatDao
    abstract fun statHistoryDao(): StatHistoryDao

    companion object {
        const val DATABASE_NAME = "moghayerat.db"

        /**
         * مهاجرت نسخه ۶ به ۷:
         *
         * تغییر currentStockTotal از Int (با پیش‌فرض ۰) به Int? (nullable).
         *
         * استراتژی:
         * - اگر endCumulativeVariance در رکورد قدیمی NOT NULL است، یعنی کاربر شمارش آخر کار را
         *   ثبت کرده (حتی اگر صفر بوده). پس مقدار currentStockTotal قدیمی (حتی اگر ۰) معتبر است
         *   و به‌صورت Int در ستون جدید ذخیره می‌شود.
         * - اگر endCumulativeVariance در رکورد قدیمی NULL است، یعنی شمارش آخر کار ثبت نشده.
         *   پس currentStockTotal نیز باید NULL باشد.
         *
         * این مهاجرت داده‌های کاربر را حفظ می‌کند و در عین حال تفاوت بین
         * «ثبت‌نشده» و «ثبت‌شده با مقدار صفر» را قابل تشخیص می‌سازد.
         */
        val MIGRATION_6_7: Migration = object : Migration(6, 7) {
            override fun migrate(db: SupportSQLiteDatabase) {
                // بازسازی جدول با ستون nullable
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS daily_stats_new (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        productId INTEGER NOT NULL,
                        isoDate TEXT NOT NULL,
                        jalaliDate TEXT NOT NULL,
                        openingStockTotal INTEGER NOT NULL,
                        morningLoadingTotal INTEGER NOT NULL,
                        actualMorningStockTotal INTEGER,
                        receiptTotal INTEGER NOT NULL,
                        returnsTotal INTEGER NOT NULL,
                        giftsTotal INTEGER NOT NULL,
                        wasteTotal INTEGER NOT NULL,
                        laboratoryTotal INTEGER NOT NULL,
                        midDayLoadingTotal INTEGER NOT NULL,
                        nightLoadingTotal INTEGER NOT NULL,
                        currentStockTotal INTEGER,
                        expectedMorningStockTotal INTEGER NOT NULL,
                        morningVariance INTEGER,
                        expectedClosingStockTotal INTEGER NOT NULL,
                        closingVariance INTEGER NOT NULL,
                        startVariance INTEGER,
                        endCumulativeVariance INTEGER,
                        nightShiftVariance INTEGER,
                        isFinalized INTEGER NOT NULL,
                        notes TEXT NOT NULL,
                        createdAt INTEGER NOT NULL,
                        updatedAt INTEGER NOT NULL,
                        FOREIGN KEY(productId) REFERENCES products(id) ON DELETE RESTRICT
                    )
                    """.trimIndent()
                )

                // کپی داده‌ها با منطق nullable شدن
                // اگر endCumulativeVariance NOT NULL است، مقدار currentStockTotal قدیمی را نگه می‌داریم
                // در غیر این صورت NULL می‌گذاریم
                db.execSQL(
                    """
                    INSERT INTO daily_stats_new (
                        id, productId, isoDate, jalaliDate, openingStockTotal, morningLoadingTotal,
                        actualMorningStockTotal, receiptTotal, returnsTotal, giftsTotal, wasteTotal,
                        laboratoryTotal, midDayLoadingTotal, nightLoadingTotal, currentStockTotal,
                        expectedMorningStockTotal, morningVariance, expectedClosingStockTotal,
                        closingVariance, startVariance, endCumulativeVariance, nightShiftVariance,
                        isFinalized, notes, createdAt, updatedAt
                    )
                    SELECT
                        id, productId, isoDate, jalaliDate, openingStockTotal, morningLoadingTotal,
                        actualMorningStockTotal, receiptTotal, returnsTotal, giftsTotal, wasteTotal,
                        laboratoryTotal, midDayLoadingTotal, nightLoadingTotal,
                        CASE WHEN endCumulativeVariance IS NOT NULL THEN currentStockTotal ELSE NULL END,
                        expectedMorningStockTotal, morningVariance, expectedClosingStockTotal,
                        closingVariance, startVariance, endCumulativeVariance, nightShiftVariance,
                        isFinalized, notes, createdAt, updatedAt
                    FROM daily_stats
                    """.trimIndent()
                )

                // جایگزینی جدول قدیمی با جدول جدید
                db.execSQL("DROP TABLE IF EXISTS daily_stats")
                db.execSQL("ALTER TABLE daily_stats_new RENAME TO daily_stats")

                // بازسازی ایندکس‌ها
                db.execSQL(
                    "CREATE UNIQUE INDEX IF NOT EXISTS index_daily_stats_productId_isoDate ON daily_stats (productId, isoDate)"
                )
            }
        }
    }
}
