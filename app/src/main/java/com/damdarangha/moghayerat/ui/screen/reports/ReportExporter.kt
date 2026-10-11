package com.damdarangha.moghayerat.ui.screen.reports

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.content.FileProvider
import com.damdarangha.moghayerat.data.db.entity.DailyStatEntity
import com.damdarangha.moghayerat.domain.model.Product
import com.damdarangha.moghayerat.util.JalaliDateConverter
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
import java.io.File
import java.io.FileWriter

/**
 * تولید گزارش PDF و CSV.
 *
 * PDF:
 * - راست‌چین
 * - خوانا
 * - دارای عنوان «گزارش مغایرت‌گیری دامداران»
 * - جدول با ستون‌های:
 *   نام محصول، مانده شب گذشته، بارگیری صبح، رسید، بارگیری شب، هدایا، ضایعات،
 *   مانده مورد انتظار، مانده واقعی، مغایرت
 *
 * CSV:
 * - استاندارد CSV با کاما به‌عنوان جداکننده
 * - قابل باز شدن در Excel
 */
object ReportExporter {

    /**
     * ساخت فایل PDF گزارش روزانه.
     *
     * @param context Context اندروید
     * @param stats فهرست آمار روزانه
     * @param products فهرست محصولات (برای نام و مشخصات)
     * @param isoDate تاریخ گزارش
     * @return فایل PDF ایجاد شده
     */
    fun exportDailyReportToPdf(
        context: Context,
        stats: List<DailyStatEntity>,
        products: List<Product>,
        isoDate: String
    ): File {
        val reportDir = File(context.cacheDir, "shared").apply { if (!exists()) mkdirs() }
        val dateSuffix = isoDate.replace("-", "")
        val pdfFile = File(reportDir, "daily_report_$dateSuffix.pdf")

        val writer = PdfWriter(pdfFile)
        val pdfDoc = PdfDocument(writer)
        val doc = Document(pdfDoc)

        // تنظیم راست‌چین
        doc.setRenderer(com.itextpdf.layout.renderer.DocumentRenderer(doc))

        // عنوان گزارش
        val title = Paragraph("گزارش مغایرت‌گیری دامداران")
            .setBold()
            .setFontSize(18f)
            .setTextAlignment(TextAlignment.CENTER)
            .setMarginBottom(8f)
        doc.add(title)

        // تاریخ
        val jalaliDate = JalaliDateConverter.gregorianToJalaliString(isoDate)
        val dateParagraph = Paragraph("تاریخ: $jalaliDate")
            .setFontSize(12f)
            .setTextAlignment(TextAlignment.CENTER)
            .setMarginBottom(16f)
        doc.add(dateParagraph)

        // خط جداکننده
        val line = LineSeparator(SolidLine())
        doc.add(line)
        doc.add(Paragraph("\n"))

        if (stats.isEmpty()) {
            doc.add(Paragraph("هیچ آماری برای این تاریخ ثبت نشده است."))
            doc.close()
            return pdfFile
        }

        // ساخت جدول
        val columnWidths = floatArrayOf(
            100f, // نام محصول
            60f,  // مانده شب گذشته
            60f,  // بارگیری صبح
            50f,  // رسید
            60f,  // بارگیری شب
            50f,  // هدایا
            50f,  // ضایعات
            70f,  // مانده مورد انتظار
            70f,  // مانده واقعی
            70f   // مغایرت
        )
        val table = Table(UnitValue.createPercentArray(columnWidths.size))
        table.setWidth(UnitValue.createPercentValue(100f))
        table.setHorizontalAlignment(HorizontalAlignment.CENTER)

        // هدر جدول
        val headers = listOf(
            "نام محصول",
            "مانده شب گذشته",
            "بارگیری صبح",
            "رسید",
            "بارگیری شب",
            "هدایا",
            "ضایعات",
            "مانده مورد انتظار",
            "مانده واقعی",
            "مغایرت"
        )
        headers.forEach { header ->
            table.addCell(
                Cell().add(
                    Paragraph(header)
                        .setBold()
                        .setFontSize(8f)
                        .setTextAlignment(TextAlignment.CENTER)
                )
            )
        }

        // ردیف‌های داده
        for (stat in stats) {
            val product = products.find { it.id == stat.productId }
            val productName = product?.name ?: "نامشخص"

            val varianceText = when {
                stat.closingVariance == 0 -> "صفر"
                stat.closingVariance > 0 -> "کسری ${stat.closingVariance}"
                else -> "اضافه ${-stat.closingVariance}"
            }

            val cells = listOf(
                productName,
                stat.openingStockTotal.toString(),
                stat.morningLoadingTotal.toString(),
                stat.receiptTotal.toString(),
                stat.nightLoadingTotal.toString(),
                stat.giftsTotal.toString(),
                stat.wasteTotal.toString(),
                stat.expectedClosingStockTotal.toString(),
                stat.currentStockTotal.toString(),
                varianceText
            )
            cells.forEach { value ->
                table.addCell(
                    Cell().add(
                        Paragraph(value)
                            .setFontSize(8f)
                            .setTextAlignment(TextAlignment.CENTER)
                    )
                )
            }
        }

        doc.add(table)

        // خلاصه پایانی
        doc.add(Paragraph("\n"))
        doc.add(line)
        doc.add(Paragraph("\n"))
        val summary = Paragraph(
            "تعداد محصولات بررسی‌شده: ${stats.size}\n" +
            "تعداد محصولات دارای مغایرت: ${stats.count { it.closingVariance != 0 }}\n" +
            "مجموع کسری (به تفکیک محصول): ${stats.filter { it.closingVariance > 0 }.sumOf { it.closingVariance }}\n" +
            "مجموع اضافه (به تفکیک محصول): ${stats.filter { it.closingVariance < 0 }.sumOf { -it.closingVariance }}"
        )
            .setFontSize(10f)
            .setTextAlignment(TextAlignment.RIGHT)
        doc.add(summary)

        doc.close()
        return pdfFile
    }

