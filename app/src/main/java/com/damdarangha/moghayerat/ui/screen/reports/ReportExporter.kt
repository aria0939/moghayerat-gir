package com.damdarangha.moghayerat.ui.screen.reports

import android.content.Context
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import android.text.TextUtils
import androidx.core.content.FileProvider
import androidx.core.content.res.ResourcesCompat
import com.damdarangha.moghayerat.R
import com.damdarangha.moghayerat.data.db.entity.DailyStatEntity
import com.damdarangha.moghayerat.domain.model.Product
import com.damdarangha.moghayerat.util.JalaliDateConverter
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

/**
 * تولید گزارش PDF با PdfDocument نیتیو اندروید.
 *
 * ویژگی‌ها:
 * - صفحه A4 افقی (842×595 نقطه)
 * - ستون‌ها از راست به چپ (RTL)
 * - فونت Vazirmatn فارسی با shaping کامل
 * - همه متن‌ها فارسی
 * - شماره صفحه فارسی
 */
object ReportExporter {

    private val HEADER_BG = Color.rgb(220, 230, 250)
    private val ROW_ALT_BG = Color.rgb(248, 250, 252)
    private val BORDER_COLOR = Color.rgb(150, 160, 180)
    private val TEXT_DARK = Color.rgb(20, 30, 50)
    private val TEXT_MUTED = Color.rgb(100, 110, 130)
    private val TITLE_COLOR = Color.rgb(18, 61, 168)
    private val DEFICIT_BG = Color.rgb(255, 235, 235)
    private val SURPLUS_BG = Color.rgb(235, 255, 235)

    // ستون‌ها - ترتیب منطقی RTL (اولین = راست‌ترین)
    private val COLUMN_HEADERS = arrayOf(
        "ردیف", "نام محصول", "مانده\nشب گذشته", "بارگیری\nصبح", "میان‌روز",
        "برگشت\nبه انبار", "رسید", "بارگیری\nشب", "هدایا", "ضایعات",
        "مانده واقعی\nپایان روز", "مانده مورد\nانتظار", "مغایرت\nآخر کار"
    )

    // عرض نسبی ستون‌ها (مجموع = 1.0)
    private val COLUMN_WEIGHTS = floatArrayOf(
        0.04f, 0.15f, 0.08f, 0.06f, 0.05f, 0.06f, 0.05f, 0.06f, 0.05f, 0.05f, 0.08f, 0.08f, 0.08f
    )

    // A4 افقی: 842 × 595 نقطه
    private const val PAGE_WIDTH = 842
    private const val PAGE_HEIGHT = 595
    private const val MARGIN = 24f
    private const val HEADER_HEIGHT = 48f
    private const val ROW_HEIGHT = 26f
    private const val FOOTER_HEIGHT = 70f

    private var fontRegular: Typeface? = null
    private var fontBold: Typeface? = null

