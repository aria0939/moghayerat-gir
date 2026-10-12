package com.damdarangha.moghayerat.ui.screen.reports

import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.damdarangha.moghayerat.MoghayeratApplication
import com.damdarangha.moghayerat.data.db.entity.DailyStatEntity
import com.damdarangha.moghayerat.ui.components.StatusChip
import com.damdarangha.moghayerat.domain.model.StatusType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportsScreen(
    onBack: () -> Unit,
    viewModel: ReportsViewModel = viewModel(
        factory = ReportsViewModel.factory(MoghayeratApplication.instance)
    )
) {
    val state by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    var showPdfDialog by remember { mutableStateOf(false) }
    var showMissingWarning by remember { mutableStateOf(false) }

    // به‌روزرسانی گزارش وقتی کاربر از صفحه ثبت آمار برمی‌گردد
    val lifecycleOwner = androidx.compose.ui.platform.LocalLifecycleOwner.current
    androidx.compose.runtime.DisposableEffect(lifecycleOwner) {
        val observer = androidx.lifecycle.LifecycleEventObserver { _, event ->
            if (event == androidx.lifecycle.Lifecycle.Event.ON_RESUME) {
                viewModel.refreshReport()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    LaunchedEffect(state.message) {
        state.message?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearMessage()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("گزارش‌ها") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "بازگشت")
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            // انتخاب گردش کار
            if (state.workflows.isNotEmpty()) {
                Text(
                    text = "انتخاب گردش کار:",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    state.workflows.forEach { workflow ->
                        FilterChip(
                            selected = state.selectedWorkflow?.id == workflow.id,
                            onClick = { viewModel.selectWorkflow(workflow) },
                            label = { Text(workflow.name) }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // کارت آمار
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "گزارش روزانه - ${state.jalaliDate}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        SummaryItem("کل محصولات", state.products.size)
                        SummaryItem("ثبت‌شده", state.stats.size)
                        SummaryItem("مغایرت صفر", state.zeroVarianceCount)
                        SummaryItem("کسری", state.deficitCount)
                        SummaryItem("اضافه", state.surplusCount)
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // دکمه خروجی PDF آمارگیری
            Button(
                onClick = {
                    val missing = state.products.size - state.stats.size
                    if (missing > 0) {
                        showMissingWarning = true
                    } else {
                        showPdfDialog = true
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.PictureAsPdf, contentDescription = null)
                Spacer(modifier = Modifier.padding(end = 6.dp))
                Text("خروجی PDF آمارگیری")
            }

            Spacer(modifier = Modifier.height(8.dp))

            // دکمه اشتراک‌گذاری (اگر فایل ساخته شده)
            if (state.reportFile != null) {
                OutlinedButton(
                    onClick = { viewModel.shareReport() },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.Share, contentDescription = null)
                    Spacer(modifier = Modifier.padding(end = 6.dp))
                    Text("اشتراک‌گذاری PDF")
                }
                Spacer(modifier = Modifier.height(8.dp))
            }

            Spacer(modifier = Modifier.height(8.dp))

            // فهرست آمار یا پیام خالی
            if (state.stats.isEmpty()) {
                // پیام مناسب وقتی آماری ثبت نشده
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.PictureAsPdf,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = if (state.selectedWorkflow != null && state.products.isNotEmpty())
                            "هنوز آماری برای امروز ثبت نشده است"
                        else "هنوز گردش کاری انتخاب نشده است",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "با ثبت آمار محصولات، گزارش در اینجا نمایش داده می‌شود",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(state.stats, key = { it.id }) { stat ->
                        val productName = state.products.find { it.id == stat.productId }?.name ?: "نامشخص"
                        ReportItemCard(stat, productName)
                    }
                }
            }
        }
    }

    // دیالوگ انتخاب نوع گزارش PDF
    if (showPdfDialog) {
        var selectedReportType by remember { mutableStateOf(ReportType.FULL) }
        AlertDialog(
            onDismissRequest = { showPdfDialog = false },
            title = { Text("نوع گزارش PDF") },
            text = {
                Column {
                    ReportType.entries.forEach { type ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            RadioButton(
                                selected = selectedReportType == type,
                                onClick = { selectedReportType = type }
                            )
                            Text(
                                text = type.label,
                                modifier = Modifier.padding(start = 8.dp)
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showPdfDialog = false
                        viewModel.exportToPdf(selectedReportType)
                    }
                ) { Text("ساخت PDF") }
            },
            dismissButton = {
                TextButton(onClick = { showPdfDialog = false }) {
                    Text("انصراف")
                }
            }
        )
    }

    // هشدار محصولات ثبت‌نشده
    if (showMissingWarning) {
        val missing = state.products.size - state.stats.size
        AlertDialog(
            onDismissRequest = { showMissingWarning = false },
            title = { Text("هشدار: محصولات ثبت‌نشده") },
            text = {
                Text(
                    "$missing محصول هنوز ثبت نشده است.\n\n" +
                    "می‌توانید گزارش موقت تهیه کنید یا به تکمیل محصولات برگردید."
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showMissingWarning = false
                        showPdfDialog = true
                    }
                ) { Text("گزارش موقت") }
            },
            dismissButton = {
                TextButton(onClick = { showMissingWarning = false }) {
                    Text("بازگشت")
                }
            }
        )
    }
}

@Composable
private fun SummaryItem(label: String, value: Int) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value.toPersian(),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 10.sp
        )
    }
}

@Composable
private fun ReportItemCard(stat: DailyStatEntity, productName: String) {
    val statusType = when {
        stat.closingVariance == 0 -> StatusType.ZERO
        stat.closingVariance > 0 -> StatusType.DEFICIT
        else -> StatusType.SURPLUS
    }
    val varianceText = when {
        stat.closingVariance == 0 -> "صفر"
        stat.closingVariance > 0 -> "کسری ${stat.closingVariance.toPersian()}"
        else -> "اضافه ${(-stat.closingVariance).toPersian()}"
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
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
                StatusChip(statusType = statusType, label = varianceText)
            }
            Spacer(modifier = Modifier.height(8.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                ReportValueItem("مانده شب گذشته", stat.openingStockTotal)
                ReportValueItem("بارگیری صبح", stat.morningLoadingTotal)
                ReportValueItem("رسید", stat.receiptTotal)
            }
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                ReportValueItem("برگشت به انبار", stat.returnsTotal)
                ReportValueItem("میان روز", stat.midDayLoadingTotal)
                ReportValueItem("بارگیری شب", stat.nightLoadingTotal)
            }
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                ReportValueItem("هدایا", stat.giftsTotal)
                ReportValueItem("ضایعات", stat.wasteTotal)
                ReportValueItem("مورد انتظار", stat.expectedClosingStockTotal)
            }
            Spacer(modifier = Modifier.height(8.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                ReportValueItem("واقعی", stat.currentStockTotal)
                ReportValueItem("مغایرت", stat.closingVariance)
            }
        }
    }
}

@Composable
private fun ReportValueItem(label: String, value: Int) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value.toPersian(),
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium
        )
    }
}

private fun Int.toPersian(): String {
    val persianDigits = arrayOf('۰', '۱', '۲', '۳', '۴', '۵', '۶', '۷', '۸', '۹')
    return this.toString().map { c ->
        if (c.isDigit()) persianDigits[c - '0'] else c
    }.joinToString("")
}
