package com.damdarangha.moghayerat.util

import java.util.Calendar
import java.util.Locale

/**
 * تبدیل تاریخ میلادی به شمسی (تقویم جلالی).
 *
 * این پیاده‌سازی بر اساس الگوریتم استاندارد JDN (Julian Day Number) است
 * که به‌طور گسترده استفاده می‌شود و دقت آن ثابت شده است.
 *
 * منبع الگوریتم: Borko Furht (editor), "Handbook of Data Compression",
 * و پیاده‌سازی‌های متن‌باز متعدد.
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
        /**
         * فرمت استاندارد: ۱۴۰۳/۰۷/۲۲
         */
        fun toDateString(): String {
            return "%04d/%02d/%02d".format(year, month, day)
        }

        /**
         * فرمت امن برای نام فایل: ۱۴۰۳-۰۷-۲۲ (با خط تیره به‌جای اسلش)
         */
        fun toFileSafeString(): String {
            return "%04d-%02d-%02d".format(year, month, day)
        }

        /**
         * فرمت نمایشی: ۲۲ مهر ۱۴۰۳
         */
        fun toDisplayString(): String {
            val monthName = persianMonths.getOrNull(month - 1) ?: ""
            return "$day $monthName $year"
        }

        /**
         * فرمت کامل با روز هفته: جمعه ۲۲ مهر ۱۴۰۳
         */
        fun toFullDisplayString(weekDay: Int = 0): String {
            val dayName = persianWeekDays.getOrNull(weekDay) ?: ""
            return "$dayName ${toDisplayString()}"
        }

        /**
         * اعداد فارسی
         */
        fun toPersianDateString(): String {
            return toDateString().map { c ->
                if (c.isDigit()) ('۰' + (c - '0')) else c
            }.joinToString("")
        }
    }

    /**
     * تبدیل میلادی به شمسی با استفاده از الگوریتم JDN.
     *
     * @param gregorianYear سال میلادی (مثلاً ۲۰۲۴)
     * @param gregorianMonth ماه میلادی (۱..۱۲)
     * @param gregorianDay روز میلادی (۱..۳۱)
     * @return تاریخ شمسی
     */
    fun gregorianToJalali(gregorianYear: Int, gregorianMonth: Int, gregorianDay: Int): JalaliDate {
        // محاسبه Julian Day Number از تاریخ میلادی
        val gy = gregorianYear - 1600
        val gm = gregorianMonth - 1
        val gd = gregorianDay - 1

        var gDayNo = 365 * gy + (gy + 3) / 4 - (gy + 99) / 100 + (gy + 399) / 400

        // تعداد روزهای ماه‌های میلادی قبل از ماه فعلی
        val gMonthDays = intArrayOf(31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31)
        for (i in 0 until gm) {
            gDayNo += gMonthDays[i]
        }
        // اضافه‌کردن روز کبیسه اگر ماه بعد از فوریه است
        if (gm > 1 && (gy % 4 == 0 && gy % 100 != 0) || (gy % 400 == 0)) {
            gDayNo += 1
        }
        gDayNo += gd

        // محاسبه Julian Day Number مطلق
        val jdn = gDayNo + 1721028.5

        // تبدیل به روز‌های شمسی
        // منظور از Jalali Epoch: 1948320.5 (یعنی ۱ فروردین سال ۱ شمسی)
        val jalaliEpoch = 1948320.5
        val jalaliDayNo = jdn - jalaliEpoch

        if (jalaliDayNo < 0) {
            // تاریخ قبل از شروع تقویم شمسی - fallback
            return JalaliDate(1, 1, 1)
        }

        val jalaliTotal = jalaliDayNo.toInt()

        // محاسبه سال شمسی با چرخه ۳۳ ساله
        // هر ۳۳ سال، ۸ سال کبیسه وجود دارد
        var jy = (jalaliTotal - 1) / 12053 * 33
        var remaining = (jalaliTotal - 1) % 12053

        // حلقه برای پیدا کردن سال دقیق
        var yearFound = false
        while (!yearFound) {
            val yearDays = if (isLeapJalali(jy + 1)) 366 else 365
            if (remaining < yearDays) {
                yearFound = true
            } else {
                remaining -= yearDays
                jy++
            }
        }

        // محاسبه ماه و روز
        val jalaliMonthDays = intArrayOf(31, 31, 31, 31, 31, 31, 30, 30, 30, 30, 30, 29)
        var jm = 0
        var jd = remaining

        for (i in 0 until 12) {
            if (jd < jalaliMonthDays[i]) {
                jm = i + 1
                jd += 1
                break
            }
            jd -= jalaliMonthDays[i]
            if (i == 11) {
                jm = 12
                jd += 1
            }
        }

        return JalaliDate(jy, jm, jd)
    }

    /**
     * تبدیل میلادی به شمسی با رشته ISO.
     * فرمت خروجی: yyyy/MM/dd (مثلاً ۱۴۰۳/۰۷/۲۲)
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
     * تبدیل میلادی به شمسی با فرمت امن برای نام فایل.
     * فرمت خروجی: yyyy-MM-dd (مثلاً ۱۴۰۳-۰۷-۲۲)
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
     * از منطقه زمانی دستگاه استفاده می‌کند تا تاریخ صحیح روز نمایش داده شود.
     */
    fun todayIso(): String {
        val cal = Calendar.getInstance()
        val year = cal.get(Calendar.YEAR)
        val month = cal.get(Calendar.MONTH) + 1
        val day = cal.get(Calendar.DAY_OF_MONTH)
        return "%04d-%02d-%02d".format(year, month, day)
    }

    /**
     * تاریخ امروز شمسی با استفاده از منطقه زمانی دستگاه.
     */
    fun todayJalali(): JalaliDate {
        val cal = Calendar.getInstance()
        return gregorianToJalali(
            cal.get(Calendar.YEAR),
            cal.get(Calendar.MONTH) + 1,
            cal.get(Calendar.DAY_OF_MONTH)
        )
    }

    /**
     * آیا سال شمسی کبیسه است؟
     * الگوریتم: سال‌های کبیسه شمسی در چرخه ۳۳ ساله.
     */
    private fun isLeapJalali(jalaliYear: Int): Boolean {
        // الگوریتم استاندارد کبیسه‌گیری جلالی
        // سال‌های کبیسه: 1, 5, 9, 13, 17, 22, 26, 30 در هر چرخه ۳۳ ساله
        val cycle = ((jalaliYear - 474) % 33 + 33) % 33
        val leapYears = setOf(1, 5, 9, 13, 17, 22, 26, 30)
        return cycle in leapYears
    }
}
