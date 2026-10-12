package com.damdarangha.moghayerat.ui.screen.reports

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.content.FileProvider
import com.damdarangha.moghayerat.data.db.entity.DailyStatEntity
import com.damdarangha.moghayerat.domain.model.Product
import com.damdarangha.moghayerat.util.JalaliDateConverter
import com.itextpdf.io.font.PdfEncodings
import com.itextpdf.kernel.colors.DeviceRgb
import com.itextpdf.kernel.font.PdfFont
import com.itextpdf.kernel.font.PdfFontFactory
import com.itextpdf.kernel.font.PdfFontFactory.EmbeddingStrategy
import com.itextpdf.kernel.events.Event
import com.itextpdf.kernel.events.IEventHandler
import com.itextpdf.kernel.events.PdfDocumentEvent
import com.itextpdf.kernel.geom.PageSize
import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.kernel.pdf.PdfWriter
import com.itextpdf.kernel.pdf.canvas.draw.SolidLine
import com.itextpdf.layout.Document
import com.itextpdf.layout.element.Cell
import com.itextpdf.layout.element.LineSeparator
import com.itextpdf.layout.element.Paragraph
import com.itextpdf.layout.element.Table
import com.itextpdf.layout.properties.HorizontalAlignment
import com.itextpdf.layout.properties.TextAlignment
import com.itextpdf.layout.properties.UnitValue
import com.itextpdf.layout.properties.VerticalAlignment
import java.io.File
import java.io.IOException

/**
 * تولید گزارش PDF شبیه فرم کاغذی آمارگیری.
 *
 * ویژگی‌ها:
 * - صفحه افقی A4 برای جا دادن همه ستون‌ها
 * - فونت Vazirmatn فارسی embed شده
 * - جدول با خطوط منظم و سربرگ فشرده
 * - صفحه‌بندی واقعی با شماره صفحه
 * - تکرار عنوان ستون‌ها در صفحات بعدی
 * - پشتیبانی از چند صفحه
 * - ۱۳ ستون مطابق قرارداد
 */
object ReportExporter {

    // رنگ‌های رسمی
    private val HEADER_BG = DeviceRgb(220, 230, 250)
    private val ROW_ALT_BG = DeviceRgb(248, 250, 252)
    private val BORDER_COLOR = DeviceRgb(150, 160, 180)
    private val TEXT_DARK = DeviceRgb(20, 30, 50)
    private val TEXT_MUTED = DeviceRgb(100, 110, 130)
    private val TITLE_COLOR = DeviceRgb(18, 61, 168)
    private val DEFICIT_BG = DeviceRgb(255, 235, 235)
    private val SURPLUS_BG = DeviceRgb(235, 255, 235)

    // ستون‌های جدول مطابق قرارداد - ترتیب از راست به چپ
    // (در iText، اولین ستون در سمت چپ قرار می‌گیرد، پس ترتیب را برعکس می‌کنیم)
    private val COLUMN_HEADERS = listOf(
        "مغایرت\nآخر کار",
        "مانده مورد\nانتظار پایان",
        "مانده واقعی\nپایان روز",
        "ضایعات",
        "هدایا",
        "بارگیری\nشب",
        "رسید",
        "برگشت\nبه انبار",
        "میان‌روز",
        "بارگیری\nصبح",
        "مانده\nشب گذشته",
        "نام محصول",
        "ردیف"
    )

    private val COLUMN_WIDTHS = floatArrayOf(
        55f,   // مغایرت آخر کار
        70f,   // مانده مورد انتظار پایان
        70f,   // مانده واقعی پایان روز
        45f,   // ضایعات
        45f,   // هدایا
        55f,   // بارگیری شب
        50f,   // رسید
        55f,   // برگشت به انبار
        50f,   // میان‌روز
        55f,   // بارگیری صبح
        70f,   // مانده شب گذشته
        130f,  // نام محصول
        30f    // ردیف
    )

