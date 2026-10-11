package com.damdarangha.moghayerat.domain.calculation

import com.damdarangha.moghayerat.domain.model.BoxAndUnits
import com.damdarangha.moghayerat.domain.model.DailyStatInput
import com.damdarangha.moghayerat.domain.model.Product
import com.damdarangha.moghayerat.domain.model.VarianceResult

/**
 * موتور محاسبات مغایرت‌گیر
 *
 * این کلاس تمام محاسبات مغایرت را به‌صورت عدد صحیح انجام می‌دهد.
 * هیچ عدد اعشاری استفاده نمی‌شود تا خطای گرد کردن رخ ندهد.
 *
 * قوانین محاسبه:
 * 1. رسید و برگشت به انبار به موجودی اضافه می‌شوند.
 * 2. بارگیری صبح، بارگیری میان روز، بارگیری شب، هدایا و ضایعات از موجودی کم می‌شوند.
 * 3. موجودی مورد انتظار پایان روز = مانده شب گذشته + رسید + برگشت به انبار - بارگیری صبح - بارگیری میان روز - بارگیری شب - هدایا - ضایعات
 * 4. مغایرت پایان روز = موجودی مورد انتظار - مانده واقعی پایان روز
 *    - صفر = مطابقت
 *    - مثبت = کسری (موجودی واقعی کمتر است)
 *    - منفی = اضافه (موجودی واقعی بیشتر است)
 *
 * نکات مهم:
 * - برای محصول با باکس رند، فقط تعداد باکس محاسبه می‌شود.
 * - تعداد عدد هرگز از ظرفیت باکس بیشتر نمی‌شود (تبدیل خودکار).
 * - از سرریز عددی با Long جلوگیری می‌شود.
 */
object VarianceCalculator {

    /**
     * تبدیل جفت باکس/عدد به تعداد کل واحد.
     * برای محصول با باکس رند، فقط تعداد باکس برگردانده می‌شود.
     *
     * @param input جفت باکس/عدد
     * @param product محصول موردنظر
     * @return تعداد کل واحد
     */
    fun toTotalUnits(input: BoxAndUnits, product: Product): Int {
        if (product.isRoundBox) {
            // برای باکس رند، فقط تعداد باکس را برمی‌گردانیم
            // تعداد عدد باید صفر باشد (این محدودیت باید در UI اعمال شود)
            return input.boxes
        }
        // برای جلوگیری از سرریز، ابتدا بررسی می‌کنیم
        // boxCapacity همیشه >= 1 است (تضمین شده در محصول)
        val safeBoxCapacity = if (product.boxCapacity <= 0) 1 else product.boxCapacity
        // ضرب با بررسی سرریز
        val boxesPart = safeMultiply(input.boxes, safeBoxCapacity)
        return boxesPart + input.units
    }

    /**
     * تبدیل تعداد کل واحد به جفت باکس/عدد استاندارد.
     * مثلاً برای ظرفیت ۲۷: ۳۰ عدد → ۱ باکس و ۳ عدد
     *
     * @param totalUnits تعداد کل واحد
     * @param product محصول موردنظر
     * @return جفت باکس/عدد استاندارد
     */
    fun fromTotalUnits(totalUnits: Int, product: Product): BoxAndUnits {
        if (product.isRoundBox) {
            // برای باکس رند، فقط تعداد باکس را برمی‌گردانیم
            return BoxAndUnits(boxes = totalUnits, units = 0)
        }
        val safeBoxCapacity = if (product.boxCapacity <= 0) 1 else product.boxCapacity
        val boxes = totalUnits / safeBoxCapacity
        val units = totalUnits % safeBoxCapacity
        return BoxAndUnits(boxes = boxes, units = units)
    }

    /**
     * نرمال‌سازی جفت باکس/عدد.
     * اگر تعداد عدد از ظرفیت باکس بیشتر باشد، به باکس تبدیل می‌شود.
     * مثلاً برای ظرفیت ۲۷: (۲ باکس، ۳۰ عدد) → (۳ باکس، ۳ عدد)
     *
     * @param input جفت باکس/عدد ورودی
     * @param product محصول موردنظر
     * @return جفت باکس/عدد نرمال‌شده
     */
    fun normalize(input: BoxAndUnits, product: Product): BoxAndUnits {
        if (product.isRoundBox) {
            // برای باکس رند، فقط تعداد باکس معتبر است
            return BoxAndUnits(boxes = input.boxes, units = 0)
        }
        val total = toTotalUnits(input, product)
        return fromTotalUnits(total, product)
    }

    /**
     * آیا جفت باکس/عدد معتبر است؟
     * - تعداد عدد باید کوچکتر از ظرفیت باکس باشد (مگر باکس رند).
     * - مقادیر نباید منفی باشند.
     *
     * @param input جفت باکس/عدد
     * @param product محصول موردنظر
     * @return true اگر معتبر باشد
     */
    fun isValid(input: BoxAndUnits, product: Product): Boolean {
        if (input.boxes < 0 || input.units < 0) return false
        if (product.isRoundBox) {
            // برای باکس رند، تعداد عدد باید صفر باشد
            return input.units == 0
        }
        val safeBoxCapacity = if (product.boxCapacity <= 0) 1 else product.boxCapacity
        return input.units < safeBoxCapacity
    }

