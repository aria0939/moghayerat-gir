package com.damdarangha.moghayerat.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.Settings
import androidx.compose.foundation.LocalIndication
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.damdarangha.moghayerat.ui.theme.BottomNavBg

/**
 * آیتم‌های نوار ناوبری پایین
 */
data class BottomNavItem(
    val route: String,
    val label: String,
    val icon: ImageVector
)

val bottomNavItems = listOf(
    BottomNavItem("home", "خانه", Icons.Default.Home),
    BottomNavItem("reports", "گزارش‌ها", Icons.Default.Assessment),
    BottomNavItem("product_management", "محصولات", Icons.Default.Inventory2),
    BottomNavItem("settings", "تنظیمات", Icons.Default.Settings)
)

/**
 * نوار ناوبری پایین — طراحی مدرن با:
 * - انیمیشن scale برای آیکون انتخاب‌شده
 * - انیمیشن fade برای نشانگر
 * - افکت ripple برای بازخورد لمسی
 * - touch target مناسب (حداقل ۴۸dp)
 */
@Composable
fun BottomNavBar(
    currentRoute: String,
    onNavigate: (String) -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = BottomNavBg,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
        shadowElevation = 12.dp,
        tonalElevation = 2.dp
    ) {
        androidx.compose.foundation.layout.Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            bottomNavItems.forEach { item ->
                val isSelected = currentRoute == item.route
                BottomNavItemView(
                    item = item,
                    isSelected = isSelected,
                    onClick = { onNavigate(item.route) },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun BottomNavItemView(
    item: BottomNavItem,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val iconScale by animateFloatAsState(
        targetValue = if (isSelected) 1.15f else 1f,
        animationSpec = spring(dampingRatio = 0.6f, stiffness = 400f),
        label = "iconScale"
    )
    val contentColor = if (isSelected) MaterialTheme.colorScheme.primary
    else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
    val bgColor = if (isSelected)
        MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
    else androidx.compose.ui.graphics.Color.Transparent

    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(bgColor)
            .clickable(
                interactionSource = interactionSource,
                indication = LocalIndication.current,
                role = Role.Button,
                onClick = onClick
            )
            .padding(vertical = 8.dp, horizontal = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = item.icon,
            contentDescription = null,
            modifier = Modifier
                .size(24.dp)
                .scale(iconScale),
            tint = contentColor
        )
        Spacer(modifier = Modifier.height(3.dp))
        Text(
            text = item.label,
            fontSize = 11.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            color = contentColor,
            maxLines = 1
        )
        Spacer(modifier = Modifier.height(3.dp))
        // نشانگر زیر آیکون فعال — با انیمیشن
        Box(
            modifier = Modifier
                .width(if (isSelected) 24.dp else 12.dp)
                .height(3.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(
                    if (isSelected) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
                )
        )
    }
}
