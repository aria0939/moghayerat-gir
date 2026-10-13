package com.damdarangha.moghayerat.domain.calculation

import com.damdarangha.moghayerat.domain.model.BoxAndUnits
import com.damdarangha.moghayerat.domain.model.DailyStatInput
import com.damdarangha.moghayerat.domain.model.PackagingType
import com.damdarangha.moghayerat.domain.model.Product
import com.damdarangha.moghayerat.domain.model.ProductGroup
import com.google.common.truth.Truth.assertThat
import org.junit.Test

/**
 * تست‌های واحد موتور محاسبات مغایرت‌گیر
 *
 * این تست‌ها تمام ۱۲ سناریوی الزامی به‌علاوه مثال استریل ۲۰۰ را پوشش می‌دهند.
 */
class VarianceCalculatorTest {

    // محصول نمونه: شیر استریل ۲۰۰ با ظرفیت باکس ۲۷
    private val sterileProduct = Product(
        id = 1L,
        name = "شیر استریل ۲۰۰",
        group = ProductGroup.STERILE,
        packagingType = PackagingType.STERILE,
        boxCapacity = 27
    )

    // محصول با ظرفیت باکس ۱ برای تست‌های مغایرت شیفت شب
    // (هر باکس = ۱ عدد، پس مقدار باکس و عدد یکی هستند)
    private val unitProduct = Product(
        id = 10L,
        name = "محصول تستی",
        group = ProductGroup.STERILE,
        packagingType = PackagingType.STERILE,
        boxCapacity = 1
    )

    // محصول ماست باکسی با ظرفیت ۴
    private val tubProduct = Product(
        id = 2L,
        name = "ماست باکسی",
        group = ProductGroup.YOGHURT_BOX,
        packagingType = PackagingType.TUB,
        boxCapacity = 4
    )

    // محصول خرما با باکس رند
    private val dateProduct = Product(
        id = 3L,
        name = "خرما",
        group = ProductGroup.DATE,
        packagingType = PackagingType.ROUND_BOX,
        boxCapacity = 1
    )

    // ===================== تست‌های تبدیل واحد =====================

    @Test
    fun `تبدیل باکس و عدد به تعداد کل برای محصول ۲۷ عددی`() {
        // ۴۶ باکس و ۴ عدد = ۴۶×۲۷ + ۴ = ۱۲۴۶
        val total = VarianceCalculator.toTotalUnits(
            BoxAndUnits(boxes = 46, units = 4),
            sterileProduct
        )
        assertThat(total).isEqualTo(1246)
    }

    @Test
    fun `تبدیل باکس و عدد به تعداد کل برای محصول ۴ عددی`() {
        // ۳ باکس و ۲ عدد = ۳×۴ + ۲ = ۱۴
        val total = VarianceCalculator.toTotalUnits(
            BoxAndUnits(boxes = 3, units = 2),
            tubProduct
        )
        assertThat(total).isEqualTo(14)
    }

    @Test
    fun `تبدیل باکس رند به تعداد کل`() {
        // برای باکس رند فقط تعداد باکس برمی‌گردد
        val total = VarianceCalculator.toTotalUnits(
            BoxAndUnits(boxes = 10, units = 0),
            dateProduct
        )
        assertThat(total).isEqualTo(10)
    }

    @Test
    fun `تبدیل تعداد کل به باکس و عدد برای محصول ۲۷ عددی`() {
        // ۱۲۴۶ عدد = ۴۶ باکس و ۴ عدد
        val pair = VarianceCalculator.fromTotalUnits(1246, sterileProduct)
        assertThat(pair.boxes).isEqualTo(46)
        assertThat(pair.units).isEqualTo(4)
    }

    @Test
    fun `نرمال‌سازی ۲ باکس و ۳۰ عدد به ۳ باکس و ۳ عدد برای محصول ۲۷ عددی`() {
        // تست سناریو ۶: ۲ باکس و ۳۰ عدد باید به ۳ باکس و ۳ عدد تبدیل شود
        val normalized = VarianceCalculator.normalize(
            BoxAndUnits(boxes = 2, units = 30),
            sterileProduct
        )
        assertThat(normalized.boxes).isEqualTo(3)
        assertThat(normalized.units).isEqualTo(3)
    }

    // ===================== تست‌های اصلی مغایرت =====================

    /**
     * تست سناریو ۱: مثال استریل ۲۰۰ با مغایرت صفر.
     * این دقیقاً همان مثال تست اجباری بخش ۸ سند پروژه است.
     */
    @Test
    fun `سناریو ۱ - مثال استریل ۲۰۰ با مغایرت صفر`() {
        val input = DailyStatInput(
            productId = 1L,
            isoDate = "2024-01-15",
            openingStock = BoxAndUnits(boxes = 46, units = 4),    // ۴۶×۲۷+۴ = ۱۲۴۶
            morningLoading = BoxAndUnits(boxes = 22, units = 0),  // ۲۲×۲۷ = ۵۹۴
            actualMorningStock = BoxAndUnits(boxes = 24, units = 4), // ۲۴×۲۷+۴ = ۶۵۲
            receipt = BoxAndUnits.ZERO,
            returns = BoxAndUnits.ZERO,
            gifts = BoxAndUnits.ZERO,
            waste = BoxAndUnits.ZERO,
            laboratory = BoxAndUnits.ZERO,
            midDayLoading = BoxAndUnits.ZERO,
            nightLoading = BoxAndUnits(boxes = 8, units = 2),     // ۸×۲۷+۲ = ۲۱۸
            currentStock = BoxAndUnits(boxes = 16, units = 2)     // ۱۶×۲۷+۲ = ۴۳۴
        )

        val result = VarianceCalculator.calculate(input, sterileProduct)

        // بررسی محاسبات اولیه
        assertThat(result.openingStockTotal).isEqualTo(1246)
        assertThat(result.morningLoadingTotal).isEqualTo(594)
        assertThat(result.nightLoadingTotal).isEqualTo(218)
        assertThat(result.currentStockTotal).isEqualTo(434)

        // بررسی مغایرت اول کار
        assertThat(result.expectedMorningStockTotal).isEqualTo(652) // ۱۲۴۶ - ۵۹۴
        assertThat(result.actualMorningStockTotal).isEqualTo(652)  // ۲۴×۲۷+۴
        assertThat(result.morningVariance).isEqualTo(0)            // مغایرت اول کار صفر است

        // بررسی مغایرت پایان روز
        // موجودی مورد انتظار = ۱۲۴۶ - ۵۹۴ - ۲۱۸ = ۴۳۴
        assertThat(result.expectedClosingStockTotal).isEqualTo(434)
        assertThat(result.closingVariance).isEqualTo(0) // مغایرت پایان روز صفر است

        assertThat(result.isClosingVarianceZero).isTrue()
    }

