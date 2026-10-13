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
 * وضعیت فرم ورودی آمار روزانه.
 *
 * این وضعیت تمام فیلدهای فرم و نتیجه محاسبه را نگه می‌دارد.
 * نتیجه محاسبه با هر تغییر ورودی به‌صورت زنده به‌روزرسانی می‌شود.
 */
data class StatEntryUiState(
    val product: Product? = null,
    val isoDate: String = "",
    val jalaliDate: String = "",
    val openingStock: BoxAndUnits = BoxAndUnits.ZERO,
    val morningLoading: BoxAndUnits = BoxAndUnits.ZERO,
    val actualMorningStock: BoxAndUnits? = null, // null = فیلد خالی
    val hasActualMorningStock: Boolean = false,  // آیا کاربر این فیلد را پر کرده است
    val receipt: BoxAndUnits = BoxAndUnits.ZERO,
    val returns: BoxAndUnits = BoxAndUnits.ZERO,         // برگشت به انبار
    val gifts: BoxAndUnits = BoxAndUnits.ZERO,
    val waste: BoxAndUnits = BoxAndUnits.ZERO,
    val laboratory: BoxAndUnits = BoxAndUnits.ZERO,      // آزمایشگاه
    val midDayLoading: BoxAndUnits = BoxAndUnits.ZERO,    // بارگیری میان روز
    val nightLoading: BoxAndUnits = BoxAndUnits.ZERO,
    val currentStock: BoxAndUnits = BoxAndUnits.ZERO,
    val notes: String = "",
    val isFinalized: Boolean = false,
    val statId: Long? = null,
    val isSaving: Boolean = false,
    val saveMessage: String? = null,
    val isLoading: Boolean = true,
    val showWarning: String? = null
) {
    /**
     * محاسبه زنده نتیجه مغایرت.
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
                currentStock = currentStock,
                notes = notes
            )
            return VarianceCalculator.calculate(input, product)
        }
}

class StatEntryViewModel(
    private val app: MoghayeratApplication,
    private val productId: Long,
    private val isoDate: String
) : ViewModel() {

    private val _uiState = MutableStateFlow(StatEntryUiState(isoDate = isoDate))
    val uiState: StateFlow<StatEntryUiState> = _uiState.asStateFlow()

    // ذخیره خودکار با debounce
    private var autoSaveJob: kotlinx.coroutines.Job? = null
    private val autoSaveDelay = 2000L  // ۲ ثانیه

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
     * ذخیره پیش‌نویس بدون نمایش پیام.
     */
    private suspend fun saveDraftSilent() {
        val state = _uiState.value
        val product = state.product ?: return
        val actualMorning = if (state.hasActualMorningStock) state.actualMorningStock else null
        val input = DailyStatInput(
            productId = product.id,
            isoDate = state.isoDate,
            openingStock = state.openingStock,
            morningLoading = state.morningLoading,
            actualMorningStock = actualMorning,
            receipt = state.receipt,
            returns = state.returns,
            gifts = state.gifts,
            waste = state.waste,
            laboratory = state.laboratory,
            midDayLoading = state.midDayLoading,
            nightLoading = state.nightLoading,
            currentStock = state.currentStock,
            notes = state.notes
        )
        val savedId = app.dailyStatRepository.saveStat(input, product, isFinalized = false)
        _uiState.update { it.copy(statId = savedId) }
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
                _uiState.update {
                    it.copy(
                        product = product,
                        jalaliDate = jalaliDate,
                        openingStock = VarianceCalculator.fromTotalUnits(existingStat.openingStockTotal, product),
                        morningLoading = VarianceCalculator.fromTotalUnits(existingStat.morningLoadingTotal, product),
                        actualMorningStock = existingStat.actualMorningStockTotal?.let { t ->
                            VarianceCalculator.fromTotalUnits(t, product)
                        },
                        hasActualMorningStock = existingStat.actualMorningStockTotal != null,
                        receipt = VarianceCalculator.fromTotalUnits(existingStat.receiptTotal, product),
                        returns = VarianceCalculator.fromTotalUnits(existingStat.returnsTotal, product),
                        gifts = VarianceCalculator.fromTotalUnits(existingStat.giftsTotal, product),
                        waste = VarianceCalculator.fromTotalUnits(existingStat.wasteTotal, product),
                        laboratory = VarianceCalculator.fromTotalUnits(existingStat.laboratoryTotal, product),
                        midDayLoading = VarianceCalculator.fromTotalUnits(existingStat.midDayLoadingTotal, product),
                        nightLoading = VarianceCalculator.fromTotalUnits(existingStat.nightLoadingTotal, product),
                        currentStock = VarianceCalculator.fromTotalUnits(existingStat.currentStockTotal, product),
                        notes = existingStat.notes,
                        isFinalized = existingStat.isFinalized,
                        statId = existingStat.id,
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

    fun updateNightLoading(value: BoxAndUnits) {
        _uiState.update { it.copy(nightLoading = value) }
        validateAndUpdate()
    }

    fun updateCurrentStock(value: BoxAndUnits) {
        _uiState.update { it.copy(currentStock = value) }
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

        // هشدار: بارگیری از موجودی قابل دسترس بیشتر شد
        val availableBeforeMorning = state.openingStock.let {
            VarianceCalculator.toTotalUnits(it, product)
        }
        val morningLoadingTotal = state.morningLoading.let {
            VarianceCalculator.toTotalUnits(it, product)
        }
        if (morningLoadingTotal > availableBeforeMorning && availableBeforeMorning > 0) {
            _uiState.update {
                it.copy(showWarning = "بارگیری صبح از مانده شب گذشته بیشتر است. آیا مطمئن هستید؟")
            }
            return
        }

        // هشدار: مغایرت بسیار بزرگ
        if (result.closingVariance.absoluteValue() > 1000) {
            _uiState.update {
                it.copy(showWarning = "مقدار مغایرت بسیار بزرگ است. لطفاً مقادیر را بررسی کنید.")
            }
            return
        }

        _uiState.update { it.copy(showWarning = null) }
    }

    /**
     * ذخیره آمار (پیش‌نویس).
     */
    fun saveDraft() {
        val state = _uiState.value
        val product = state.product ?: return
        val actualMorning = if (state.hasActualMorningStock) state.actualMorningStock else null

        val input = DailyStatInput(
            productId = product.id,
            isoDate = state.isoDate,
            openingStock = state.openingStock,
            morningLoading = state.morningLoading,
            actualMorningStock = actualMorning,
            receipt = state.receipt,
            returns = state.returns,
            gifts = state.gifts,
            waste = state.waste,
                    laboratory = state.laboratory,
            midDayLoading = state.midDayLoading,
            nightLoading = state.nightLoading,
            currentStock = state.currentStock,
            notes = state.notes
        )

        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true) }
            try {
                val savedId = app.dailyStatRepository.saveStat(input, product, isFinalized = false)
                _uiState.update {
                    it.copy(
                        isSaving = false,
                        statId = savedId,  // به‌روزرسانی statId برای جلوگیری از رکورد تکراری
                        saveMessage = "آمار با موفقیت ذخیره شد"
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
     * نهایی کردن آمار.
     * ابتدا اطلاعات را ذخیره می‌کند، سپس آن را نهایی می‌کند.
     * اگر statId وجود نداشته باشد، ابتدا یک آمار جدید می‌سازد.
     */
    fun finalizeStat() {
        val state = _uiState.value
        val product = state.product ?: return

        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true) }
            try {
                // ابتدا ذخیره آخرین تغییرات
                val actualMorning = if (state.hasActualMorningStock) state.actualMorningStock else null
                val input = DailyStatInput(
                    productId = product.id,
                    isoDate = state.isoDate,
                    openingStock = state.openingStock,
                    morningLoading = state.morningLoading,
                    actualMorningStock = actualMorning,
                    receipt = state.receipt,
                    returns = state.returns,
                    gifts = state.gifts,
                    waste = state.waste,
                    laboratory = state.laboratory,
                    midDayLoading = state.midDayLoading,
                    nightLoading = state.nightLoading,
                    currentStock = state.currentStock,
                    notes = state.notes
                )
                val savedId = app.dailyStatRepository.saveStat(input, product, isFinalized = true)
                _uiState.update {
                    it.copy(
                        isSaving = false,
                        isFinalized = true,
                        statId = savedId,  // به‌روزرسانی statId برای جلوگیری از رکورد تکراری
                        saveMessage = "آمار نهایی شد"
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

    fun clearSaveMessage() {
        _uiState.update { it.copy(saveMessage = null) }
    }

    companion object {
        fun factory(app: MoghayeratApplication, productId: Long, isoDate: String): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return StatEntryViewModel(app, productId, isoDate) as T
                }
            }
    }
}

private fun Int.absoluteValue(): Int = if (this < 0) -this else this
