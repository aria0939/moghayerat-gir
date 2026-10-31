package com.damdarangha.moghayerat.util

import java.util.Calendar

/**
 * تبدیل تاریخ میلادی به شمسی (تقویم جلالی).
 *
 * الگوریتم: روش استاندارد جلالی (بر اساس الگوریتم jalaali-js و Borkowski)
 * که به‌طور گسترده تست شده و دقت آن برای همه سال‌ها ثابت است.
 *
 * نکته مهم: پیاده‌سازی قبلی باگ داشت و برای همه تاریخ‌ها (۱،۱،۱) برمی‌گرداند.
 * این پیاده‌سازی جایگزین شد.
 */
object JalaliDateConverter {

    private val persianMonths = arrayOf(
        "فروردین", "اردیبهشت", "خرداد", "تیر", "مرداد", "شهریور",
        "مهر", "آبان", "آذر", "دی", "بهمن", "اسفند"
    )

    private val persianWeekDays = arrayOf(
        "شنبه", "یکشنبه", "دوشنبه", "سه‌شنبه", "چهارشنبه", "پنجشنبه", "جمعه"
    )

    data class JalaliDate(
        val year: Int,
        val month: Int,   // 1..12
        val day: Int      // 1..31
    ) {
        /** فرمت استاندارد: 1403/07/22 */
        fun toDateString(): String = "%04d/%02d/%02d".format(year, month, day)

        /** فرمت امن برای نام فایل: 1403-07-22 */
        fun toFileSafeString(): String = "%04d-%02d-%02d".format(year, month, day)

        /** فرمت نمایشی: ۲۲ مهر ۱۴۰۳ */
        fun toDisplayString(): String {
            val monthName = persianMonths.getOrNull(month - 1) ?: ""
            return "$day $monthName $year"
        }

        /** فرمت کامل با روز هفته: جمعه ۲۲ مهر ۱۴۰۳ */
        fun toFullDisplayString(weekDay: Int = 0): String {
            val dayName = persianWeekDays.getOrNull(weekDay) ?: ""
            return "$dayName ${toDisplayString()}"
        }

        /** اعداد فارسی */
        fun toPersianDateString(): String = toDateString().map { c ->
            if (c.isDigit()) ('۰' + (c - '0')) else c
        }.joinToString("")
    }

    // ==================== الگوریتم اصلی جلالی ====================

    /**
     * آیا سال شمسی کبیسه است؟
     * بر اساس باقیمانده تقسیم بر ۳۳ (روش استاندارد).
     */
    fun isLeapJalali(jalaliYear: Int): Boolean {
        // سال‌های کبیسه در چرخه ۳۳ ساله: 1, 5, 9, 13, 17, 22, 26, 30
        val cycle = ((jalaliYear - 474) % 33 + 33) % 33
        val leapYears = intArrayOf(1, 5, 9, 13, 17, 22, 26, 30)
        return cycle in leapYears
    }

