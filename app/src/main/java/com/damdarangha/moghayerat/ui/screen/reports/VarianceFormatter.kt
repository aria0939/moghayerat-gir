package com.damdarangha.moghayerat.ui.screen.reports

import com.damdarangha.moghayerat.domain.model.Product

/**
 * فرمت‌ساز مشترک برای نمایش مغایرت.
 *
 * قالب:
 * - کسری ۱ باکس و ۲ عدد: "-۱B + ۲'"
 * - اضافه ۱ باکس و ۲ عدد: "+۱B + ۲'"
 * - کسری فقط ۱ باکس: "-۱B"
 * - اضافه فقط ۲ عدد: "+۲'"
 * - مطابقت: "۰"
 * - ثبت‌نشده: "—"
 *
 * قواعد:
 * ۱. تمام اعداد فارسی
 * ۲. B انگلیسی بزرگ = باکس
 * ۳. ' ساده = عدد
 * ۴. - برای منفی، + برای مثبت
 * ۵. علامت ابتدای عبارت = علامت کل
 * ۶. + وسط = جداکننده، نه جمع جبری
 * ۷. بخش‌های صفر حذف شوند
 * ۸. صفر ≠ ثبت‌نشده
 * ۹. باکس رند فقط B
 *
 * علامت‌گذاری:
 * - منفی = کسری
 * - مثبت = اضافه
 * - صفر = مطابقت
 */
object VarianceFormatter {

    private val persianDigits = arrayOf('۰', '۱', '۲', '۳', '۴', '۵', '۶', '۷', '۸', '۹')

    /**
     * تبدیل عدد به اعداد فارسی.
     */
    fun toPersian(value: Int): String {
        val safe = if (value == Int.MIN_VALUE) 0 else value
        return safe.toString().map { c ->
            if (c.isDigit()) persianDigits[c - '0'] else c
        }.joinToString("")
    }

    /**
     * فرمت مقدار بدون علامت (قدرمطلق).
     * فقط برای نمایش مقدار مطلق استفاده می‌شود.
     */
    fun formatAbsolute(total: Int, product: Product): String {
        if (total == 0) return "۰"
        val abs = if (total == Int.MIN_VALUE) 0 else kotlin.math.abs(total)
        return if (product.isRoundBox) {
            "${toPersian(abs)}B"
        } else {
            val capacity = if (product.boxCapacity <= 0) 1 else product.boxCapacity
            val boxes = abs / capacity
            val units = abs % capacity
            when {
                boxes == 0 -> "${toPersian(units)}'"
                units == 0 -> "${toPersian(boxes)}B"
                else -> "${toPersian(boxes)}B + ${toPersian(units)}'"
            }
        }
    }

    /**
     * فرمت مغایرت با علامت.
     *
     * قرارداد علامت:
     * - منفی = کسری
     * - مثبت = اضافه
     * - صفر = مطابقت
     *
     * @param variance مقدار مغایرت (منفی=کسری، مثبت=اضافه، صفر=مطابقت)
     * @param product محصول موردنظر
     * @param isRegistered آیا این مقدار ثبت‌شده است؟ (null = ثبت‌نشده → "—")
     * @return رشته قالب‌بندی‌شده
     */
    fun formatVariance(
        variance: Int?,
        product: Product,
        isRegistered: Boolean = true
    ): String {
        if (!isRegistered || variance == null) return "—"
        if (variance == 0) return "۰"

        val sign = if (variance > 0) "+" else "-"
        val abs = if (variance == Int.MIN_VALUE) 0 else kotlin.math.abs(variance)

        // استفاده از LRM (Left-to-Right Mark) برای اطمینان از اینکه علامت‌های لاتین
        // (B، '، +، -) در متن RTL به‌درستی نمایش داده می‌شوند و در جای درست قرار می‌گیرند.
        val absStr = if (product.isRoundBox) {
            "${toPersian(abs)}B\u200E"
        } else {
            val capacity = if (product.boxCapacity <= 0) 1 else product.boxCapacity
            val boxes = abs / capacity
            val units = abs % capacity
            when {
                boxes == 0 -> "${toPersian(units)}'\u200E"
                units == 0 -> "${toPersian(boxes)}B\u200E"
                // بدون فاصله اطراف + (مثلاً 1B+2')
                else -> "${toPersian(boxes)}B+${toPersian(units)}'\u200E"
            }
        }

        // علامت در ابتدا می‌آید با فاصله (مثلاً + 1B+2' یا - 2')
        return "$sign $absStr"
    }

    /**
     * فرمت مقدار ساده (بدون علامت مغایرت).
     * برای نمایش مقادیر موجودی، بارگیری و غیره.
     *
     * @param total تعداد کل واحد
     * @param product محصول
     * @return رشته مثل "۲B + ۶'" یا "۳B" یا "۶'" یا "۰"
     */
    fun formatValue(total: Int, product: Product): String {
        if (total == 0) return "۰"
        return if (product.isRoundBox) {
            "${toPersian(total)}B"
        } else {
            val capacity = if (product.boxCapacity <= 0) 1 else product.boxCapacity
            val boxes = total / capacity
            val units = total % capacity
            when {
                boxes == 0 -> "${toPersian(units)}'"
                units == 0 -> "${toPersian(boxes)}B"
                else -> "${toPersian(boxes)}B + ${toPersian(units)}'"
            }
        }
    }
}