    /**
     * ساخت فایل PDF گزارش آمارگیری.
     */
    fun exportWorkflowReportToPdf(
        context: Context,
        workflowName: String,
        stats: List<DailyStatEntity>,
        products: List<Product>,
        isoDate: String
    ): File {
        // ایجاد پوشه مقصد به‌صورت امن
        val reportDir = File(context.cacheDir, "shared")
        if (!reportDir.exists()) {
            val created = reportDir.mkdirs()
            if (!created && !reportDir.exists()) {
                throw IOException("امکان ایجاد پوشه گزارش وجود ندارد: ${reportDir.absolutePath}")
            }
        }

        // تاریخ شمسی با فرمت امن برای نام فایل
        val jalaliDateSafe = JalaliDateConverter.gregorianToJalaliFileSafeString(isoDate)
        val jalaliDisplay = JalaliDateConverter.gregorianToJalaliString(isoDate)

        // پاک‌سازی نام گردش کار
        val safeName = sanitizeFileName(workflowName)

        // نام فایل نهایی
        val fileName = "گزارش_${safeName}_$jalaliDateSafe.pdf"
        val pdfFile = File(reportDir, fileName)

        // بارگذاری فونت Vazirmatn از assets
        val fontRegular = loadFontFromAssets(context, "vazirmatn_regular.ttf")
        val fontBold = loadFontFromAssets(context, "vazirmatn_bold.ttf")

        val writer = PdfWriter(pdfFile)
        val pdfDoc = PdfDocument(writer)
        pdfDoc.defaultPageSize = PageSize.A4.rotate()
        val doc = Document(pdfDoc, pdfDoc.defaultPageSize)
        doc.setMargins(28f, 28f, 40f, 28f)
        doc.setFont(fontRegular)
        doc.setFontSize(8f)

        // ===================== سربرگ =====================
        val titlePara = Paragraph("گزارش آمارگیری و مغایرت‌گیری دامداران")
            .setFont(fontBold)
            .setFontSize(16f)
            .setFontColor(TITLE_COLOR)
            .setTextAlignment(TextAlignment.CENTER)
            .setMarginBottom(4f)
        doc.add(titlePara)

        val subInfoPara = Paragraph("گردش کار: $workflowName    |    تاریخ: $jalaliDisplay")
            .setFont(fontRegular)
            .setFontSize(11f)
            .setFontColor(TEXT_DARK)
            .setTextAlignment(TextAlignment.CENTER)
            .setMarginBottom(8f)
        doc.add(subInfoPara)

        // آمار خلاصه
        val totalCount = products.size
        val registeredCount = stats.size
        val varianceCount = stats.count { it.closingVariance != 0 }
        val remainingCount = (totalCount - registeredCount).coerceAtLeast(0)

        val summaryPara = Paragraph(
            "کل محصولات: $totalCount    |    " +
            "ثبت‌شده: $registeredCount    |    " +
            "باقی‌مانده: $remainingCount    |    " +
            "دارای مغایرت: $varianceCount"
        )
            .setFont(fontRegular)
            .setFontSize(10f)
            .setFontColor(TEXT_MUTED)
            .setTextAlignment(TextAlignment.CENTER)
            .setMarginBottom(10f)
        doc.add(summaryPara)

        // خط جداکننده
        doc.add(LineSeparator(SolidLine()).setMarginBottom(10f))

        // اگر هیچ محصولی وجود ندارد، پیام مناسب
        if (products.isEmpty()) {
            val emptyPara = Paragraph("هیچ محصولی در این گردش کار ثبت نشده است.")
                .setFont(fontRegular)
                .setFontSize(12f)
                .setFontColor(TEXT_MUTED)
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginTop(40f)
            doc.add(emptyPara)
            doc.close()
            return pdfFile
        }

        // ===================== جدول =====================
        val table = Table(UnitValue.createPercentArray(COLUMN_WIDTHS))
        table.setWidth(UnitValue.createPercentValue(100f))
        table.setHorizontalAlignment(HorizontalAlignment.CENTER)
        table.setFontSize(7.5f)
        table.setFont(fontRegular)

        // هدر جدول (تکرار در هر صفحه)
        COLUMN_HEADERS.forEach { header ->
            table.addHeaderCell(
                Cell()
                    .add(
                        Paragraph(header)
                            .setFont(fontBold)
                            .setFontColor(TEXT_DARK)
                            .setTextAlignment(TextAlignment.CENTER)
                            .setFontSize(7.5f)
                            .setMultipliedLeading(1.1f)
                    )
                    .setBackgroundColor(HEADER_BG)
                    .setBorder(com.itextpdf.layout.borders.SolidBorder(BORDER_COLOR, 0.5f))
                    .setTextAlignment(TextAlignment.CENTER)
                    .setVerticalAlignment(VerticalAlignment.MIDDLE)
                    .setPadding(4f)
            )
        }

        // ردیف‌های داده - بر اساس ترتیب products
        var rowIndex = 1
        products.forEach { product ->
            val stat = stats.find { it.productId == product.id }

            // مقادیر هر ستون (به ترتیب COLUMN_HEADERS از چپ به راست)
            val cells = if (stat == null) {
                // محصول ثبت‌نشده
                listOf(
                    "ثبت‌نشده",  // مغایرت آخر کار
                    "—",         // مانده مورد انتظار
                    "—",         // مانده واقعی پایان
                    "—",         // ضایعات
                    "—",         // هدایا
                    "—",         // بارگیری شب
                    "—",         // رسید
                    "—",         // برگشت به انبار
                    "—",         // میان‌روز
                    "—",         // بارگیری صبح
                    "—",         // مانده شب گذشته
                    product.name,  // نام محصول
                    ReportFormatter.toPersianDigits(rowIndex)  // ردیف
                )
            } else {
                listOf(
                    ReportFormatter.formatVariance(stat.closingVariance),  // مغایرت آخر کار
                    ReportFormatter.formatBoxUnit(stat.expectedClosingStockTotal, product),  // مانده مورد انتظار
                    ReportFormatter.formatBoxUnit(stat.currentStockTotal, product),  // مانده واقعی پایان
                    ReportFormatter.formatBoxUnit(stat.wasteTotal, product),  // ضایعات
                    ReportFormatter.formatBoxUnit(stat.giftsTotal, product),  // هدایا
                    ReportFormatter.formatBoxUnit(stat.nightLoadingTotal, product),  // بارگیری شب
                    ReportFormatter.formatBoxUnit(stat.receiptTotal, product),  // رسید
                    ReportFormatter.formatBoxUnit(stat.returnsTotal, product),  // برگشت به انبار
                    ReportFormatter.formatBoxUnit(stat.midDayLoadingTotal, product),  // میان‌روز
                    ReportFormatter.formatBoxUnit(stat.morningLoadingTotal, product),  // بارگیری صبح
                    ReportFormatter.formatBoxUnit(stat.openingStockTotal, product),  // مانده شب گذشته
                    product.name,  // نام محصول
                    ReportFormatter.toPersianDigits(rowIndex)  // ردیف
                )
            }

            cells.forEachIndexed { idx, value ->
                val cell = Cell()
                    .add(
                        Paragraph(value)
                            .setFont(fontRegular)
                            .setTextAlignment(TextAlignment.CENTER)
                            .setFontSize(7.5f)
                            .setFontColor(
                                if (stat == null && idx == 0) TEXT_MUTED
                                else TEXT_DARK
                            )
                            .setMultipliedLeading(1.1f)
                    )
                    .setBorder(com.itextpdf.layout.borders.SolidBorder(BORDER_COLOR, 0.5f))
                    .setTextAlignment(TextAlignment.CENTER)
                    .setVerticalAlignment(VerticalAlignment.MIDDLE)
                    .setPadding(3f)

                // رنگ ردیف متناوب
                if (rowIndex % 2 == 0) {
                    cell.setBackgroundColor(ROW_ALT_BG)
                }

                // رنگ خاص برای ستون مغایرت آخر کار (idx=0)
                if (idx == 0 && stat != null && stat.closingVariance != 0) {
                    cell.setBackgroundColor(
                        if (stat.closingVariance > 0) DEFICIT_BG
                        else SURPLUS_BG
                    )
                }

                // نام محصول را راست‌چین کن
                if (idx == 11) {
                    cell.setTextAlignment(TextAlignment.RIGHT)
                }

                table.addCell(cell)
            }
            rowIndex++
        }

        doc.add(table)

        // ===================== فوتر =====================
        doc.add(LineSeparator(SolidLine()).setMarginTop(20f).setMarginBottom(20f))

        // فضای امضا
        val signTable = Table(floatArrayOf(33f, 33f, 33f))
        signTable.setWidth(UnitValue.createPercentValue(100f))
        signTable.setFont(fontRegular)
        signTable.setFontSize(10f)

        signTable.addCell(
            Cell()
                .add(Paragraph("نام و امضای آمارگیر:").setFont(fontBold).setFontColor(TEXT_DARK).setFontSize(10f))
                .add(Paragraph("\n\n....................................").setFont(fontRegular).setFontColor(TEXT_MUTED))
                .setBorder(com.itextpdf.layout.borders.SolidBorder(BORDER_COLOR, 0.5f))
                .setPadding(8f)
        )
        signTable.addCell(
            Cell()
                .add(Paragraph("توضیحات:").setFont(fontBold).setFontColor(TEXT_DARK).setFontSize(10f))
                .add(Paragraph("\n\n....................................").setFont(fontRegular).setFontColor(TEXT_MUTED))
                .setBorder(com.itextpdf.layout.borders.SolidBorder(BORDER_COLOR, 0.5f))
                .setPadding(8f)
        )
        signTable.addCell(
            Cell()
                .add(Paragraph("تأیید مسئول:").setFont(fontBold).setFontColor(TEXT_DARK).setFontSize(10f))
                .add(Paragraph("\n\n....................................").setFont(fontRegular).setFontColor(TEXT_MUTED))
                .setBorder(com.itextpdf.layout.borders.SolidBorder(BORDER_COLOR, 0.5f))
                .setPadding(8f)
        )
        doc.add(signTable)

        // ===================== صفحه‌بندی واقعی =====================
        // بستن سند برای قطعی شدن صفحه‌بندی
        doc.close()

        // اضافه‌کردن شماره صفحه به هر صفحه بعد از بستن سند
        val pageCount = pdfDoc.numberOfPages
        for (i in 1..pageCount) {
            val page = pdfDoc.getPage(i)
            val canvas = com.itextpdf.kernel.pdf.canvas.PdfCanvas(page)
            canvas.setFillColor(TEXT_MUTED)
            canvas.beginText()
            canvas.setFontAndSize(fontRegular, 8f)
            val text = "صفحه $i از $pageCount"
            canvas.moveText(page.pageSize.width.toDouble() / 2.0 - 50.0, 20.0)
            canvas.showText(text)
            canvas.endText()
        }
        return pdfFile
    }

