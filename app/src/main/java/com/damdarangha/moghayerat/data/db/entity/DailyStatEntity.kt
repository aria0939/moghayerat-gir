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
    // ===== عملیات روزانه — کل (روز + شب) =====
    val receiptTotal: Int,
    val returnsTotal: Int,              // برگشت به انبار (به موجودی اضافه می‌شود)
    val giftsTotal: Int,
    val wasteTotal: Int,
    val laboratoryTotal: Int,               // آزمایشگاه (از موجودی کم می‌شود)
    val midDayLoadingTotal: Int,        // بارگیری میان روز (از موجودی کم می‌شود)
    // ===== عملیات روزانه — فقط روز (اول کار) =====
    // این مقادیر در مرحله اول کار وارد می‌شوند و جدا از مقادیر شب ذخیره می‌شوند.
    // مقدار شب = کل - روز (محاسبه می‌شود، ذخیره نمی‌شود).
    val dayReceiptTotal: Int = 0,
    val dayReturnsTotal: Int = 0,
    val dayGiftsTotal: Int = 0,
    val dayWasteTotal: Int = 0,
    val dayLaboratoryTotal: Int = 0,
    val dayMidDayLoadingTotal: Int = 0,
    // ===== فیلدهای شب =====
    val nightLoadingTotal: Int,
    val currentStockTotal: Int? = null,      // null = شمارش آخر کار ثبت‌نشده، Int (شامل 0) = ثبت‌شده
    // مقادیر محاسبه‌شده
    val expectedMorningStockTotal: Int,
    val morningVariance: Int?,
    val expectedClosingStockTotal: Int,
    val closingVariance: Int,
    // مغایرت‌های جدید (قرارداد جدید: منفی=کسری، مثبت=اضافه)
    val startVariance: Int? = null,               // مغایرت اول کار
    val endCumulativeVariance: Int? = null,       // مغایرت تجمعی آخر کار
    val nightShiftVariance: Int? = null,            // مغایرت واقعی شیفت شب
    // وضعیت
    val isFinalized: Boolean = false,
    val notes: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
