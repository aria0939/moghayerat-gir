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
    version = 8,  // افزایش نسخه برای ستون‌های عملیات روزانه روز (day*)
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
         * مهاجرت نسخه ۶ به ۷: تبدیل currentStockTotal به nullable.
         */
        val MIGRATION_6_7: Migration = object : Migration(6, 7) {
            override fun migrate(db: SupportSQLiteDatabase) {
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
                db.execSQL("DROP TABLE IF EXISTS daily_stats")
                db.execSQL("ALTER TABLE daily_stats_new RENAME TO daily_stats")
                db.execSQL(
                    "CREATE UNIQUE INDEX IF NOT EXISTS index_daily_stats_productId_isoDate ON daily_stats (productId, isoDate)"
                )
            }
        }

        /**
         * مهاجرت نسخه ۷ به ۸: اضافه کردن ستون‌های عملیات روزانه روز (day*).
         *
         * این ستون‌ها مقادیر عملیات روزانه را که در مرحله اول کار (روز) وارد شده‌اند،
         * جدا از مقادیر کل (روز + شب) ذخیره می‌کنند.
         *
         * ستون‌های جدید:
         * - dayReceiptTotal, dayReturnsTotal, dayGiftsTotal
         * - dayWasteTotal, dayLaboratoryTotal, dayMidDayLoadingTotal
         *
         * همه با پیش‌فرض ۰ (برای سازگاری با داده قدیمی).
         */
        val MIGRATION_7_8: Migration = object : Migration(7, 8) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE daily_stats ADD COLUMN dayReceiptTotal INTEGER NOT NULL DEFAULT 0")
                db.execSQL("ALTER TABLE daily_stats ADD COLUMN dayReturnsTotal INTEGER NOT NULL DEFAULT 0")
                db.execSQL("ALTER TABLE daily_stats ADD COLUMN dayGiftsTotal INTEGER NOT NULL DEFAULT 0")
                db.execSQL("ALTER TABLE daily_stats ADD COLUMN dayWasteTotal INTEGER NOT NULL DEFAULT 0")
                db.execSQL("ALTER TABLE daily_stats ADD COLUMN dayLaboratoryTotal INTEGER NOT NULL DEFAULT 0")
                db.execSQL("ALTER TABLE daily_stats ADD COLUMN dayMidDayLoadingTotal INTEGER NOT NULL DEFAULT 0")
            }
        }
    }
}
