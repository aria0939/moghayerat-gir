package com.damdarangha.moghayerat.ui.screen.reports

import android.content.Context
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import android.text.TextUtils
import androidx.core.content.FileProvider
import com.damdarangha.moghayerat.data.db.entity.DailyStatEntity
import com.damdarangha.moghayerat.domain.model.Product
import com.damdarangha.moghayerat.util.JalaliDateConverter
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

/**
 * تولید گزارش PDF با استفاده از PdfDocument نیتیو اندروید.
 *
 * مزایا:
 * - Arabic shaping کامل (حروف متصل)
 * - BiDi صحیح (RTL)
 * - پشتیبانی از فونت Vazirmatn
 * - اعداد فارسی صحیح
 * - بدون نیاز به کتابخانه خارجی
 */
object ReportExporter {

    // رنگ‌ها
    private val HEADER_BG = Color.rgb(220, 230, 250)
    private val ROW_ALT_BG = Color.rgb(248, 250, 252)
    private val BORDER_COLOR = Color.rgb(150, 160, 180)
    private val TEXT_DARK = Color.rgb(20, 30, 50)
    private val TEXT_MUTED = Color.rgb(100, 110, 130)
    private val TITLE_COLOR = Color.rgb(18, 61, 168)
    private val DEFICIT_BG = Color.rgb(255, 235, 235)
    private val SURPLUS_BG = Color.rgb(235, 255, 235)
    private val WHITE = Color.WHITE

    // ستون‌ها - ترتیب منطقی RTL (اولین = راست‌ترین)
    private val COLUMN_HEADERS = arrayOf(
        "ردیف",
        "نام محصول",
        "مانده\nشب گذشته",
        "بارگیری\nصبح",
        "میان\u200Cروز",
        "برگشت\nبه انبار",
        "رسید",
        "بارگیری\nشب",
        "هدایا",
        "ضایعات",
        "مانده واقعی\nپایان روز",
        "مانده مورد\nانتظار",
        "مغایرت\nآخر کار"
    )

    // عرض نسبی ستون‌ها (درصد از عرض جدول)
    private val COLUMN_WEIGHTS = floatArrayOf(
        0.04f,   // ردیف
        0.16f,   // نام محصول
        0.08f,   // مانده شب گذشته
        0.06f,   // بارگیری صبح
        0.05f,   // میان‌روز
        0.06f,   // برگشت به انبار
        0.05f,   // رسید
        0.06f,   // بارگیری شب
        0.05f,   // هدایا
        0.05f,   // ضایعات
        0.08f,   // مانده واقعی پایان
        0.08f,   // مانده مورد انتظار
        0.08f    // مغایرت آخر کار
    )

    // ابعاد صفحه A4 افقی (نقاط)
    private const val PAGE_WIDTH = 842
    private const val PAGE_HEIGHT = 595
    private const val MARGIN = 28f
    private const val HEADER_HEIGHT = 50f
    private const val ROW_HEIGHT = 28f
    private const val FOOTER_HEIGHT = 80f

