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
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
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
import androidx.compose.ui.platform.LocalLifecycleOwner
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
    onProductClick: (Product, String) -> Unit,  // product + stage ("START" یا "END")
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
            val registered = state.startCount
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
                        StatBox("اول کار", state.startCount)
                        StatBox("آخر کار", state.endCount)
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

            Spacer(modifier = Modifier.height(8.dp))

            // دکمه بررسی پایان شیفت
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "بررسی پایان شیفت",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Switch(
                    checked = state.shiftReviewEnabled,
                    onCheckedChange = { viewModel.toggleShiftReview() }
                )
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
                        val statInfo = state.statInfos[product.id]
                        ProductItemCard(
                            product = product,
                            statInfo = statInfo,
                            onStartClick = { onProductClick(product, "START") },
                            onEndClick = { onProductClick(product, "END") }
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
    statInfo: ProductStatInfo?,
    onStartClick: () -> Unit,
    onEndClick: () -> Unit
) {
    val status = statInfo?.status ?: StatStatus.NOT_REGISTERED
    val (statusColor, statusIcon, statusText) = when (status) {
        StatStatus.NOT_REGISTERED -> Triple(Color(0xFF9CA3AF), Icons.Default.Add, "ثبت‌نشده")
        StatStatus.DRAFT -> Triple(Color(0xFFFFBC4D), Icons.Default.Edit, "پیش‌نویس")
        StatStatus.REGISTERED -> Triple(Color(0xFF4CAF50), Icons.Default.CheckCircle, "ثبت‌شده")
        StatStatus.HAS_VARIANCE -> Triple(Color(0xFFFA727D), Icons.Default.Warning, "دارای مغایرت")
        StatStatus.FINALIZED -> Triple(MaterialTheme.colorScheme.primary, Icons.Default.Lock, "نهایی‌شده")
    }

    // رنگ مغایرت شیفت شب
    val nightVariance = statInfo?.nightShiftVariance
    val nightColor = when {
        nightVariance == null -> Color(0xFF9CA3AF)  // خاکستری - نامشخص
        nightVariance == 0 -> Color(0xFF4CAF50)     // سبز - مطابقت
        nightVariance < 0 -> Color(0xFFEF5350)     // قرمز - کسری
        else -> Color(0xFF42A5F5)                   // آبی - اضافه
    }

    // متن مغایرت اول کار - با VarianceFormatter
    val startVariance = statInfo?.startVariance
    val startText = if (startVariance != null) {
        com.damdarangha.moghayerat.ui.screen.reports.VarianceFormatter.formatVariance(
            startVariance, product, isRegistered = true
        )
    } else "اول کار"

    // متن مغایرت آخر کار - با VarianceFormatter
    val endVariance = statInfo?.endCumulativeVariance
    val endText = if (endVariance != null) {
        com.damdarangha.moghayerat.ui.screen.reports.VarianceFormatter.formatVariance(
            endVariance, product, isRegistered = true
        )
    } else "آخر کار"

    // متن مغایرت شیفت شب - با VarianceFormatter
    val nightText = if (nightVariance != null) {
        com.damdarangha.moghayerat.ui.screen.reports.VarianceFormatter.formatVariance(
            nightVariance, product, isRegistered = true
        )
    } else "—"

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(1.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            // ردیف اول: آیکون وضعیت + نام محصول + برچسب وضعیت
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(statusColor.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = statusIcon,
                        contentDescription = statusText,
                        tint = statusColor,
                        modifier = Modifier.size(16.dp)
                    )
                }

                Spacer(modifier = Modifier.size(8.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = product.name,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontSize = 14.sp
                    )
                    val capacityText = if (product.isRoundBox) "باکس رند"
                    else "باکس ${product.boxCapacity.toPersian()} عددی"
                    Text(
                        text = capacityText,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 10.sp
                    )
                }

                Card(
                    colors = CardDefaults.cardColors(containerColor = statusColor.copy(alpha = 0.15f)),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = statusText,
                        style = MaterialTheme.typography.labelSmall,
                        color = statusColor,
                        fontWeight = FontWeight.Medium,
                        fontSize = 10.sp,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // ردیف دوم: اول کار | آخر کار | مغایرت شب
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // دکمه اول کار
                OutlinedButton(
                    onClick = onStartClick,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 4.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = startText,
                        style = MaterialTheme.typography.labelMedium,
                        fontSize = 11.sp,
                        fontWeight = if (startVariance != null) FontWeight.Bold else FontWeight.Normal
                    )
                }

                // دکمه آخر کار
                OutlinedButton(
                    onClick = onEndClick,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 4.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = endText,
                        style = MaterialTheme.typography.labelMedium,
                        fontSize = 11.sp,
                        fontWeight = if (endVariance != null) FontWeight.Bold else FontWeight.Normal
                    )
                }

                // نتیجه مغایرت شیفت شب - مهم‌ترین عدد
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(nightColor.copy(alpha = 0.15f))
                        .padding(vertical = 6.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = nightText,
                        style = MaterialTheme.typography.labelLarge,
                        color = nightColor,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }
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
