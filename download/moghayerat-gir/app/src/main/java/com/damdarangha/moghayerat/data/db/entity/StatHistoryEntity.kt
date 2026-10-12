package com.damdarangha.moghayerat.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * موجودیت تاریخچه تغییرات آمار (برای ردیابی)
 */
@Entity(tableName = "stat_history")
data class StatHistoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val statId: Long,        // شناسه آمار تغییر یافته
    val productId: Long,
    val isoDate: String,
    val changeType: String,  // CREATE, UPDATE, FINALIZE
    val previousValue: String?, // JSON مقدار قبلی
    val newValue: String?,      // JSON مقدار جدید
    val changedAt: Long = System.currentTimeMillis(),
    val note: String = ""
)