    fun exportWorkflowReportToPdf(
        context: Context,
        workflowName: String,
        stats: List<DailyStatEntity>,
        products: List<Product>,
        isoDate: String
    ): File {
        // ایجاد پوشه
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

        // بارگذاری فونت Vazirmatn
        val fontRegular = loadTypeface(context, "vazirmatn_regular.ttf")
        val fontBold = loadTypeface(context, "vazirmatn_bold.ttf")

        // Paint ها
        val titlePaint = TextPaint().apply {
            color = TITLE_COLOR
            textSize = 16f
            typeface = fontBold
            isAntiAlias = true
        }
        val subtitlePaint = TextPaint().apply {
            color = TEXT_DARK
            textSize = 11f
            typeface = fontRegular
            isAntiAlias = true
        }
        val summaryPaint = TextPaint().apply {
            color = TEXT_MUTED
            textSize = 10f
            typeface = fontRegular
            isAntiAlias = true
        }
        val headerPaint = TextPaint().apply {
            color = TEXT_DARK
            textSize = 7.5f
            typeface = fontBold
            isAntiAlias = true
        }
        val cellPaint = TextPaint().apply {
            color = TEXT_DARK
            textSize = 7.5f
            typeface = fontRegular
            isAntiAlias = true
        }
        val footerPaint = TextPaint().apply {
            color = TEXT_MUTED
            textSize = 8f
            typeface = fontRegular
            isAntiAlias = true
        }
        val signLabelPaint = TextPaint().apply {
            color = TEXT_DARK
            textSize = 10f
            typeface = fontBold
            isAntiAlias = true
        }
        val signLinePaint = TextPaint().apply {
            color = TEXT_MUTED
            textSize = 10f
            typeface = fontRegular
            isAntiAlias = true
        }
        val borderPaint = Paint().apply {
            color = BORDER_COLOR
            strokeWidth = 0.5f
            style = Paint.Style.STROKE
            isAntiAlias = true
        }
        val fillPaint = Paint().apply {
            style = Paint.Style.FILL
            isAntiAlias = true
        }
        val linePaint = Paint().apply {
            color = BORDER_COLOR
            strokeWidth = 1f
            isAntiAlias = true
        }

        // ساخت PDF
        val document = PdfDocument()

        // محاسبه ابعاد جدول
        val tableWidth = PAGE_WIDTH - 2 * MARGIN
        val columnWidths = COLUMN_WEIGHTS.map { it * tableWidth }
        val tableStartX = MARGIN
        val tableStartY = MARGIN + 100f // بعد از سربرگ

        // آمار خلاصه
        val totalCount = products.size
        val registeredCount = stats.size
        val varianceCount = stats.count { it.closingVariance != 0 }
        val remainingCount = (totalCount - registeredCount).coerceAtLeast(0)

        // صفحات
        var pageNum = 0
        val contentHeight = PAGE_HEIGHT - tableStartY - FOOTER_HEIGHT
        val rowsPerPage = (contentHeight / ROW_HEIGHT).toInt()
        val totalRows = products.size + 1 // +1 برای هدر
        val totalPages = ((totalRows + rowsPerPage - 1) / rowsPerPage).coerceAtLeast(1)
        var currentRow = 0

        // اگر هیچ محصولی نیست
        if (products.isEmpty()) {
            val pageInfo = PdfDocument.PageInfo.Builder(PAGE_WIDTH, PAGE_HEIGHT, 1).create()
            val page = document.startPage(pageInfo)
            val canvas = page.canvas

            // عنوان
            drawCenteredText(canvas, "گزارش آمارگیری و مغایرت‌گیری دامداران",
                PAGE_WIDTH / 2f, MARGIN + 20f, titlePaint)
            drawCenteredText(canvas, "گردش کار: $workflowName    |    تاریخ: $jalaliDisplay",
                PAGE_WIDTH / 2f, MARGIN + 45f, subtitlePaint)
            drawCenteredText(canvas, "هیچ محصولی در این گردش کار ثبت نشده است.",
                PAGE_WIDTH / 2f, PAGE_HEIGHT / 2f, cellPaint)

            // شماره صفحه
            drawCenteredText(canvas, "1 / 1",
                PAGE_WIDTH / 2f, PAGE_HEIGHT - 20f, footerPaint)

            document.finishPage(page)
            document.writeTo(FileOutputStream(pdfFile))
            document.close()
            return pdfFile
        }

        // تولید صفحات
        while (currentRow <= products.size) {
            pageNum++
            val pageInfo = PdfDocument.PageInfo.Builder(PAGE_WIDTH, PAGE_HEIGHT, pageNum).create()
            val page = document.startPage(pageInfo)
            val canvas = page.canvas

            // ===================== سربرگ (فقط صفحه اول) =====================
            if (pageNum == 1) {
                drawCenteredText(canvas, "گزارش آمارگیری و مغایرت‌گیری دامداران",
                    PAGE_WIDTH / 2f, MARGIN + 15f, titlePaint)
                drawCenteredText(canvas, "گردش کار: $workflowName    |    تاریخ: $jalaliDisplay",
                    PAGE_WIDTH / 2f, MARGIN + 35f, subtitlePaint)
                drawCenteredText(canvas,
                    "کل محصولات: $totalCount    |    ثبت‌شده: $registeredCount    |    " +
                    "باقی‌مانده: $remainingCount    |    دارای مغایرت: $varianceCount",
                    PAGE_WIDTH / 2f, MARGIN + 52f, summaryPaint)

                // خط جداکننده
                canvas.drawLine(MARGIN, MARGIN + 65f, PAGE_WIDTH - MARGIN, MARGIN + 65f, linePaint)
            } else {
                // صفحات بعدی: فقط نام گردش کار و تاریخ
                drawCenteredText(canvas, "گردش کار: $workflowName    |    تاریخ: $jalaliDisplay",
                    PAGE_WIDTH / 2f, MARGIN + 15f, subtitlePaint)
                canvas.drawLine(MARGIN, MARGIN + 25f, PAGE_WIDTH - MARGIN, MARGIN + 25f, linePaint)
            }

            // ===================== جدول =====================
            var y = tableStartY

            // هدر جدول (در هر صفحه تکرار می‌شود)
            // پس‌زمینه هدر
            fillPaint.color = HEADER_BG
            canvas.drawRect(tableStartX, y, tableStartX + tableWidth, y + HEADER_HEIGHT, fillPaint)

            // متن هدر و خطوط ستون‌ها
            var x = tableStartX
            for (i in COLUMN_HEADERS.indices) {
                val colWidth = columnWidths[i]
                // خط راست ستون
                canvas.drawLine(x, y, x, y + HEADER_HEIGHT, borderPaint)
                // متن هدر (درست در وسط ستون)
                drawCellText(canvas, COLUMN_HEADERS[i], x, y, colWidth, HEADER_HEIGHT, headerPaint, true)
                x += colWidth
            }
            // خط چپ ستون آخر
            canvas.drawLine(x, y, x, y + HEADER_HEIGHT, borderPaint)
            // خطوط افقی هدر
            canvas.drawLine(tableStartX, y, tableStartX + tableWidth, y, borderPaint)
            canvas.drawLine(tableStartX, y + HEADER_HEIGHT, tableStartX + tableWidth, y + HEADER_HEIGHT, borderPaint)

            y += HEADER_HEIGHT

            // ردیف‌های داده
            val rowsThisPage = minOf(rowsPerPage - 1, products.size - currentRow)
            for (rowOffset in 0 until rowsThisPage) {
                val productIndex = currentRow + rowOffset
                if (productIndex >= products.size) break

                val product = products[productIndex]
                val stat = stats.find { it.productId == product.id }
                val rowIndexDisplay = productIndex + 1

                // پس‌زمینه ردیف متناوب
                if (rowIndexDisplay % 2 == 0) {
                    fillPaint.color = ROW_ALT_BG
                    canvas.drawRect(tableStartX, y, tableStartX + tableWidth, y + ROW_HEIGHT, fillPaint)
                }

                // آماده‌سازی مقادیر
                val values = if (stat == null) {
                    arrayOf(
                        ReportFormatter.toPersianDigits(rowIndexDisplay),
                        product.name,
                        "—", "—", "—", "—", "—", "—", "—", "—", "—", "—", "ثبت\u200Cنشده"
                    )
                } else {
                    arrayOf(
                        ReportFormatter.toPersianDigits(rowIndexDisplay),
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

                // رنگ خاص برای ستون مغایرت
                if (stat != null && stat.closingVariance != 0) {
                    val varianceBg = if (stat.closingVariance > 0) DEFICIT_BG else SURPLUS_BG
                    val lastColX = tableStartX + tableWidth - columnWidths[12]
                    fillPaint.color = varianceBg
                    canvas.drawRect(lastColX, y, tableStartX + tableWidth, y + ROW_HEIGHT, fillPaint)
                }

                // رسم مقادیر سلول‌ها
                x = tableStartX
                for (i in values.indices) {
                    val colWidth = columnWidths[i]
                    canvas.drawLine(x, y, x, y + ROW_HEIGHT, borderPaint)
                    val isProductName = i == 1
                    drawCellText(canvas, values[i], x, y, colWidth, ROW_HEIGHT, cellPaint, isProductName)
                    x += colWidth
                }
                canvas.drawLine(x, y, x, y + ROW_HEIGHT, borderPaint)
                canvas.drawLine(tableStartX, y + ROW_HEIGHT, tableStartX + tableWidth, y + ROW_HEIGHT, borderPaint)

                y += ROW_HEIGHT
            }

            currentRow += rowsThisPage

            // ===================== فوتر =====================
            // خط جداکننده فوتر
            canvas.drawLine(MARGIN, PAGE_HEIGHT - FOOTER_HEIGHT,
                PAGE_WIDTH - MARGIN, PAGE_HEIGHT - FOOTER_HEIGHT, linePaint)

            // جدول امضا
            val signY = PAGE_HEIGHT - FOOTER_HEIGHT + 15f
            val signWidth = tableWidth / 3f

            // سه ستون امضا
            val signLabels = arrayOf("نام و امضای آمارگیر:", "توضیحات:", "تأیید مسئول:")
            for (i in 0..2) {
                val sx = tableStartX + i * signWidth
                // خطوط کادر
                canvas.drawRect(sx, signY, sx + signWidth, PAGE_HEIGHT - 30f, borderPaint)
                // برچسب
                drawRtlText(canvas, signLabels[i], sx + 8f, signY + 8f, signLabelPaint)
                // خط امضا
                canvas.drawLine(sx + 8f, PAGE_HEIGHT - 45f, sx + signWidth - 8f, PAGE_HEIGHT - 45f, signLinePaint)
            }

            // شماره صفحه
            drawCenteredText(canvas, "$pageNum / $totalPages",
                PAGE_WIDTH / 2f, PAGE_HEIGHT - 15f, footerPaint)

            document.finishPage(page)
        }

        // نوشتن فایل
        pdfFile.outputStream().use { out ->
            document.writeTo(out)
        }
        document.close()

        return pdfFile
    }

    /**
     * رسم متن در وسط یک سلول.
     */
    private fun drawCellText(
        canvas: Canvas,
        text: String,
        x: Float,
        y: Float,
        width: Float,
        height: Float,
        paint: TextPaint,
        isRightAligned: Boolean = false
    ) {
        val textWidth = paint.measureText(text)
        val padding = 4f
        val availableWidth = width - 2 * padding

        if (textWidth <= availableWidth) {
            // متن در یک خط جا می‌شود
            val textX = if (isRightAligned) {
                x + width - textWidth - padding
            } else {
                x + (width - textWidth) / 2f
            }
            val textY = y + (height + paint.textSize) / 2f - paint.descent()
            canvas.drawText(text, textX, textY, paint)
        } else {
            // متن چندخطی با StaticLayout
            val sl = StaticLayout.Builder
                .obtain(text, 0, text.length, paint, availableWidth.toInt())
                .setAlignment(if (isRightAligned) Layout.Alignment.ALIGN_OPPOSITE else Layout.Alignment.ALIGN_CENTER)
                .setLineSpacing(0f, 1f)
                .setIncludePad(false)
                .setMaxLines(2)
                .setEllipsize(TextUtils.TruncateAt.END)
                .build()

            canvas.save()
            val tx = if (isRightAligned) x + padding else x + (width - sl.width) / 2f
            val ty = y + (height - sl.height) / 2f
            canvas.translate(tx, ty)
            sl.draw(canvas)
            canvas.restore()
        }
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
        canvas.drawText(text, centerX - textWidth / 2f, y, paint)
    }

    /**
     * رسم متن راست‌چین.
     */
    private fun drawRtlText(
        canvas: Canvas,
        text: String,
        x: Float,
        y: Float,
        paint: TextPaint
    ) {
        canvas.drawText(text, x, y + paint.textSize, paint)
    }

    /**
     * بارگذاری Typeface از res/font.
     */
    private fun loadTypeface(context: Context, fontFileName: String): Typeface {
        return try {
            val fontResId = context.resources.getIdentifier(
                fontFileName.substringBefore("."),
                "font",
                context.packageName
            )
            if (fontResId != 0) {
                Typeface.createFromResource(context.resources, fontResId)
            } else {
                Typeface.DEFAULT
            }
        } catch (e: Exception) {
            Typeface.DEFAULT
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

    fun validateFile(file: File): Boolean {
        return file.exists() && file.length() > 0 && file.canRead()
    }

    fun getFileUri(context: Context, file: File): Uri {
        return FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )
    }

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

    fun copyToDestination(context: Context, sourceFile: File, destinationUri: Uri): Boolean {
        return try {
            if (!validateFile(sourceFile)) return false
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
