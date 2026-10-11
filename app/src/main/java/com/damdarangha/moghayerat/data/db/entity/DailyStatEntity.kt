package com.damdarangha.moghayerat.data.db.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * موجودیت آمار روزانه یک محصول
 *
 * برای هر (محصول، تاریخ) فقط یک رکورد وجود دارد.
 * مقادیر به‌صورت تعداد کل واحد (Int) ذخیره می‌شوند، نه باکس و عدد.
 * این کار محاسبات را ساده‌تر می‌کند و از خطای گرد کردن جلوگیری می‌کند.
 */
@Entity(
    tableName = "daily_stats",
    foreignKeys = [
        ForeignKey(
            entity = ProductEntity::class,
            parentColumns = ["id"],
            childColumns = ["productId"],
            onDelete = ForeignKey.RESTRICT // جلوگیری از حذف محصولی که آمار دارد
        )
    ],
    indices = [
        Index(value = ["productId", "isoDate"], unique = true) // یک آمار برای هر محصول و تاریخ
    ]
)
data class DailyStatEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val productId: Long,
    val isoDate: String,        // تاریخ میلادی به فرمت yyyy-MM-dd
    val jalaliDate: String,     // تاریخ شمسی به فرمت yyyy/MM/dd برای نمایش
    // مقادیر به‌صورت تعداد کل واحد ذخیره می‌شوند
    val openingStockTotal: Int,
    val morningLoadingTotal: Int,
    val actualMorningStockTotal: Int?,  // null اگر کاربر وارد نکرده باشد
    val receiptTotal: Int,
    val returnsTotal: Int,              // برگشت به انبار (به موجودی اضافه می‌شود)
    val giftsTotal: Int,
    val wasteTotal: Int,
    val midDayLoadingTotal: Int,        // بارگیری میان روز (از موجودی کم می‌شود)
    val nightLoadingTotal: Int,
    val currentStockTotal: Int,
    // مقادیر محاسبه‌شده
    val expectedMorningStockTotal: Int,
    val morningVariance: Int?,
    val expectedClosingStockTotal: Int,
    val closingVariance: Int,
    // وضعیت
    val isFinalized: Boolean = false,  // آیا آمار نهایی شده است
    val notes: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
