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
     *
     * این متد یک ذخیره کامل است و همه فیلدها را بازنویسی می‌کند.
     * برای ذخیره مرحله‌ای (فقط اول کار یا فقط آخر کار) از [saveStartStage] یا [saveEndStage] استفاده کنید.
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
            returnsTotal = result.returnsTotal,
            giftsTotal = result.giftsTotal,
            wasteTotal = result.wasteTotal,
            laboratoryTotal = result.laboratoryTotal,
            midDayLoadingTotal = result.midDayLoadingTotal,
            nightLoadingTotal = result.nightLoadingTotal,
            currentStockTotal = result.currentStockTotal,
            expectedMorningStockTotal = result.expectedMorningStockTotal,
            morningVariance = result.morningVariance,
            expectedClosingStockTotal = result.expectedClosingStockTotal,
            closingVariance = result.closingVariance,
            startVariance = result.startVariance,
            endCumulativeVariance = result.endCumulativeVariance,
            nightShiftVariance = result.nightShiftVariance,
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
     * ذخیره مرحله اول کار (START).
     *
     * فقط فیلدهای مربوط به اول کار را به‌روزرسانی می‌کند و فیلدهای آخر کار را دست نمی‌زند.
     * فیلدهای اول کار:
     * - openingStock (مانده شب گذشته)
     * - morningLoading (بارگیری صبح)
     * - actualMorningStock (شمارش واقعی اول کار)
     * - startVariance (مغایرت اول کار)
     *
     * اگر آمار قبلی برای آخر کار ثبت شده باشد، آن داده‌ها حفظ می‌شوند و
     * nightShiftVariance بر اساس startVariance جدید و endCumulativeVariance قدیمی محاسبه می‌شود.
     *
     * @param input ورودی آمار - فقط فیلدهای اول کار استفاده می‌شوند
     * @param product محصول موردنظر
     * @param isFinalized آیا این مرحله نهایی شود
     * @return شناسه آمار ذخیره‌شده
     */
    suspend fun saveStartStage(
        input: DailyStatInput,
        product: Product,
        isFinalized: Boolean = false
    ): Long {
        val now = System.currentTimeMillis()
        val existing = dailyStatDao.getStat(input.productId, input.isoDate)
        val jalaliDate = JalaliDateConverter.gregorianToJalaliString(input.isoDate)

        // محاسبه کامل برای استخراج مقادیر اول کار و عملیات روزانه
        val fullResult = VarianceCalculator.calculate(input, product)

        // حفظ داده‌های آخر کار از رکورد موجود (در صورت وجود)
        // نکته: عملیات روزانه (receipt, returns, gifts, waste, lab, midDay) از input استفاده می‌کنند
        // زیرا در START نیز قابل ویرایش هستند. فقط nightLoading و currentStock حفظ می‌شوند.
        val currentStockTotal: Int? = existing?.currentStockTotal
        val nightLoadingTotal: Int = existing?.nightLoadingTotal ?: fullResult.nightLoadingTotal
        val receiptTotal: Int = fullResult.receiptTotal
        val returnsTotal: Int = fullResult.returnsTotal
        val giftsTotal: Int = fullResult.giftsTotal
        val wasteTotal: Int = fullResult.wasteTotal
        val laboratoryTotal: Int = fullResult.laboratoryTotal
        val midDayLoadingTotal: Int = fullResult.midDayLoadingTotal

        // محاسبه دوباره expectedClosingStock بر اساس openingStock جدید + عملیات روزانه + nightLoading موجود
        // expected = opening + receipt + returns - morning - midDay - night - gifts - waste - lab
        val expectedClosingStockTotal = (
            fullResult.openingStockTotal +
            receiptTotal + returnsTotal -
            fullResult.morningLoadingTotal -
            midDayLoadingTotal - nightLoadingTotal -
            giftsTotal - wasteTotal - laboratoryTotal
        )

        // محاسبه closingVariance و endCumulativeVariance اگر currentStock داریم
        // نکته: این مقادیر مجدداً محاسبه می‌شوند چون openingStock (و در نتیجه expected) تغییر کرده
        val closingVariance: Int = if (currentStockTotal != null) {
            expectedClosingStockTotal - currentStockTotal
        } else 0

        val endCumulativeVariance: Int? = if (currentStockTotal != null) {
            // actual - expected (منفی=کسری، مثبت=اضافه)
            currentStockTotal - expectedClosingStockTotal
        } else null

        // محاسبه nightShiftVariance اگر هر دو موجود باشند
        val nightShiftVariance: Int? = if (fullResult.startVariance != null && endCumulativeVariance != null) {
            endCumulativeVariance - fullResult.startVariance
        } else null

        val entity = DailyStatEntity(
            id = existing?.id ?: 0L,
            productId = input.productId,
            isoDate = input.isoDate,
            jalaliDate = jalaliDate,
            openingStockTotal = fullResult.openingStockTotal,
            morningLoadingTotal = fullResult.morningLoadingTotal,
            actualMorningStockTotal = fullResult.actualMorningStockTotal,
            receiptTotal = receiptTotal,
            returnsTotal = returnsTotal,
            giftsTotal = giftsTotal,
            wasteTotal = wasteTotal,
            laboratoryTotal = laboratoryTotal,
            midDayLoadingTotal = midDayLoadingTotal,
            nightLoadingTotal = nightLoadingTotal,
            currentStockTotal = currentStockTotal,
            expectedMorningStockTotal = fullResult.expectedMorningStockTotal,
            morningVariance = fullResult.morningVariance,
            expectedClosingStockTotal = expectedClosingStockTotal,
            closingVariance = closingVariance,
            startVariance = fullResult.startVariance,
            endCumulativeVariance = endCumulativeVariance,
            nightShiftVariance = nightShiftVariance,
            isFinalized = isFinalized,
            notes = input.notes.ifEmpty { existing?.notes ?: "" },
            createdAt = existing?.createdAt ?: now,
            updatedAt = now
        )

        return persistEntity(entity, existing, "SAVE_START_STAGE")
    }

    /**
     * ذخیره مرحله آخر کار (END).
     *
     * فقط فیلدهای مربوط به آخر کار را به‌روزرسانی می‌کند و فیلدهای اول کار را دست نمی‌زند.
     * فیلدهای آخر کار:
     * - receipt, returns, gifts, waste, laboratory, midDayLoading (عملیات روزانه)
     * - nightLoading (بارگیری شب)
     * - currentStock (مانده واقعی آخر کار)
     * - endCumulativeVariance (مغایرت تجمعی آخر کار)
     * - nightShiftVariance (مغایرت شیفت شب)
     *
     * اگر آمار قبلی برای اول کار ثبت شده باشد، آن داده‌ها حفظ می‌شوند.
     *
     * @param input ورودی آمار - فقط فیلدهای آخر کار استفاده می‌شوند
     * @param product محصول موردنظر
     * @param isFinalized آیا این مرحله نهایی شود
     * @return شناسه آمار ذخیره‌شده
     */
    suspend fun saveEndStage(
        input: DailyStatInput,
        product: Product,
        isFinalized: Boolean = false
    ): Long {
        val now = System.currentTimeMillis()
        val existing = dailyStatDao.getStat(input.productId, input.isoDate)
        val jalaliDate = JalaliDateConverter.gregorianToJalaliString(input.isoDate)

        // محاسبه کامل برای استخراج مقادیر آخر کار
        val fullResult = VarianceCalculator.calculate(input, product)

        // اگر آمار اول کار قبلاً ثبت شده، آن را حفظ می‌کنیم
        val openingStockTotal: Int = existing?.openingStockTotal ?: fullResult.openingStockTotal
        val morningLoadingTotal: Int = existing?.morningLoadingTotal ?: fullResult.morningLoadingTotal
        val actualMorningStockTotal: Int? = existing?.actualMorningStockTotal ?: fullResult.actualMorningStockTotal
        val startVariance: Int? = existing?.startVariance ?: fullResult.startVariance
        val expectedMorningStockTotal: Int = existing?.expectedMorningStockTotal
            ?: fullResult.expectedMorningStockTotal
        val morningVariance: Int? = existing?.morningVariance ?: fullResult.morningVariance

        // محاسبه expectedClosingStock بر اساس داده‌های آخر کار + opening قدیمی
        // expected = opening + receipt + returns - morning - midDay - night - gifts - waste - lab
        val expectedClosingStockTotal = (
            openingStockTotal +
            fullResult.receiptTotal + fullResult.returnsTotal -
            morningLoadingTotal -
            fullResult.midDayLoadingTotal - fullResult.nightLoadingTotal -
            fullResult.giftsTotal - fullResult.wasteTotal - fullResult.laboratoryTotal
        )

        val currentStockTotal: Int? = fullResult.currentStockTotal
        val closingVariance: Int = if (currentStockTotal != null) {
            expectedClosingStockTotal - currentStockTotal
        } else 0

        // محاسبه endCumulativeVariance و nightShiftVariance
        // قرارداد: منفی=کسری، مثبت=اضافه
        val endCumulativeVariance: Int? = if (currentStockTotal != null) {
            currentStockTotal - expectedClosingStockTotal
        } else null

        val nightShiftVariance: Int? = if (startVariance != null && endCumulativeVariance != null) {
            endCumulativeVariance - startVariance
        } else null

        val entity = DailyStatEntity(
            id = existing?.id ?: 0L,
            productId = input.productId,
            isoDate = input.isoDate,
            jalaliDate = jalaliDate,
            openingStockTotal = openingStockTotal,
            morningLoadingTotal = morningLoadingTotal,
            actualMorningStockTotal = actualMorningStockTotal,
            receiptTotal = fullResult.receiptTotal,
            returnsTotal = fullResult.returnsTotal,
            giftsTotal = fullResult.giftsTotal,
            wasteTotal = fullResult.wasteTotal,
            laboratoryTotal = fullResult.laboratoryTotal,
            midDayLoadingTotal = fullResult.midDayLoadingTotal,
            nightLoadingTotal = fullResult.nightLoadingTotal,
            currentStockTotal = currentStockTotal,
            expectedMorningStockTotal = expectedMorningStockTotal,
            morningVariance = morningVariance,
            expectedClosingStockTotal = expectedClosingStockTotal,
            closingVariance = closingVariance,
            startVariance = startVariance,
            endCumulativeVariance = endCumulativeVariance,
            nightShiftVariance = nightShiftVariance,
            isFinalized = isFinalized,
            notes = input.notes.ifEmpty { existing?.notes ?: "" },
            createdAt = existing?.createdAt ?: now,
            updatedAt = now
        )

        return persistEntity(entity, existing, "SAVE_END_STAGE")
    }

    /**
     * متد کمکی برای ذخیره موجودیت و ثبت در تاریخچه.
     */
    private suspend fun persistEntity(
        entity: DailyStatEntity,
        existing: DailyStatEntity?,
        changeType: String
    ): Long {
        return if (existing != null) {
            dailyStatDao.update(entity)
            statHistoryDao.insert(
                StatHistoryEntity(
                    statId = existing.id,
                    productId = entity.productId,
                    isoDate = entity.isoDate,
                    changeType = changeType,
                    previousValue = gson.toJson(existing),
                    newValue = gson.toJson(entity),
                    note = when (changeType) {
                        "SAVE_START_STAGE" -> "ذخیره مرحله اول کار"
                        "SAVE_END_STAGE" -> "ذخیره مرحله آخر کار"
                        else -> "به‌روزرسانی آمار"
                    }
                )
            )
            existing.id
        } else {
            val newId = dailyStatDao.insert(entity)
            statHistoryDao.insert(
                StatHistoryEntity(
                    statId = newId,
                    productId = entity.productId,
                    isoDate = entity.isoDate,
                    changeType = "CREATE",
                    previousValue = null,
                    newValue = gson.toJson(entity),
                    note = when (changeType) {
                        "SAVE_START_STAGE" -> "ایجاد با مرحله اول کار"
                        "SAVE_END_STAGE" -> "ایجاد با مرحله آخر کار"
                        else -> "ایجاد آمار جدید"
                    }
                )
            )
            newId
        }
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
     * اگر آمار روز قبل وجود نداشت یا شمارش آخر کار ثبت نشده بود، null برمی‌گرداند.
     */
    suspend fun getCarryOverStock(productId: Long, currentIsoDate: String): BoxAndUnits? {
        val previousStat = dailyStatDao.getPreviousDayStat(productId, currentIsoDate) ?: return null
        // اگر آخر کار ثبت نشده باشد، مانده قابل اتکایی هم نداریم
        val previousTotal = previousStat.currentStockTotal ?: return null
        return BoxAndUnits(boxes = previousTotal, units = 0)
    }

    /**
     * دریافت مانده پایان روز قبل به‌صورت تعداد کل واحد.
     */
    suspend fun getPreviousClosingStockTotal(productId: Long, currentIsoDate: String): Int? {
        return dailyStatDao.getPreviousDayStat(productId, currentIsoDate)?.currentStockTotal
    }
}