    fun exportWorkflowReportToPdf(
        context: Context,
        workflowName: String,
        stats: List<DailyStatEntity>,
        products: List<Product>,
        isoDate: String
    ): File {
        // بارگذاری فونت
        if (fontRegular == null) fontRegular = loadTypeface(context)
        if (fontBold == null) fontBold = loadTypefaceBold(context)

        // پوشه خروجی
        val reportDir = File(context.cacheDir, "shared")
        if (!reportDir.exists()) {
            val created = reportDir.mkdirs()
            if (!created && !reportDir.exists())
                throw IOException("امکان ایجاد پوشه گزارش وجود ندارد")
        }

        val jalaliDateSafe = JalaliDateConverter.gregorianToJalaliFileSafeString(isoDate)
        val jalaliDisplay = JalaliDateConverter.gregorianToJalaliString(isoDate)
        val safeName = sanitizeFileName(workflowName)
        val fileName = "گزارش_${safeName}_$jalaliDateSafe.pdf"
        val pdfFile = File(reportDir, fileName)

        // Paint ها
        val titlePaint = TextPaint().apply {
            color = TITLE_COLOR; textSize = 15f; typeface = fontBold; isAntiAlias = true
        }
        val subtitlePaint = TextPaint().apply {
            color = TEXT_DARK; textSize = 10f; typeface = fontRegular; isAntiAlias = true
        }
        val summaryPaint = TextPaint().apply {
            color = TEXT_MUTED; textSize = 9f; typeface = fontRegular; isAntiAlias = true
        }
        val headerPaint = TextPaint().apply {
            color = TEXT_DARK; textSize = 7f; typeface = fontBold; isAntiAlias = true
        }
        val cellPaint = TextPaint().apply {
            color = TEXT_DARK; textSize = 7f; typeface = fontRegular; isAntiAlias = true
        }
        val footerPaint = TextPaint().apply {
            color = TEXT_MUTED; textSize = 8f; typeface = fontRegular; isAntiAlias = true
        }
        val signLabelPaint = TextPaint().apply {
            color = TEXT_DARK; textSize = 9f; typeface = fontBold; isAntiAlias = true
        }
        val borderPaint = Paint().apply {
            color = BORDER_COLOR; strokeWidth = 0.5f; style = Paint.Style.STROKE; isAntiAlias = true
        }
        val fillPaint = Paint().apply { style = Paint.Style.FILL; isAntiAlias = true }
        val linePaint = Paint().apply {
            color = BORDER_COLOR; strokeWidth = 1f; isAntiAlias = true
        }

        val document = PdfDocument()

        // ابعاد جدول
        val tableWidth = PAGE_WIDTH - 2 * MARGIN
        val columnWidths = COLUMN_WEIGHTS.map { it * tableWidth }
        // tableRightX = لبه راست جدول (نزدیک به لبه راست صفحه)
        val tableRightX = PAGE_WIDTH - MARGIN
        val tableStartY = MARGIN + 90f

        // آمار خلاصه
        val totalCount = products.size
        val registeredCount = stats.size
        val varianceCount = stats.count { it.closingVariance != 0 }
        val remainingCount = (totalCount - registeredCount).coerceAtLeast(0)

        // حالت خالی
        if (products.isEmpty()) {
            val pageInfo = PdfDocument.PageInfo.Builder(PAGE_WIDTH, PAGE_HEIGHT, 1).create()
            val page = document.startPage(pageInfo)
            val canvas = page.canvas

            drawCenteredText(canvas, "گزارش آمارگیری و مغایرت‌گیری دامداران",
                PAGE_WIDTH / 2f, MARGIN + 20f, titlePaint)
            drawCenteredText(canvas, "گردش کار: $workflowName    |    تاریخ: $jalaliDisplay",
                PAGE_WIDTH / 2f, MARGIN + 40f, subtitlePaint)
            drawCenteredText(canvas, "هیچ محصولی در این گردش کار ثبت نشده است.",
                PAGE_WIDTH / 2f, PAGE_HEIGHT / 2f, cellPaint)
            drawCenteredText(canvas, "صفحه ۱ از ۱",
                PAGE_WIDTH / 2f, PAGE_HEIGHT - 20f, footerPaint)

            document.finishPage(page)
            FileOutputStream(pdfFile).use { document.writeTo(it) }
            document.close()
            return pdfFile
        }

        // صفحات
        var pageNum = 0
        val contentHeight = PAGE_HEIGHT - tableStartY - FOOTER_HEIGHT
        val rowsPerPage = (contentHeight / ROW_HEIGHT).toInt().coerceAtLeast(1)
        var currentRow = 0
        val totalPages = ((products.size + rowsPerPage - 1) / rowsPerPage).coerceAtLeast(1)

        while (currentRow < products.size) {
            pageNum++
            val pageInfo = PdfDocument.PageInfo.Builder(PAGE_WIDTH, PAGE_HEIGHT, pageNum).create()
            val page = document.startPage(pageInfo)
            val canvas = page.canvas

            // ===================== سربرگ =====================
            if (pageNum == 1) {
                drawCenteredText(canvas, "گزارش آمارگیری و مغایرت‌گیری دامداران",
                    PAGE_WIDTH / 2f, MARGIN + 15f, titlePaint)
                drawCenteredText(canvas, "گردش کار: $workflowName    |    تاریخ: $jalaliDisplay",
                    PAGE_WIDTH / 2f, MARGIN + 33f, subtitlePaint)
                drawCenteredText(canvas,
                    "کل محصولات: ${toPersian(totalCount)}    |    " +
                    "ثبت‌شده: ${toPersian(registeredCount)}    |    " +
                    "باقی‌مانده: ${toPersian(remainingCount)}    |    " +
                    "دارای مغایرت: ${toPersian(varianceCount)}",
                    PAGE_WIDTH / 2f, MARGIN + 50f, summaryPaint)
                canvas.drawLine(MARGIN, MARGIN + 62f, PAGE_WIDTH - MARGIN, MARGIN + 62f, linePaint)
            } else {
                drawCenteredText(canvas, "گردش کار: $workflowName    |    تاریخ: $jalaliDisplay",
                    PAGE_WIDTH / 2f, MARGIN + 15f, subtitlePaint)
                canvas.drawLine(MARGIN, MARGIN + 25f, PAGE_WIDTH - MARGIN, MARGIN + 25f, linePaint)
            }

            // ===================== جدول =====================
            var y = tableStartY

            // هدر جدول - پس‌زمینه
            fillPaint.color = HEADER_BG
            canvas.drawRect(MARGIN, y, tableRightX, y + HEADER_HEIGHT, fillPaint)

            // رسم هدر ستون‌ها از راست به چپ
            // ستون 0 (ردیف) در سمت راست، ستون 12 (مغایرت) در سمت چپ
            var xRight = tableRightX // شروع از لبه راست
            for (i in COLUMN_HEADERS.indices) {
                val colWidth = columnWidths[i]
                val colLeft = xRight - colWidth // لبه چپ این ستون

                // خط جداکننده راست ستون
                canvas.drawLine(xRight, y, xRight, y + HEADER_HEIGHT, borderPaint)

                // متن هدر در وسط ستون
                drawRtlCellText(canvas, COLUMN_HEADERS[i], colLeft, y, colWidth, HEADER_HEIGHT, headerPaint, false)

                xRight = colLeft // حرکت به سمت چپ
            }
            // خط چپ ستون آخر
            canvas.drawLine(xRight, y, xRight, y + HEADER_HEIGHT, borderPaint)
            // خطوط افقی هدر
            canvas.drawLine(MARGIN, y, tableRightX, y, borderPaint)
            canvas.drawLine(MARGIN, y + HEADER_HEIGHT, tableRightX, y + HEADER_HEIGHT, borderPaint)

            y += HEADER_HEIGHT

            // ردیف‌های داده
            val rowsThisPage = minOf(rowsPerPage, products.size - currentRow)
            for (rowOffset in 0 until rowsThisPage) {
                val productIndex = currentRow + rowOffset
                if (productIndex >= products.size) break

                val product = products[productIndex]
                val stat = stats.find { it.productId == product.id }
                val rowIndexDisplay = productIndex + 1

                // پس‌زمینه ردیف متناوب
                if (rowIndexDisplay % 2 == 0) {
                    fillPaint.color = ROW_ALT_BG
                    canvas.drawRect(MARGIN, y, tableRightX, y + ROW_HEIGHT, fillPaint)
                }

                // مقادیر
                val values = if (stat == null) {
                    arrayOf(
                        toPersian(rowIndexDisplay), product.name,
                        "—", "—", "—", "—", "—", "—", "—", "—", "—", "—", "ثبت‌نشده"
                    )
                } else {
                    arrayOf(
                        toPersian(rowIndexDisplay), product.name,
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

                // رنگ خاص برای ستون مغایرت (ستون 12 = چپ‌ترین)
                if (stat != null && stat.closingVariance != 0) {
                    val varianceBg = if (stat.closingVariance > 0) DEFICIT_BG else SURPLUS_BG
                    fillPaint.color = varianceBg
                    // ستون 12 از MARGIN شروع می‌شود به عرض columnWidths[12]
                    canvas.drawRect(MARGIN, y, MARGIN + columnWidths[12], y + ROW_HEIGHT, fillPaint)
                }

                // رسم سلول‌ها از راست به چپ
                xRight = tableRightX
                for (i in values.indices) {
                    val colWidth = columnWidths[i]
                    val colLeft = xRight - colWidth

                    canvas.drawLine(xRight, y, xRight, y + ROW_HEIGHT, borderPaint)

                    // ستون نام محصول (i=1) راست‌چین، بقیه وسط‌چین
                    drawRtlCellText(canvas, values[i], colLeft, y, colWidth, ROW_HEIGHT, cellPaint, i == 1)

                    xRight = colLeft
                }
                canvas.drawLine(xRight, y, xRight, y + ROW_HEIGHT, borderPaint)
                canvas.drawLine(MARGIN, y + ROW_HEIGHT, tableRightX, y + ROW_HEIGHT, borderPaint)

                y += ROW_HEIGHT
            }

            currentRow += rowsThisPage

            // ===================== فوتر =====================
            canvas.drawLine(MARGIN, PAGE_HEIGHT - FOOTER_HEIGHT,
                PAGE_WIDTH - MARGIN, PAGE_HEIGHT - FOOTER_HEIGHT, linePaint)

            val signY = PAGE_HEIGHT - FOOTER_HEIGHT + 12f
            val signWidth = tableWidth / 3f
            val signLabels = arrayOf("نام و امضای آمارگیر:", "توضیحات:", "تأیید مسئول:")

            // سه ستون امضا از راست به چپ
            var sxRight = tableRightX
            for (i in 0..2) {
                val sxLeft = sxRight - signWidth
                canvas.drawRect(sxLeft, signY, sxRight, PAGE_HEIGHT - 25f, borderPaint)
                // برچسب در سمت راست سلول
                drawRtlCellText(canvas, signLabels[i], sxLeft, signY, signWidth, 20f, signLabelPaint, true)
                // خط امضا
                canvas.drawLine(sxLeft + 8f, PAGE_HEIGHT - 38f, sxRight - 8f, PAGE_HEIGHT - 38f, borderPaint)
                sxRight = sxLeft
            }

            // شماره صفحه فارسی
            drawCenteredText(canvas, "صفحه ${toPersian(pageNum)} از ${toPersian(totalPages)}",
                PAGE_WIDTH / 2f, PAGE_HEIGHT - 12f, footerPaint)

            document.finishPage(page)
        }

        // نوشتن فایل
        FileOutputStream(pdfFile).use { out -> document.writeTo(out) }
        document.close()

        return pdfFile
    }

    /**
     * رسم متن در سلول RTL.
     * از StaticLayout برای shaping صحیح فارسی استفاده می‌کند.
     */
    private fun drawRtlCellText(
        canvas: Canvas,
        text: String,
        cellLeft: Float,
        cellTop: Float,
        cellWidth: Float,
        cellHeight: Float,
        paint: TextPaint,
        isRightAligned: Boolean
    ) {
        val padding = 3f
        val availableWidth = (cellWidth - 2 * padding).toInt().coerceAtLeast(1)

        // ALIGN_OPPOSITE = راست‌چین در RTL
        // ALIGN_CENTER = وسط‌چین
        val alignment = if (isRightAligned) Layout.Alignment.ALIGN_OPPOSITE else Layout.Alignment.ALIGN_CENTER

        val sl = StaticLayout.Builder
            .obtain(text, 0, text.length, paint, availableWidth)
            .setAlignment(alignment)
            .setLineSpacing(0f, 1f)
            .setIncludePad(false)
            .setMaxLines(2)
            .setEllipsize(TextUtils.TruncateAt.END)
            .build()

        canvas.save()
        val tx = if (isRightAligned) cellLeft + padding else cellLeft + (cellWidth - sl.width) / 2f
        val ty = cellTop + (cellHeight - sl.height) / 2f
        canvas.translate(tx, ty)
        sl.draw(canvas)
        canvas.restore()
    }

    /**
     * رسم متن وسط‌چین.
     */
    private fun drawCenteredText(
        canvas: Canvas,
        text: String,
        centerX: Float,
        y: Float,
        paint: TextPaint
    ) {
        val textWidth = paint.measureText(text)
        canvas.drawText(text, centerX - textWidth / 2f, y + paint.textSize, paint)
    }

    /**
     * تبدیل عدد به اعداد فارسی.
     */
    private fun toPersian(value: Int): String {
        val persianDigits = arrayOf('۰', '۱', '۲', '۳', '۴', '۵', '۶', '۷', '۸', '۹')
        return value.toString().map { c -> if (c.isDigit()) persianDigits[c - '0'] else c }.joinToString("")
    }

    /**
     * بارگذاری فونت Vazirmatn Regular.
     */
    private fun loadTypeface(context: Context): Typeface {
        return try {
            ResourcesCompat.getFont(context, R.font.vazirmatn_regular) ?: Typeface.DEFAULT
        } catch (e: Exception) {
            Typeface.DEFAULT
        }
    }

    /**
     * بارگذاری فونت Vazirmatn Bold.
     */
    private fun loadTypefaceBold(context: Context): Typeface {
        return try {
            ResourcesCompat.getFont(context, R.font.vazirmatn_bold) ?: loadTypeface(context)
        } catch (e: Exception) {
            loadTypeface(context)
        }
    }

    private fun sanitizeFileName(name: String): String {
        return name.replace(" ", "_").replace("/", "-").replace("\\", "-")
            .replace(":", "-").replace("*", "-").replace("?", "-")
            .replace("\"", "-").replace("<", "-").replace(">", "-")
            .replace("|", "-").trim()
    }

    fun validateFile(file: File): Boolean {
        return file.exists() && file.length() > 0 && file.canRead()
    }

    fun getFileUri(context: Context, file: File): Uri {
        return FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
    }

    fun openPdf(activityContext: Context, file: File) {
        if (!validateFile(file)) throw IOException("فایل PDF معتبر نیست یا وجود ندارد")
        val uri = getFileUri(activityContext, file)
        val viewIntent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(uri, "application/pdf")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            if (activityContext !is android.app.Activity) addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        activityContext.startActivity(viewIntent)
    }

    fun shareFile(activityContext: Context, file: File, mimeType: String = "application/pdf") {
        if (!validateFile(file)) throw IOException("فایل PDF معتبر نیست یا وجود ندارد")
        val uri = getFileUri(activityContext, file)
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = mimeType
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            if (activityContext !is android.app.Activity) addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        activityContext.startActivity(Intent.createChooser(shareIntent, "اشتراک‌گذاری فایل PDF"))
    }

    fun copyToDestination(context: Context, sourceFile: File, destinationUri: Uri): Boolean {
        return try {
            if (!validateFile(sourceFile)) return false
            sourceFile.inputStream().use { input ->
                context.contentResolver.openOutputStream(destinationUri)?.use { output ->
                    input.copyTo(output)
                    output.flush()
                } ?: return false
            }
            true
        } catch (e: Exception) { false }
    }
}
