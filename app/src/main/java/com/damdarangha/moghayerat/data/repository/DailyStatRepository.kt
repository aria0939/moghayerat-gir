package com.damdarangha.moghayerat.data.repository

import com.damdarangha.moghayerat.data.db.dao.DailyStatDao
import com.damdarangha.moghayerat.data.db.dao.StatHistoryDao
import com.damdarangha.moghayerat.data.db.entity.DailyStatEntity
import com.damdarangha.moghayerat.data.db.entity.StatHistoryEntity
import com.damdarangha.moghayerat.domain.calculation.VarianceCalculator
import com.damdarangha.moghayerat.domain.model.BoxAndUnits
import com.damdarangha.moghayerat.domain.model.DailyStatInput
import com.damdarangha.moghayerat.domain.model.Product
import com.damdarangha.moghayerat.domain.model.VarianceResult
import com.damdarangha.moghayerat.util.JalaliDateConverter
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Repository برای آمار روزانه.
 *
 * مسئولیت‌ها:
 * - ذخیره و بازیابی آمار روزانه
 * - محاسبه مغایرت با استفاده از VarianceCalculator
 * - مدیریت وضعیت پیش‌نویس و نهایی
 * - انتقال مانده پایان روز به روز بعد
 * - ردیابی تغییرات در تاریخچه
 */
class DailyStatRepository(
    private val dailyStatDao: DailyStatDao,
    private val statHistoryDao: StatHistoryDao,
    private val gson: Gson
) {

    suspend fun getStat(productId: Long, isoDate: String): DailyStatEntity? =
        dailyStatDao.getStat(productId, isoDate)

    fun observeStat(productId: Long, isoDate: String): Flow<DailyStatEntity?> =
        dailyStatDao.observeStat(productId, isoDate)

    fun observeStatsForDate(isoDate: String): Flow<List<DailyStatEntity>> =
        dailyStatDao.observeStatsForDate(isoDate)

    suspend fun getStatsForDate(isoDate: String): List<DailyStatEntity> =
        dailyStatDao.getStatsForDate(isoDate)

    fun observeStatsForDateRange(startDate: String, endDate: String): Flow<List<DailyStatEntity>> =
        dailyStatDao.observeStatsForDateRange(startDate, endDate)

    fun observeProductHistory(productId: Long): Flow<List<DailyStatEntity>> =
        dailyStatDao.observeProductHistory(productId)

    fun observeZeroVarianceStats(isoDate: String): Flow<List<DailyStatEntity>> =
        dailyStatDao.observeZeroVarianceStats(isoDate)

    fun observeDeficitStats(isoDate: String): Flow<List<DailyStatEntity>> =
        dailyStatDao.observeDeficitStats(isoDate)

    fun observeSurplusStats(isoDate: String): Flow<List<DailyStatEntity>> =
        dailyStatDao.observeSurplusStats(isoDate)

    suspend fun countStatsForDate(isoDate: String): Int =
        dailyStatDao.countStatsForDate(isoDate)

    suspend fun countVarianceStatsForDate(isoDate: String): Int =
        dailyStatDao.countVarianceStatsForDate(isoDate)

    /**
     * دریافت آخرین آمار قبل از تاریخ مشخص (برای انتقال مانده).
     */
    suspend fun getLatestStatBeforeDate(productId: Long, referenceDate: String): DailyStatEntity? =
        dailyStatDao.getLatestStatBeforeDate(productId, referenceDate)

    /**
     * ذخیره آمار روزانه.
     * اگر آمار از قبل وجود داشته باشد، آن را به‌روزرسانی می‌کند.
     * تاریخچه تغییرات نیز ذخیره می‌شود.
     */
    suspend fun saveStat(
        input: DailyStatInput,
        product: Product,
        isFinalized: Boolean = false
    ): Long {
        val result = VarianceCalculator.calculate(input, product)
        val jalaliDate = JalaliDateConverter.gregorianToJalaliString(input.isoDate)
        val now = System.currentTimeMillis()

        val existing = dailyStatDao.getStat(input.productId, input.isoDate)
        val entity = DailyStatEntity(
            id = existing?.id ?: 0L,
            productId = input.productId,
            isoDate = input.isoDate,
            jalaliDate = jalaliDate,
            openingStockTotal = result.openingStockTotal,
            morningLoadingTotal = result.morningLoadingTotal,
            actualMorningStockTotal = result.actualMorningStockTotal,
            receiptTotal = result.receiptTotal,
            giftsTotal = result.giftsTotal,
            wasteTotal = result.wasteTotal,
            nightLoadingTotal = result.nightLoadingTotal,
            currentStockTotal = result.currentStockTotal,
            expectedMorningStockTotal = result.expectedMorningStockTotal,
            morningVariance = result.morningVariance,
            expectedClosingStockTotal = result.expectedClosingStockTotal,
            closingVariance = result.closingVariance,
            isFinalized = isFinalized,
            notes = input.notes,
            createdAt = existing?.createdAt ?: now,
            updatedAt = now
        )

        val id = if (existing != null) {
            dailyStatDao.update(entity)
            // ثبت در تاریخچه
            statHistoryDao.insert(
                StatHistoryEntity(
                    statId = existing.id,
                    productId = input.productId,
                    isoDate = input.isoDate,
                    changeType = "UPDATE",
                    previousValue = gson.toJson(existing),
                    newValue = gson.toJson(entity),
                    note = "به‌روزرسانی آمار"
                )
            )
            existing.id
        } else {
            val newId = dailyStatDao.insert(entity)
            statHistoryDao.insert(
                StatHistoryEntity(
                    statId = newId,
                    productId = input.productId,
                    isoDate = input.isoDate,
                    changeType = "CREATE",
                    previousValue = null,
                    newValue = gson.toJson(entity),
                    note = "ایجاد آمار جدید"
                )
            )
            newId
        }

        return id
    }

    /**
     * نهایی کردن آمار.
     */
    suspend fun finalizeStat(id: Long) {
        dailyStatDao.finalize(id)
    }

    /**
     * لغو نهایی‌سازی آمار.
     */
    suspend fun unfinalizeStat(id: Long) {
        dailyStatDao.unfinalize(id)
    }

    /**
     * حذف آمار.
     */
    suspend fun deleteStat(id: Long) {
        dailyStatDao.delete(id)
        statHistoryDao.deleteForStat(id)
    }

    /**
     * دریافت مانده پایان روز قبل برای استفاده به‌عنوان مانده شب گذشته روز جاری.
     * اگر آمار روز قبل وجود نداشت، null برمی‌گرداند.
     */
    suspend fun getCarryOverStock(productId: Long, currentIsoDate: String): BoxAndUnits? {
        val previousStat = dailyStatDao.getPreviousDayStat(productId, currentIsoDate) ?: return null
        // تبدیل تعداد کل به باکس و عدد با استفاده از محصول فعلی
        // نیاز به محصول داریم، اما برای سادگی، تعداد کل را برمی‌گردانیم
        // تبدیل در ViewModel انجام می‌شود
        return BoxAndUnits(boxes = previousStat.currentStockTotal, units = 0)
            // این مقدار موقت است و در ViewModel با اطلاعات محصول نرمال‌سازی می‌شود
    }

    /**
     * دریافت مانده پایان روز قبل به‌صورت تعداد کل واحد.
     */
    suspend fun getPreviousClosingStockTotal(productId: Long, currentIsoDate: String): Int? {
        return dailyStatDao.getPreviousDayStat(productId, currentIsoDate)?.currentStockTotal
    }
}
