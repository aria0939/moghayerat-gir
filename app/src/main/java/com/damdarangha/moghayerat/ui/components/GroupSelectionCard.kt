package com.damdarangha.moghayerat.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.damdarangha.moghayerat.domain.model.ProductGroup

/**
 * کارت بزرگ انتخاب گروه محصول در صفحه اصلی.
 */
@Composable
fun GroupSelectionCard(
    group: ProductGroup,
    activeProductCount: Int,
    lastStatStatus: String?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val (icon, color) = when (group) {
        ProductGroup.STERILE -> Icons.Default.WaterDrop to Color(0xFF2E7D32)
        ProductGroup.TUB -> Icons.Default.Inventory2 to Color(0xFF1565C0)
        ProductGroup.DATE -> Icons.Default.Spa to Color(0xFF6A1B9A)
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(120.dp),
        onClick = onClick,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Icon(
                imageVector = icon,
                contentDescription = group.displayName,
                tint = color,
                modifier = Modifier.size(36.dp)
            )
            Text(
                text = group.displayName,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "${activeProductCount.toPersianDigits()} محصول فعال",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                if (lastStatStatus != null) {
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = lastStatStatus,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}

private fun Int.toPersianDigits(): String {
    val persianDigits = arrayOf('۰', '۱', '۲', '۳', '۴', '۵', '۶', '۷', '۸', '۹')
    return this.toString().map { c ->
        if (c.isDigit()) persianDigits[c - '0'] else c
    }.joinToString("")
}