    /**
     * ساخت فایل CSV گزارش روزانه.
     */
    fun exportDailyReportToCsv(
        context: Context,
        stats: List<DailyStatEntity>,
        products: List<Product>,
        isoDate: String
    ): File {
        val reportDir = File(context.cacheDir, "shared").apply { if (!exists()) mkdirs() }
        val dateSuffix = isoDate.replace("-", "")
        val csvFile = File(reportDir, "daily_report_$dateSuffix.csv")

        val writer = FileWriter(csvFile, Charsets.UTF_8)

        // BOM برای تشخیص UTF-8 در Excel
        writer.write("\uFEFF")

        // هدر
        writer.write("نام محصول,مانده شب گذشته,بارگیری صبح,رسید,بارگیری شب,هدایا,ضایعات,مانده مورد انتظار,مانده واقعی,مغایرت\n")

        // ردیف‌ها
        for (stat in stats) {
            val product = products.find { it.id == stat.productId }
            val productName = product?.name ?: "نامشخص"
            val varianceText = when {
                stat.closingVariance == 0 -> "صفر"
                stat.closingVariance > 0 -> "کسری ${stat.closingVariance}"
                else -> "اضافه ${-stat.closingVariance}"
            }
            writer.write(
                "$productName,${stat.openingStockTotal},${stat.morningLoadingTotal}," +
                "${stat.receiptTotal},${stat.nightLoadingTotal},${stat.giftsTotal}," +
                "${stat.wasteTotal},${stat.expectedClosingStockTotal}," +
                "${stat.currentStockTotal},$varianceText\n"
            )
        }
        writer.flush()
        writer.close()
        return csvFile
    }

    /**
     * اشتراک‌گذاری فایل با Intent استاندارد اندروید.
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
