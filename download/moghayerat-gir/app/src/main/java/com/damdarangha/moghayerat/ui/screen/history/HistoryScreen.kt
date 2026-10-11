package com.damdarangha.moghayerat.ui.screen.history

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.damdarangha.moghayerat.MoghayeratApplication
import com.damdarangha.moghayerat.data.db.entity.DailyStatEntity
import com.damdarangha.moghayerat.ui.components.StatusChip
import com.damdarangha.moghayerat.domain.model.StatusType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    onStatClick: (statId: Long, productId: Long, isoDate: String) -> Unit,
    onBack: () -> Unit,
    viewModel: HistoryViewModel = viewModel(
        factory = HistoryViewModel.factory(MoghayeratApplication.instance)
    )
) {
    val state by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("تاریخچه آمارها") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "بازگشت")
                    }
                },
                windowInsets = WindowInsets.statusBars
            )
        }
    ) { padding ->
        if (state.stats.isEmpty() && !state.isLoading) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "هنوز آماری ثبت نشده است.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            return@Scaffold
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(state.stats, key = { it.id }) { stat ->
                val productName = state.products[stat.productId]?.name ?: "نامشخص"
                HistoryItemCard(
                    stat = stat,
                    productName = productName,
                    onClick = { onStatClick(stat.id, stat.productId, stat.isoDate) }
                )
            }
        }
    }
}

@Composable
private fun HistoryItemCard(
    stat: DailyStatEntity,
    productName: String,
    onClick: () -> Unit
) {
    val statusType = when {
        stat.closingVariance == 0 -> StatusType.ZERO
        stat.closingVariance > 0 -> StatusType.DEFICIT
        else -> StatusType.SURPLUS
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(1.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = productName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                StatusChip(statusType = statusType)
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "تاریخ: ${stat.jalaliDate}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(4.dp))
            val varianceText = when {
                stat.closingVariance == 0 -> "بدون مغایرت"
                stat.closingVariance > 0 -> "کسری ${stat.closingVariance.toPersian()} عدد"
                else -> "اضافه ${(-stat.closingVariance).toPersian()} عدد"
            }
            Text(
                text = varianceText,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary
            )
            if (stat.isFinalized) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "(نهایی شده)",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

private fun Int.toPersian(): String {
    val persianDigits = arrayOf('۰', '۱', '۲', '۳', '۴', '۵', '۶', '۷', '۸', '۹')
    return this.toString().map { c ->
        if (c.isDigit()) persianDigits[c - '0'] else c
    }.joinToString("")
}
