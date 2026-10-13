package com.damdarangha.moghayerat.ui.screen.stat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.damdarangha.moghayerat.MoghayeratApplication
import com.damdarangha.moghayerat.domain.calculation.VarianceCalculator
import com.damdarangha.moghayerat.domain.model.BoxAndUnits
import com.damdarangha.moghayerat.domain.model.DailyStatInput
import com.damdarangha.moghayerat.domain.model.Product
import com.damdarangha.moghayerat.domain.model.VarianceResult
import com.damdarangha.moghayerat.util.JalaliDateConverter
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * مرحله ثبت آمار.
 *
 * - START: فقط اول کار (openingStock، morningLoading، actualMorningStock)
 * - END: فقط آخر کار (receipt، returns، gifts، waste، laboratory، midDayLoading، nightLoading، currentStock)
 * - FULL: هر دو مرحله با هم (حالت قدیمی)
 */
enum class StatEntryStage {
    START, END, FULL;

    companion object {
        fun fromString(value: String?): StatEntryStage = when (value?.uppercase()) {
            "START" -> START
            "END" -> END
            else -> FULL
        }
    }
}

/**
 * وضعیت فرم ورودی آمار روزانه.
 *
 * این وضعیت تمام فیلدهای فرم و نتیجه محاسبه را نگه می‌دارد.
 * نتیجه محاسبه با هر تغییر ورودی به‌صورت زنده به‌روزرسانی می‌شود.
 *
 * نکته مهم: فیلدهای عملیات روزانه به دو گروه تقسیم شده‌اند:
 * - فیلدهای اول کار (startReceipt، startReturns، ...) که در مرحله اول کار وارد می‌شوند.
 * - فیلدهای آخر کار (receipt، returns، ...) که در مرحله آخر کار وارد می‌شوند.
 *
 * این جداسازی باعث می‌شود تغییری که در اول کار داده می‌شود به آخر کار منتقل نشود.
 */
data class StatEntryUiState(
    val product: Product? = null,
    val isoDate: String = "",
    val jalaliDate: String = "",
    // ===== فیلدهای اول کار =====
    val openingStock: BoxAndUnits = BoxAndUnits.ZERO,
    val morningLoading: BoxAndUnits = BoxAndUnits.ZERO,
    val actualMorningStock: BoxAndUnits? = null, // null = فیلد خالی
    val hasActualMorningStock: Boolean = false,  // آیا کاربر این فیلد را پر کرده است
    // عملیات روزانه اول کار (جدا از آخر کار)
    val startReceipt: BoxAndUnits = BoxAndUnits.ZERO,
    val startReturns: BoxAndUnits = BoxAndUnits.ZERO,
    val startGifts: BoxAndUnits = BoxAndUnits.ZERO,
    val startWaste: BoxAndUnits = BoxAndUnits.ZERO,
    val startLaboratory: BoxAndUnits = BoxAndUnits.ZERO,
    val startMidDayLoading: BoxAndUnits = BoxAndUnits.ZERO,
    // ===== فیلدهای آخر کار =====
    val nightLoading: BoxAndUnits = BoxAndUnits.ZERO,
    val currentStock: BoxAndUnits? = null,    // null = شمارش آخر کار ثبت‌نشده
    val hasCurrentStock: Boolean = false,    // آیا کاربر شمارش آخر کار را انجام داده
    // عملیات روزانه آخر کار (جدا از اول کار)
    val receipt: BoxAndUnits = BoxAndUnits.ZERO,
    val returns: BoxAndUnits = BoxAndUnits.ZERO,         // برگشت به انبار
    val gifts: BoxAndUnits = BoxAndUnits.ZERO,
    val waste: BoxAndUnits = BoxAndUnits.ZERO,
    val laboratory: BoxAndUnits = BoxAndUnits.ZERO,      // آزمایشگاه
    val midDayLoading: BoxAndUnits = BoxAndUnits.ZERO,    // بارگیری میان روز
    val notes: String = "",
    val isFinalized: Boolean = false,
    val statId: Long? = null,
    val isSaving: Boolean = false,
    val saveMessage: String? = null,
    val isLoading: Boolean = true,
    val showWarning: String? = null,
    // پرچم‌های وضعیت ثبت هر مرحله (برای نمایش در UI)
    val startStageSaved: Boolean = false,   // آیا مرحله اول کار ثبت شده
    val endStageSaved: Boolean = false      // آیا مرحله آخر کار ثبت شده
) {
    /**
     * محاسبه زنده نتیجه مغایرت.
     *
     * نکته: برای مرحله START، فقط فیلدهای اول کار را در نظر می‌گیرد.
     * برای مرحله END، فقط فیلدهای آخر کار را در نظر می‌گیرد.
     */
    val varianceResult: VarianceResult?
        get() {
            val product = this.product ?: return null
            val actualMorning = if (hasActualMorningStock) actualMorningStock else null
            val input = DailyStatInput(
                productId = product.id,
                isoDate = isoDate,
                openingStock = openingStock,
                morningLoading = morningLoading,
                actualMorningStock = actualMorning,
                receipt = receipt,
                returns = returns,
                gifts = gifts,
                waste = waste,
                laboratory = laboratory,
                midDayLoading = midDayLoading,
                nightLoading = nightLoading,
                currentStock = if (hasCurrentStock) currentStock else null,
                notes = notes
            )
            return VarianceCalculator.calculate(input, product)
        }
}