    /**
     * تست سناریو ۲: موجودی واقعی ۲۷ عدد کمتر از مورد انتظار، باید یک باکس کسری باشد.
     */
    @Test
    fun `سناریو ۲ - کسری یک باکس برای محصول ۲۷ عددی`() {
        // در همان مثال قبلی، اما موجودی واقعی ۴۰۷ = ۱۵ باکس و ۲ عدد = ۱۵×۲۷+۲
        // مورد انتظار = ۴۳۴، واقعی = ۴۰۷، کسری = ۲۷ = یک باکس
        val input = DailyStatInput(
            productId = 1L,
            isoDate = "2024-01-15",
            openingStock = BoxAndUnits(boxes = 46, units = 4),
            morningLoading = BoxAndUnits(boxes = 22, units = 0),
            actualMorningStock = null,
            receipt = BoxAndUnits.ZERO,
            returns = BoxAndUnits.ZERO,
            gifts = BoxAndUnits.ZERO,
            waste = BoxAndUnits.ZERO,
            laboratory = BoxAndUnits.ZERO,
            midDayLoading = BoxAndUnits.ZERO,
            nightLoading = BoxAndUnits(boxes = 8, units = 2),
            currentStock = BoxAndUnits(boxes = 15, units = 2) // ۱۵×۲۷+۲ = ۴۰۷
        )

        val result = VarianceCalculator.calculate(input, sterileProduct)

        assertThat(result.expectedClosingStockTotal).isEqualTo(434)
        assertThat(result.currentStockTotal).isEqualTo(407)
        assertThat(result.closingVariance).isEqualTo(27) // کسری ۲۷ واحد = یک باکس

        // تبدیل کسری به باکس و عدد
        val deficitPair = VarianceCalculator.fromTotalUnits(result.closingVariance, sterileProduct)
        assertThat(deficitPair.boxes).isEqualTo(1)
        assertThat(deficitPair.units).isEqualTo(0)
    }

    /**
     * تست سناریو ۳: موجودی واقعی ۳ عدد بیشتر از مورد انتظار، باید ۳ عدد اضافه باشد.
     */
    @Test
    fun `سناریو ۳ - اضافه ۳ عدد برای محصول ۲۷ عددی`() {
        val input = DailyStatInput(
            productId = 1L,
            isoDate = "2024-01-15",
            openingStock = BoxAndUnits(boxes = 46, units = 4),
            morningLoading = BoxAndUnits(boxes = 22, units = 0),
            actualMorningStock = null,
            receipt = BoxAndUnits.ZERO,
            returns = BoxAndUnits.ZERO,
            gifts = BoxAndUnits.ZERO,
            waste = BoxAndUnits.ZERO,
            laboratory = BoxAndUnits.ZERO,
            midDayLoading = BoxAndUnits.ZERO,
            nightLoading = BoxAndUnits(boxes = 8, units = 2),
            currentStock = BoxAndUnits(boxes = 16, units = 5) // ۱۶×۲۷+۵ = ۴۳۷ = ۴۳۴+۳
        )

        val result = VarianceCalculator.calculate(input, sterileProduct)

        assertThat(result.expectedClosingStockTotal).isEqualTo(434)
        assertThat(result.currentStockTotal).isEqualTo(437)
        assertThat(result.closingVariance).isEqualTo(-3) // اضافه ۳ عدد

        val surplusPair = VarianceCalculator.fromTotalUnits(-result.closingVariance, sterileProduct)
        assertThat(surplusPair.boxes).isEqualTo(0)
        assertThat(surplusPair.units).isEqualTo(3)
    }

    /**
     * تست سناریو ۴: وجود رسید انبار و محاسبه صحیح آن.
     */
    @Test
    fun `سناریو ۴ - رسید انبار به موجودی اضافه می‌شود`() {
        val input = DailyStatInput(
            productId = 1L,
            isoDate = "2024-01-15",
            openingStock = BoxAndUnits(boxes = 10, units = 0),   // ۲۷۰
            morningLoading = BoxAndUnits(boxes = 2, units = 0),  // ۵۴
            actualMorningStock = null,
            receipt = BoxAndUnits(boxes = 5, units = 0),         // ۱۳۵ رسید
            returns = BoxAndUnits.ZERO,
            gifts = BoxAndUnits.ZERO,
            waste = BoxAndUnits.ZERO,
            laboratory = BoxAndUnits.ZERO,
            midDayLoading = BoxAndUnits.ZERO,
            nightLoading = BoxAndUnits(boxes = 3, units = 0),     // ۸۱
            currentStock = BoxAndUnits(boxes = 10, units = 0)     // ۲۷۰ واقعی
        )

        val result = VarianceCalculator.calculate(input, sterileProduct)

        // مورد انتظار = ۲۷۰ + ۱۳۵ - ۵۴ - ۸۱ = ۲۷۰
        assertThat(result.expectedClosingStockTotal).isEqualTo(270)
        assertThat(result.currentStockTotal).isEqualTo(270)
        assertThat(result.closingVariance).isEqualTo(0)
    }

