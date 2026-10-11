package com.damdarangha.moghayerat.util

import java.util.Calendar
import java.util.Locale

/**
 * تبدیل تاریخ میلادی به شمسی (بدون نیاز به کتابخانه خارجی)
 * پیاده‌سازی الگوریتم استاندارد تبدیل جلالی
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
        val day: Int,     // 1..31
        val weekDay: Int  // 0=شنبه ... 6=جمعه
    ) {
        /**
         * فرمت استاندارد: ۱۴۰۳/۰۷/۲۲
         */
        fun toDateString(): String {
            return "%04d/%02d/%02d".format(year, month, day)
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
        fun toFullDisplayString(): String {
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
     * تبدیل میلادی به شمسی
     */
    fun gregorianToJalali(gregorianYear: Int, gregorianMonth: Int, gregorianDay: Int): JalaliDate {
        val gy = gregorianYear - 1600
        val gm = gregorianMonth - 1
        val gd = gregorianDay - 1

        val gDaysNo = intArrayOf(365, 366)
        val jalaliMonthDays = intArrayOf(31, 31, 31, 31, 31, 31, 30, 30, 30, 30, 30, 29)

        var totalDays = 0
        for (i in 0 until gy) {
            totalDays += if (isLeapGregorian(1600 + i)) 366 else 365
        }
        for (i in 0 until gm) {
            totalDays += when (i) {
                0, 2, 4, 6, 7, 9, 11 -> 31
                3, 5, 8, 10 -> 30
                1 -> if (isLeapGregorian(gregorianYear)) 29 else 28
                else -> 30
            }
        }
        totalDays += gd

        // تبدیل به روزهای شمسی
        val jalaliEpoch = 1948320 // تعداد روزهای ۱ فروردین ۱ شمسی به روز یولیوس
        val jalaliTotal = totalDays + 1948321 // انتقال به مبدا شمسی
        // محاسبه سال شمسی

        var jy: Int
        var days: Int = jalaliTotal - jalaliEpoch
        if (days < 0) days = 0

        // محاسبه سال
        jy = 0
        while (true) {
            val yearDays = if (isLeapJalali(jy + 1)) 366 else 365
            if (days < yearDays) break
            days -= yearDays
            jy++
        }
        jy += 1

        // محاسبه ماه
        var jm = 0
        for (i in 0 until 12) {
            if (days < jalaliMonthDays[i]) {
                jm = i + 1
                break
            }
            days -= jalaliMonthDays[i]
            if (i == 11) jm = 12
        }

        val jd = days + 1

        // محاسبه روز هفته (۰=شنبه)
        val cal = Calendar.getInstance()
        cal.set(gregorianYear, gregorianMonth - 1, gregorianDay)
        // در Calendar: یکشنبه=1، ..., جمعه=7
        val dayOfWeek = cal.get(Calendar.DAY_OF_WEEK)
        // تبدیل به شنبه=0
        val persianWeekDay = (dayOfWeek + 1) % 7

        return JalaliDate(jy, jm, jd, persianWeekDay)
    }

    /**
     * تبدیل میلادی به شمسی با رشته ISO
     */
    fun gregorianToJalaliString(isoDate: String): String {
        val parts = isoDate.split("-")
        if (parts.size != 3) return isoDate
        val year = parts[0].toInt()
        val month = parts[1].toInt()
        val day = parts[2].toInt()
        return gregorianToJalali(year, month, day).toDateString()
    }

    /**
     * تاریخ امروز به میلادی در فرمت yyyy-MM-dd
     */
    fun todayIso(): String {
        val cal = Calendar.getInstance()
        val year = cal.get(Calendar.YEAR)
        val month = cal.get(Calendar.MONTH) + 1
        val day = cal.get(Calendar.DAY_OF_MONTH)
        return "%04d-%02d-%02d".format(year, month, day)
    }

    /**
     * تاریخ امروز شمسی
     */
    fun todayJalali(): JalaliDate {
        val cal = Calendar.getInstance()
        return gregorianToJalali(
            cal.get(Calendar.YEAR),
            cal.get(Calendar.MONTH) + 1,
            cal.get(Calendar.DAY_OF_MONTH)
        )
    }

    private fun isLeapGregorian(year: Int): Boolean {
        return (year % 4 == 0 && year % 100 != 0) || (year % 400 == 0)
    }

    private fun isLeapJalali(jalaliYear: Int): Boolean {
        // الگوریتم کبیسه‌گیری جلالی
        // سال کبیسه شمسی بر اساس چرخه ۳۳ ساله
        val cycle = ((jalaliYear - 474) % 2820 + 2820) % 2820
        val yearInCycle = jalaliYear - 474 - cycle
        val leap = yearInCycle * 682 % 2816 < 682
        return leap
    }
}
