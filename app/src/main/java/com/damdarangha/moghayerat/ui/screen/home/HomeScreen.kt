package com.damdarangha.moghayerat.ui.screen.home

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.Backup
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.damdarangha.moghayerat.MoghayeratApplication
import com.damdarangha.moghayerat.R
import com.damdarangha.moghayerat.domain.model.Workflow
import com.damdarangha.moghayerat.ui.theme.GroupDateBg
import com.damdarangha.moghayerat.ui.theme.GroupDateColor
import com.damdarangha.moghayerat.ui.theme.GroupMilkBg
import com.damdarangha.moghayerat.ui.theme.GroupMilkColor
import com.damdarangha.moghayerat.ui.theme.GroupSterileBg
import com.damdarangha.moghayerat.ui.theme.GroupSterileColor
import com.damdarangha.moghayerat.ui.theme.GroupYoghurtBg
import com.damdarangha.moghayerat.ui.theme.GroupYoghurtColor
import com.damdarangha.moghayerat.ui.theme.HeroGradientEnd
import com.damdarangha.moghayerat.ui.theme.HeroGradientStart

/**
 * صفحه اصلی برنامه - داشبورد با کارت‌های گردش کار
 */
@Composable
fun HomeScreen(
    onWorkflowClick: (Workflow) -> Unit,
    onReportsClick: () -> Unit,
    onHistoryClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onWorkflowManagementClick: () -> Unit,
    onBackupClick: () -> Unit,
    viewModel: HomeViewModel = viewModel(
        factory = HomeViewModel.factory(MoghayeratApplication.instance)
    )
) {
    val state by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(12.dp))

        // ===== هدر: لوگو + آیکون اعلان =====
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_logo),
                contentDescription = "لوگوی دامداران",
                modifier = Modifier.height(48.dp).aspectRatio(1f),
                contentScale = ContentScale.Fit,
                alignment = Alignment.Center
            )
            IconButton(
                onClick = { /* برای آینده */ },
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Notifications,
                    contentDescription = "اعلان‌ها",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(22.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // ===== کارت Hero =====
        HeroCard(
            title = "مغایرت‌گیر دامداران",
            subtitle = "مدیریت هوشمند موجودی و کنترل دقیق مغایرت محصولات",
            dateText = state.todayJalali
        )

        Spacer(modifier = Modifier.height(20.dp))

        // ===== بخش گردش کارها =====
        Text(
            text = "گردش کارهای آمارگیر",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
            fontSize = 18.sp
        )

        Spacer(modifier = Modifier.height(10.dp))

        // کارت‌های گردش کار
        state.workflows.forEach { workflow ->
            WorkflowCard(
                workflowName = workflow.name,
                workflowId = workflow.id,
                totalProducts = workflow.totalProducts,
                registeredCount = workflow.registeredToday,
                onClick = {
                    // ساخت Workflow موقت برای پاس دادن (فقط id و name نیاز است)
                    onWorkflowClick(
                        com.damdarangha.moghayerat.domain.model.Workflow(
                            id = workflow.id,
                            name = workflow.name
                        )
                    )
                }
            )
            Spacer(modifier = Modifier.height(10.dp))
        }

        Spacer(modifier = Modifier.height(20.dp))

        // ===== بخش دسترسی سریع =====
        Text(
            text = "دسترسی سریع",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
            fontSize = 18.sp
        )

        Spacer(modifier = Modifier.height(10.dp))

        QuickAccessGrid(
            onReportsClick = onReportsClick,
            onHistoryClick = onHistoryClick,
            onBackupClick = onBackupClick,
            onWorkflowManagementClick = onWorkflowManagementClick
        )

        // فضای خالی برای نوار پایین
        Spacer(modifier = Modifier.height(96.dp))
    }
}

/**
 * کارت Hero - کارت معرفی بزرگ با گرادیان آبی
 */
