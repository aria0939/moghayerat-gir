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
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.filled.LocalDrink
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material.icons.filled.Cake
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.damdarangha.moghayerat.MoghayeratApplication
import com.damdarangha.moghayerat.R
import com.damdarangha.moghayerat.domain.model.ProductGroup
import com.damdarangha.moghayerat.ui.theme.CategoryCardShape
import com.damdarangha.moghayerat.ui.theme.DatePillShape
import com.damdarangha.moghayerat.ui.theme.GroupDateBg
import com.damdarangha.moghayerat.ui.theme.GroupDateColor
import com.damdarangha.moghayerat.ui.theme.GroupDonutBg
import com.damdarangha.moghayerat.ui.theme.GroupDonutColor
import com.damdarangha.moghayerat.ui.theme.GroupMilkBg
import com.damdarangha.moghayerat.ui.theme.GroupMilkColor
import com.damdarangha.moghayerat.ui.theme.GroupSterileBg
import com.damdarangha.moghayerat.ui.theme.GroupSterileColor
import com.damdarangha.moghayerat.ui.theme.GroupYoghurtBg
import com.damdarangha.moghayerat.ui.theme.GroupYoghurtColor
import com.damdarangha.moghayerat.ui.theme.HeroCardShape
import com.damdarangha.moghayerat.ui.theme.HeroGradientEnd
import com.damdarangha.moghayerat.ui.theme.HeroGradientStart
import com.damdarangha.moghayerat.ui.theme.QuickAccessShape

/**
 * صفحه اصلی برنامه - داشبورد با کارت Hero و شبکه‌ای از گروه‌های محصول
 *
 * طراحی مطابق تصویر مرجع:
 * - هدر تمیز با لوگو و آیکون اعلان ظریف
 * - کارت Hero با گرادیان آبی روشن + لوگو + تاریخ شمسی در کپسول سفید
 * - ۵ کارت گروه محصول در شبکه ۲ ستونی (آخرین کارت تمام عرض)
 * - بخش دسترسی سریع (۴ کارت در شبکه ۲×۲) با آیکون‌های بزرگ‌تر
 * - نوار ناوبری پایین با ۴ تب
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
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(12.dp))

        // ===== هدر بالای صفحه: لوگو + آیکون اعلان ظریف =====
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // لوگوی دامداران (تنها لوگو در صفحه)
            Image(
                painter = painterResource(id = R.drawable.ic_logo),
                contentDescription = "لوگوی دامداران",
                modifier = Modifier
                    .height(44.dp)
                    .width(110.dp),
                contentScale = ContentScale.Fit
            )
            // آیکون اعلان ظریف (بدون دایره بزرگ)
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

        // ===== کارت Hero با گرادیان + لوگو + تاریخ شمسی =====
        HeroCard(
            title = "مغایرت‌گیر دامداران",
            subtitle = "مدیریت هوشمند موجودی و کنترل دقیق مغایرت محصولات",
            dateText = state.todayJalali
        )

        Spacer(modifier = Modifier.height(20.dp))

        // ===== عنوان بخش گروه‌های محصول =====
        SectionHeader(title = "انتخاب گروه محصول")

        Spacer(modifier = Modifier.height(10.dp))

        // ===== شبکه ۲ ستونی کارت‌های گروه محصول =====
        ProductGroupGrid(
            state = state,
            onGroupClick = onGroupClick
        )

        Spacer(modifier = Modifier.height(20.dp))

        // ===== عنوان بخش دسترسی سریع =====
        SectionHeader(title = "دسترسی سریع")

        Spacer(modifier = Modifier.height(10.dp))

        // ===== شبکه ۲×۲ دسترسی سریع =====
        QuickAccessGrid(
            onReportsClick = onReportsClick,
            onHistoryClick = onHistoryClick,
            onBackupClick = onBackupClick,
            onProductManagementClick = onProductManagementClick
        )

        // فضای خالی برای نوار پایین
        Spacer(modifier = Modifier.height(96.dp))
    }
}

/**
 * عنوان بخش - راست‌چین، پررنگ، با فاصله مناسب
 */
@Composable
private fun SectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onBackground,
        fontSize = 18.sp
    )
}

/**
 * کارت Hero - کارت معرفی بزرگ با گرادیان آبی، لوگو و تاریخ شمسی در کپسول سفید
 */
@Composable
private fun HeroCard(
    title: String,
    subtitle: String,
    dateText: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = HeroCardShape,
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
                // متن عنوان و زیرعنوان و تاریخ
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
                            .clip(DatePillShape)
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

                // لوگو در سمت چپ کارت - اندازه مناسب
                Image(
                    painter = painterResource(id = R.drawable.ic_logo),
                    contentDescription = "لوگو",
                    modifier = Modifier.size(80.dp),
                    contentScale = ContentScale.Fit
                )
            }
        }
    }
}

/**
 * شبکه ۲ ستونی کارت‌های گروه محصول
 * آخرین کارت تمام عرض ردیف را اشغال می‌کند
 */
