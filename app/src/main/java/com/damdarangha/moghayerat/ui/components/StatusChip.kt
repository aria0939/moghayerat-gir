package com.damdarangha.moghayerat.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.damdarangha.moghayerat.domain.model.StatusType
import com.damdarangha.moghayerat.ui.theme.statusColors

/**
 * کامپوننت چیپ وضعیت - برای نمایش وضعیت مغایرت.
 *
 * ویژگی‌ها:
 * - رنگ‌بندی متفاوت برای هر وضعیت
 * - آیکون و متن همزمان (رنگ تنها معیار نیست)
 * - شکل گرد و خوانا
 * - سازگار با تم روشن و تاریک
 */
@Composable
fun StatusChip(
    statusType: StatusType,
    modifier: Modifier = Modifier,
    label: String? = null
) {
    val statusColors = statusColors()
    val (bgColor: Color, fgColor: Color, icon: ImageVector) = when (statusType) {
        StatusType.ZERO -> Triple(
            statusColors.successBg,
            statusColors.success,
            Icons.Filled.CheckCircle
        )
        StatusType.DEFICIT -> Triple(
            statusColors.deficitBg,
            statusColors.deficit,
            Icons.Filled.ArrowDownward
        )
        StatusType.SURPLUS -> Triple(
            statusColors.surplusBg,
            statusColors.surplus,
            Icons.Filled.ArrowUpward
        )
        StatusType.INCOMPLETE -> Triple(
            statusColors.warningBg,
            statusColors.warning,
            Icons.Filled.Warning
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