    /**
     * محاسبه کامل مغایرت برای یک آمار روزانه.
     *
     * @param input ورودی آمار روزانه
     * @param product محصول موردنظر
     * @return نتیجه محاسبات
     */
    fun calculate(input: DailyStatInput, product: Product): VarianceResult {
        val openingStockTotal = toTotalUnits(input.openingStock, product)
        val morningLoadingTotal = toTotalUnits(input.morningLoading, product)
        val receiptTotal = toTotalUnits(input.receipt, product)
        val returnsTotal = toTotalUnits(input.returns, product)
        val giftsTotal = toTotalUnits(input.gifts, product)
        val wasteTotal = toTotalUnits(input.waste, product)
        val midDayLoadingTotal = toTotalUnits(input.midDayLoading, product)
        val nightLoadingTotal = toTotalUnits(input.nightLoading, product)
        val currentStockTotal = toTotalUnits(input.currentStock, product)

        // موجودی مورد انتظار بعد از بارگیری صبح
        val expectedMorningStockTotal = safeSubtract(openingStockTotal, morningLoadingTotal)

        // در صورت ورود مانده واقعی صبح، مغایرت اول کار را محاسبه می‌کنیم
        val actualMorningStockTotal: Int? = input.actualMorningStock?.let {
            toTotalUnits(it, product)
        }
        val morningVariance: Int? = actualMorningStockTotal?.let { actual ->
            safeSubtract(expectedMorningStockTotal, actual)
        }

        // موجودی مورد انتظار پایان روز
        // = مانده شب گذشته + رسید + برگشت به انبار - بارگیری صبح - بارگیری میان روز - بارگیری شب - هدایا - ضایعات
        val expectedClosingStockTotal = safeAdd(openingStockTotal, receiptTotal)
            .let { safeAdd(it, returnsTotal) }
            .let { safeSubtract(it, morningLoadingTotal) }
            .let { safeSubtract(it, midDayLoadingTotal) }
            .let { safeSubtract(it, nightLoadingTotal) }
            .let { safeSubtract(it, giftsTotal) }
            .let { safeSubtract(it, wasteTotal) }

        // مغایرت پایان روز = موجودی مورد انتظار - مانده واقعی
        val closingVariance = safeSubtract(expectedClosingStockTotal, currentStockTotal)

        return VarianceResult(
            openingStockTotal = openingStockTotal,
            morningLoadingTotal = morningLoadingTotal,
            receiptTotal = receiptTotal,
            returnsTotal = returnsTotal,
            giftsTotal = giftsTotal,
            wasteTotal = wasteTotal,
            midDayLoadingTotal = midDayLoadingTotal,
            nightLoadingTotal = nightLoadingTotal,
            expectedMorningStockTotal = expectedMorningStockTotal,
            actualMorningStockTotal = actualMorningStockTotal,
            morningVariance = morningVariance,
            expectedClosingStockTotal = expectedClosingStockTotal,
            currentStockTotal = currentStockTotal,
            closingVariance = closingVariance
        )
    }

    /**
     * ضرب امن برای جلوگیری از سرریز عددی.
     * در صورت سرریز، یک IllegalStateException پرتاب می‌شود.
     */
    private fun safeMultiply(a: Int, b: Int): Int {
        if (a == 0 || b == 0) return 0
        if (a > 0 && b > 0 && (a > Int.MAX_VALUE / b || b > Int.MAX_VALUE / a)) {
            throw IllegalStateException("سرریز عددی در ضرب: $a × $b")
        }
        if (a > 0 && b < 0 && (b < Int.MIN_VALUE / a)) {
            throw IllegalStateException("سرریز عددی در ضرب: $a × $b")
        }
        if (a < 0 && b > 0 && (a < Int.MIN_VALUE / b)) {
            throw IllegalStateException("سرریز عددی در ضرب: $a × $b")
        }
        return a * b
    }

    /**
     * جمع امن برای جلوگیری از سرریز عددی.
     */
    private fun safeAdd(a: Int, b: Int): Int {
        if (b > 0 && a > Int.MAX_VALUE - b) {
            throw IllegalStateException("سرریز عددی در جمع: $a + $b")
        }
        if (b < 0 && a < Int.MIN_VALUE - b) {
            throw IllegalStateException("سرریز عددی در جمع: $a + $b")
        }
        return a + b
    }

    /**
     * تفریق امن برای جلوگیری از سرریز عددی.
     */
    private fun safeSubtract(a: Int, b: Int): Int {
        if (b > 0 && a < Int.MIN_VALUE + b) {
            throw IllegalStateException("سرریز عددی در تفریق: $a - $b")
        }
        if (b < 0 && a > Int.MAX_VALUE + b) {
            throw IllegalStateException("سرریز عددی در تفریق: $a - $b")
        }
        return a - b
    }
}