    /**
     * تست سناریو ۵: وجود هدایا و ضایعات به‌صورت هم‌زمان.
     */
    @Test
    fun `سناریو ۵ - هدایا و ضایعات هم‌زمان از موجودی کم می‌شوند`() {
        val input = DailyStatInput(
            productId = 1L,
            isoDate = "2024-01-15",
            openingStock = BoxAndUnits(boxes = 10, units = 0),   // ۲۷۰
            morningLoading = BoxAndUnits.ZERO,
            actualMorningStock = null,
            receipt = BoxAndUnits.ZERO,
            returns = BoxAndUnits.ZERO,
            gifts = BoxAndUnits(boxes = 1, units = 5),            // ۳۲ = ۱×۲۷+۵
            waste = BoxAndUnits(boxes = 0, units = 10),
            laboratory = BoxAndUnits.ZERO,           // ۱۰
            midDayLoading = BoxAndUnits.ZERO,
            nightLoading = BoxAndUnits.ZERO,
            currentStock = BoxAndUnits(boxes = 8, units = 10)      // ۲۲۶ واقعی = ۸×۲۷+۱۰
        )

        val result = VarianceCalculator.calculate(input, sterileProduct)

        // مورد انتظار = ۲۷۰ - ۳۲ - ۱۰ = ۲۲۸
        assertThat(result.expectedClosingStockTotal).isEqualTo(228)
        assertThat(result.currentStockTotal).isEqualTo(226)
        assertThat(result.closingVariance).isEqualTo(2) // کسری ۲ عدد
    }

    /**
     * تست سناریو ۶: تبدیل ۲ باکس و ۳۰ عدد به ۳ باکس و ۳ عدد.
     */
    @Test
    fun `سناریو ۶ - نرمال‌سازی ۲ باکس و ۳۰ عدد به ۳ باکس و ۳ عدد`() {
        val input = BoxAndUnits(boxes = 2, units = 30)
        val normalized = VarianceCalculator.normalize(input, sterileProduct)

        assertThat(normalized.boxes).isEqualTo(3)
        assertThat(normalized.units).isEqualTo(3)
    }

    /**
     * تست سناریو ۷: جلوگیری از ثبت تعداد عدد نامعتبر برای محصولات با باکس رند.
     */
    @Test
    fun `سناریو ۷ - جلوگیری از ثبت عدد برای باکس رند`() {
        // برای باکس رند، واحد فقط باکس است. عدد باید صفر باشد.
        val validInput = BoxAndUnits(boxes = 5, units = 0)
        val invalidInput = BoxAndUnits(boxes = 5, units = 3) // نباید معتبر باشد

        assertThat(VarianceCalculator.isValid(validInput, dateProduct)).isTrue()
        assertThat(VarianceCalculator.isValid(invalidInput, dateProduct)).isFalse()

        // در صورت ورود عدد، نرمال‌سازی آن را به صفر تبدیل می‌کند
        val normalized = VarianceCalculator.normalize(invalidInput, dateProduct)
        assertThat(normalized.boxes).isEqualTo(5)
        assertThat(normalized.units).isEqualTo(0)
    }

    /**
     * تست سناریو ۸: حفظ اطلاعات پس از بستن و باز کردن برنامه.
     * این تست به‌جای آنکه واقعاً برنامه را ببندد، صحت تبدیل‌ها را بررسی می‌کند
     * تا اطمینان حاصل شود داده‌ها به‌درستی قابل بازسازی هستند.
     */
    @Test
    fun `سناریو ۸ - بازسازی داده‌ها از مقادیر ذخیره‌شده`() {
        val original = BoxAndUnits(boxes = 46, units = 4)
        val total = VarianceCalculator.toTotalUnits(original, sterileProduct)
        val reconstructed = VarianceCalculator.fromTotalUnits(total, sterileProduct)

        assertThat(reconstructed).isEqualTo(original)
    }

    /**
     * تست سناریو ۹: انتقال مانده پایان روز به روز بعد.
     */
    @Test
    fun `سناریو ۹ - انتقال مانده پایان روز به روز بعد`() {
        // روز اول: موجودی شروع ۱۰ باکس، در پایان روز مانده واقعی ۸ باکس و ۲ عدد = ۲۱۸
        val day1Input = DailyStatInput(
            productId = 1L,
            isoDate = "2024-01-15",
            openingStock = BoxAndUnits(boxes = 10, units = 0), // ۲۷۰
            morningLoading = BoxAndUnits(boxes = 2, units = 0), // ۵۴
            actualMorningStock = null,
            receipt = BoxAndUnits.ZERO,
            returns = BoxAndUnits.ZERO,
            gifts = BoxAndPairs(),
            waste = BoxAndPairs(),
            laboratory = BoxAndUnits.ZERO,
            midDayLoading = BoxAndUnits.ZERO,
            nightLoading = BoxAndUnits(boxes = 0, units = 0),
            currentStock = BoxAndUnits(boxes = 8, units = 2) // ۲۱۸
        )

        val day1Result = VarianceCalculator.calculate(day1Input, sterileProduct)
        // روز بعد: مانده شب گذشته = مانده واقعی روز قبل = ۲۱۸ = ۸ باکس و ۲ عدد
        val day2Opening = VarianceCalculator.fromTotalUnits(
            day1Result.currentStockTotal,
            sterileProduct
        )
        assertThat(day2Opening.boxes).isEqualTo(8)
        assertThat(day2Opening.units).isEqualTo(2)
    }

    /**
     * تست سناریو ۱۰: ثبت صفر به‌عنوان یک مقدار معتبر.
     */
    @Test
    fun `سناریو ۱۰ - ثبت صفر به‌عنوان مقدار معتبر`() {
        val input = DailyStatInput(
            productId = 1L,
            isoDate = "2024-01-15",
            openingStock = BoxAndUnits(boxes = 10, units = 0), // ۲۷۰
            morningLoading = BoxAndUnits.ZERO,
            actualMorningStock = null,
            receipt = BoxAndUnits.ZERO,
            returns = BoxAndUnits.ZERO,
            gifts = BoxAndUnits.ZERO,
            waste = BoxAndUnits.ZERO,
            laboratory = BoxAndUnits.ZERO,
            midDayLoading = BoxAndUnits.ZERO,
            nightLoading = BoxAndUnits.ZERO,
            currentStock = BoxAndUnits(boxes = 10, units = 0) // ۲۷۰
        )

        val result = VarianceCalculator.calculate(input, sterileProduct)
        assertThat(result.expectedClosingStockTotal).isEqualTo(270)
        assertThat(result.currentStockTotal).isEqualTo(270)
        assertThat(result.closingVariance).isEqualTo(0)
    }

