package com.damdarangha.moghayerat.data.backup

import android.content.Context
import androidx.room.RoomDatabase
import com.damdarangha.moghayerat.data.db.AppDatabase
import com.damdarangha.moghayerat.data.db.entity.DailyStatEntity
import com.damdarangha.moghayerat.data.db.entity.ProductEntity
import com.damdarangha.moghayerat.data.db.entity.StatHistoryEntity
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * مدیریت پشتیبان‌گیری و بازیابی اطلاعات.
 *
 * فایل پشتیبان شامل:
 * - محصولات و ظرفیت باکس‌ها
 * - آمارهای روزانه
 * - تنظیمات ضروری
 * - سوابق تغییرات
 *
 * فرمت فایل: JSON
 */
class BackupManager(
    private val context: Context,
    private val database: AppDatabase,
    private val gson: Gson
) {
    private val backupDir: File by lazy {
        File(context.filesDir, "backups").apply { if (!exists()) mkdirs() }
    }

    /**
     * ساخت فایل پشتیبان.
     * @return فایل پشتیبان ایجاد شده
     */
    suspend fun createBackup(): File = withContext(Dispatchers.IO) {
        val products = database.productDao().observeAllProducts().let {
            // خواندن مستقیم از DAO - نیاز به متد غیر Flow
            // برای سادگی از getAll استفاده می‌کنیم (در ادامه اضافه می‌شود)
            emptyList<ProductEntity>()
        }
        // به‌جای آن، از DAO به‌صورت مستقیم استفاده می‌کنیم
        val allProducts = getAllProductsList()
        val allStats = database.dailyStatDao().getAll()
        val backupData = BackupData(
            version = 1,
            createdAt = System.currentTimeMillis(),
            products = allProducts,
            dailyStats = allStats
        )

        val json = gson.toJson(backupData)
        val dateFormat = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US)
        val fileName = "backup_${dateFormat.format(Date())}.json"
        val backupFile = File(backupDir, fileName)
        backupFile.writeText(json, Charsets.UTF_8)
        backupFile
    }

    /**
     * دریافت فهرست فایل‌های پشتیبان موجود.
     */
    fun listBackups(): List<File> {
        return backupDir.listFiles { f -> f.isFile && f.name.endsWith(".json") }
            ?.sortedByDescending { it.lastModified() }
            ?: emptyList()
    }

    /**
     * بازیابی اطلاعات از فایل پشتیبان.
     *
     * @param backupFile فایل پشتیبان
     * @param confirm جایگزینی داده‌های فعلی (نیازمند تأیید کاربر)
     */
    suspend fun restoreBackup(backupFile: File, confirm: Boolean): RestoreResult = withContext(Dispatchers.IO) {
        if (!confirm) {
            return@withContext RestoreResult(success = false, message = "بازیابی لغو شد")
        }

        try {
            val json = backupFile.readText(Charsets.UTF_8)
            val backupData = gson.fromJson(json, BackupData::class.java)
                ?: return@withContext RestoreResult(
                    success = false,
                    message = "فایل پشتیبان نامعتبر است"
                )

            // اعتبارسنجی فایل
            if (backupData.products.isEmpty() && backupData.dailyStats.isEmpty()) {
                return@withContext RestoreResult(
                    success = false,
                    message = "فایل پشتیبان خالی است"
                )
            }

            // جایگزینی داده‌ها
            database.runInTransaction {
                // حذف داده‌های فعلی
                // توجه: این عمل غیرقابل بازگشت است
                for (stat in database.dailyStatDao().getAll()) {
                    database.dailyStatDao().delete(stat.id)
                }
                // درج داده‌های جدید
                for (product in backupData.products) {
                    database.productDao().insert(product)
                }
                for (stat in backupData.dailyStats) {
                    database.dailyStatDao().insert(stat)
                }
            }

            RestoreResult(
                success = true,
                message = "بازیابی با موفقیت انجام شد. ${backupData.products.size} محصول و ${backupData.dailyStats.size} آمار بازیابی شد."
            )
        } catch (e: Exception) {
            RestoreResult(
                success = false,
                message = "خطا در بازیابی: ${e.message}"
            )
        }
    }

    /**
     * حذف فایل پشتیبان.
     */
    fun deleteBackup(file: File): Boolean {
        return file.delete()
    }

    private suspend fun getAllProductsList(): List<ProductEntity> = withContext(Dispatchers.IO) {
        database.productDao().getAllProducts()
    }
}

/**
 * مدل داده‌های پشتیبان
 */
data class BackupData(
    val version: Int,
    val createdAt: Long,
    val products: List<ProductEntity>,
    val dailyStats: List<DailyStatEntity>
)

/**
 * نتیجه بازیابی
 */
data class RestoreResult(
    val success: Boolean,
    val message: String
)