class StatEntryViewModel(
    private val app: MoghayeratApplication,
    private val productId: Long,
    private val isoDate: String,
    val stage: StatEntryStage
) : ViewModel() {

    private val _uiState = MutableStateFlow(StatEntryUiState(isoDate = isoDate))
    val uiState: StateFlow<StatEntryUiState> = _uiState.asStateFlow()

    // ذخیره خودکار با debounce
    private var autoSaveJob: kotlinx.coroutines.Job? = null
    private val autoSaveDelay = 2000L  // ۲ ثانیه

    /**
     * آیا این مرحله START است.
     */
    val isStartStage: Boolean get() = stage == StatEntryStage.START

    /**
     * آیا این مرحله END است.
     */
    val isEndStage: Boolean get() = stage == StatEntryStage.END

    /**
     * آغاز ذخیره خودکار با debounce.
     * هر بار که فیلدی تغییر می‌کند، تایمر ریست می‌شود.
     * اگر ۲ ثانیه تغییری نباشد، ذخیره انجام می‌شود.
     */
    private fun scheduleAutoSave() {
        autoSaveJob?.cancel()
        autoSaveJob = viewModelScope.launch {
            kotlinx.coroutines.delay(autoSaveDelay)
            // فقط اگر محصول بارگذاری شده و نهایی نشده
            val state = _uiState.value
            if (state.product != null && !state.isFinalized && !state.isSaving) {
                try {
                    saveDraftSilent()
                } catch (_: Exception) {
                    // خطای ذخیره خودکار بی‌صدا نادیده گرفته می‌شود
                }
            }
        }
    }

    /**
     * ساخت DailyStatInput با توجه به مرحله فعلی.
     *
     * - در START: از فیلدهای اول کار (openingStock، morningLoading، actualMorningStock)
     *   و فیلدهای عملیات روزانه اول کار (startReceipt، startReturns، ...) استفاده می‌کند.
     *   فیلدهای آخر کار (nightLoading، currentStock) از state فعلی می‌گیرد که معمولاً ZERO یا
     *   مقادیر بارگذاری‌شده از DB است.
     * - در END: از فیلدهای آخر کار (nightLoading، currentStock) و فیلدهای عملیات روزانه آخر کار
     *   (receipt، returns، ...) استفاده می‌کند. فیلدهای اول کار از state فعلی می‌گیرد.
     * - در FULL: همه فیلدها از state فعلی می‌گیرد (که عملیات روزانه آخر کار را نشان می‌دهد).
     */
    private fun buildInput(): DailyStatInput {
        val state = _uiState.value
        val product = state.product ?: throw IllegalStateException("product null")
        val actualMorning = if (state.hasActualMorningStock) state.actualMorningStock else null

        // انتخاب فیلدهای عملیات روزانه بر اساس مرحله
        val (effReceipt, effReturns, effGifts, effWaste, effLaboratory, effMidDayLoading) = when (stage) {
            StatEntryStage.START -> SixTuple(
                state.startReceipt, state.startReturns, state.startGifts,
                state.startWaste, state.startLaboratory, state.startMidDayLoading
            )
            StatEntryStage.END -> SixTuple(
                state.receipt, state.returns, state.gifts,
                state.waste, state.laboratory, state.midDayLoading
            )
            StatEntryStage.FULL -> SixTuple(
                state.receipt, state.returns, state.gifts,
                state.waste, state.laboratory, state.midDayLoading
            )
        }

        return DailyStatInput(
            productId = product.id,
            isoDate = state.isoDate,
            openingStock = state.openingStock,
            morningLoading = state.morningLoading,
            actualMorningStock = actualMorning,
            receipt = effReceipt,
            returns = effReturns,
            gifts = effGifts,
            waste = effWaste,
            laboratory = effLaboratory,
            midDayLoading = effMidDayLoading,
            nightLoading = state.nightLoading,
            currentStock = if (state.hasCurrentStock) state.currentStock else null,
            notes = state.notes
        )
    }

    /** helper برای برگرداندن ۶ مقدار هم‌زمان */
    private data class SixTuple(
        val a: BoxAndUnits, val b: BoxAndUnits, val c: BoxAndUnits,
        val d: BoxAndUnits, val e: BoxAndUnits, val f: BoxAndUnits
    )

    /**
     * ذخیره پیش‌نویس بدون نمایش پیام.
     * بر اساس مرحله فعلی، از متد مناسب repository استفاده می‌کند.
     */
    private suspend fun saveDraftSilent() {
        val state = _uiState.value
        val product = state.product ?: return
        val input = buildInput()

        val savedId = when (stage) {
            StatEntryStage.START -> app.dailyStatRepository.saveStartStage(input, product, isFinalized = false)
            StatEntryStage.END -> app.dailyStatRepository.saveEndStage(input, product, isFinalized = false)
            StatEntryStage.FULL -> app.dailyStatRepository.saveStat(input, product, isFinalized = false)
        }
        _uiState.update {
            it.copy(
                statId = savedId,
                startStageSaved = stage == StatEntryStage.START || it.startStageSaved,
                endStageSaved = stage == StatEntryStage.END || it.endStageSaved
            )
        }
    }

    init {
        loadInitialData()
    }

    private fun loadInitialData() {
        viewModelScope.launch {
            // بارگذاری محصول
            val product = app.productRepository.getById(productId)
            if (product == null) {
                _uiState.update { it.copy(isLoading = false) }
                return@launch
            }

            // تبدیل تاریخ میلادی به شمسی
            val jalaliDate = JalaliDateConverter.gregorianToJalaliString(isoDate)

            // بارگذاری آمار موجود (در صورت وجود)
            val existingStat = app.dailyStatRepository.getStat(productId, isoDate)

            // بارگذاری مانده پایان روز قبل (در صورت وجود و در صورت عدم وجود آمار موجود)
            val previousStockTotal = if (existingStat == null) {
                app.dailyStatRepository.getPreviousClosingStockTotal(productId, isoDate)
            } else null

            val previousOpeningStock = previousStockTotal?.let { total ->
                VarianceCalculator.fromTotalUnits(total, product)
            } ?: BoxAndUnits.ZERO

            if (existingStat != null) {
                // اگر آمار موجود است، مقادیر آن را بارگذاری می‌کنیم
                // نکته کلیدی: از currentStockTotal != null برای تشخیص hasCurrentStock استفاده می‌کنیم
                // (نه currentStockTotal > 0) زیرا صفر یک مقدار معتبر برای «ثبت‌شده با مقدار صفر» است.
                //
                // نکته در مورد فیلدهای start*: چون در DB فقط یک ستون receiptTotal داریم،
                // مقدار آن را در هر دو فیلد startReceipt و receipt کپی می‌کنیم. این فقط مقدار اولیه
                // است؛ وقتی کاربر در START یا END تغییری می‌دهد، فقط فیلد همان مرحله به‌روزرسانی می‌شود
                // و در فیلد دیگر تغییری ایجاد نمی‌شود.
                _uiState.update {
                    val receiptBU = VarianceCalculator.fromTotalUnits(existingStat.receiptTotal, product)
                    val returnsBU = VarianceCalculator.fromTotalUnits(existingStat.returnsTotal, product)
                    val giftsBU = VarianceCalculator.fromTotalUnits(existingStat.giftsTotal, product)
                    val wasteBU = VarianceCalculator.fromTotalUnits(existingStat.wasteTotal, product)
                    val laboratoryBU = VarianceCalculator.fromTotalUnits(existingStat.laboratoryTotal, product)
                    val midDayBU = VarianceCalculator.fromTotalUnits(existingStat.midDayLoadingTotal, product)
                    it.copy(
                        product = product,
                        jalaliDate = jalaliDate,
                        openingStock = VarianceCalculator.fromTotalUnits(existingStat.openingStockTotal, product),
                        morningLoading = VarianceCalculator.fromTotalUnits(existingStat.morningLoadingTotal, product),
                        actualMorningStock = existingStat.actualMorningStockTotal?.let { t ->
                            VarianceCalculator.fromTotalUnits(t, product)
                        },
                        hasActualMorningStock = existingStat.actualMorningStockTotal != null,
                        // عملیات روزانه اول کار (کپی از DB به‌عنوان مقدار اولیه)
                        startReceipt = receiptBU,
                        startReturns = returnsBU,
                        startGifts = giftsBU,
                        startWaste = wasteBU,
                        startLaboratory = laboratoryBU,
                        startMidDayLoading = midDayBU,
                        // عملیات روزانه آخر کار (همان مقدار DB)
                        receipt = receiptBU,
                        returns = returnsBU,
                        gifts = giftsBU,
                        waste = wasteBU,
                        laboratory = laboratoryBU,
                        midDayLoading = midDayBU,
                        nightLoading = VarianceCalculator.fromTotalUnits(existingStat.nightLoadingTotal, product),
                        currentStock = existingStat.currentStockTotal?.let { t ->
                            VarianceCalculator.fromTotalUnits(t, product)
                        },
                        hasCurrentStock = existingStat.currentStockTotal != null,
                        notes = existingStat.notes,
                        isFinalized = existingStat.isFinalized,
                        statId = existingStat.id,
                        // تعیین پرچم‌های ثبت هر مرحله بر اساس داده موجود:
                        endStageSaved = existingStat.currentStockTotal != null,
                        startStageSaved = existingStat.actualMorningStockTotal != null
                            || existingStat.startVariance != null
                            || existingStat.openingStockTotal > 0
                            || existingStat.morningLoadingTotal > 0
                            || (existingStat.currentStockTotal == null && existingStat.id > 0L),
                        isLoading = false
                    )
                }
            } else {
                // آمار جدید - اگر مانده روز قبل وجود دارد، آن را به‌عنوان مانده شب گذشته پیشنهاد بده
                _uiState.update {
                    it.copy(
                        product = product,
                        jalaliDate = jalaliDate,
                        openingStock = previousOpeningStock,
                        isLoading = false
                    )
                }
            }
        }
    }

    // ===================== به‌روزرسانی فیلدها =====================

    fun updateOpeningStock(value: BoxAndUnits) {
        _uiState.update { it.copy(openingStock = value) }
        validateAndUpdate()
    }

    fun updateMorningLoading(value: BoxAndUnits) {
        _uiState.update { it.copy(morningLoading = value) }
        validateAndUpdate()
    }

    fun updateActualMorningStock(value: BoxAndUnits) {
        _uiState.update { it.copy(actualMorningStock = value) }
        validateAndUpdate()
    }

    fun setHasActualMorningStock(has: Boolean) {
        _uiState.update {
            it.copy(
                hasActualMorningStock = has,
                actualMorningStock = if (has) it.actualMorningStock ?: BoxAndUnits.ZERO else null
            )
        }
        validateAndUpdate()
    }

    fun updateReceipt(value: BoxAndUnits) {
        _uiState.update { it.copy(receipt = value) }
        validateAndUpdate()
    }

    fun updateReturns(value: BoxAndUnits) {
        _uiState.update { it.copy(returns = value) }
        validateAndUpdate()
    }

    fun updateGifts(value: BoxAndUnits) {
        _uiState.update { it.copy(gifts = value) }
        validateAndUpdate()
    }

    fun updateWaste(value: BoxAndUnits) {
        _uiState.update { it.copy(waste = value) }
        validateAndUpdate()
    }

    fun updateLaboratory(value: BoxAndUnits) {
        _uiState.update { it.copy(laboratory = value) }
        validateAndUpdate()
    }

    fun updateMidDayLoading(value: BoxAndUnits) {
        _uiState.update { it.copy(midDayLoading = value) }
        validateAndUpdate()
    }

    // ===== متدهای آپدیت عملیات روزانه اول کار (جدا از آخر کار) =====

    fun updateStartReceipt(value: BoxAndUnits) {
        _uiState.update { it.copy(startReceipt = value) }
        validateAndUpdate()
    }

    fun updateStartReturns(value: BoxAndUnits) {
        _uiState.update { it.copy(startReturns = value) }
        validateAndUpdate()
    }

    fun updateStartGifts(value: BoxAndUnits) {
        _uiState.update { it.copy(startGifts = value) }
        validateAndUpdate()
    }

    fun updateStartWaste(value: BoxAndUnits) {
        _uiState.update { it.copy(startWaste = value) }
        validateAndUpdate()
    }

    fun updateStartLaboratory(value: BoxAndUnits) {
        _uiState.update { it.copy(startLaboratory = value) }
        validateAndUpdate()
    }

    fun updateStartMidDayLoading(value: BoxAndUnits) {
        _uiState.update { it.copy(startMidDayLoading = value) }
        validateAndUpdate()
    }

    fun updateNightLoading(value: BoxAndUnits) {
        _uiState.update { it.copy(nightLoading = value) }
        validateAndUpdate()
    }

    fun updateCurrentStock(value: BoxAndUnits) {
        _uiState.update { it.copy(currentStock = value, hasCurrentStock = true) }
        validateAndUpdate()
    }

    /**
     * لغو صریح ثبت آخر کار (پاک کردن currentStock).
     * این متد فقط در END معنا دارد.
     */
    fun clearCurrentStock() {
        _uiState.update {
            it.copy(currentStock = null, hasCurrentStock = false)
        }
        validateAndUpdate()
    }

    fun updateNotes(notes: String) {
        _uiState.update { it.copy(notes = notes) }
    }

    fun clearWarning() {
        _uiState.update { it.copy(showWarning = null) }
    }

    /**
     * اعتبارسنجی و نمایش هشدارها.
     */
    private fun validateAndUpdate() {
        val state = _uiState.value
        val product = state.product ?: return
        val result = state.varianceResult ?: return

        // ذخیره خودکار با debounce
        scheduleAutoSave()

        // هشدار: بارگیری از موجودی قابل دسترس بیشتر شد (فقط در START)
        if (stage == StatEntryStage.START || stage == StatEntryStage.FULL) {
            val availableBeforeMorning = VarianceCalculator.toTotalUnits(state.openingStock, product)
            val morningLoadingTotal = VarianceCalculator.toTotalUnits(state.morningLoading, product)
            if (morningLoadingTotal > availableBeforeMorning && availableBeforeMorning > 0) {
                _uiState.update {
                    it.copy(showWarning = "بارگیری صبح از مانده شب گذشته بیشتر است. آیا مطمئن هستید؟")
                }
                return
            }
        }

        // هشدار: مغایرت بسیار بزرگ
        val maxVariance = listOfNotNull(
            result.startVariance,
            result.endCumulativeVariance,
            result.nightShiftVariance
        ).maxOfOrNull { kotlin.math.abs(it) } ?: 0

        if (maxVariance > 1000) {
            _uiState.update {
                it.copy(showWarning = "مقدار مغایرت بسیار بزرگ است. لطفاً مقادیر را بررسی کنید.")
            }
            return
        }

        _uiState.update { it.copy(showWarning = null) }
    }

    /**
     * ذخیره آمار (پیش‌نویس) - stage-aware.
     */
    fun saveDraft() {
        val state = _uiState.value
        val product = state.product ?: return

        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true) }
            try {
                val input = buildInput()
                val savedId = when (stage) {
                    StatEntryStage.START -> app.dailyStatRepository.saveStartStage(input, product, isFinalized = false)
                    StatEntryStage.END -> app.dailyStatRepository.saveEndStage(input, product, isFinalized = false)
                    StatEntryStage.FULL -> app.dailyStatRepository.saveStat(input, product, isFinalized = false)
                }
                _uiState.update {
                    it.copy(
                        isSaving = false,
                        statId = savedId,
                        startStageSaved = if (stage == StatEntryStage.START) true else it.startStageSaved,
                        endStageSaved = if (stage == StatEntryStage.END) true else it.endStageSaved,
                        saveMessage = when (stage) {
                            StatEntryStage.START -> "ذخیره اول کار انجام شد"
                            StatEntryStage.END -> "ذخیره آخر کار انجام شد"
                            StatEntryStage.FULL -> "آمار با موفقیت ذخیره شد"
                        }
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isSaving = false,
                        saveMessage = "خطا در ذخیره‌سازی: ${e.message}"
                    )
                }
            }
        }
    }

    /**
     * نهایی کردن آمار - stage-aware.
     */
    fun finalizeStat() {
        val state = _uiState.value
        val product = state.product ?: return

        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true) }
            try {
                val input = buildInput()
                val savedId = when (stage) {
                    StatEntryStage.START -> app.dailyStatRepository.saveStartStage(input, product, isFinalized = true)
                    StatEntryStage.END -> app.dailyStatRepository.saveEndStage(input, product, isFinalized = true)
                    StatEntryStage.FULL -> app.dailyStatRepository.saveStat(input, product, isFinalized = true)
                }
                _uiState.update {
                    it.copy(
                        isSaving = false,
                        isFinalized = true,
                        statId = savedId,
                        startStageSaved = if (stage == StatEntryStage.START) true else it.startStageSaved,
                        endStageSaved = if (stage == StatEntryStage.END) true else it.endStageSaved,
                        saveMessage = when (stage) {
                            StatEntryStage.START -> "اول کار نهایی شد"
                            StatEntryStage.END -> "آخر کار نهایی شد"
                            StatEntryStage.FULL -> "آمار نهایی شد"
                        }
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isSaving = false,
                        saveMessage = "خطا در نهایی‌سازی: ${e.message}"
                    )
                }
            }
        }
    }

    /**
     * لغو نهایی‌سازی.
     */
    fun unfinalizeStat() {
        val statId = _uiState.value.statId ?: return
        viewModelScope.launch {
            app.dailyStatRepository.unfinalizeStat(statId)
            _uiState.update { it.copy(isFinalized = false) }
        }
    }

    /**
     * ریست کردن فیلدهای مرحله فعلی.
     *
     * - در START: فیلدهای اول کار (openingStock، morningLoading، actualMorningStock)
     *   و فیلدهای عملیات روزانه (receipt، returns، gifts، waste، laboratory، midDayLoading)
     *   صفر می‌شوند. فیلدهای آخر کار (nightLoading، currentStock) دست‌نخورده می‌مانند.
     * - در END: فیلدهای آخر کار (nightLoading، currentStock) و فیلدهای عملیات روزانه
     *   صفر می‌شوند. فیلدهای اول کار دست‌نخورده می‌مانند.
     * - در FULL: همه فیلدها صفر می‌شوند.
     *
     * اگر آمار نهایی شده باشد، ابتدا نهایی‌سازی لغو می‌شود.
     * سپس مقادیر صفر شده در دیتابیس ذخیره می‌شوند.
     */
    fun resetCurrentStage() {
        val state = _uiState.value
        val product = state.product ?: return
        val statId = state.statId

        viewModelScope.launch {
            // اگر نهایی شده، ابتدا لغو نهایی کن
            if (state.isFinalized && statId != null) {
                app.dailyStatRepository.unfinalizeStat(statId)
            }

            val newState = when (stage) {
                StatEntryStage.START -> state.copy(
                    openingStock = BoxAndUnits.ZERO,
                    morningLoading = BoxAndUnits.ZERO,
                    actualMorningStock = null,
                    hasActualMorningStock = false,
                    // عملیات روزانه اول کار
                    startReceipt = BoxAndUnits.ZERO,
                    startReturns = BoxAndUnits.ZERO,
                    startGifts = BoxAndUnits.ZERO,
                    startWaste = BoxAndUnits.ZERO,
                    startLaboratory = BoxAndUnits.ZERO,
                    startMidDayLoading = BoxAndUnits.ZERO,
                    isFinalized = false,
                    showWarning = null
                )
                StatEntryStage.END -> state.copy(
                    // عملیات روزانه آخر کار
                    receipt = BoxAndUnits.ZERO,
                    returns = BoxAndUnits.ZERO,
                    gifts = BoxAndUnits.ZERO,
                    waste = BoxAndUnits.ZERO,
                    laboratory = BoxAndUnits.ZERO,
                    midDayLoading = BoxAndUnits.ZERO,
                    nightLoading = BoxAndUnits.ZERO,
                    currentStock = null,
                    hasCurrentStock = false,
                    isFinalized = false,
                    showWarning = null
                )
                StatEntryStage.FULL -> state.copy(
                    openingStock = BoxAndUnits.ZERO,
                    morningLoading = BoxAndUnits.ZERO,
                    actualMorningStock = null,
                    hasActualMorningStock = false,
                    startReceipt = BoxAndUnits.ZERO,
                    startReturns = BoxAndUnits.ZERO,
                    startGifts = BoxAndUnits.ZERO,
                    startWaste = BoxAndUnits.ZERO,
                    startLaboratory = BoxAndUnits.ZERO,
                    startMidDayLoading = BoxAndUnits.ZERO,
                    receipt = BoxAndUnits.ZERO,
                    returns = BoxAndUnits.ZERO,
                    gifts = BoxAndUnits.ZERO,
                    waste = BoxAndUnits.ZERO,
                    laboratory = BoxAndUnits.ZERO,
                    midDayLoading = BoxAndUnits.ZERO,
                    nightLoading = BoxAndUnits.ZERO,
                    currentStock = null,
                    hasCurrentStock = false,
                    notes = "",
                    isFinalized = false,
                    showWarning = null
                )
            }
            _uiState.value = newState

            // ذخیره در دیتابیس (با try-catch برای جلوگیری از کرش)
            try {
                val input = buildInput()
                val savedId = when (stage) {
                    StatEntryStage.START -> app.dailyStatRepository.saveStartStage(input, product, isFinalized = false)
                    StatEntryStage.END -> app.dailyStatRepository.saveEndStage(input, product, isFinalized = false)
                    StatEntryStage.FULL -> app.dailyStatRepository.saveStat(input, product, isFinalized = false)
                }
                _uiState.update {
                    it.copy(
                        statId = savedId,
                        startStageSaved = if (stage == StatEntryStage.START) false else it.startStageSaved,
                        endStageSaved = if (stage == StatEntryStage.END) false else it.endStageSaved,
                        saveMessage = when (stage) {
                            StatEntryStage.START -> "فیلدهای اول کار پاک شدند"
                            StatEntryStage.END -> "فیلدهای آخر کار پاک شدند"
                            StatEntryStage.FULL -> "همه فیلدها پاک شدند"
                        }
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isSaving = false,
                        saveMessage = "خطا در ذخیره پس از ریست: ${e.message}"
                    )
                }
            }
        }
    }

    /**
     * حذف کامل آمار (کل رکورد از دیتابیس).
     *
     * این عمل غیرقابل بازگشت است. تمام داده‌های مربوط به این محصول و تاریخ حذف می‌شود
     * و فرم به حالت اولیه برمی‌گردد.
     */
    fun deleteStat() {
        val state = _uiState.value
        val statId = state.statId
        if (statId == null) {
            // اگر statId نبود، فقط state را ریست کن
            _uiState.update {
                it.copy(
                    openingStock = BoxAndUnits.ZERO,
                    morningLoading = BoxAndUnits.ZERO,
                    actualMorningStock = null,
                    hasActualMorningStock = false,
                    startReceipt = BoxAndUnits.ZERO,
                    startReturns = BoxAndUnits.ZERO,
                    startGifts = BoxAndUnits.ZERO,
                    startWaste = BoxAndUnits.ZERO,
                    startLaboratory = BoxAndUnits.ZERO,
                    startMidDayLoading = BoxAndUnits.ZERO,
                    receipt = BoxAndUnits.ZERO,
                    returns = BoxAndUnits.ZERO,
                    gifts = BoxAndUnits.ZERO,
                    waste = BoxAndUnits.ZERO,
                    laboratory = BoxAndUnits.ZERO,
                    midDayLoading = BoxAndUnits.ZERO,
                    nightLoading = BoxAndUnits.ZERO,
                    currentStock = null,
                    hasCurrentStock = false,
                    notes = "",
                    isFinalized = false,
                    statId = null,
                    startStageSaved = false,
                    endStageSaved = false,
                    showWarning = null,
                    saveMessage = "آمار پاک شد"
                )
            }
            return
        }

        viewModelScope.launch {
            try {
                app.dailyStatRepository.deleteStat(statId)
                _uiState.update {
                    it.copy(
                        openingStock = BoxAndUnits.ZERO,
                        morningLoading = BoxAndUnits.ZERO,
                        actualMorningStock = null,
                        hasActualMorningStock = false,
                        startReceipt = BoxAndUnits.ZERO,
                        startReturns = BoxAndUnits.ZERO,
                        startGifts = BoxAndUnits.ZERO,
                        startWaste = BoxAndUnits.ZERO,
                        startLaboratory = BoxAndUnits.ZERO,
                        startMidDayLoading = BoxAndUnits.ZERO,
                        receipt = BoxAndUnits.ZERO,
                        returns = BoxAndUnits.ZERO,
                        gifts = BoxAndUnits.ZERO,
                        waste = BoxAndUnits.ZERO,
                        laboratory = BoxAndUnits.ZERO,
                        midDayLoading = BoxAndUnits.ZERO,
                        nightLoading = BoxAndUnits.ZERO,
                        currentStock = null,
                        hasCurrentStock = false,
                        notes = "",
                        isFinalized = false,
                        statId = null,
                        startStageSaved = false,
                        endStageSaved = false,
                        showWarning = null,
                        saveMessage = "آمار کامل حذف شد"
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(saveMessage = "خطا در حذف آمار: ${e.message}")
                }
            }
        }
    }

    fun clearSaveMessage() {
        _uiState.update { it.copy(saveMessage = null) }
    }

    companion object {
        fun factory(app: MoghayeratApplication, productId: Long, isoDate: String, stage: StatEntryStage): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return StatEntryViewModel(app, productId, isoDate, stage) as T
                }
            }
    }
}