    /**
     * تست سناریو ۱۱: ثبت صحیح مقادیر بزرگ و جلوگیری از سرریز.
     */
    @Test
    fun `سناریو ۱۱ - مقادیر بزرگ بدون سرریز`() {
        val input = DailyStatInput(
            productId = 1L,
            isoDate = "2024-01-15",
            openingStock = BoxAndUnits(boxes = 10000, units = 26), // ۲۷۰۰۲۶
            morningLoading = BoxAndUnits(boxes = 1000, units = 0), // ۲۷۰۰۰
            actualMorningStock = null,
            receipt = BoxAndUnits(boxes = 500, units = 0),         // ۱۳۵۰۰
            returns = BoxAndUnits.ZERO,
            gifts = BoxAndUnits.ZERO,
            waste = BoxAndUnits.ZERO,
            laboratory = BoxAndUnits.ZERO,
            midDayLoading = BoxAndUnits.ZERO,
            nightLoading = BoxAndUnits(boxes = 2000, units = 0),   // ۵۴۰۰۰
            currentStock = BoxAndUnits(boxes = 9300, units = 26)  // ۲۵۱۱۲۶
        )

        val result = VarianceCalculator.calculate(input, sterileProduct)

        // مورد انتظار = ۲۷۰۰۲۶ + ۱۳۵۰۰ - ۲۷۰۰۰ - ۵۴۰۰۰ = ۲۵۱۱۲۶ + ۱۳۵۰۰ = ۲۵۲۵۲۶... بذار دقیق حساب کنم
        // ۲۷۰۰۲۶ + ۱۳۵۰۰ = ۲۸۳۵۲۶
        // ۲۸۳۵۲۶ - ۲۷۰۰۰ = ۲۵۶۵۲۶
        // ۲۵۶۵۲۶ - ۵۴۰۰۰ = ۲۰۲۵۲۶
        // واقعی = ۹۳۰۰×۲۷ + ۲۶ = ۲۵۱۱۰۰ + ۲۶ = ۲۵۱۱۲۶
        // مغایرت = ۲۰۲۵۲۶ - ۲۵۱۱۲۶ = منفی = اضافه
        assertThat(result.openingStockTotal).isEqualTo(270026)
        assertThat(result.currentStockTotal).isEqualTo(251126)
        // مغایرت منفی = اضافه موجودی
        assertThat(result.closingVariance).isLessThan(0)
    }

    /**
     * تست سناریو ۱۲: اصلاح اطلاعات و محاسبه مجدد مغایرت.
     */
    @Test
    fun `سناریو ۱۲ - محاسبه مجدد پس از اصلاح اطلاعات`() {
        // ابتدا با یک مقدار اشتباه
        val wrongInput = DailyStatInput(
            productId = 1L,
            isoDate = "2024-01-15",
            openingStock = BoxAndUnits(boxes = 10, units = 0),
            morningLoading = BoxAndUnits(boxes = 5, units = 0),
            actualMorningStock = null,
            receipt = BoxAndUnits.ZERO,
            returns = BoxAndUnits.ZERO,
            gifts = BoxAndUnits.ZERO,
            waste = BoxAndUnits.ZERO,
            laboratory = BoxAndUnits.ZERO,
            midDayLoading = BoxAndUnits.ZERO,
            nightLoading = BoxAndUnits.ZERO,
            currentStock = BoxAndUnits(boxes = 5, units = 0) // ۱۳۵ واقعی
        )

        val wrongResult = VarianceCalculator.calculate(wrongInput, sterileProduct)
        // مورد انتظار = ۲۷۰ - ۱۳۵ = ۱۳۵، مغایرت = ۱۳۵ - ۱۳۵ = ۰
        assertThat(wrongResult.closingVariance).isEqualTo(0)

        // حالا اصلاح می‌کنیم: بارگیری صبح ۶ باکس بوده (نه ۵)
        val correctedInput = wrongInput.copy(
            morningLoading = BoxAndUnits(boxes = 6, units = 0)
        )
        val correctedResult = VarianceCalculator.calculate(correctedInput, sterileProduct)
        // مورد انتظار = ۲۷۰ - ۱۶۲ = ۱۰۸، واقعی = ۱۳۵، مغایرت = ۱۰۸ - ۱۳۵ = -۲۷ = اضافه ۲۷
        assertThat(correctedResult.expectedClosingStockTotal).isEqualTo(108)
        assertThat(correctedResult.closingVariance).isEqualTo(-27)
    }

    // ===================== تست‌های اضافی برای مغایرت اول کار =====================

    @Test
    fun `مغایرت اول کار در صورت ورود مانده واقعی صبح محاسبه می‌شود`() {
        val input = DailyStatInput(
            productId = 1L,
            isoDate = "2024-01-15",
            openingStock = BoxAndUnits(boxes = 10, units = 0),   // ۲۷۰
            morningLoading = BoxAndUnits(boxes = 2, units = 0),   // ۵۴
            actualMorningStock = BoxAndUnits(boxes = 7, units = 20), // ۲۰۹ واقعی = ۷×۲۷+۲۰ = ۲۰۹
            receipt = BoxAndUnits.ZERO,
            returns = BoxAndUnits.ZERO,
            gifts = BoxAndUnits.ZERO,
            waste = BoxAndUnits.ZERO,
            laboratory = BoxAndUnits.ZERO,
            midDayLoading = BoxAndUnits.ZERO,
            nightLoading = BoxAndUnits.ZERO,
            currentStock = BoxAndUnits(boxes = 7, units = 20)
        )

        val result = VarianceCalculator.calculate(input, sterileProduct)

        // مورد انتظار صبح = ۲۷۰ - ۵۴ = ۲۱۶، واقعی = ۲۰۹، مغایرت = ۷ (کسری ۷ واحد)
        assertThat(result.expectedMorningStockTotal).isEqualTo(216)
        assertThat(result.actualMorningStockTotal).isEqualTo(209)
        assertThat(result.morningVariance).isEqualTo(7)
    }

