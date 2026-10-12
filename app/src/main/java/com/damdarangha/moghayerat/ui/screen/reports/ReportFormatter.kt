package com.damdarangha.moghayerat.ui.screen.reports

import com.damdarangha.moghayerat.domain.model.Product

/**
 * فرمول‌ساز مشترک برای نمایش مقادیر در PDF و UI.
 *
 * قواعد:
 * - اعداد فارسی نمایش داده شوند
 * - مقدار صفر معتبر: «۰»
 * - مقدار nullable واردنشده: «—»
 * - محصول بی‌رکورد: «ثبت‌نشده»
 * - برای محصولات رند: فقط تعداد باکس
 * - برای محصولات عادی: «X ب + Y ع» (باکس + عدد)
 * - مغایرت مثبت: «کسری X»، منفی: «اضافه X»، صفر: «صفر»
 * - جلوگیری از سرریز Int.MIN_VALUE
 */
object ReportFormatter {

    /**
     * تبدیل عدد به اعداد فارسی.
     * جلوگیری از سرریز Int.MIN_VALUE.
     */
    fun toPersianDigits(value: Int): String {
        val safeValue = if (value == Int.MIN_VALUE) 0 else value
        val persianDigits = arrayOf('۰', '۱', '۲', '۳', '۴', '۵', '۶', '۷', '۸', '۹')
        return safeValue.toString().map { c ->
            if (c.isDigit()) persianDigits[c - '0'] else c
        }.joinToString("")
    }

    /**
     * تبدیل عدد nullable به اعداد فارسی یا «—» اگر null باشد.
     */
    fun toPersianDigitsOrNull(value: Int?): String {
        return if (value == null) "—" else toPersianDigits(value)
    }

    /**
     * قالب‌بندی مقدار به‌صورت باکس و عدد.
     *
     * - مقدار ۰: «۰»
     * - محصولات رند: «X باکس»
     * - محصولات عادی: «X ب + Y ع» یا «X باکس» اگر عدد صفر باشد
     *
     * @param total تعداد کل واحد
     * @param product محصول موردنظر
     * @return رشته قالب‌بندی‌شده
     */
    fun formatBoxUnit(total: Int, product: Product): String {
        if (total == 0) return "۰"

        // جلوگیری از سرریز
        val safeTotal = if (total == Int.MIN_VALUE) 0 else total

        return if (product.isRoundBox) {
            "${toPersianDigits(safeTotal)} باکس"
        } else {
            val capacity = if (product.boxCapacity <= 0) 1 else product.boxCapacity
            // ضرب امن
            val safeCapacity = capacity.coerceAtMost(10000)
            val boxes = safeTotal / safeCapacity
            val units = safeTotal % safeCapacity
            if (units == 0) {
                "${toPersianDigits(boxes)} باکس"
            } else {
                "${toPersianDigits(boxes)} ب + ${toPersianDigits(units)} ع"
            }
        }
    }

    /**
     * قالب‌بندی مغایرت.
     *
     * - صفر: «صفر»
     * - مثبت (کسری): «کسری X»
     * - منفی (اضافه): «اضافه X»
     * - null: «—»
     *
     * @param variance مقدار مغایرت (مثبت=کسری، منفی=اضافه)
     * @return رشته قالب‌بندی‌شده
     */
    fun formatVariance(variance: Int?): String {
        if (variance == null) return "—"
        return when {
            variance == 0 -> "صفر"
            variance > 0 -> "کسری ${toPersianDigits(variance)}"
            else -> {
                // جلوگیری از سرریز Int.MIN_VALUE
                val absValue = if (variance == Int.MIN_VALUE) 0 else -variance
                "اضافه ${toPersianDigits(absValue)}"
            }
        }
    }

    /**
     * قالب‌بندی مغایرت با باکس و عدد.
     *
     * - صفر: «صفر»
     * - مثبت (کسری): «کسری X ب + Y ع»
     * - منفی (اضافه): «اضافه X ب + Y ع»
     * - null: «—»
     *
     * @param variance مقدار مغایرت
     * @param product محصول موردنظر
     * @return رشته قالب‌بندی‌شده
     */
    fun formatVarianceWithBox(variance: Int?, product: Product): String {
        if (variance == null) return "—"
        if (variance == 0) return "صفر"

        // جلوگیری از سرریز
        val safeVariance = if (variance == Int.MIN_VALUE) 0 else variance
        val absValue = if (safeVariance >= 0) safeVariance else -safeVariance
        val prefix = if (safeVariance > 0) "کسری " else "اضافه "

        return if (product.isRoundBox) {
            "$prefix${toPersianDigits(absValue)} باکس"
        } else {
            val capacity = if (product.boxCapacity <= 0) 1 else product.boxCapacity
            val boxes = absValue / capacity
            val units = absValue % capacity
            if (units == 0) {
                "$prefix${toPersianDigits(boxes)} باکس"
            } else {
                "$prefix${toPersianDigits(boxes)} ب + ${toPersianDigits(units)} ع"
            }
        }
    }
}
