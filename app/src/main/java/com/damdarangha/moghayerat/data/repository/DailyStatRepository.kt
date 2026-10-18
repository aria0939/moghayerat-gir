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
     * دریافت «تاریخ فعال» — تاریخی که باید آمارها روی آن کار شوند.
     *
     * منطق:
     * ۱. اگر آمار غیر نهایی‌شده‌ای وجود دارد، تاریخ آخرین غیر نهایی‌شده برگردانده می‌شود.
     *    این برای وقتی است که شیفت از نیمه‌شب عبور کرده ولی هنوز «آخر کار» ثبت نشده.
     * ۲. در غیر این صورت، امروز برگردانده می‌شود.
     */
    suspend fun getActiveDate(): String {
        val unfinalized = dailyStatDao.getLatestUnfinalizedDate()
        return unfinalized ?: JalaliDateConverter.todayIso()
    }

    /**
     * دریافت آخرین تاریخ آمار برای محصول خاص.
     */
    suspend fun getLatestStatDateForProduct(productId: Long): String? =
        dailyStatDao.getLatestStatDateForProduct(productId)

    /**
     * دریافت آخرین آمار غیر نهایی‌شده برای محصول خاص.
     */
    suspend fun getLatestUnfinalizedStatForProduct(productId: Long): DailyStatEntity? =
        dailyStatDao.getLatestUnfinalizedStatForProduct(productId)

    /**
     * دریافت «تاریخ فعال» برای محصول خاص.
     * اگر محصول آمار غیر نهایی‌شده دارد، تاریخ آن برگردانده می‌شود.
     * در غیر این صورت، امروز.
     */
    suspend fun getActiveDateForProduct(productId: Long): String {
        val unfinalized = dailyStatDao.getLatestUnfinalizedStatForProduct(productId)
        return unfinalized?.isoDate ?: JalaliDateConverter.todayIso()
    }

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
            // در حالت FULL، مقادیر day* از existing حفظ می‌شوند (یا ۰ اگر رکورد جدید)
            dayReceiptTotal = existing?.dayReceiptTotal ?: 0,
            dayReturnsTotal = existing?.dayReturnsTotal ?: 0,
            dayGiftsTotal = existing?.dayGiftsTotal ?: 0,
            dayWasteTotal = existing?.dayWasteTotal ?: 0,
            dayLaboratoryTotal = existing?.dayLaboratoryTotal ?: 0,
            dayMidDayLoadingTotal = existing?.dayMidDayLoadingTotal ?: 0,
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

        // محاسبه کامل برای استخراج مقادیر اول کار و عملیات روزانه روز
        val fullResult = VarianceCalculator.calculate(input, product)

        // حفظ داده‌های آخر کار از رکورد موجود (در صورت وجود)
        val currentStockTotal: Int? = existing?.currentStockTotal
        val nightLoadingTotal: Int = existing?.nightLoadingTotal ?: fullResult.nightLoadingTotal

        // عملیات روزانه روز — این مقادیر از input می‌آیند (در START وارد شده‌اند)
        val dayReceiptTotal: Int = fullResult.receiptTotal
        val dayReturnsTotal: Int = fullResult.returnsTotal
        val dayGiftsTotal: Int = fullResult.giftsTotal
        val dayWasteTotal: Int = fullResult.wasteTotal
        val dayLaboratoryTotal: Int = fullResult.laboratoryTotal
        val dayMidDayLoadingTotal: Int = fullResult.midDayLoadingTotal

        // محاسبه مقادیر شب از رکورد موجود
        // شب = کل - روز (اگر رکورد موجود باشد)
        val nightReceipt: Int = (existing?.receiptTotal ?: 0) - (existing?.dayReceiptTotal ?: 0)
        val nightReturns: Int = (existing?.returnsTotal ?: 0) - (existing?.dayReturnsTotal ?: 0)
        val nightGifts: Int = (existing?.giftsTotal ?: 0) - (existing?.dayGiftsTotal ?: 0)
        val nightWaste: Int = (existing?.wasteTotal ?: 0) - (existing?.dayWasteTotal ?: 0)
        val nightLaboratory: Int = (existing?.laboratoryTotal ?: 0) - (existing?.dayLaboratoryTotal ?: 0)
        val nightMidDay: Int = (existing?.midDayLoadingTotal ?: 0) - (existing?.dayMidDayLoadingTotal ?: 0)

        // کل = روز + شب
        val totalReceipt = dayReceiptTotal + nightReceipt
        val totalReturns = dayReturnsTotal + nightReturns
        val totalGifts = dayGiftsTotal + nightGifts
        val totalWaste = dayWasteTotal + nightWaste
        val totalLaboratory = dayLaboratoryTotal + nightLaboratory
        val totalMidDay = dayMidDayLoadingTotal + nightMidDay

        // محاسبه expectedClosingStock با کل (روز + شب)
        val expectedClosingStockTotal = (
            fullResult.openingStockTotal +
            totalReceipt + totalReturns -
            fullResult.morningLoadingTotal -
            totalMidDay - nightLoadingTotal -
            totalGifts - totalWaste - totalLaboratory
        )

        val closingVariance: Int = if (currentStockTotal != null) {
            expectedClosingStockTotal - currentStockTotal
        } else 0

        val endCumulativeVariance: Int? = if (currentStockTotal != null) {
            currentStockTotal - expectedClosingStockTotal
        } else null

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
            // کل = روز + شب
            receiptTotal = totalReceipt,
            returnsTotal = totalReturns,
            giftsTotal = totalGifts,
            wasteTotal = totalWaste,
            laboratoryTotal = totalLaboratory,
            midDayLoadingTotal = totalMidDay,
            // فقط روز (از input)
            dayReceiptTotal = dayReceiptTotal,
            dayReturnsTotal = dayReturnsTotal,
            dayGiftsTotal = dayGiftsTotal,
            dayWasteTotal = dayWasteTotal,
            dayLaboratoryTotal = dayLaboratoryTotal,
            dayMidDayLoadingTotal = dayMidDayLoadingTotal,
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

        // محاسبه کامل برای استخراج مقادیر شب (از input)
        val fullResult = VarianceCalculator.calculate(input, product)

        // حفظ داده‌های اول کار از رکورد موجود
        val openingStockTotal: Int = existing?.openingStockTotal ?: fullResult.openingStockTotal
        val morningLoadingTotal: Int = existing?.morningLoadingTotal ?: fullResult.morningLoadingTotal
        val actualMorningStockTotal: Int? = existing?.actualMorningStockTotal ?: fullResult.actualMorningStockTotal
        val startVariance: Int? = existing?.startVariance ?: fullResult.startVariance
        val expectedMorningStockTotal: Int = existing?.expectedMorningStockTotal
            ?: fullResult.expectedMorningStockTotal
        val morningVariance: Int? = existing?.morningVariance ?: fullResult.morningVariance

        // حفظ مقادیر روز از رکورد موجود
        val dayReceiptTotal: Int = existing?.dayReceiptTotal ?: 0
        val dayReturnsTotal: Int = existing?.dayReturnsTotal ?: 0
        val dayGiftsTotal: Int = existing?.dayGiftsTotal ?: 0
        val dayWasteTotal: Int = existing?.dayWasteTotal ?: 0
        val dayLaboratoryTotal: Int = existing?.dayLaboratoryTotal ?: 0
        val dayMidDayLoadingTotal: Int = existing?.dayMidDayLoadingTotal ?: 0

        // مقادیر شب از input (این مقادیر در فرم آخر کار وارد شده‌اند)
        val nightReceipt: Int = fullResult.receiptTotal
        val nightReturns: Int = fullResult.returnsTotal
        val nightGifts: Int = fullResult.giftsTotal
        val nightWaste: Int = fullResult.wasteTotal
        val nightLaboratory: Int = fullResult.laboratoryTotal
        val nightMidDay: Int = fullResult.midDayLoadingTotal

        // کل = روز + شب
        val totalReceipt = dayReceiptTotal + nightReceipt
        val totalReturns = dayReturnsTotal + nightReturns
        val totalGifts = dayGiftsTotal + nightGifts
        val totalWaste = dayWasteTotal + nightWaste
        val totalLaboratory = dayLaboratoryTotal + nightLaboratory
        val totalMidDay = dayMidDayLoadingTotal + nightMidDay

        // محاسبه expectedClosingStock با کل (روز + شب)
        val expectedClosingStockTotal = (
            openingStockTotal +
            totalReceipt + totalReturns -
            morningLoadingTotal -
            totalMidDay - fullResult.nightLoadingTotal -
            totalGifts - totalWaste - totalLaboratory
        )

        val currentStockTotal: Int? = fullResult.currentStockTotal
        val closingVariance: Int = if (currentStockTotal != null) {
            expectedClosingStockTotal - currentStockTotal
        } else 0

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
            // کل = روز + شب
            receiptTotal = totalReceipt,
            returnsTotal = totalReturns,
            giftsTotal = totalGifts,
            wasteTotal = totalWaste,
            laboratoryTotal = totalLaboratory,
            midDayLoadingTotal = totalMidDay,
            // فقط روز (حفظ از رکورد موجود)
            dayReceiptTotal = dayReceiptTotal,
            dayReturnsTotal = dayReturnsTotal,
            dayGiftsTotal = dayGiftsTotal,
            dayWasteTotal = dayWasteTotal,
            dayLaboratoryTotal = dayLaboratoryTotal,
            dayMidDayLoadingTotal = dayMidDayLoadingTotal,
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
