package com.damdarangha.moghayerat.ui.screen.reports

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.content.FileProvider
import com.damdarangha.moghayerat.data.db.entity.DailyStatEntity
import com.damdarangha.moghayerat.domain.model.Product
import com.damdarangha.moghayerat.util.JalaliDateConverter
import com.itextpdf.kernel.colors.ColorConstants
import com.itextpdf.kernel.colors.DeviceRgb
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
import java.io.FileWriter

/**
 * تولید گزارش PDF شبیه فرم کاغذی آمارگیری.
 *
 * ویژگی‌ها:
 * - صفحه افقی A4 برای جا دادن همه ستون‌ها
 * - جدول با خطوط منظم
 * - سربرگ با اطلاعات جلسه
 * - فوتر با فضای امضا و توضیحات
 * - تکرار عنوان ستون‌ها در صفحات بعدی
 * - پشتیبانی از چند صفحه
 */
object ReportExporter {

    // رنگ‌های رسمی
    private val HEADER_BG = DeviceRgb(220, 230, 250)   // آبی بسیار روشن
    private val ROW_ALT_BG = DeviceRgb(248, 250, 252)   // خاکستری بسیار روشن
    private val BORDER_COLOR = DeviceRgb(150, 160, 180) // خاکستری آبی
    private val TEXT_DARK = DeviceRgb(20, 30, 50)
    private val TEXT_MUTED = DeviceRgb(100, 110, 130)
    private val TITLE_COLOR = DeviceRgb(18, 61, 168)    // آبی برند