@Composable
private fun ProductGroupGrid(
    state: HomeUiState,
    onGroupClick: (ProductGroup) -> Unit
) {
    val groups = listOf(
        GroupCardData(ProductGroup.STERILE, "استریل", state.sterileCount, Icons.Default.WaterDrop, GroupSterileColor, GroupSterileBg),
        GroupCardData(ProductGroup.MILK, "شیر", state.milkCount, Icons.Default.LocalDrink, GroupMilkColor, GroupMilkBg),
        GroupCardData(ProductGroup.YOGHURT_BOX, "ماست پاکتی", state.yoghurtBoxCount, Icons.Default.Inventory2, GroupYoghurtColor, GroupYoghurtBg),
        GroupCardData(ProductGroup.DATE, "خرما", state.dateCount, Icons.Default.Spa, GroupDateColor, GroupDateBg),
        GroupCardData(ProductGroup.DONUT_CAKE, "دونات و کیک", state.donutCakeCount, Icons.Default.Cake, GroupDonutColor, GroupDonutBg)
    )

    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        // ردیف اول: ۲ کارت
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            GroupCard(groups[0], modifier = Modifier.weight(1f)) { onGroupClick(groups[0].group) }
            GroupCard(groups[1], modifier = Modifier.weight(1f)) { onGroupClick(groups[1].group) }
        }
        // ردیف دوم: ۲ کارت
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            GroupCard(groups[2], modifier = Modifier.weight(1f)) { onGroupClick(groups[2].group) }
            GroupCard(groups[3], modifier = Modifier.weight(1f)) { onGroupClick(groups[3].group) }
        }
        // ردیف سوم: ۱ کارت تمام عرض
        GroupCard(groups[4], modifier = Modifier.fillMaxWidth()) { onGroupClick(groups[4].group) }
    }
}

private data class GroupCardData(
    val group: ProductGroup,
    val title: String,
    val activeCount: Int,
    val icon: ImageVector,
    val iconColor: Color,
    val cardBg: Color
)

/**
 * کارت گروه محصول - مطابق تصویر مرجع
 * دایره آیکون کوچک‌تر (40dp)، فلش ظریف، فاصله‌گذاری هماهنگ
 */
@Composable
private fun GroupCard(
    data: GroupCardData,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier,
        shape = CategoryCardShape,
        colors = CardDefaults.cardColors(containerColor = data.cardBg),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // دایره آیکون - اندازه 40dp (مطابق مرجع)
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(data.iconColor),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = data.icon,
                    contentDescription = data.title,
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // عنوان و زیرعنوان
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = data.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 15.sp
                )
                Text(
                    text = "${data.activeCount.toPersian()} محصول فعال",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 11.sp
                )
            }

            // فلش ظریف به سمت چپ
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                contentDescription = "باز کردن",
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

/**
 * شبکه ۲×۲ دسترسی سریع - آیکون‌های بزرگ‌تر و برجسته‌تر
 */
@Composable
private fun QuickAccessGrid(
    onReportsClick: () -> Unit,
    onHistoryClick: () -> Unit,
    onBackupClick: () -> Unit,
    onProductManagementClick: () -> Unit
) {
    val items = listOf(
        QuickAccessItem("تاریخچه", Icons.Default.History, GroupSterileColor, GroupSterileBg, onHistoryClick),
        QuickAccessItem("گزارش‌ها", Icons.Default.Assessment, GroupDateColor, GroupDateBg, onReportsClick),
        QuickAccessItem("پشتیبان", Icons.Default.Backup, GroupMilkColor, GroupMilkBg, onBackupClick),
        QuickAccessItem("محصولات", Icons.Default.Inventory2, GroupYoghurtColor, GroupYoghurtBg, onProductManagementClick)
    )

    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            QuickAccessCard(items[0], modifier = Modifier.weight(1f))
            QuickAccessCard(items[1], modifier = Modifier.weight(1f))
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            QuickAccessCard(items[2], modifier = Modifier.weight(1f))
            QuickAccessCard(items[3], modifier = Modifier.weight(1f))
        }
    }
}

private data class QuickAccessItem(
    val title: String,
    val icon: ImageVector,
    val iconColor: Color,
    val bgColor: Color,
    val onClick: () -> Unit
)

/**
 * کارت دسترسی سریع - آیکون بزرگ‌تر (56dp) و برجسته‌تر
 */
@Composable
private fun QuickAccessCard(
    item: QuickAccessItem,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = QuickAccessShape,
        colors = CardDefaults.cardColors(containerColor = item.bgColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        onClick = item.onClick
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // دایره آیکون بزرگ‌تر - 56dp (مطابق مرجع)
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

// تابع کمکی برای تبدیل اعداد فارسی
private fun Int.toPersian(): String {
    val persianDigits = arrayOf('۰', '۱', '۲', '۳', '۴', '۵', '۶', '۷', '۸', '۹')
    return this.toString().map { c ->
        if (c.isDigit()) persianDigits[c - '0'] else c
    }.joinToString("")
}
