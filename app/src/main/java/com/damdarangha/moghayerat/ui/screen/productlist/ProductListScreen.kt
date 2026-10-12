package com.damdarangha.moghayerat.ui.screen.productlist

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.compose.runtime.DisposableEffect
import androidx.lifecycle.viewmodel.compose.viewModel
import com.damdarangha.moghayerat.MoghayeratApplication
import com.damdarangha.moghayerat.domain.model.Product
import com.damdarangha.moghayerat.domain.model.StatStatus
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.statusBars

/**
 * صفحه فهرست محصولات یک گردش کار.
 *
 * نمایش:
 * - نام گردش کار
 * - تعداد کل محصولات، ثبت‌شده، باقی‌مانده و درصد پیشرفت
 * - فیلد جست‌وجو
 * - فهرست محصولات با وضعیت ثبت
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductListScreen(
    workflowId: Long,
    onProductClick: (Product) -> Unit,
    onBack: () -> Unit,
    viewModel: ProductListViewModel = viewModel(
        factory = ProductListViewModel.factory(MoghayeratApplication.instance, workflowId)
    )
) {
    val state by viewModel.uiState.collectAsState()

    // به‌روزرسانی وضعیت‌ها وقتی کاربر از صفحه ثبت آمار برمی‌گردد
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.refreshStatStatuses()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(state.workflowName.ifEmpty { "محصولات" }) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "بازگشت")
                    }
                },
                windowInsets = WindowInsets.statusBars
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            // ===== کارت آمار =====
            val total = state.products.size
            val registered = state.todayStatsCount
            val remaining = (total - registered).coerceAtLeast(0)
            val progress = if (total > 0) registered.toFloat() / total else 0f

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        StatBox("کل محصولات", total)
                        StatBox("ثبت‌شده", registered)
                        StatBox("باقی‌مانده", remaining)
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    LinearProgressIndicator(
                        progress = { progress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = MaterialTheme.colorScheme.primary,
                        trackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "پیشرفت: ${(progress * 100).toInt().toPersian()}%",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // ===== جست‌وجو =====
            OutlinedTextField(
                value = state.searchQuery,
                onValueChange = viewModel::updateSearchQuery,
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("جست‌وجوی محصول...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            if (state.products.isEmpty() && !state.isLoading) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "هیچ محصول فعالی در این گردش کار وجود ندارد.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(state.products, key = { it.id }) { product ->
                        ProductItemCard(
                            product = product,
                            statStatus = state.statStatuses[product.id] ?: StatStatus.NOT_REGISTERED,
                            onClick = { onProductClick(product) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun StatBox(label: String, value: Int) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value.toPersian(),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun ProductItemCard(
    product: Product,
    statStatus: StatStatus,
    onClick: () -> Unit
) {
    val (statusColor, statusIcon, statusText) = when (statStatus) {
        StatStatus.NOT_REGISTERED -> Triple(Color(0xFF9CA3AF), Icons.Default.Add, "ثبت‌نشده")
        StatStatus.DRAFT -> Triple(Color(0xFFFFBC4D), Icons.Default.Edit, "پیش‌نویس")
        StatStatus.REGISTERED -> Triple(Color(0xFF4CAF50), Icons.Default.CheckCircle, "ثبت‌شده")
        StatStatus.HAS_VARIANCE -> Triple(Color(0xFFFA727D), Icons.Default.Warning, "دارای مغایرت")
        StatStatus.FINALIZED -> Triple(MaterialTheme.colorScheme.primary, Icons.Default.Lock, "نهایی‌شده")
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(1.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // دایره وضعیت
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(statusColor.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = statusIcon,
                    contentDescription = statusText,
                    tint = statusColor,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.size(12.dp))

            // نام و ظرفیت
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = product.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                val capacityText = if (product.isRoundBox) "باکس رند"
                else "باکس ${product.boxCapacity.toPersian()} عددی"
                Text(
                    text = capacityText,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // برچسب وضعیت
            Card(
                colors = CardDefaults.cardColors(containerColor = statusColor.copy(alpha = 0.15f)),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = statusText,
                    style = MaterialTheme.typography.labelSmall,
                    color = statusColor,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
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
