package com.damdarangha.moghayerat.ui.screen.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.Backup
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.damdarangha.moghayerat.MoghayeratApplication
import com.damdarangha.moghayerat.R
import com.damdarangha.moghayerat.domain.model.ProductGroup
import com.damdarangha.moghayerat.ui.components.GroupSelectionCard
import com.damdarangha.moghayerat.ui.components.statusBarsPadding

/**
 * صفحه اصلی برنامه.
 *
 * شامل:
 * - لوگوی برنامه و عنوان
 * - تاریخ شمسی امروز
 * - وضعیت ثبت آمار روز
 * - سه کارت بزرگ برای انتخاب گروه محصول
 * - دکمه شروع آمارگیری
 * - دسترسی به گزارش‌های روزانه
 * - دسترسی به تنظیمات، تاریخچه، مدیریت محصولات و پشتیبان‌گیری
 */
@Composable
fun HomeScreen(
    onGroupClick: (ProductGroup) -> Unit,
    onReportsClick: () -> Unit,
    onHistoryClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onProductManagementClick: () -> Unit,
    onBackupClick: () -> Unit,
    viewModel: HomeViewModel = viewModel(
        factory = HomeViewModel.factory(MoghayeratApplication.instance)
    )
) {
    val state by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()  // جلوگیری از overlap با notification bar
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // هدر بالای صفحه
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            ),
            shape = RoundedCornerShape(20.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // لوگوی دامداران
                Image(
                    painter = painterResource(id = R.drawable.ic_logo),
                    contentDescription = "لوگوی دامداران",
                    modifier = Modifier
                        .size(80.dp)
                        .padding(4.dp),
                    contentScale = ContentScale.Fit
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "مغایرت‌گیر دامداران",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = state.todayJalali,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Spacer(modifier = Modifier.height(8.dp))
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                ) {
                    Text(
                        text = if (state.todayStatsCount > 0)
                            "وضعیت امروز: ${state.todayStatsCount.toPersian()} آمار ثبت شده" +
                                if (state.todayVarianceCount > 0) " | ${state.todayVarianceCount.toPersian()} مغایرت" else ""
                        else "هنوز آماری برای امروز ثبت نشده است",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // کارت‌های انتخاب گروه
        Text(
            text = "انتخاب گروه محصول",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(vertical = 8.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))

        GroupSelectionCard(
            group = ProductGroup.STERILE,
            activeProductCount = state.sterileCount,
            lastStatStatus = null,
            onClick = { onGroupClick(ProductGroup.STERILE) }
        )
        Spacer(modifier = Modifier.height(12.dp))
        GroupSelectionCard(
            group = ProductGroup.TUB,
            activeProductCount = state.tubCount,
            lastStatStatus = null,
            onClick = { onGroupClick(ProductGroup.TUB) }
        )
        Spacer(modifier = Modifier.height(12.dp))
        GroupSelectionCard(
            group = ProductGroup.DATE,
            activeProductCount = state.dateCount,
            lastStatStatus = null,
            onClick = { onGroupClick(ProductGroup.DATE) }
        )

        Spacer(modifier = Modifier.height(24.dp))

        // دکمه‌های دسترسی سریع
        Text(
            text = "دسترسی سریع",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(vertical = 8.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))

        // دو ردیف دکمه
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            QuickAccessButton(
                icon = Icons.Default.Assessment,
                label = "گزارش‌ها",
                onClick = onReportsClick,
                modifier = Modifier.weight(1f)
            )
            QuickAccessButton(
                icon = Icons.Default.History,
                label = "تاریخچه",
                onClick = onHistoryClick,
                modifier = Modifier.weight(1f)
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            QuickAccessButton(
                icon = Icons.Default.Inventory,
                label = "محصولات",
                onClick = onProductManagementClick,
                modifier = Modifier.weight(1f)
            )
            QuickAccessButton(
                icon = Icons.Default.Backup,
                label = "پشتیبان",
                onClick = onBackupClick,
                modifier = Modifier.weight(1f)
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            IconButton(onClick = onSettingsClick) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "تنظیمات",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }

        Spacer(modifier = Modifier.height(40.dp))
    }
}

@Composable
private fun QuickAccessButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .height(80.dp),
        onClick = onClick,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

private fun Int.toPersian(): String {
    val persianDigits = arrayOf('۰', '۱', '۲', '۳', '۴', '۵', '۶', '۷', '۸', '۹')
    return this.toString().map { c ->
        if (c.isDigit()) persianDigits[c - '0'] else c
    }.joinToString("")
}
