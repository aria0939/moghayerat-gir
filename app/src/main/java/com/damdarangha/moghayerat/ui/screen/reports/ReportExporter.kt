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
import com.itextpdf.kernel.geom.PageSize
import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.kernel.pdf.PdfWriter
import com.itextpdf.kernel.pdf.canvas.draw.SolidLine
import com.itextpdf.layout.Document
import com.itextpdf.layout.element.Cell
import com.itextpdf.layout.element.LineSeparator
import com.itextpdf.layout.element.Paragraph
import com.itextpdf.layout.element.Table
import com.itextpdf.layout.properties.BaseDirection
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
 * - صفحه افقی A4
 * - فونت Vazirmatn فارسی embed شده
 * - RTL کامل با BaseDirection.RIGHT_TO_LEFT
 * - جدول با ۱۳ ستون و ترتیب منطقی RTL
 * - شماره صفحه با Paragraph (نه PdfCanvas)
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

    // ستون‌های جدول - ترتیب منطقی RTL (اولین = راست‌ترین)
    private val COLUMN_HEADERS = listOf(
        "ردیف",
        "نام محصول",
        "مانده\nشب گذشته",
        "بارگیری\nصبح",
        "میان‌روز",
        "برگشت\nبه انبار",
        "رسید",
        "بارگیری\nشب",
        "هدایا",
        "ضایعات",
        "مانده واقعی\nپایان روز",
        "مانده مورد\nانتظار پایان",
        "مغایرت\nآخر کار"
    )

    // عرض ستون‌ها - متناسب با ستون‌های بالا
    private val COLUMN_WIDTHS = floatArrayOf(
        30f,   // ردیف
        130f,  // نام محصول
        70f,   // مانده شب گذشته
        55f,   // بارگیری صبح
        50f,   // میان‌روز
        55f,   // برگشت به انبار
        50f,   // رسید
        55f,   // بارگیری شب
        45f,   // هدایا
        45f,   // ضایعات
        70f,   // مانده واقعی پایان روز
        70f,   // مانده مورد انتظار پایان
        55f    // مغایرت آخر کار
    )

    fun exportWorkflowReportToPdf(
        context: Context,
        workflowName: String,
        stats: List<DailyStatEntity>,
        products: List<Product>,
        isoDate: String
    ): File {
        // ایجاد پوشه مقصد
        val reportDir = File(context.cacheDir, "shared")
        if (!reportDir.exists()) {
            val created = reportDir.mkdirs()
            if (!created && !reportDir.exists()) {
                throw IOException("امکان ایجاد پوشه گزارش وجود ندارد: ${reportDir.absolutePath}")
            }
        }

        val jalaliDateSafe = JalaliDateConverter.gregorianToJalaliFileSafeString(isoDate)
        val jalaliDisplay = JalaliDateConverter.gregorianToJalaliString(isoDate)
        val safeName = sanitizeFileName(workflowName)
        val fileName = "گزارش_${safeName}_$jalaliDateSafe.pdf"
        val pdfFile = File(reportDir, fileName)

        // بارگذاری فونت
        val fontRegular = loadFontFromAssets(context, "vazirmatn_regular.ttf")
        val fontBold = loadFontFromAssets(context, "vazirmatn_bold.ttf")

        val writer = PdfWriter(pdfFile)
        val pdfDoc = PdfDocument(writer)
        pdfDoc.defaultPageSize = PageSize.A4.rotate()
        val doc = Document(pdfDoc, pdfDoc.defaultPageSize)
        doc.setFont(fontRegular)
        doc.setFontSize(8f)
        // ✅ تنظیم جهت پایه سند به RTL
        doc.setBaseDirection(BaseDirection.RIGHT_TO_LEFT)

        // ===================== سربرگ =====================
        val titlePara = Paragraph("گزارش آمارگیری و مغایرت‌گیری دامداران")
            .setFont(fontBold)
            .setFontSize(16f)
            .setFontColor(TITLE_COLOR)
            .setTextAlignment(TextAlignment.CENTER)
            .setBaseDirection(BaseDirection.RIGHT_TO_LEFT)
            .setMarginBottom(4f)
        doc.add(titlePara)

        val subInfoPara = Paragraph("گردش کار: $workflowName    |    تاریخ: $jalaliDisplay")
            .setFont(fontRegular)
            .setFontSize(11f)
            .setFontColor(TEXT_DARK)
            .setTextAlignment(TextAlignment.CENTER)
            .setBaseDirection(BaseDirection.RIGHT_TO_LEFT)
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
            .setBaseDirection(BaseDirection.RIGHT_TO_LEFT)
            .setMarginBottom(10f)
        doc.add(summaryPara)

        doc.add(LineSeparator(SolidLine()).setMarginBottom(10f))

        // اگر هیچ محصولی وجود ندارد
        if (products.isEmpty()) {
            val emptyPara = Paragraph("هیچ محصولی در این گردش کار ثبت نشده است.")
                .setFont(fontRegular)
                .setFontSize(12f)
                .setFontColor(TEXT_MUTED)
                .setTextAlignment(TextAlignment.CENTER)
                .setBaseDirection(BaseDirection.RIGHT_TO_LEFT)
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
        // ✅ تنظیم جهت جدول به RTL
        table.setBaseDirection(BaseDirection.RIGHT_TO_LEFT)

        // هدر جدول
        COLUMN_HEADERS.forEach { header ->
            table.addHeaderCell(
                Cell()
                    .add(
                        Paragraph(header)
                            .setFont(fontBold)
                            .setFontColor(TEXT_DARK)
                            .setTextAlignment(TextAlignment.CENTER)
                            .setBaseDirection(BaseDirection.RIGHT_TO_LEFT)
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

        // ردیف‌های داده
        var rowIndex = 1
        products.forEach { product ->
            val stat = stats.find { it.productId == product.id }

            // مقادیر هر ستون به ترتیب منطقی RTL
            val cells = if (stat == null) {
                listOf(
                    ReportFormatter.toPersianDigits(rowIndex),
                    product.name,
                    "—", "—", "—", "—", "—", "—", "—", "—", "—", "—",
                    "ثبت‌نشده"
                )
            } else {
                listOf(
                    ReportFormatter.toPersianDigits(rowIndex),
                    product.name,
                    ReportFormatter.formatBoxUnit(stat.openingStockTotal, product),
                    ReportFormatter.formatBoxUnit(stat.morningLoadingTotal, product),
                    ReportFormatter.formatBoxUnit(stat.midDayLoadingTotal, product),
                    ReportFormatter.formatBoxUnit(stat.returnsTotal, product),
                    ReportFormatter.formatBoxUnit(stat.receiptTotal, product),
                    ReportFormatter.formatBoxUnit(stat.nightLoadingTotal, product),
                    ReportFormatter.formatBoxUnit(stat.giftsTotal, product),
                    ReportFormatter.formatBoxUnit(stat.wasteTotal, product),
                    ReportFormatter.formatBoxUnit(stat.currentStockTotal, product),
                    ReportFormatter.formatBoxUnit(stat.expectedClosingStockTotal, product),
                    ReportFormatter.formatVariance(stat.closingVariance)
                )
            }

            cells.forEachIndexed { idx, value ->
                val cell = Cell()
                    .add(
                        Paragraph(value)
                            .setFont(fontRegular)
                            .setTextAlignment(
                                if (idx == 1) TextAlignment.RIGHT
                                else TextAlignment.CENTER
                            )
                            .setBaseDirection(BaseDirection.RIGHT_TO_LEFT)
                            .setFontSize(7.5f)
                            .setFontColor(
                                if (stat == null && idx == 12) TEXT_MUTED
                                else TEXT_DARK
                            )
                            .setMultipliedLeading(1.1f)
                    )
                    .setBorder(com.itextpdf.layout.borders.SolidBorder(BORDER_COLOR, 0.5f))
                    .setTextAlignment(
                        if (idx == 1) TextAlignment.RIGHT
                        else TextAlignment.CENTER
                    )
                    .setBaseDirection(BaseDirection.RIGHT_TO_LEFT)
                    .setVerticalAlignment(VerticalAlignment.MIDDLE)
                    .setPadding(3f)

                if (rowIndex % 2 == 0) {
                    cell.setBackgroundColor(ROW_ALT_BG)
                }

                // رنگ ستون مغایرت (آخرین ستون)
                if (idx == 12 && stat != null && stat.closingVariance != 0) {
                    cell.setBackgroundColor(
                        if (stat.closingVariance > 0) DEFICIT_BG
                        else SURPLUS_BG
                    )
                }

                table.addCell(cell)
            }
            rowIndex++
        }

        doc.add(table)

        // ===================== فوتر امضا =====================
        doc.add(LineSeparator(SolidLine()).setMarginTop(20f).setMarginBottom(20f))

        val signTable = Table(floatArrayOf(33f, 33f, 33f))
        signTable.setWidth(UnitValue.createPercentValue(100f))
        signTable.setFont(fontRegular)
        signTable.setFontSize(10f)
        signTable.setBaseDirection(BaseDirection.RIGHT_TO_LEFT)

        signTable.addCell(createSignCell("نام و امضای آمارگیر:", fontBold, fontRegular))
        signTable.addCell(createSignCell("توضیحات:", fontBold, fontRegular))
        signTable.addCell(createSignCell("تأیید مسئول:", fontBold, fontRegular))
        doc.add(signTable)

        // ===================== شماره صفحه =====================
        // استفاده از Paragraph به جای PdfCanvas برای پشتیبانی صحیح از RTL
        // شماره صفحه در زمان END_PAGE توسط event handler اضافه می‌شود
        // اما به جای PdfCanvas از یک روش ساده استفاده می‌کنیم:
        // تعداد صفحات را بعد از close می‌گیریم و در یک بار بازنویسی اضافه می‌کنیم
        doc.close()

        // بعد از close، تعداد صفحات نهایی را می‌گیریم
        // و با یک PdfDocument جدید شماره صفحه را اضافه می‌کنیم
        // در عوض، برای سادگی، شماره صفحه را قبل از close اضافه می‌کنیم
        // با استفاده از یک Paragraph ساده در انتهای هر صفحه

        // روش جایگزین: باز کردن فایل با PdfReader و اضافه‌کردن شماره صفحه
        try {
            val reader = com.itextpdf.kernel.pdf.PdfReader(pdfFile)
            val stamper = com.itextpdf.kernel.pdf.PdfDocument(reader, com.itextpdf.kernel.pdf.PdfWriter(File(reportDir, "temp_$fileName")))
            val pageCount = stamper.numberOfPages
            for (i in 1..pageCount) {
                val page = stamper.getPage(i)
                val canvas = com.itextpdf.kernel.pdf.canvas.PdfCanvas(page)
                canvas.setFillColor(TEXT_MUTED)
                // استفاده از font برای showText - فقط عدد که ASCII است
                canvas.beginText()
                canvas.setFontAndSize(fontRegular, 8f)
                val text = "$i / $pageCount"
                canvas.moveText(page.pageSize.width.toDouble() / 2.0 - 20.0, 20.0)
                canvas.showText(text)
                canvas.endText()
            }
            stamper.close()
            reader.close()
            // جایگزینی فایل اصلی با فایل temp
            val tempFile = File(reportDir, "temp_$fileName")
            pdfFile.delete()
            tempFile.renameTo(pdfFile)
        } catch (e: Exception) {
            // اگر اضافه‌کردن شماره صفحه شکست خورد، فایل اصلی بدون شماره صفحه استفاده می‌شود
        }

        return pdfFile
    }

    /**
     * ساخت سلول امضا.
     */
    private fun createSignCell(label: String, fontBold: PdfFont, fontRegular: PdfFont): Cell {
        return Cell()
            .add(
                Paragraph(label)
                    .setFont(fontBold)
                    .setFontColor(TEXT_DARK)
                    .setFontSize(10f)
                    .setBaseDirection(BaseDirection.RIGHT_TO_LEFT)
            )
            .add(
                Paragraph("\n\n....................................")
                    .setFont(fontRegular)
                    .setFontColor(TEXT_MUTED)
                    .setBaseDirection(BaseDirection.RIGHT_TO_LEFT)
            )
            .setBorder(com.itextpdf.layout.borders.SolidBorder(BORDER_COLOR, 0.5f))
            .setBaseDirection(BaseDirection.RIGHT_TO_LEFT)
            .setPadding(8f)
    }

    /**
     * بارگذاری فونت از assets.
     */
    private fun loadFontFromAssets(context: Context, fontFileName: String): PdfFont {
        return try {
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
                PdfFontFactory.createFont()
            }
        } catch (e: Exception) {
            PdfFontFactory.createFont()
        }
    }

    /**
     * پاک‌سازی نام فایل.
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
     * باز کردن فایل PDF.
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
