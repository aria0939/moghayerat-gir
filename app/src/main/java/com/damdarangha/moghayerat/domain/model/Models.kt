package com.damdarangha.moghayerat.domain.model

/**
 * نوع بسته‌بندی محصول
 * - STERILE: ظرفیت باکس ثابت (مانند ۱۰ برای استریل)
 * - TUB: ظرفیت باکس ۴ (دبه)
 * - ROUND_BOX: باکس رند، فقط تعداد باکس ثبت می‌شود (خرما)
 * - STANDARD_BOX: باکس استاندارد با ظرفیت متغیر (شیر، ماست باکسی، دونات و کیک)
 */
enum class PackagingType(val displayName: String) {
    STERILE("باکس استاندارد"),
    TUB("باکس ۴ عددی"),
    ROUND_BOX("باکس رند"),
    STANDARD_BOX("باکس استاندارد")
}

/**
 * گروه‌بندی محصول
 */
enum class ProductGroup(val displayName: String) {
    STERILE("استریل"),
    MILK("شیر"),
    YOGHURT_BOX("ماست باکسی"),
    DATE("خرما"),
    DONUT_CAKE("دونات و کیک")
}

/**
 * مدل محصول (مستقل از لایه داده)
 */
data class Product(
    val id: Long = 0L,
    val name: String,
    val group: ProductGroup,
    val packagingType: PackagingType,
    val boxCapacity: Int = 1, // ظرفیت هر باکس (تعداد واحد در هر باکس). برای ROUND_BOX این مقدار نادیده گرفته می‌شود.
    val displayOrder: Int = 0,
    val isActive: Boolean = true,
    val createdAt: Long = System.currentTimeMillis()
) {
    /**
     * آیا این محصول از نوع باکس رند است؟
     */
    val isRoundBox: Boolean get() = packagingType == PackagingType.ROUND_BOX
}

/**
 * جفت باکس و عدد
 * این مدل برای ورودی‌های کاربر استفاده می‌شود.
 */
data class BoxAndUnits(
    val boxes: Int = 0,
    val units: Int = 0
) {
    /**
     * آیا هر دو فیلد صفر هستند؟
     */
    fun isZero(): Boolean = boxes == 0 && units == 0

    /**
     * آیا فیلد خالی است؟ (هر دو صفر)
     */
    fun isEmpty(): Boolean = isZero()

    companion object {
        val ZERO = BoxAndUnits(0, 0)
    }
}

/**
 * اطلاعات خام آمار روزانه یک محصول
 * این مدل قبل از محاسبه مغایرت استفاده می‌شود.
 */
data class DailyStatInput(
    val productId: Long,
    val isoDate: String, // تاریخ میلادی به فرمت yyyy-MM-dd
    val openingStock: BoxAndUnits,         // مانده شب گذشته
    val morningLoading: BoxAndUnits,       // بارگیری صبح / شهرستان
    val actualMorningStock: BoxAndUnits?,  // مانده واقعی بعد از بارگیری صبح (اختیاری)
    val receipt: BoxAndUnits,              // رسید انبار
    val returns: BoxAndUnits,              // برگشت به انبار (به موجودی اضافه می‌شود)
    val gifts: BoxAndUnits,                // هدایا
    val waste: BoxAndUnits,                // ضایعات
    val midDayLoading: BoxAndUnits,        // بارگیری میان روز (از موجودی کم می‌شود)
    val nightLoading: BoxAndUnits,         // بارگیری شب
    val currentStock: BoxAndUnits,         // مانده واقعی پایان روز
    val notes: String = ""                 // توضیحات اختیاری
)

/**
 * نتیجه محاسبه مغایرت
 */
data class VarianceResult(
    val openingStockTotal: Int,             // مانده شب گذشته به عدد کل
    val morningLoadingTotal: Int,          // بارگیری صبح به عدد کل
    val receiptTotal: Int,                 // رسید به عدد کل
    val returnsTotal: Int,                 // برگشت به انبار به عدد کل
    val giftsTotal: Int,                   // هدایا به عدد کل
    val wasteTotal: Int,                   // ضایعات به عدد کل
    val midDayLoadingTotal: Int,           // بارگیری میان روز به عدد کل
    val nightLoadingTotal: Int,            // بارگیری شب به عدد کل
    val expectedMorningStockTotal: Int,    // موجودی مورد انتظار بعد از بارگیری صبح
    val actualMorningStockTotal: Int?,     // موجودی واقعی بعد از بارگیری صبح (در صورت ورود)
    val morningVariance: Int?,             // مغایرت اول کار (در صورت ورود مانده واقعی صبح)
    val expectedClosingStockTotal: Int,    // موجودی مورد انتظار پایان روز
    val currentStockTotal: Int,            // موجودی واقعی پایان روز به عدد کل
    val closingVariance: Int               // مغایرت پایان روز (مثبت=کسری، منفی=اضافه)
) {
    /**
     * آیا مغایرت پایان روز صفر است؟
     */
    val isClosingVarianceZero: Boolean get() = closingVariance == 0

    /**
     * آیا مغایرت اول کار صفر است؟
     */
    val isMorningVarianceZero: Boolean get() = morningVariance == 0

    /**
     * برچسب وضعیت پایان روز
     * - "مغایرت صفر است؛ موجودی مطابقت دارد"
     * - "کسری X واحد"
     * - "اضافه X واحد"
     */
    val closingStatusText: String
        get() = when {
            closingVariance == 0 -> "مغایرت صفر است؛ موجودی مطابقت دارد"
            closingVariance > 0 -> "کسری ${closingVariance.toPersianDigits()} واحد"
            else -> "اضافه ${(-closingVariance).toPersianDigits()} واحد"
        }

    /**
     * نوع وضعیت: ZERO, DEFICIT, SURPLUS
     */
    val closingStatusType: StatusType
        get() = when {
            closingVariance == 0 -> StatusType.ZERO
            closingVariance > 0 -> StatusType.DEFICIT
            else -> StatusType.SURPLUS
        }
}

enum class StatusType(val label: String) {
    ZERO("مطابقت"),
    DEFICIT("کسری"),
    SURPLUS("اضافه"),
    INCOMPLETE("ناقص")
}

/**
 * تبدیل اعداد انگلیسی به فارسی
 */
fun Int.toPersianDigits(): String {
    val persianDigits = arrayOf('۰', '۱', '۲', '۳', '۴', '۵', '۶', '۷', '۸', '۹')
    return this.toString().map { c ->
        if (c.isDigit()) persianDigits[c - '0'] else c
    }.joinToString("")
}

/**
 * تبدیل رشته اعداد فارسی به عدد صحیح
 */
fun String.persianToEnglishDigits(): String {
    val persianDigits = arrayOf('۰', '۱', '۲', '۳', '۴', '۵', '۶', '۷', '۸', '۹')
    val arabicDigits = arrayOf('٠', '١', '٢', '٣', '٤', '٥', '٦', '٧', '٨', '٩')
    return this.map { c ->
        val persianIdx = persianDigits.indexOf(c)
        val arabicIdx = arabicDigits.indexOf(c)
        when {
            persianIdx >= 0 -> persianIdx.toString()
            arabicIdx >= 0 -> arabicIdx.toString()
            else -> c.toString()
        }
    }.joinToString("")
}
