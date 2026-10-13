package com.damdarangha.moghayerat.ui.screen.stat

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Switch
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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.damdarangha.moghayerat.MoghayeratApplication
import com.damdarangha.moghayerat.domain.model.BoxAndUnits
import com.damdarangha.moghayerat.ui.components.BoxUnitInput
import com.damdarangha.moghayerat.ui.components.LoadingView
import com.damdarangha.moghayerat.ui.components.VarianceResultCard

/**
 * صفحه ثبت آمار محصول.
 *
 * این صفحه شامل تمام فیلدهای آمار روزانه است و نتیجه مغایرت را به‌صورت زنده نمایش می‌دهد.
 *
 * بخش‌ها:
 * ۱. اطلاعات موجودی (مانده شب گذشته، بارگیری صبح، مانده واقعی بعد از بارگیری صبح)
 * ۲. عملیات روزانه (رسید، هدایا، ضایعات، بارگیری شب)
 * ۳. موجودی پایان روز (مانده واقعی حال حاضر)
 * ۴. نتیجه مغایرت‌گیری (کارت بزرگ)
 * ۵. دکمه‌های ذخیره و نهایی‌سازی
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatEntryScreen(
    productId: Long,
    isoDate: String,
    stage: String = "FULL",
    onBack: () -> Unit,
    viewModel: StatEntryViewModel = viewModel(
        factory = StatEntryViewModel.factory(
            MoghayeratApplication.instance, productId, isoDate, StatEntryStage.fromString(stage)
        )
    )
) {
    val isStartStage = viewModel.isStartStage
    val isEndStage = viewModel.isEndStage
    val state by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    // state برای منوی بیش‌تر و دیالوگ‌های تأیید
    var showOverflowMenu by remember { mutableStateOf(false) }
    var showResetDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    // نمایش پیام ذخیره
    LaunchedEffect(state.saveMessage) {
        state.saveMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearSaveMessage()
        }
    }

    // نمایش هشدار
    LaunchedEffect(state.showWarning) {
        state.showWarning?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearWarning()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = when {
                                isStartStage -> "اول کار — ${state.product?.name ?: ""}"
                                isEndStage -> "آخر کار — ${state.product?.name ?: ""}"
                                else -> state.product?.name ?: "آمار"
                            },
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = state.jalaliDate,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "بازگشت")
                    }
                },
                actions = {
                    if (state.isFinalized) {
                        IconButton(onClick = { viewModel.unfinalizeStat() }) {
                            Icon(Icons.Default.LockOpen, contentDescription = "لغو نهایی")
                        }
                    }
                    // منوی بیش‌تر: ریست / حذف کامل
                    IconButton(onClick = { showOverflowMenu = true }) {
                        Icon(Icons.Default.MoreVert, contentDescription = "گزینه‌های بیش‌تر")
                    }
                    DropdownMenu(
                        expanded = showOverflowMenu,
                        onDismissRequest = { showOverflowMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = {
                                Text(
                                    when {
                                        isStartStage -> "پاک‌کردن فیلدهای اول کار"
                                        isEndStage -> "پاک‌کردن فیلدهای آخر کار"
                                        else -> "پاک‌کردن همه فیلدها"
                                    }
                                )
                            },
                            onClick = {
                                showOverflowMenu = false
                                showResetDialog = true
                            },
                            leadingIcon = {
                                Icon(Icons.Default.DeleteSweep, contentDescription = null)
                            }
                        )
                        if (state.statId != null) {
                            DropdownMenuItem(
                                text = { Text("حذف کامل آمار") },
                                onClick = {
                                    showOverflowMenu = false
                                    showDeleteDialog = true
                                },
                                leadingIcon = {
                                    Icon(
                                        Icons.Default.Delete,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.error
                                    )
                                }
                            )
                        }
                    }
                },
                windowInsets = WindowInsets.statusBars
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            if (state.product != null) {
                val saveLabel = when {
                    isStartStage -> "ذخیره اول کار"
                    isEndStage -> "ذخیره آخر کار"
                    else -> "ذخیره پیش‌نویس"
                }
                val finalizeLabel = when {
                    isStartStage -> "نهایی کردن اول کار"
                    isEndStage -> "نهایی کردن آخر کار"
                    else -> "نهایی‌سازی"
                }
                BottomActionButtons(
                    isFinalized = state.isFinalized,
                    isSaving = state.isSaving,
                    saveLabel = saveLabel,
                    finalizeLabel = finalizeLabel,
                    onSaveDraft = { viewModel.saveDraft() },
                    onFinalize = {
                        if (isStartStage) {
                            // در مرحله اول کار، نهایی‌سازی فقط اول کار را ثبت می‌کند
                            viewModel.saveDraft()
                        } else {
                            viewModel.finalizeStat()
                        }
                    }
                )
            }
        }
    ) { padding ->
        // دیالوگ تأیید ریست مرحله
        if (showResetDialog) {
            AlertDialog(
                onDismissRequest = { showResetDialog = false },
                title = { Text("تأیید پاک‌کردن فیلدها") },
                text = {
                    val msg = when {
                        isStartStage -> "تمام فیلدهای اول کار و عملیات روزانه صفر خواهند شد. فیلدهای آخر کار (بارگیری شب و مانده واقعی) دست‌نخورده می‌مانند."
                        isEndStage -> "تمام فیلدهای آخر کار (بارگیری شب، مانده واقعی) و عملیات روزانه صفر خواهند شد. فیلدهای اول کار دست‌نخورده می‌مانند."
                        else -> "تمام فیلدهای فرم صفر خواهند شد."
                    }
                    if (state.isFinalized) {
                        Text("$msg\n\n⚠️ این آمار نهایی شده است. ابتدا نهایی‌سازی لغو می‌شود، سپس فیلدها پاک می‌شوند.")
                    } else {
                        Text(msg)
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            showResetDialog = false
                            viewModel.resetCurrentStage()
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.error
                        )
                    ) { Text("پاک‌کردن") }
                },
                dismissButton = {
                    TextButton(onClick = { showResetDialog = false }) {
                        Text("انصراف")
                    }
                }
            )
        }

        // دیالوگ تأیید حذف کامل
        if (showDeleteDialog) {
            AlertDialog(
                onDismissRequest = { showDeleteDialog = false },
                title = { Text("تأیید حذف کامل آمار") },
                text = {
                    Text("⚠️ این عمل غیرقابل بازگشت است. تمام داده‌های این محصول برای این تاریخ حذف خواهد شد و فرم به حالت اولیه برمی‌گردد.\n\nآیا مطمئن هستید؟")
                },
                confirmButton = {
                    Button(
                        onClick = {
                            showDeleteDialog = false
                            viewModel.deleteStat()
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.error
                        )
                    ) { Text("حذف کامل") }
                },
                dismissButton = {
                    TextButton(onClick = { showDeleteDialog = false }) {
                        Text("انصراف")
                    }
                }
            )
        }

        if (state.isLoading) {
            LoadingView(modifier = Modifier.padding(padding))
            return@Scaffold
        }

        val product = state.product ?: return@Scaffold

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // اطلاعات محصول
            ProductInfoCard(state = state)

            // ===== نمایش فیلدهای اول کار (فقط در START یا FULL) =====
            if (isStartStage || !isEndStage) {
                // بخش ۱: اطلاعات موجودی
                SectionHeader(title = "بخش ۱: تحویل انبار (اول کار)")

                BoxUnitInput(
                    label = "۱. مانده شب گذشته",
                    value = state.openingStock,
                    product = product,
                    onValueChange = viewModel::updateOpeningStock,
                    enabled = !state.isFinalized && !isEndStage  // در END غیرفعال
                )

                BoxUnitInput(
                    label = "۲. بارگیری شهرستان / بارگیری صبح",
                    value = state.morningLoading,
                    product = product,
                    onValueChange = viewModel::updateMorningLoading,
                    enabled = !state.isFinalized && !isEndStage  // در END غیرفعال
                )

                // فیلد شمارش اول کار
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                    ),
                    elevation = CardDefaults.cardElevation(0.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "شمارش واقعی اول کار",
                                style = MaterialTheme.typography.titleSmall,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Switch(
                                checked = state.hasActualMorningStock,
                                onCheckedChange = viewModel::setHasActualMorningStock,
                                enabled = !state.isFinalized && !isEndStage
                            )
                        }
                        if (state.hasActualMorningStock) {
                            Spacer(modifier = Modifier.height(8.dp))
                            BoxUnitInput(
                                label = "۴. مانده واقعی بعد از بارگیری صبح",
                                value = state.actualMorningStock ?: BoxAndUnits.ZERO,
                                product = product,
                                onValueChange = viewModel::updateActualMorningStock,
                                enabled = !state.isFinalized && !isEndStage
                            )
                        }
                    }
                }
            }

            // ===== بخش ۲: عملیات روزانه - در هر سه مرحله (START, END, FULL) =====
            // این فیلدها در طول روز وارد می‌شوند و در هر دو مرحله اول کار و آخر کار قابل ویرایش هستند.
            SectionHeader(title = "بخش ۲: عملیات روزانه")

            BoxUnitInput(
                label = "۵. رسید انبار",
                value = state.receipt,
                product = product,
                onValueChange = viewModel::updateReceipt,
                enabled = !state.isFinalized  // فعال در همه مراحل
            )

            BoxUnitInput(
                label = "۶. برگشت به انبار",
                value = state.returns,
                product = product,
                onValueChange = viewModel::updateReturns,
                enabled = !state.isFinalized
            )

            BoxUnitInput(
                label = "۷. میان روز",
                value = state.midDayLoading,
                product = product,
                onValueChange = viewModel::updateMidDayLoading,
                enabled = !state.isFinalized
            )

            BoxUnitInput(
                label = "۸. هدایا",
                value = state.gifts,
                product = product,
                onValueChange = viewModel::updateGifts,
                enabled = !state.isFinalized
            )

            BoxUnitInput(
                label = "۹. ضایعات",
                value = state.waste,
                product = product,
                onValueChange = viewModel::updateWaste,
                enabled = !state.isFinalized
            )

            BoxUnitInput(
                label = "۱۰. آزمایشگاه",
                value = state.laboratory,
                product = product,
                onValueChange = viewModel::updateLaboratory,
                enabled = !state.isFinalized
            )

            // ===== بخش ۳: بارگیری شب و شمارش آخر کار - فقط در END یا FULL =====
            if (isEndStage || !isStartStage) {
                SectionHeader(title = "بخش ۳: بارگیری شب و شمارش آخر کار")

                BoxUnitInput(
                    label = "۱۲. بارگیری شب",
                    value = state.nightLoading,
                    product = product,
                    onValueChange = viewModel::updateNightLoading,
                    enabled = !state.isFinalized && !isStartStage  // در START غیرفعال
                )

                BoxUnitInput(
                    label = "۱۱. مانده واقعی حال حاضر",
                    value = state.currentStock ?: BoxAndUnits.ZERO,
                    product = product,
                    onValueChange = viewModel::updateCurrentStock,
                    enabled = !state.isFinalized && !isStartStage  // در START غیرفعال
                )
            }

            // بخش ۴: نتیجه مغایرت‌گیری
            SectionHeader(title = "بخش ۴: نتیجه مغایرت‌گیری")

            // نمایش مغایرت اول کار (اگر ثبت شده)
            state.varianceResult?.let { result ->
                // کارت مغایرت اول کار
                result.startVariance?.let { startVar ->
                    val startText = when {
                        startVar == 0 -> "مطابقت (۰)"
                        startVar > 0 -> "اضافه ${startVar.toPersian()}"
                        else -> "کسری ${(-startVar).toPersian()}"
                    }
                    val startColor = when {
                        startVar == 0 -> MaterialTheme.colorScheme.primary
                        startVar > 0 -> MaterialTheme.colorScheme.tertiary
                        else -> MaterialTheme.colorScheme.error
                    }
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = startColor.copy(alpha = 0.1f)
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "مغایرت اول کار",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = startText,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = startColor
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }

                // کارت مغایرت آخر کار
                result.endCumulativeVariance?.let { endVar ->
                    val endText = when {
                        endVar == 0 -> "مطابقت (۰)"
                        endVar > 0 -> "اضافه ${endVar.toPersian()}"
                        else -> "کسری ${(-endVar).toPersian()}"
                    }
                    val endColor = when {
                        endVar == 0 -> MaterialTheme.colorScheme.primary
                        endVar > 0 -> MaterialTheme.colorScheme.tertiary
                        else -> MaterialTheme.colorScheme.error
                    }
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = endColor.copy(alpha = 0.1f)
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "مغایرت آخر کار",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = endText,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = endColor
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }

                // کارت مغایرت شیفت شب (مهم‌ترین)
                result.nightShiftVariance?.let { nightVar ->
                    val nightText = when {
                        nightVar == 0 -> "مطابقت (۰)"
                        nightVar > 0 -> "اضافه ${nightVar.toPersian()}"
                        else -> "کسری ${(-nightVar).toPersian()}"
                    }
                    val nightColor = when {
                        nightVar == 0 -> MaterialTheme.colorScheme.primary
                        nightVar > 0 -> MaterialTheme.colorScheme.tertiary
                        else -> MaterialTheme.colorScheme.error
                    }
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = nightColor.copy(alpha = 0.15f)
                        ),
                        shape = RoundedCornerShape(12.dp),
                        elevation = CardDefaults.cardElevation(2.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "مغایرت واقعی شیفت شب",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = nightColor
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = nightText,
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.ExtraBold,
                                color = nightColor
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }

                // کارت کامل نتیجه
                VarianceResultCard(result = result, product = product)
            }

            // توضیحات
            SectionHeader(title = "توضیحات")
            OutlinedTextField(
                value = state.notes,
                onValueChange = viewModel::updateNotes,
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("توضیحات اختیاری (مثلاً علت کسری یا ضایعات)") },
                minLines = 2,
                maxLines = 4,
                enabled = !state.isFinalized
            )

            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

@Composable
private fun SectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
    )
}

@Composable
private fun ProductInfoCard(state: StatEntryUiState) {
    val product = state.product ?: return
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "گروه محصول:",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Text(
                    text = product.group.displayName,
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "ظرفیت باکس:",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Text(
                    text = if (product.isRoundBox) "باکس رند"
                    else "${product.boxCapacity.toPersian()} عدد",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "تاریخ:",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Text(
                    text = state.jalaliDate,
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
            if (state.isFinalized) {
                Spacer(modifier = Modifier.height(8.dp))
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.padding(end = 4.dp))
                        Text(
                            text = "این آمار نهایی شده است. برای ویرایش، ابتدا نهایی‌سازی را لغو کنید.",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun BottomActionButtons(
    isFinalized: Boolean,
    isSaving: Boolean,
    saveLabel: String = "ذخیره پیش‌نویس",
    finalizeLabel: String = "نهایی‌سازی",
    onSaveDraft: () -> Unit,
    onFinalize: () -> Unit
) {
    var showFinalizeDialog by remember { mutableStateOf(false) }

    if (showFinalizeDialog) {
        AlertDialog(
            onDismissRequest = { showFinalizeDialog = false },
            title = { Text("تأیید نهایی‌سازی") },
            text = {
                Text("پس از نهایی‌سازی، برای ویرایش آمار باید ابتدا نهایی‌سازی را لغو کنید. آیا مطمئن هستید؟")
            },
            confirmButton = {
                Button(
                    onClick = {
                        showFinalizeDialog = false
                        onFinalize()
                    }
                ) { Text("تأیید و نهایی‌سازی") }
            },
            dismissButton = {
                OutlinedButton(onClick = { showFinalizeDialog = false }) {
                    Text("انصراف")
                }
            }
        )
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        OutlinedButton(
            onClick = onSaveDraft,
            enabled = !isFinalized && !isSaving,
            modifier = Modifier.weight(1f)
        ) {
            Icon(Icons.Default.Save, contentDescription = null)
            Spacer(modifier = Modifier.padding(end = 4.dp))
            Text(saveLabel)
        }
        Button(
            onClick = { showFinalizeDialog = true },
            enabled = !isFinalized && !isSaving,
            modifier = Modifier.weight(1f),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary
            )
        ) {
            Icon(Icons.Default.CheckCircle, contentDescription = null)
            Spacer(modifier = Modifier.padding(end = 4.dp))
            Text(finalizeLabel)
        }
    }
}

private fun Int.toPersian(): String {
    val persianDigits = arrayOf('۰', '۱', '۲', '۳', '۴', '۵', '۶', '۷', '۸', '۹')
    return this.toString().map { c ->
        if (c.isDigit()) persianDigits[c - '0'] else c
    }.joinToString("")
}
