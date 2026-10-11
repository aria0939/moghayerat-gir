package com.damdarangha.moghayerat.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.damdarangha.moghayerat.domain.model.BoxAndUnits
import com.damdarangha.moghayerat.domain.model.Product
import com.damdarangha.moghayerat.domain.model.persianToEnglishDigits

/**
 * کامپوننت ورودی باکس و عدد.
 *
 * ویژگی‌ها:
 * - دو فیلد جداگانه برای باکس و عدد
 * - تبدیل خودکار اعداد فارسی به انگلیسی
 * - صفحه‌کلید عددی
 * - برای محصول با باکس رند، فیلد عدد غیرفعال می‌شود
 * - نمایش معادل کل عددی در صورت نیاز
 *
 * @param label برچسب فیلد (مثلاً "مانده شب گذشته")
 * @param value مقدار فعلی
 * @param product محصول موردنظر
 * @param onValueChange تابع فراخوانی هنگام تغییر مقدار
 * @param showTotal نمایش تعداد کل (اختیاری)
 * @param modifier Modifier
 * @param enabled آیا فیلد فعال است
 */
@Composable
fun BoxUnitInput(
    label: String,
    value: BoxAndUnits,
    product: Product,
    onValueChange: (BoxAndUnits) -> Unit,
    modifier: Modifier = Modifier,
    showTotal: Boolean = true,
    enabled: Boolean = true
) {
    val isRoundBox = product.isRoundBox

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = label,
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                // فیلد باکس
                OutlinedTextField(
                    value = if (value.boxes == 0) "" else value.boxes.toString(),
                    onValueChange = { input ->
                        val normalized = input.persianToEnglishDigits()
                        val boxes = normalized.toIntOrNull() ?: 0
                        if (boxes >= 0) {
                            onValueChange(BoxAndUnits(boxes = boxes, units = value.units))
                        }
                    },
                    label = { Text("باکس") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    enabled = enabled,
                    modifier = Modifier.weight(1f),
                    textStyle = LocalTextStylePadded()
                )

                Spacer(modifier = Modifier.width(4.dp))

                // فیلد عدد (برای باکس رند غیرفعال می‌شود)
                OutlinedTextField(
                    value = if (value.units == 0) "" else value.units.toString(),
                    onValueChange = { input ->
                        if (isRoundBox) return@OutlinedTextField
                        val normalized = input.persianToEnglishDigits()
                        val units = normalized.toIntOrNull() ?: 0
                        if (units >= 0) {
                            onValueChange(BoxAndUnits(boxes = value.boxes, units = units))
                        }
                    },
                    label = { Text("عدد") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    enabled = enabled && !isRoundBox,
                    modifier = Modifier.weight(1f),
                    textStyle = LocalTextStylePadded()
                )
            }

            // نمایش ظرفیت باکس و معادل کل
            if (!isRoundBox && showTotal) {
                Spacer(modifier = Modifier.height(6.dp))
                val total = value.boxes * product.boxCapacity + value.units
                Text(
                    text = "ظرفیت باکس: ${product.boxCapacity.toPersianDigitsString()} | معادل کل: ${total.toPersianDigitsString()} عدد",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.End,
                    modifier = Modifier.fillMaxWidth()
                )
            } else if (isRoundBox) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "این محصول با باکس رند ثبت می‌شود",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.End,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
private fun LocalTextStylePadded() = MaterialTheme.typography.bodyLarge.copy(
    textAlign = TextAlign.End
)

// تابع کمکی برای نمایش اعداد فارسی
private fun Int.toPersianDigitsString(): String {
    val persianDigits = arrayOf('۰', '۱', '۲', '۳', '۴', '۵', '۶', '۷', '۸', '۹')
    return this.toString().map { c ->
        if (c.isDigit()) persianDigits[c - '0'] else c
    }.joinToString("")
}