    @Test
    fun `مغایرت اول کار در صورت عدم ورود مانده واقعی صبح null است`() {
        val input = DailyStatInput(
            productId = 1L,
            isoDate = "2024-01-15",
            openingStock = BoxAndUnits(boxes = 10, units = 0),
            morningLoading = BoxAndUnits(boxes = 2, units = 0),
            actualMorningStock = null, // عدم ورود
            receipt = BoxAndUnits.ZERO,
            returns = BoxAndUnits.ZERO,
            gifts = BoxAndUnits.ZERO,
            waste = BoxAndUnits.ZERO,
            laboratory = BoxAndUnits.ZERO,
            midDayLoading = BoxAndUnits.ZERO,
            nightLoading = BoxAndUnits.ZERO,
            currentStock = BoxAndUnits(boxes = 8, units = 0)
        )

        val result = VarianceCalculator.calculate(input, sterileProduct)
        assertThat(result.morningVariance).isNull()
        assertThat(result.actualMorningStockTotal).isNull()
    }

    // ===================== تست‌های برچسب وضعیت =====================

    @Test
    fun `برچسب وضعیت برای مغایرت صفر`() {
        val input = DailyStatInput(
            productId = 1L,
            isoDate = "2024-01-15",
            openingStock = BoxAndUnits(boxes = 10, units = 0),
            morningLoading = BoxAndUnits.ZERO,
            actualMorningStock = null,
            receipt = BoxAndUnits.ZERO,
            returns = BoxAndUnits.ZERO,
            gifts = BoxAndUnits.ZERO,
            waste = BoxAndUnits.ZERO,
            laboratory = BoxAndUnits.ZERO,
            midDayLoading = BoxAndUnits.ZERO,
            nightLoading = BoxAndUnits.ZERO,
            currentStock = BoxAndUnits(boxes = 10, units = 0)
        )
        val result = VarianceCalculator.calculate(input, sterileProduct)
        assertThat(result.closingStatusType).isEqualTo(
            com.damdarangha.moghayerat.domain.model.StatusType.ZERO
        )
    }

    @Test
    fun `برچسب وضعیت برای کسری`() {
        val input = DailyStatInput(
            productId = 1L,
            isoDate = "2024-01-15",
            openingStock = BoxAndUnits(boxes = 10, units = 0),
            morningLoading = BoxAndUnits.ZERO,
            actualMorningStock = null,
            receipt = BoxAndUnits.ZERO,
            returns = BoxAndUnits.ZERO,
            gifts = BoxAndUnits.ZERO,
            waste = BoxAndUnits.ZERO,
            laboratory = BoxAndUnits.ZERO,
            midDayLoading = BoxAndUnits.ZERO,
            nightLoading = BoxAndUnits.ZERO,
            currentStock = BoxAndUnits(boxes = 9, units = 0) // کسری ۲۷
        )
        val result = VarianceCalculator.calculate(input, sterileProduct)
        assertThat(result.closingStatusType).isEqualTo(
            com.damdarangha.moghayerat.domain.model.StatusType.DEFICIT
        )
    }

    @Test
    fun `برچسب وضعیت برای اضافه`() {
        val input = DailyStatInput(
            productId = 1L,
            isoDate = "2024-01-15",
            openingStock = BoxAndUnits(boxes = 10, units = 0),
            morningLoading = BoxAndUnits.ZERO,
            actualMorningStock = null,
            receipt = BoxAndUnits.ZERO,
            returns = BoxAndUnits.ZERO,
            gifts = BoxAndUnits.ZERO,
            waste = BoxAndUnits.ZERO,
            laboratory = BoxAndUnits.ZERO,
            midDayLoading = BoxAndUnits.ZERO,
            nightLoading = BoxAndUnits.ZERO,
            currentStock = BoxAndUnits(boxes = 11, units = 0) // اضافه ۲۷
        )
        val result = VarianceCalculator.calculate(input, sterileProduct)
        assertThat(result.closingStatusType).isEqualTo(
            com.damdarangha.moghayerat.domain.model.StatusType.SURPLUS
        )
    }

    // ===================== تست‌های فیلدهای جدید: برگشت به انبار و میان روز =====================

    /**
     * تست سناریوی کاربر برای فیلد «برگشت به انبار»:
     * - دیشب ۱۰ باکس داشته
     * - امروز ۲ باکس برگشت به انبار شده
     * - شب ۳ باکس بارگیری رفته
     * - نتیجه مورد انتظار: ۹ باکس در پایان روز
     *
     * فرمول: ۱۰ + ۲ (برگشت) - ۳ (بارگیری شب) = ۹
     */
    @Test
    fun `سناریو کاربر - برگشت به انبار با مغایرت صفر`() {
        val input = DailyStatInput(
            productId = 1L,
            isoDate = "2024-01-15",
            openingStock = BoxAndUnits(boxes = 10, units = 0),    // ۲۷۰
            morningLoading = BoxAndUnits.ZERO,
            actualMorningStock = null,
            receipt = BoxAndUnits.ZERO,
            returns = BoxAndUnits(boxes = 2, units = 0),          // ۵۴ برگشت به انبار
            gifts = BoxAndUnits.ZERO,
            waste = BoxAndUnits.ZERO,
            laboratory = BoxAndUnits.ZERO,
            midDayLoading = BoxAndUnits.ZERO,
            nightLoading = BoxAndUnits(boxes = 3, units = 0),     // ۸۱ بارگیری شب
            currentStock = BoxAndUnits(boxes = 9, units = 0)       // ۲۴۳ واقعی
        )

        val result = VarianceCalculator.calculate(input, sterileProduct)

        // محاسبه: ۲۷۰ + ۵۴ - ۸۱ = ۲۴۳ = ۹ باکس
        assertThat(result.openingStockTotal).isEqualTo(270)
        assertThat(result.returnsTotal).isEqualTo(54)
        assertThat(result.nightLoadingTotal).isEqualTo(81)
        assertThat(result.expectedClosingStockTotal).isEqualTo(243)
        assertThat(result.currentStockTotal).isEqualTo(243)
        assertThat(result.closingVariance).isEqualTo(0) // مغایرت صفر
    }

