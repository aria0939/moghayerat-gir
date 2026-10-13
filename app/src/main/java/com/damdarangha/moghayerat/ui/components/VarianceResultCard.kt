package com.damdarangha.moghayerat.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.damdarangha.moghayerat.domain.calculation.VarianceCalculator
import com.damdarangha.moghayerat.domain.model.Product
import com.damdarangha.moghayerat.domain.model.VarianceResult
import com.damdarangha.moghayerat.domain.model.toPersianDigits

/**
 * کارت نتیجه مغایرت‌گیری.
 *
 * این کارت مهم‌ترین بخش رابط کاربری است و شامل:
 * - موجودی مورد انتظار پایان روز
 * - موجودی واقعی پایان روز
 * - میزان کسری یا اضافه
 * - نتیجه مغایرت اول کار (در صورت ورود)
 * - وضعیت کلی محصول
 *
 * مقادیر هم به شکل باکس و عدد و هم به شکل تعداد کل نمایش داده می‌شوند.
 */
@Composable
fun VarianceResultCard(
    result: VarianceResult,
    product: Product,
    modifier: Modifier = Modifier
) {
    val statusColors = com.damdarangha.moghayerat.ui.theme.statusColors()
    val (cardBg, cardFg) = when (result.closingStatusType) {
        com.damdarangha.moghayerat.domain.model.StatusType.ZERO ->
            statusColors.successBg to statusColors.success
        com.damdarangha.moghayerat.domain.model.StatusType.DEFICIT ->
            statusColors.deficitBg to statusColors.deficit
        com.damdarangha.moghayerat.domain.model.StatusType.SURPLUS ->
            statusColors.surplusBg to statusColors.surplus
        else -> Color.Gray to Color.Black
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = cardBg),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "نتیجه مغایرت‌گیری",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = cardFg
            )
            Spacer(modifier = Modifier.height(8.dp))

            // پیام وضعیت اصلی
            Text(
                text = result.closingStatusText,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = cardFg,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(16.dp))

            // مغایرت پایان روز به باکس و عدد
            val variancePair = if (result.closingVariance >= 0) {
                VarianceCalculator.fromTotalUnits(result.closingVariance, product)
            } else {
                VarianceCalculator.fromTotalUnits(-result.closingVariance, product)
            }

            if (result.closingVariance != 0) {
                if (product.isRoundBox) {
                    Text(
                        text = "معادل: ${variancePair.boxes.toPersianDigits()} باکس",
                        style = MaterialTheme.typography.titleMedium,
                        color = cardFg
                    )
                } else {
                    Text(
                        text = "معادل: ${variancePair.boxes.toPersianDigits()} باکس و ${variancePair.units.toPersianDigits()} عدد",
                        style = MaterialTheme.typography.titleMedium,
                        color = cardFg
                    )
                    Text(
                        text = "(مجموع ${result.closingVariance.absoluteValue.toPersianDigits()} عدد)",
                        style = MaterialTheme.typography.bodySmall,
                        color = cardFg
                    )
                }
            }

            Divider(
                modifier = Modifier.padding(vertical = 12.dp),
                color = cardFg.copy(alpha = 0.2f)
            )

            // جدول مقادیر
            ResultRow(
                label = "موجودی مورد انتظار پایان روز",
                value = result.expectedClosingStockTotal.toPersianDigits(),
                product = product,
                total = result.expectedClosingStockTotal,
                color = MaterialTheme.colorScheme.onSurface
            )
            ResultRow(
                label = "موجودی واقعی پایان روز",
                value = (result.currentStockTotal ?: 0).toPersianDigits(),
                product = product,
                total = result.currentStockTotal ?: 0,
                color = MaterialTheme.colorScheme.onSurface
            )

            // مغایرت اول کار (در صورت ورود)
            AnimatedVisibility(
                visible = result.morningVariance != null,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Column {
                    Divider(
                        modifier = Modifier.padding(vertical = 8.dp),
                        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                    )
                    Text(
                        text = "مغایرت اول کار",
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    val morningStatus = when {
                        result.morningVariance == 0 -> "مطابقت دارد"
                        result.morningVariance!! > 0 -> "کسری ${result.morningVariance.toPersianDigits()} عدد"
                        else -> "اضافه ${(-result.morningVariance).toPersianDigits()} عدد"
                    }
                    Text(
                        text = morningStatus,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}

@Composable
private fun ResultRow(
    label: String,
    value: String,
    product: Product,
    total: Int,
    color: Color
) {
    val pair = VarianceCalculator.fromTotalUnits(total, product)
    val displayValue = if (product.isRoundBox) {
        "${pair.boxes.toPersianDigits()} باکس"
    } else {
        "${pair.boxes.toPersianDigits()} باکس و ${pair.units.toPersianDigits()} عدد"
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = color
        )
        Text(
            text = displayValue,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Medium,
            color = color
        )
    }
}

private val Int.absoluteValue: Int get() = if (this < 0) -this else this