    /**
     * بارگذاری فونت از assets.
     * فونت‌های Vazirmatn در res/font هستند ولی برای iText باید از assets خوانده شوند.
     */
    private fun loadFontFromAssets(context: Context, fontFileName: String): PdfFont {
        return try {
            // کپی فونت از res/font به cache برای خواندن
            val fontResId = context.resources.getIdentifier(
                fontFileName.substringBefore("."),
                "font",
                context.packageName
            )
            if (fontResId != 0) {
                val tempFile = File(context.cacheDir, fontFileName)
                if (!tempFile.exists()) {
                    context.resources.openRawResource(fontResId).use { input ->
                        tempFile.outputStream().use { output ->
                            input.copyTo(output)
                        }
                    }
                }
                PdfFontFactory.createFont(
                    tempFile.absolutePath,
                    PdfEncodings.IDENTITY_H,
                    EmbeddingStrategy.PREFER_EMBEDDED
                )
            } else {
                // fallback به فونت پیش‌فرض
                PdfFontFactory.createFont()
            }
        } catch (e: Exception) {
            // در صورت خطا، فونت پیش‌فرض استفاده شود
            PdfFontFactory.createFont()
        }
    }

    /**
     * پاک‌سازی نام فایل از کاراکترهای غیرمجاز.
     */
    private fun sanitizeFileName(name: String): String {
        return name
            .replace(" ", "_")
            .replace("/", "-")
            .replace("\\", "-")
            .replace(":", "-")
            .replace("*", "-")
            .replace("?", "-")
            .replace("\"", "-")
            .replace("<", "-")
            .replace(">", "-")
            .replace("|", "-")
            .trim()
    }