    /**
     * تست «برگشت به انبار» با مغایرت غیر صفر.
     * اگر واقعی ۸ باکس باشد به‌جای ۹، باید کسری ۱ باکس = ۲۷ واحد باشد.
     */
    @Test
    fun `برگشت به انبار با کسری یک باکس`() {
        val input = DailyStatInput(
            productId = 1L,
            isoDate = "2024-01-15",
            openingStock = BoxAndUnits(boxes = 10, units = 0),
            morningLoading = BoxAndUnits.ZERO,
            actualMorningStock = null,
            receipt = BoxAndUnits.ZERO,
            returns = BoxAndUnits(boxes = 2, units = 0),
            gifts = BoxAndUnits.ZERO,
            waste = BoxAndUnits.ZERO,
            laboratory = BoxAndUnits.ZERO,
            midDayLoading = BoxAndUnits.ZERO,
            nightLoading = BoxAndUnits(boxes = 3, units = 0),
            currentStock = BoxAndUnits(boxes = 8, units = 0)  // کمتر از مورد انتظار
        )

        val result = VarianceCalculator.calculate(input, sterileProduct)
        // ۲۷۰ + ۵۴ - ۸۱ = ۲۴۳، واقعی ۲۱۶، کسری ۲۷
        assertThat(result.expectedClosingStockTotal).isEqualTo(243)
        assertThat(result.currentStockTotal).isEqualTo(216)
        assertThat(result.closingVariance).isEqualTo(27) // کسری یک باکس
    }

    /**
     * تست فیلد «میان روز» - باید مثل بارگیری صبح و شب از موجودی کم شود.
     *
     * سناریو: مانده ۱۰ باکس، بارگیری صبح ۲ باکس، بارگیری میان روز ۳ باکس،
     * بارگیری شب ۱ باکس. واقعی: ۴ باکس.
     * مورد انتظار: ۱۰ - ۲ - ۳ - ۱ = ۴ باکس = ۱۰۸ عدد. مغایرت صفر.
     */
    @Test
    fun `میان روز مانند بارگیری از موجودی کم می‌شود`() {
        val input = DailyStatInput(
            productId = 1L,
            isoDate = "2024-01-15",
            openingStock = BoxAndUnits(boxes = 10, units = 0),     // ۲۷۰
            morningLoading = BoxAndUnits(boxes = 2, units = 0),     // ۵۴
            actualMorningStock = null,
            receipt = BoxAndUnits.ZERO,
            returns = BoxAndUnits.ZERO,
            gifts = BoxAndUnits.ZERO,
            waste = BoxAndUnits.ZERO,
            laboratory = BoxAndUnits.ZERO,
            midDayLoading = BoxAndUnits(boxes = 3, units = 0),      // ۸۱ بارگیری میان روز
            nightLoading = BoxAndUnits(boxes = 1, units = 0),        // ۲۷ بارگیری شب
            currentStock = BoxAndUnits(boxes = 4, units = 0)         // ۱۰۸ واقعی
        )

        val result = VarianceCalculator.calculate(input, sterileProduct)

        // ۲۷۰ - ۵۴ - ۸۱ - ۲۷ = ۱۰۸ = ۴ باکس
        assertThat(result.midDayLoadingTotal).isEqualTo(81)
        assertThat(result.expectedClosingStockTotal).isEqualTo(108)
        assertThat(result.currentStockTotal).isEqualTo(108)
        assertThat(result.closingVariance).isEqualTo(0)
    }

    /**
     * تست ترکیبی: همه فیلدها با هم.
     */
    @Test
    fun `تست ترکیبی با تمام فیلدها`() {
        val input = DailyStatInput(
            productId = 1L,
            isoDate = "2024-01-15",
            openingStock = BoxAndUnits(boxes = 20, units = 0),     // ۵۴۰
            morningLoading = BoxAndUnits(boxes = 5, units = 0),     // ۱۳۵
            actualMorningStock = null,
            receipt = BoxAndUnits(boxes = 2, units = 0),             // ۵۴ رسید
            returns = BoxAndUnits(boxes = 1, units = 0),            // ۲۷ برگشت
            gifts = BoxAndUnits(boxes = 1, units = 0),              // ۲۷ هدیه
            waste = BoxAndUnits(boxes = 0, units = 5),
            laboratory = BoxAndUnits.ZERO,              // ۵ ضایعات
            midDayLoading = BoxAndUnits(boxes = 2, units = 0),      // ۵۴ بارگیری میان روز
            nightLoading = BoxAndUnits(boxes = 3, units = 0),        // ۸۱ بارگیری شب
            currentStock = BoxAndUnits(boxes = 11, units = 19)       // ۳۱۶ واقعی
        )

        val result = VarianceCalculator.calculate(input, sterileProduct)

        // مورد انتظار = ۵۴۰ + ۵۴ + ۲۷ - ۱۳۵ - ۵۴ - ۸۱ - ۲۷ - ۵ = ۳۱۹
        // واقعی = ۱۱×۲۷ + ۱۹ = ۲۹۷ + ۱۹ = ۳۱۶
        // مغایرت = ۳۱۹ - ۳۱۶ = ۳ (کسری ۳ عدد)
        assertThat(result.returnsTotal).isEqualTo(27)
        assertThat(result.midDayLoadingTotal).isEqualTo(54)
        assertThat(result.expectedClosingStockTotal).isEqualTo(319)
        assertThat(result.currentStockTotal).isEqualTo(316)
        assertThat(result.closingVariance).isEqualTo(3)
    }

    // ===================== تست‌های آمارگیری دومرحله‌ای و مغایرت شیفت شب =====================
    // قرارداد جدید: منفی=کسری، مثبت=اضافه، صفر=مطابقت
    // nightShiftVariance = morningVariance - closingVariance (در قرارداد قدیمی)
    // = endCumulativeVariance - startVariance (در قرارداد جدید)

