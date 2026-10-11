package com.damdarangha.moghayerat.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import com.damdarangha.moghayerat.domain.model.StatusType

/**
 * کامپوننت چیپ وضعیت - برای نمایش وضعیت مغایرت.
 *
 * ویژگی‌ها:
 * - رنگ‌بندی متفاوت برای هر وضعیت
 * - آیکون و متن همزمان (رنگ تنها معیار نیست)
 * - شکل گرد و خوانا
 */
@Composable
fun StatusChip(
    statusType: StatusType,
    modifier: Modifier = Modifier,
    label: String? = null
) {
    val (bgColor, fgColor, icon) = when (statusType) {
        StatusType.ZERO -> Triple(
            com.damdarangha.moghayerat.ui.theme.SuccessGreenLight,
            com.damdarangha.moghayerat.ui.theme.SuccessGreen,
            androidx.compose.material.icons.Icons.Default.CheckCircle
        )
        StatusType.DEFICIT -> Triple(
            com.damdarangha.moghayerat.ui.theme.DeficitRedLight,
            com.damdarangha.moghayerat.ui.theme.DeficitRed,
            androidx.compose.material.icons.Icons.Default.ArrowDownward
        )
        StatusType.SURPLUS -> Triple(
            com.damdarangha.moghayerat.ui.theme.SurplusBlueLight,
            com.damdarangha.moghayerat.ui.theme.SurplusBlue,
            androidx.compose.material.icons.Icons.Default.ArrowUpward
        )
        StatusType.INCOMPLETE -> Triple(
            com.damdarangha.moghayerat.ui.theme.WarningOrangeLight,
            com.damdarangha.moghayerat.ui.theme.WarningOrange,
            androidx.compose.material.icons.Icons.Default.Warning
        )
    }

    val displayLabel = label ?: statusType.label

    Box(
        modifier = modifier
            .background(
                color = bgColor,
                shape = RoundedCornerShape(8.dp)
            )
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = icon,
                contentDescription = displayLabel,
                tint = fgColor,
                modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = displayLabel,
                color = fgColor,
                style = MaterialTheme.typography.labelMedium
            )
        }
    }
}