    /**
     * اعتبارسنجی فایل.
     */
    fun validateFile(file: File): Boolean {
        return file.exists() && file.length() > 0 && file.canRead()
    }

    /**
     * ساخت Uri معتبر FileProvider.
     */
    fun getFileUri(context: Context, file: File): Uri {
        return FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )
    }

    /**
     * باز کردن فایل PDF با PDF Viewer.
     */
    fun openPdf(activityContext: Context, file: File) {
        if (!validateFile(file)) {
            throw IOException("فایل PDF معتبر نیست یا وجود ندارد")
        }
        val uri = getFileUri(activityContext, file)
        val viewIntent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(uri, "application/pdf")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            if (activityContext !is android.app.Activity) {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
        }
        activityContext.startActivity(viewIntent)
    }

    /**
     * اشتراک‌گذاری فایل.
     */
    fun shareFile(activityContext: Context, file: File, mimeType: String = "application/pdf") {
        if (!validateFile(file)) {
            throw IOException("فایل PDF معتبر نیست یا وجود ندارد")
        }
        val uri = getFileUri(activityContext, file)
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = mimeType
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            if (activityContext !is android.app.Activity) {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
        }
        activityContext.startActivity(Intent.createChooser(shareIntent, "اشتراک‌گذاری فایل PDF"))
    }

    /**
     * کپی فایل به مقصد دائمی.
     */
    fun copyToDestination(context: Context, sourceFile: File, destinationUri: Uri): Boolean {
        return try {
            if (!validateFile(sourceFile)) {
                return false
            }
            val inputStream = sourceFile.inputStream()
            val outputStream = context.contentResolver.openOutputStream(destinationUri)
            if (outputStream == null) {
                inputStream.close()
                return false
            }
            inputStream.use { input ->
                outputStream.use { output ->
                    input.copyTo(output)
                    output.flush()
                }
            }
            true
        } catch (e: Exception) {
            false
        }
    }
}