@Composable
private fun HeroCard(
    title: String,
    subtitle: String,
    dateText: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(HeroGradientStart, HeroGradientEnd)
                    )
                )
                .padding(18.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        fontSize = 20.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.75f),
                        lineHeight = 18.sp,
                        fontSize = 12.sp
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    // کپسول سفید تاریخ شمسی
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(Color.White)
                            .padding(horizontal = 10.dp, vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(5.dp))
                        Text(
                            text = dateText,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Medium,
                            fontSize = 11.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.width(10.dp))

                Image(
                    painter = painterResource(id = R.drawable.ic_logo),
                    contentDescription = "لوگو",
                    modifier = Modifier.height(72.dp).aspectRatio(1f),
                    contentScale = ContentScale.Fit,
                    alignment = Alignment.Center
                )
            }
        }
    }
}

/**
 * کارت گردش کار - نمایش نام، تعداد محصولات، ثبت‌شده، باقی‌مانده و درصد پیشرفت
 */
@Composable
private fun WorkflowCard(
    workflowName: String,
    workflowId: Long,
    totalProducts: Int,
    registeredCount: Int,
    onClick: () -> Unit
) {
    val totalCount = totalProducts
    val remainingCount = (totalCount - registeredCount).coerceAtLeast(0)
    val progress = if (totalCount > 0) registeredCount.toFloat() / totalCount else 0f

    // رنگ کارت بر اساس نام گردش کار
    val (cardBg, accentColor) = when (workflowName) {
        "استریل" -> GroupSterileBg to GroupSterileColor
        "دبه و خرما" -> GroupYoghurtBg to GroupYoghurtColor
        else -> GroupMilkBg to GroupMilkColor
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = cardBg),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        onClick = onClick
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // دایره آیکون
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(accentColor),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = workflowName,
                        tint = Color.White,
                        modifier = Modifier.size(22.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                // نام و آمار
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = workflowName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontSize = 16.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        StatItem("کل محصولات", totalCount)
                        StatItem("ثبت‌شده", registeredCount)
                        StatItem("باقی‌مانده", remainingCount)
                    }
                }

                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                    contentDescription = "باز کردن",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // نوار پیشرفت
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = accentColor,
                trackColor = accentColor.copy(alpha = 0.2f)
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "پیشرفت: ${(progress * 100).toInt().toPersian()}%",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 11.sp
            )
        }
    }
}

@Composable
private fun StatItem(label: String, value: Int) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value.toPersian(),
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
            fontSize = 14.sp
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 10.sp
        )
    }
}

/**
 * شبکه ۲×۲ دسترسی سریع
 */
@Composable
private fun QuickAccessGrid(
    onReportsClick: () -> Unit,
    onHistoryClick: () -> Unit,
    onBackupClick: () -> Unit,
    onWorkflowManagementClick: () -> Unit
) {
    val items = listOf(
        QuickAccessItem("تاریخچه", Icons.Default.History, GroupSterileColor, GroupSterileBg, onHistoryClick),
        QuickAccessItem("گزارش‌ها", Icons.Default.Assessment, GroupDateColor, GroupDateBg, onReportsClick),
        QuickAccessItem("پشتیبان", Icons.Default.Backup, GroupMilkColor, GroupMilkBg, onBackupClick),
        QuickAccessItem("مدیریت گردش کار", Icons.Default.Inventory2, GroupYoghurtColor, GroupYoghurtBg, onWorkflowManagementClick)
    )

    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            QuickAccessCard(items[0], Modifier.weight(1f))
            QuickAccessCard(items[1], Modifier.weight(1f))
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            QuickAccessCard(items[2], Modifier.weight(1f))
            QuickAccessCard(items[3], Modifier.weight(1f))
        }
    }
}

private data class QuickAccessItem(
    val title: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val iconColor: Color,
    val bgColor: Color,
    val onClick: () -> Unit
)

@Composable
private fun QuickAccessCard(
    item: QuickAccessItem,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = item.bgColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        onClick = item.onClick
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(item.iconColor),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = item.icon,
                    contentDescription = item.title,
                    tint = Color.White,
                    modifier = Modifier.size(28.dp)
                )
            }
            Text(
                text = item.title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 13.sp
            )
        }
    }
}

// تابع کمکی برای اعداد فارسی
private fun Int.toPersian(): String {
    val persianDigits = arrayOf('۰', '۱', '۲', '۳', '۴', '۵', '۶', '۷', '۸', '۹')
    return this.toString().map { c ->
        if (c.isDigit()) persianDigits[c - '0'] else c
    }.joinToString("")
}