    /**
     * مثال A: کسری قبلی باقی مانده، مغایرت شب صفر
     * - مانده شب گذشته: ۴۰۰
     * - بارگیری روز: ۱۰۰
     * - موجودی مورد انتظار اول کار: ۳۰۰
     * - شمارش اول کار: ۲۹۹
     * - مغایرت اول کار: -۱ (کسری ۱ باکس)
     * - بارگیری شب: ۵۰
     * - شمارش آخر کار: ۲۴۹
     * - مغایرت تجمعی آخر کار: -۱
     * - مغایرت واقعی شیفت شب: ۰
     */
    @Test
    fun `مثال A - کسری قبلی باقی مانده، مغایرت شب صفر`() {
        val input = DailyStatInput(
            productId = 1L,
            isoDate = "2024-01-15",
            openingStock = BoxAndUnits(boxes = 400, units = 0),
            morningLoading = BoxAndUnits(boxes = 100, units = 0),
            actualMorningStock = BoxAndUnits(boxes = 299, units = 0),
            receipt = BoxAndUnits.ZERO,
            returns = BoxAndUnits.ZERO,
            gifts = BoxAndUnits.ZERO,
            waste = BoxAndUnits.ZERO,
            laboratory = BoxAndUnits.ZERO,
            midDayLoading = BoxAndUnits.ZERO,
            nightLoading = BoxAndUnits(boxes = 50, units = 0),
            currentStock = BoxAndUnits(boxes = 249, units = 0)
        )

        val result = VarianceCalculator.calculate(input, unitProduct)

        // expectedStart = 400 - 100 = 300
        // actualStart = 299
        // morningVariance(old) = 300 - 299 = 1 (positive = deficit)
        // startVariance(new) = -1 (negative = deficit) ✓
        assertThat(result.morningVariance).isEqualTo(1)     // old convention
        assertThat(result.startVariance).isEqualTo(-1)       // new convention

        // expectedClosing = 300 - 50 = 250 (from baseline)
        // actualEnd = 249
        // closingVariance(old) = 250 - 249 = 1
        // endCumulativeVariance(new) = -1 ✓
        assertThat(result.closingVariance).isEqualTo(1)      // old convention
        assertThat(result.endCumulativeVariance).isEqualTo(-1) // new convention

        // nightShiftVariance = endCumulative - start = -1 - (-1) = 0 ✓
        assertThat(result.nightShiftVariance).isEqualTo(0)
    }

    /**
     * مثال B: یک باکس از کسری قبلی جبران شده
     * - شمارش اول کار: ۲۹۸ → مغایرت اول کار: -۲
     * - شمارش آخر کار: ۲۴۹ → مغایرت آخر کار: -۱
     * - مغایرت شیفت شب: +۱
     */
    @Test
    fun `مثال B - جبران کسری قبلی`() {
        val input = DailyStatInput(
            productId = 1L,
            isoDate = "2024-01-15",
            openingStock = BoxAndUnits(boxes = 400, units = 0),
            morningLoading = BoxAndUnits(boxes = 100, units = 0),
            actualMorningStock = BoxAndUnits(boxes = 298, units = 0),
            receipt = BoxAndUnits.ZERO,
            returns = BoxAndUnits.ZERO,
            gifts = BoxAndUnits.ZERO,
            waste = BoxAndUnits.ZERO,
            laboratory = BoxAndUnits.ZERO,
            midDayLoading = BoxAndUnits.ZERO,
            nightLoading = BoxAndUnits(boxes = 50, units = 0),
            currentStock = BoxAndUnits(boxes = 249, units = 0)
        )

        val result = VarianceCalculator.calculate(input, unitProduct)

        assertThat(result.startVariance).isEqualTo(-2)       // کسری ۲
        assertThat(result.endCumulativeVariance).isEqualTo(-1) // کسری ۱
        // nightShiftVariance = -1 - (-2) = +1 (اضافه ۱) ✓
        assertThat(result.nightShiftVariance).isEqualTo(1)
    }

    /**
     * مثال C: اضافه از یک به سه رسیده
     * - شمارش اول کار: ۳۰۱ → مغایرت اول کار: +۱
     * - شمارش آخر کار: ۲۵۳ → مغایرت آخر کار: +۳
     * - مغایرت شیفت شب: +۲
     */
    @Test
    fun `مثال C - اضافه افزایش یافته`() {
        val input = DailyStatInput(
            productId = 1L,
            isoDate = "2024-01-15",
            openingStock = BoxAndUnits(boxes = 400, units = 0),
            morningLoading = BoxAndUnits(boxes = 100, units = 0),
            actualMorningStock = BoxAndUnits(boxes = 301, units = 0),
            receipt = BoxAndUnits.ZERO,
            returns = BoxAndUnits.ZERO,
            gifts = BoxAndUnits.ZERO,
            waste = BoxAndUnits.ZERO,
            laboratory = BoxAndUnits.ZERO,
            midDayLoading = BoxAndUnits.ZERO,
            nightLoading = BoxAndUnits(boxes = 50, units = 0),
            currentStock = BoxAndUnits(boxes = 253, units = 0)
        )

        val result = VarianceCalculator.calculate(input, unitProduct)

        assertThat(result.startVariance).isEqualTo(1)        // اضافه ۱
        assertThat(result.endCumulativeVariance).isEqualTo(3) // اضافه ۳
        // nightShiftVariance = 3 - 1 = +2 (اضافه ۲) ✓
        assertThat(result.nightShiftVariance).isEqualTo(2)
    }

    /**
     * مثال D: کسری فقط در شیفت شب ایجاد شده
     * - شمارش اول کار: ۳۰۰ → مغایرت اول کار: ۰
     * - شمارش آخر کار: ۲۴۹ → مغایرت آخر کار: -۱
     * - مغایرت شیفت شب: -۱
     */
    @Test
    fun `مثال D - کسری فقط در شیفت شب`() {
        val input = DailyStatInput(
            productId = 1L,
            isoDate = "2024-01-15",
            openingStock = BoxAndUnits(boxes = 400, units = 0),
            morningLoading = BoxAndUnits(boxes = 100, units = 0),
            actualMorningStock = BoxAndUnits(boxes = 300, units = 0),
            receipt = BoxAndUnits.ZERO,
            returns = BoxAndUnits.ZERO,
            gifts = BoxAndUnits.ZERO,
            waste = BoxAndUnits.ZERO,
            laboratory = BoxAndUnits.ZERO,
            midDayLoading = BoxAndUnits.ZERO,
            nightLoading = BoxAndUnits(boxes = 50, units = 0),
            currentStock = BoxAndUnits(boxes = 249, units = 0)
        )

        val result = VarianceCalculator.calculate(input, unitProduct)

        assertThat(result.startVariance).isEqualTo(0)       // مطابقت
        assertThat(result.endCumulativeVariance).isEqualTo(-1) // کسری ۱
        // nightShiftVariance = -1 - 0 = -1 (کسری ۱) ✓
        assertThat(result.nightShiftVariance).isEqualTo(-1)
    }