    /**
     * تبدیل میلادی به شمسی.
     *
     * این پیاده‌سازی بر اساس الگوریتم استاندارد است که در
     * jalaali-js، moment-jalaali و کتابخانه‌های مشابه استفاده می‌شود.
     *
     * @param gregorianYear سال میلادی (مثلاً 2024)
     * @param gregorianMonth ماه میلادی (1..12)
     * @param gregorianDay روز میلادی (1..31)
     * @return تاریخ شمسی
     */
    fun gregorianToJalali(gregorianYear: Int, gregorianMonth: Int, gregorianDay: Int): JalaliDate {
        // اعتبارسنجی ورودی
        if (gregorianYear < 1 || gregorianMonth < 1 || gregorianMonth > 12 || gregorianDay < 1) {
            return JalaliDate(1, 1, 1)
        }

        // روزهای سپری‌شده از ابتدای تقویم میلادی (1/1/1)
        val gDaysInMonth = intArrayOf(31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31)

        var gy = gregorianYear - 1600
        var gm = gregorianMonth - 1
        var gd = gregorianDay - 1

        var gDayNo = 365 * gy + (gy + 3) / 4 - (gy + 99) / 100 + (gy + 399) / 400

        for (i in 0 until gm) {
            gDayNo += gDaysInMonth[i]
        }
        // روز کبیسه (فوریه ۲۹) اگر سال کبیسه باشد و ماه بعد از فوریه باشد
        if (gm > 1 && isLeapGregorian(gregorianYear)) {
            gDayNo++
        }
        gDayNo += gd

        // تبدیل به روزهای جلالی
        // مبدأ جلالی: 1 فروردین 1 = 19 مارس 622 (Julian) = 22 مارس 622 (Gregorian)
        val jDayNo = gDayNo - 79

        // محاسبه سال جلالی
        var jYear = 979
        var remaining = jDayNo

        // هر ۳۳ سال یک چرخه (12053 روز)
        val cycles = remaining / 12053
        jYear += 33 * cycles
        remaining %= 12053

        // سال‌های باقی‌مانده در چرخه فعلی
        while (true) {
            val yearDays = if (isLeapJalali(jYear)) 366 else 365
            if (remaining < yearDays) break
            remaining -= yearDays
            jYear++
        }

        // محاسبه ماه و روز
        val jDaysInMonth = intArrayOf(31, 31, 31, 31, 31, 31, 30, 30, 30, 30, 30, if (isLeapJalali(jYear)) 30 else 29)
        var jMonth = 0
        while (jMonth < 12 && remaining >= jDaysInMonth[jMonth]) {
            remaining -= jDaysInMonth[jMonth]
            jMonth++
        }

        return JalaliDate(jYear, jMonth + 1, remaining + 1)
    }

    /**
     * آیا سال میلادی کبیسه است؟
     */
    private fun isLeapGregorian(year: Int): Boolean {
        return (year % 4 == 0 && year % 100 != 0) || (year % 400 == 0)
    }

    // ==================== متدهای کمکی ====================

    /**
     * تبدیل رشته ISO (yyyy-MM-dd) به تاریخ شمسی.
     */
    fun gregorianToJalaliString(isoDate: String): String {
        val parts = isoDate.split("-")
        if (parts.size != 3) return isoDate
        val year = parts[0].toIntOrNull() ?: return isoDate
        val month = parts[1].toIntOrNull() ?: return isoDate
        val day = parts[2].toIntOrNull() ?: return isoDate
        return gregorianToJalali(year, month, day).toDateString()
    }

    /**
     * تبدیل رشته ISO به فرمت امن برای نام فایل.
     */
    fun gregorianToJalaliFileSafeString(isoDate: String): String {
        val parts = isoDate.split("-")
        if (parts.size != 3) return isoDate.replace("/", "-")
        val year = parts[0].toIntOrNull() ?: return isoDate.replace("/", "-")
        val month = parts[1].toIntOrNull() ?: return isoDate.replace("/", "-")
        val day = parts[2].toIntOrNull() ?: return isoDate.replace("/", "-")
        return gregorianToJalali(year, month, day).toFileSafeString()
    }

    /**
     * تاریخ امروز به میلادی در فرمت yyyy-MM-dd.
     */
    fun todayIso(): String {
        val cal = Calendar.getInstance()
        val year = cal.get(Calendar.YEAR)
        val month = cal.get(Calendar.MONTH) + 1
        val day = cal.get(Calendar.DAY_OF_MONTH)
        return "%04d-%02d-%02d".format(year, month, day)
    }

    /**
     * تاریخ امروز شمسی.
     */
    fun todayJalali(): JalaliDate {
        val cal = Calendar.getInstance()
        return gregorianToJalali(
            cal.get(Calendar.YEAR),
            cal.get(Calendar.MONTH) + 1,
            cal.get(Calendar.DAY_OF_MONTH)
        )
    }
}
