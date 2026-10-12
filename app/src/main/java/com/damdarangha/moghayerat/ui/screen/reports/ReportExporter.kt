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
 * ستون‌ها (۱۴ ستون، RTL):
 * 1. ردیف
 * 2. نام محصول
 * 3. مانده (openingStock)
 * 4. تحویل روز (morningLoading)
 * 5. آزمایشگاه (receipt)
 * 6. متفرقه / هدایا (gifts)
 * 7. میان روز (midDayLoading)
 * 8. برگشت به انبار (returns)
 * 9. موجودی کل (opening + receipt + returns)
 * 10. تعداد بارگیری شده (morning + midDay + night)
 * 11. مانده (expectedClosing)
 * 12. توضیحات (notes)
 * 13. کسری یا اضافه (label)
 * 14. مغایرت (value)
 *
 * فرمت مقادیر: 2B + 2'
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

    // ۱۴ ستون - ترتیب RTL (اولین = راست‌ترین)
    private val COLUMN_HEADERS = arrayOf(
        "ردیف",
        "نام محصول",
        "مانده",
        "تحویل\nروز",
        "آزمایشگاه",
        "متفرقه\n/ هدایا",
        "میان\nروز",
        "برگشت\nبه انبار",
        "موجودی کل",
        "تعداد\nبارگیری شده",
        "مانده",
        "توضیحات",
        "کسری یا\nاضافه",
        "مغایرت"
    )

    // عرض نسبی ستون‌ها (مجموع = 1.0)
    private val COLUMN_WEIGHTS = floatArrayOf(
        0.03f,   // ردیف
        0.13f,   // نام محصول
        0.07f,   // مانده
        0.06f,   // تحویل روز
        0.06f,   // آزمایشگاه
        0.06f,   // متفرقه / هدایا
        0.05f,   // میان روز
        0.06f,   // برگشت به انبار
        0.07f,   // موجودی کل
        0.07f,   // تعداد بارگیری شده
        0.07f,   // مانده
        0.08f,   // توضیحات
        0.06f,   // کسری یا اضافه
        0.07f    // مغایرت
    )

    private const val PAGE_WIDTH = 842
    private const val PAGE_HEIGHT = 595
    private const val MARGIN = 20f
    private const val HEADER_HEIGHT = 46f
    private const val ROW_HEIGHT = 26f
    private const val FOOTER_HEIGHT = 65f

    private var fontRegular: Typeface? = null
    private var fontBold: Typeface? = null

    fun exportWorkflowReportToPdf(
        context: Context,
        workflowName: String,
        stats: List<DailyStatEntity>,
        products: List<Product>,
        isoDate: String
    ): File {
        if (fontRegular == null) fontRegular = loadTypeface(context, R.font.vazirmatn_regular)
        if (fontBold == null) fontBold = loadTypeface(context, R.font.vazirmatn_bold)

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

        // Paint برای عنوان‌ها (regular، کوچک‌تر)
        val titlePaint = TextPaint().apply {
            color = TITLE_COLOR; textSize = 15f; typeface = fontBold; isAntiAlias = true
        }
        val subtitlePaint = TextPaint().apply {
            color = TEXT_DARK; textSize = 10f; typeface = fontRegular; isAntiAlias = true
        }
        val summaryPaint = TextPaint().apply {
            color = TEXT_MUTED; textSize = 9f; typeface = fontRegular; isAntiAlias = true
        }

        // Paint برای هدر ستون‌ها (regular، کوچک)
        val headerPaint = TextPaint().apply {
            color = TEXT_DARK; textSize = 7f; typeface = fontBold; isAntiAlias = true
        }

        // Paint برای نام محصول (regular، کوچک)
        val namePaint = TextPaint().apply {
            color = TEXT_DARK; textSize = 7f; typeface = fontRegular; isAntiAlias = true
        }

        // Paint برای مقادیر عددی (BOLD، بزرگ‌تر)
        val valuePaint = TextPaint().apply {
            color = TEXT_DARK; textSize = 8.5f; typeface = fontBold; isAntiAlias = true
        }

        // Paint برای توضیحات و متن‌های غیرعددی
        val textPaint = TextPaint().apply {
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
        val linePaint = Paint().apply { color = BORDER_COLOR; strokeWidth = 1f; isAntiAlias = true }

        val document = PdfDocument()

        val tableWidth = PAGE_WIDTH - 2 * MARGIN
        val columnWidths = COLUMN_WEIGHTS.map { it * tableWidth }
        val tableRightX = PAGE_WIDTH - MARGIN
        val tableStartY = MARGIN + 85f

        val totalCount = products.size
        val registeredCount = stats.size
        val varianceCount = stats.count { it.closingVariance != 0 }
        val remainingCount = (totalCount - registeredCount).coerceAtLeast(0)

        // حالت خالی
        if (products.isEmpty()) {
            val pageInfo = PdfDocument.PageInfo.Builder(PAGE_WIDTH, PAGE_HEIGHT, 1).create()
            val page = document.startPage(pageInfo)
            val canvas = page.canvas
            drawCenteredText(canvas, "گزارش آمارگیری و مغایرت‌گیری دامداران", PAGE_WIDTH / 2f, MARGIN + 20f, titlePaint)
            drawCenteredText(canvas, "گردش کار: $workflowName    |    تاریخ: $jalaliDisplay", PAGE_WIDTH / 2f, MARGIN + 40f, subtitlePaint)
            drawCenteredText(canvas, "هیچ محصولی در این گردش کار ثبت نشده است.", PAGE_WIDTH / 2f, PAGE_HEIGHT / 2f, textPaint)
            drawCenteredText(canvas, "صفحه ۱ از ۱", PAGE_WIDTH / 2f, PAGE_HEIGHT - 20f, footerPaint)
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
                drawCenteredText(canvas, "گزارش آمارگیری و مغایرت‌گیری دامداران", PAGE_WIDTH / 2f, MARGIN + 12f, titlePaint)
                drawCenteredText(canvas, "گردش کار: $workflowName    |    تاریخ: $jalaliDisplay", PAGE_WIDTH / 2f, MARGIN + 30f, subtitlePaint)
                drawCenteredText(canvas,
                    "کل: ${toPersian(totalCount)}  |  ثبت‌شده: ${toPersian(registeredCount)}  |  " +
                    "باقی‌مانده: ${toPersian(remainingCount)}  |  مغایرت: ${toPersian(varianceCount)}",
                    PAGE_WIDTH / 2f, MARGIN + 46f, summaryPaint)
                canvas.drawLine(MARGIN, MARGIN + 58f, PAGE_WIDTH - MARGIN, MARGIN + 58f, linePaint)
            } else {
                drawCenteredText(canvas, "گردش کار: $workflowName    |    تاریخ: $jalaliDisplay", PAGE_WIDTH / 2f, MARGIN + 12f, subtitlePaint)
                canvas.drawLine(MARGIN, MARGIN + 22f, PAGE_WIDTH - MARGIN, MARGIN + 22f, linePaint)
            }

            // ===================== جدول =====================
            var y = tableStartY

            // هدر جدول
            fillPaint.color = HEADER_BG
            canvas.drawRect(MARGIN, y, tableRightX, y + HEADER_HEIGHT, fillPaint)

            var xRight = tableRightX
            for (i in COLUMN_HEADERS.indices) {
                val colWidth = columnWidths[i]
                val colLeft = xRight - colWidth
                canvas.drawLine(xRight, y, xRight, y + HEADER_HEIGHT, borderPaint)
                drawRtlCellText(canvas, COLUMN_HEADERS[i], colLeft, y, colWidth, HEADER_HEIGHT, headerPaint, false)
                xRight = colLeft
            }
            canvas.drawLine(xRight, y, xRight, y + HEADER_HEIGHT, borderPaint)
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

                if (rowIndexDisplay % 2 == 0) {
                    fillPaint.color = ROW_ALT_BG
                    canvas.drawRect(MARGIN, y, tableRightX, y + ROW_HEIGHT, fillPaint)
                }

                // محاسبه ستون‌های مشتق‌شده
                val openingTotal = stat?.openingStockTotal ?: 0
                val receiptTotal = stat?.receiptTotal ?: 0
                val returnsTotal = stat?.returnsTotal ?: 0
                val morningTotal = stat?.morningLoadingTotal ?: 0
                val midDayTotal = stat?.midDayLoadingTotal ?: 0
                val nightTotal = stat?.nightLoadingTotal ?: 0
                val giftsTotal = stat?.giftsTotal ?: 0
                val wasteTotal = stat?.wasteTotal ?: 0

                // موجودی کل = مانده شب گذشته + رسید + برگشت به انبار
                val totalInventory = openingTotal + receiptTotal + returnsTotal
                // تعداد بارگیری شده = بارگیری روز + میان روز + بارگیری شب
                val totalLoaded = morningTotal + midDayTotal + nightTotal

                // مقادیر هر ستون
                val values = if (stat == null) {
                    arrayOf(
                        toPersian(rowIndexDisplay), product.name,
                        "—", "—", "—", "—", "—", "—", "—", "—", "—",
                        "", "ثبت‌نشده", "—"
                    )
                } else {
                    arrayOf(
                        toPersian(rowIndexDisplay),    // 1. ردیف
                        product.name,                  // 2. نام محصول
                        formatBoxUnit(openingTotal, product),        // 3. مانده
                        formatBoxUnit(morningTotal, product),       // 4. تحویل روز
                        formatBoxUnit(receiptTotal, product),       // 5. آزمایشگاه
                        formatBoxUnit(giftsTotal, product),          // 6. متفرقه / هدایا
                        formatBoxUnit(midDayTotal, product),        // 7. میان روز
                        formatBoxUnit(returnsTotal, product),       // 8. برگشت به انبار
                        formatBoxUnit(totalInventory, product),     // 9. موجودی کل
                        formatBoxUnit(totalLoaded, product),        // 10. تعداد بارگیری شده
                        formatBoxUnit(stat.expectedClosingStockTotal, product), // 11. مانده
                        stat.notes ?: "",                            // 12. توضیحات
                        getVarianceLabel(stat.closingVariance),     // 13. کسری یا اضافه
                        formatVarianceValue(stat.closingVariance, product) // 14. مغایرت
                    )
                }

                // رنگ ستون مغایرت (ستون 13 = چپ‌ترین)
                if (stat != null && stat.closingVariance != 0) {
                    val varianceBg = if (stat.closingVariance > 0) DEFICIT_BG else SURPLUS_BG
                    fillPaint.color = varianceBg
                    var vx = tableRightX
                    for (i in 0 until 13) vx -= columnWidths[i]
                    canvas.drawRect(vx - columnWidths[13], y, vx, y + ROW_HEIGHT, fillPaint)
                }

                // رسم سلول‌ها از راست به چپ
                xRight = tableRightX
                for (i in values.indices) {
                    val colWidth = columnWidths[i]
                    val colLeft = xRight - colWidth

                    canvas.drawLine(xRight, y, xRight, y + ROW_HEIGHT, borderPaint)

                    // انتخاب paint مناسب:
                    // ستون 1 (ردیف): valuePaint (بولد، بزرگ)
                    // ستون 2 (نام محصول): namePaint (regular، کوچک)
                    // ستون 12 (توضیحات): textPaint (regular)
                    // ستون 13 (کسری یا اضافه): textPaint (regular)
                    // بقیه: valuePaint (بولد، بزرگ)
                    val cellPaint = when (i) {
                        1 -> namePaint      // نام محصول
                        11 -> textPaint     // توضیحات
                        12 -> textPaint     // کسری یا اضافه
                        else -> valuePaint  // مقادیر عددی (بولد، بزرگ‌تر)
                    }

                    drawRtlCellText(canvas, values[i], colLeft, y, colWidth, ROW_HEIGHT, cellPaint, i == 1)
                    xRight = colLeft
                }
                canvas.drawLine(xRight, y, xRight, y + ROW_HEIGHT, borderPaint)
                canvas.drawLine(MARGIN, y + ROW_HEIGHT, tableRightX, y + ROW_HEIGHT, borderPaint)
                y += ROW_HEIGHT
            }

            currentRow += rowsThisPage

            // ===================== فوتر =====================
            canvas.drawLine(MARGIN, PAGE_HEIGHT - FOOTER_HEIGHT, PAGE_WIDTH - MARGIN, PAGE_HEIGHT - FOOTER_HEIGHT, linePaint)
            val signY = PAGE_HEIGHT - FOOTER_HEIGHT + 10f
            val signWidth = tableWidth / 3f
            val signLabels = arrayOf("نام و امضای آمارگیر:", "توضیحات:", "تأیید مسئول:")

            var sxRight = tableRightX
            for (i in 0..2) {
                val sxLeft = sxRight - signWidth
                canvas.drawRect(sxLeft, signY, sxRight, PAGE_HEIGHT - 22f, borderPaint)
                drawRtlCellText(canvas, signLabels[i], sxLeft, signY, signWidth, 18f, signLabelPaint, true)
                canvas.drawLine(sxLeft + 8f, PAGE_HEIGHT - 35f, sxRight - 8f, PAGE_HEIGHT - 35f, borderPaint)
                sxRight = sxLeft
            }

            drawCenteredText(canvas, "صفحه ${toPersian(pageNum)} از ${toPersian(totalPages)}", PAGE_WIDTH / 2f, PAGE_HEIGHT - 10f, footerPaint)
            document.finishPage(page)
        }

        FileOutputStream(pdfFile).use { out -> document.writeTo(out) }
        document.close()
        return pdfFile
    }

    /**
     * فرمت مقدار به‌صورت: 2B + 2'
     * B = باکس، ' = عدد
     */
    private fun formatBoxUnit(total: Int, product: Product): String {
        if (total == 0) return "0"
        return if (product.isRoundBox) {
            "${total}B"
        } else {
            val capacity = if (product.boxCapacity <= 0) 1 else product.boxCapacity
            val boxes = total / capacity
            val units = total % capacity
            if (units == 0) "${boxes}B"
            else "${boxes}B + ${units}'"
        }
    }

    /**
     * برچسب کسری یا اضافه.
     */
    private fun getVarianceLabel(variance: Int): String {
        return when {
            variance == 0 -> "صفر"
            variance > 0 -> "کسری"
            else -> "اضافه"
        }
    }

    /**
     * مقدار مغایرت با فرمت B + '
     */
    private fun formatVarianceValue(variance: Int, product: Product): String {
        if (variance == 0) return "0"
        val absValue = if (variance > 0) variance else -variance
        return if (product.isRoundBox) {
            "${absValue}B"
        } else {
            val capacity = if (product.boxCapacity <= 0) 1 else product.boxCapacity
            val boxes = absValue / capacity
            val units = absValue % capacity
            if (units == 0) "${boxes}B"
            else "${boxes}B + ${units}'"
        }
    }

    private fun drawRtlCellText(
        canvas: Canvas, text: String, cellLeft: Float, cellTop: Float,
        cellWidth: Float, cellHeight: Float, paint: TextPaint, isRightAligned: Boolean
    ) {
        if (text.isEmpty()) return
        val padding = 3f
        val availableWidth = (cellWidth - 2 * padding).toInt().coerceAtLeast(1)
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

    private fun drawCenteredText(canvas: Canvas, text: String, centerX: Float, y: Float, paint: TextPaint) {
        val textWidth = paint.measureText(text)
        canvas.drawText(text, centerX - textWidth / 2f, y + paint.textSize, paint)
    }

    private fun toPersian(value: Int): String {
        val persianDigits = arrayOf('۰', '۱', '۲', '۳', '۴', '۵', '۶', '۷', '۸', '۹')
        return value.toString().map { c -> if (c.isDigit()) persianDigits[c - '0'] else c }.joinToString("")
    }

    private fun loadTypeface(context: Context, fontResId: Int): Typeface {
        return try {
            ResourcesCompat.getFont(context, fontResId) ?: Typeface.DEFAULT
        } catch (e: Exception) { Typeface.DEFAULT }
    }

    private fun sanitizeFileName(name: String): String {
        return name.replace(" ", "_").replace("/", "-").replace("\\", "-")
            .replace(":", "-").replace("*", "-").replace("?", "-")
            .replace("\"", "-").replace("<", "-").replace(">", "-")
            .replace("|", "-").trim()
    }

    fun validateFile(file: File): Boolean = file.exists() && file.length() > 0 && file.canRead()

    fun getFileUri(context: Context, file: File): Uri =
        FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)

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
            type = mimeType; putExtra(Intent.EXTRA_STREAM, uri)
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
                    input.copyTo(output); output.flush()
                } ?: return false
            }
            true
        } catch (e: Exception) { false }
    }
}