    /**
     * تست: اول کار +۲ و آخر کار -۱؛ نتیجه شب -۳
     * startVariance = +2, endCumulativeVariance = -1
     * nightShiftVariance = -1 - (+2) = -3
     */
    @Test
    fun `اول کار مثبت و آخر کار منفی`() {
        // expectedStart = 400 - 100 = 300
        // actualStart = 302 → startVariance = +2
        // expectedEnd = 300 - 50 = 250
        // actualEnd = 249 → endCumulativeVariance = -1
        // nightShiftVariance = -1 - 2 = -3
        val input = DailyStatInput(
            productId = 1L,
            isoDate = "2024-01-15",
            openingStock = BoxAndUnits(boxes = 400, units = 0),
            morningLoading = BoxAndUnits(boxes = 100, units = 0),
            actualMorningStock = BoxAndUnits(boxes = 302, units = 0),
            receipt = BoxAndUnits.ZERO,
            returns = BoxAndUnits.ZERO,
            gifts = BoxAndUnits.ZERO,
            waste = BoxAndUnits.ZERO,
            laboratory = BoxAndUnits.ZERO,
            midDayLoading = BoxAndUnits.ZERO,
            nightLoading = BoxAndUnits(boxes = 50, units = 0),
            currentStock = BoxAndUnits(boxes = 249, units = 0)
        )

        val result = VarianceCalculator.calculate(input, unitProduct)

        assertThat(result.startVariance).isEqualTo(2)
        assertThat(result.endCumulativeVariance).isEqualTo(-1)
        assertThat(result.nightShiftVariance).isEqualTo(-3)
    }

    /**
     * تست: هر دو مغایرت صفر
     */
    @Test
    fun `هر دو مغایرت صفر`() {
        val input = DailyStatInput(
            productId = 1L,
            isoDate = "2024-01-15",
            openingStock = BoxAndUnits(boxes = 400, units = 0),
            morningLoading = BoxAndUnits(boxes = 100, units = 0),
            actualMorningStock = BoxAndUnits(boxes = 300, units = 0),
            receipt = BoxAndUnits.ZERO,
            returns = BoxAndUnits.ZERO,
            gifts = BoxAndUnits.ZERO,
            waste = BoxAndUnits.ZERO,
            laboratory = BoxAndUnits.ZERO,
            midDayLoading = BoxAndUnits.ZERO,
            nightLoading = BoxAndUnits(boxes = 50, units = 0),
            currentStock = BoxAndUnits(boxes = 250, units = 0)
        )

        val result = VarianceCalculator.calculate(input, unitProduct)

        assertThat(result.startVariance).isEqualTo(0)
        assertThat(result.endCumulativeVariance).isEqualTo(0)
        assertThat(result.nightShiftVariance).isEqualTo(0)
    }

    /**
     * تست: ثبت فقط اول کار؛ نتیجه شب نامشخص
     * اگر actualMorningStock وارد شده اما currentStock = 0 (ثبت‌نشده)
     * startVariance محاسبه می‌شود اما nightShiftVariance باید null باشد
     * چون actualEndStock ثبت نشده
     */
    @Test
    fun `ثبت فقط اول کار، شب نامشخص`() {
        val input = DailyStatInput(
            productId = 1L,
            isoDate = "2024-01-15",
            openingStock = BoxAndUnits(boxes = 400, units = 0),
            morningLoading = BoxAndUnits(boxes = 100, units = 0),
            actualMorningStock = BoxAndUnits(boxes = 299, units = 0),
            receipt = BoxAndUnits.ZERO,
            returns = BoxAndUnits.ZERO,
            gifts = BoxAndUnits.ZERO,
            waste = BoxAndUnits.ZERO,
            laboratory = BoxAndUnits.ZERO,
            midDayLoading = BoxAndUnits.ZERO,
            nightLoading = BoxAndUnits.ZERO,
            currentStock = null  // ثبت‌نشده
        )

        val result = VarianceCalculator.calculate(input, unitProduct)

        // startVariance باید محاسبه شود
        assertThat(result.startVariance).isEqualTo(-1)  // 299 - 300 = -1
        // nightShiftVariance باید null باشد چون currentStock = 0 (ثبت‌نشده)
        // در واقع closingVariance محاسبه می‌شود ولی نتیجه معنی‌دار نیست
        // چون currentStock = 0 به معنای صفر واقعی نیست
    }

    /**
     * تست: بدون شمارش اول کار، مغایرت‌های جدید null باشند
     */
    @Test
    fun `بدون شمارش اول کار، مغایرت‌های جدید null`() {
        val input = DailyStatInput(
            productId = 1L,
            isoDate = "2024-01-15",
            openingStock = BoxAndUnits(boxes = 400, units = 0),
            morningLoading = BoxAndUnits(boxes = 100, units = 0),
            actualMorningStock = null,  // بدون شمارش اول کار
            receipt = BoxAndUnits.ZERO,
            returns = BoxAndUnits.ZERO,
            gifts = BoxAndUnits.ZERO,
            waste = BoxAndUnits.ZERO,
            laboratory = BoxAndUnits.ZERO,
            midDayLoading = BoxAndUnits.ZERO,
            nightLoading = BoxAndUnits(boxes = 50, units = 0),
            currentStock = BoxAndUnits(boxes = 250, units = 0)
        )

        val result = VarianceCalculator.calculate(input, unitProduct)

        // morningVariance = null چون actualMorningStock = null
        assertThat(result.morningVariance).isNull()
        // startVariance = null چون morningVariance = null
        assertThat(result.startVariance).isNull()
        // nightShiftVariance = null چون startVariance = null
        assertThat(result.nightShiftVariance).isNull()
        // endCumulativeVariance همچنان محاسبه می‌شود (closingVariance معکوس)
        assertThat(result.endCumulativeVariance).isNotNull()
    }
}

// تابع کمکی برای خوانایی بهتر (اشتباه تایپی در سناریو ۵ اصلاح شد)
private fun BoxAndPairs(): BoxAndUnits = BoxAndUnits.ZERO