    /**
     * ساخت فایل PDF گزارش آمارگیری.
     *
     * @param context Context
     * @param workflowName نام گردش کار
     * @param stats فهرست آمار روزانه
     * @param products فهرست محصولات
     * @param isoDate تاریخ آمارگیری
     * @return فایل PDF ایجاد شده
     */
    fun exportWorkflowReportToPdf(
        context: Context,
        workflowName: String,
        stats: List<DailyStatEntity>,
        products: List<Product>,
        isoDate: String
    ): File {
        val reportDir = File(context.cacheDir, "shared").apply { if (!exists()) mkdirs() }
        val jalaliDate = JalaliDateConverter.gregorianToJalaliString(isoDate)
        val safeName = workflowName.replace(" ", "_").replace("/", "-")
            .replace("\\", "-").replace(":", "-")
        val fileName = "گزارش_${safeName}_$jalaliDate.pdf"
        val pdfFile = File(reportDir, fileName)

        val writer = PdfWriter(pdfFile)
        val pdfDoc = PdfDocument(writer)
        pdfDoc.defaultPageSize = PageSize.A4.rotate()
        val doc = Document(pdfDoc, pdfDoc.defaultPageSize)
        doc.setMargins(28f, 28f, 40f, 28f)

        // ===================== سربرگ =====================
        val titlePara = Paragraph("گزارش آمارگیری و مغایرت‌گیری دامداران")
            .setBold()
            .setFontSize(16f)
            .setFontColor(TITLE_COLOR)
            .setTextAlignment(TextAlignment.CENTER)
            .setMarginBottom(4f)
        doc.add(titlePara)

        val subInfoPara = Paragraph("گردش کار: $workflowName    |    تاریخ: $jalaliDate")
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
            .setFontSize(10f)
            .setFontColor(TEXT_MUTED)
            .setTextAlignment(TextAlignment.CENTER)
            .setMarginBottom(10f)
        doc.add(summaryPara)

        // خط جداکننده
        doc.add(LineSeparator(SolidLine()).setMarginBottom(10f))

        // اگر هیچ محصولی وجود ندارد، پیام مناسب نمایش بده
        if (products.isEmpty()) {
            val emptyPara = Paragraph("هیچ محصولی در این گردش کار ثبت نشده است.")
                .setFontSize(12f)
                .setFontColor(TEXT_MUTED)
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginTop(40f)
            doc.add(emptyPara)
            doc.close()
            return pdfFile
        }

        // ===================== جدول =====================
        val numColumns = 13
        val columnWidths = floatArrayOf(
            30f,   // ۱. ردیف
            130f,  // ۲. نام محصول
            80f,   // ۳. مانده شب گذشته
            80f,   // ۴. بارگیری صبح
            90f,   // ۵. مانده واقعی صبح
            60f,   // ۶. مغایرت اول کار
            60f,   // ۷. رسید
            70f,   // ۸. بارگیری شب
            50f,   // ۹. هدایا
            50f,   // ۱۰. ضایعات
            80f,   // ۱۱. مانده واقعی پایان
            80f,   // ۱۲. مانده مورد انتظار
            60f    // ۱۳. مغایرت آخر کار
        )

        val table = Table(UnitValue.createPercentArray(columnWidths))
        table.setWidth(UnitValue.createPercentValue(100f))
        table.setHorizontalAlignment(HorizontalAlignment.CENTER)
        table.setFontSize(8f)

        // هدر جدول
        val headers = listOf(
            "ردیف",
            "نام محصول",
            "مانده شب گذشته",
            "بارگیری صبح",
            "مانده واقعی صبح",
            "مغایرت اول کار",
            "رسید",
            "بارگیری شب",
            "هدایا",
            "ضایعات",
            "مانده واقعی پایان",
            "مانده مورد انتظار",
            "مغایرت آخر کار"
        )
        headers.forEach { header ->
            table.addHeaderCell(
                Cell()
                    .add(
                        Paragraph(header)
                            .setBold()
                            .setFontColor(TEXT_DARK)
                            .setTextAlignment(TextAlignment.CENTER)
                            .setFontSize(8f)
                    )
                    .setBackgroundColor(HEADER_BG)
                    .setBorder(com.itextpdf.layout.borders.SolidBorder(BORDER_COLOR, 0.5f))
                    .setTextAlignment(TextAlignment.CENTER)
                    .setVerticalAlignment(VerticalAlignment.MIDDLE)
                    .setPadding(4f)
            )
        }

        // ردیف‌های داده - بر اساس ترتیب products (فهرست گردش کار)
        var rowIndex = 1
        products.forEach { product ->
            val stat = stats.find { it.productId == product.id }

            // اگر آمار ثبت نشده، ردیف خالی با عنوان «ثبت‌نشده»
            val cells = if (stat == null) {
                listOf(
                    rowIndex.toString(),
                    product.name,
                    "—", "—", "—", "—", "—", "—", "—", "—",
                    "—", "—",
                    "ثبت‌نشده"
                )
            } else {
                val morningVar = stat.morningVariance?.let { if (it == 0) "صفر" else it.toString() } ?: "—"
                val closingVar = when {
                    stat.closingVariance == 0 -> "صفر"
                    stat.closingVariance > 0 -> "کسری ${stat.closingVariance}"
                    else -> "اضافه ${-stat.closingVariance}"
                }
                listOf(
                    rowIndex.toString(),
                    product.name,
                    formatBoxUnit(stat.openingStockTotal, product),
                    formatBoxUnit(stat.morningLoadingTotal, product),
                    stat.actualMorningStockTotal?.let { formatBoxUnit(it, product) } ?: "—",
                    morningVar,
                    formatBoxUnit(stat.receiptTotal, product),
                    formatBoxUnit(stat.nightLoadingTotal, product),
                    formatBoxUnit(stat.giftsTotal, product),
                    formatBoxUnit(stat.wasteTotal, product),
                    formatBoxUnit(stat.currentStockTotal, product),
                    formatBoxUnit(stat.expectedClosingStockTotal, product),
                    closingVar
                )
            }

            cells.forEachIndexed { idx, value ->
                val cell = Cell()
                    .add(
                        Paragraph(value)
                            .setTextAlignment(TextAlignment.CENTER)
                            .setFontSize(8f)
                            .setFontColor(
                                if (stat == null && idx == 12) TEXT_MUTED
                                else TEXT_DARK
                            )
                    )
                    .setBorder(com.itextpdf.layout.borders.SolidBorder(BORDER_COLOR, 0.5f))
                    .setTextAlignment(TextAlignment.CENTER)
                    .setVerticalAlignment(VerticalAlignment.MIDDLE)
                    .setPadding(3f)

                // رنگ ردیف متناوب
                if (rowIndex % 2 == 0) {
                    cell.setBackgroundColor(ROW_ALT_BG)
                }
                // رنگ خاص برای ستون مغایرت آخر کار
                if (idx == 12 && stat != null && stat.closingVariance != 0) {
                    val varianceBg = if (stat.closingVariance > 0)
                        DeviceRgb(255, 235, 235)  // کسری - قرمز روشن
                    else DeviceRgb(235, 255, 235)  // اضافه - سبز روشن
                    cell.setBackgroundColor(varianceBg)
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
        signTable.setFontSize(10f)

        signTable.addCell(
            Cell()
                .add(Paragraph("نام و امضای آمارگیر:").setFontColor(TEXT_DARK).setFontSize(10f))
                .add(Paragraph("\n\n....................................").setFontColor(TEXT_MUTED))
                .setBorder(com.itextpdf.layout.borders.SolidBorder(BORDER_COLOR, 0.5f))
                .setPadding(8f)
        )
        signTable.addCell(
            Cell()
                .add(Paragraph("توضیحات:").setFontColor(TEXT_DARK).setFontSize(10f))
                .add(Paragraph("\n\n....................................").setFontColor(TEXT_MUTED))
                .setBorder(com.itextpdf.layout.borders.SolidBorder(BORDER_COLOR, 0.5f))
                .setPadding(8f)
        )
        signTable.addCell(
            Cell()
                .add(Paragraph("تأیید مسئول:").setFontColor(TEXT_DARK).setFontSize(10f))
                .add(Paragraph("\n\n....................................").setFontColor(TEXT_MUTED))
                .setBorder(com.itextpdf.layout.borders.SolidBorder(BORDER_COLOR, 0.5f))
                .setPadding(8f)
        )
        doc.add(signTable)

        // شماره صفحه
        val pageCount = pdfDoc.numberOfPages
        val pageNumberPara = Paragraph("صفحه ۱ از $pageCount    |    تولید شده توسط برنامه مغایرت‌گیر دامداران")
            .setFontSize(8f)
            .setFontColor(TEXT_MUTED)
            .setTextAlignment(TextAlignment.CENTER)
            .setMarginTop(10f)
        doc.add(pageNumberPara)

        doc.close()
        return pdfFile
    }

    /**
     * قالب‌بندی مقدار به‌صورت باکس و عدد.
     * برای محصولات رند فقط تعداد باکس.
     */
    private fun formatBoxUnit(total: Int, product: Product): String {
        if (total == 0) return "۰"
        return if (product.isRoundBox) {
            "${total} باکس"
        } else {
            val capacity = if (product.boxCapacity <= 0) 1 else product.boxCapacity
            val boxes = total / capacity
            val units = total % capacity
            if (units == 0) "${boxes} باکس"
            else "${boxes} ب / ${units} ع"
        }
    }

    /**
     * اشتراک‌گذاری فایل با Intent استاندارد.
     */
    fun shareFile(context: Context, file: File, mimeType: String = "*/*") {
        val uri: Uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )
        val shareIntent = Intent().apply {
            action = Intent.ACTION_SEND
            type = mimeType
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.startActivity(Intent.createChooser(shareIntent, "اشتراک‌گذاری فایل"))
    }
}
