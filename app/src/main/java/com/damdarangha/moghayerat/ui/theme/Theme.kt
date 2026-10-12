package com.damdarangha.moghayerat.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection

/**
 * تم اصلی برنامه - با رنگ‌های برند دامداران
 *
 * پالت رنگ:
 * - آبی اصلی: #123DA8
 * - قرمز مرجانی: #FA727D
 *
 * ویژگی‌ها:
 * - تم روشن به‌عنوان پیش‌فرض
 * - چیدمان RTL (راست‌چین)
 * - فونت Vazirmatn فارسی
 * - پشتیبانی کامل از حالت تاریک با رنگ‌های بهینه‌شده
 * - استفاده از LocalIsDarkTheme برای هماهنگی تمام Components
 */
@Composable
fun MoghayeratTheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) {
        darkColorScheme(
            primary = DarkPrimary,
            onPrimary = DarkOnPrimary,
            primaryContainer = DarkPrimaryContainer,
            onPrimaryContainer = DarkOnPrimaryContainer,
            secondary = DarkSecondary,
            onSecondary = DarkOnSecondary,
            tertiary = DarkTertiary,
            onTertiary = DarkOnTertiary,
            background = DarkBackground,
            onBackground = DarkOnBackground,
            surface = DarkSurface,
            onSurface = DarkOnSurface,
            surfaceVariant = DarkSurfaceVariant,
            onSurfaceVariant = DarkOnSurfaceVariant,
            error = DarkError,
            onError = DarkOnError,
            outline = DarkOutline
        )
    } else {
        lightColorScheme(
            primary = LightPrimary,
            onPrimary = LightOnPrimary,
            primaryContainer = LightPrimaryContainer,
            onPrimaryContainer = LightOnPrimaryContainer,
            secondary = LightSecondary,
            onSecondary = LightOnSecondary,
            tertiary = LightTertiary,
            onTertiary = LightOnTertiary,
            background = LightBackground,
            onBackground = LightOnBackground,
            surface = LightSurface,
            onSurface = LightOnSurface,
            surfaceVariant = LightSurfaceVariant,
            onSurfaceVariant = LightOnSurfaceVariant,
            error = LightError,
            onError = LightOnError,
            outline = LightOutline
        )
    }

    // هماهنگ کردن CompositionLocal برای هم وضعیت RTL و هم وضعیت تم تاریک
    CompositionLocalProvider(
        LocalLayoutDirection provides LayoutDirection.Rtl,
        LocalIsDarkTheme provides darkTheme
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = AppTypography,
            shapes = AppShapes,
            content = content
        )
    }
}

/**
 * رنگ‌های وضعیت سفارشی برای استفاده در کامپوننت‌ها.
 * از LocalIsDarkTheme استفاده می‌کند تا با تم فعلی هماهنگ باشد.
 */
@Composable
fun statusColors(): StatusColors {
    val isDark = isDarkTheme()
    return if (isDark) {
        StatusColors(
            success = Color(0xFF7C9CFF),
            successBg = Color(0xFF1A2A4A),
            deficit = Color(0xFFFF8A8A),
            deficitBg = Color(0xFF3D1518),
            surplus = Color(0xFF66BB6A),
            surplusBg = Color(0xFF1B3320),
            warning = Color(0xFFFFB74D),
            warningBg = Color(0xFF33291A)
        )
    } else {
        StatusColors(
            success = Color(0xFF123DA8),
            successBg = Color(0xFFE8EAFA),
            deficit = Color(0xFFFA727D),
            deficitBg = Color(0xFFFFE5E5),
            surplus = Color(0xFF4CAF50),
            surplusBg = Color(0xFFE8F5E9),
            warning = Color(0xFFFFBC4D),
            warningBg = Color(0xFFFFF3E0)
        )
    }
}

data class StatusColors(
    val success: androidx.compose.ui.graphics.Color,
    val successBg: androidx.compose.ui.graphics.Color,
    val deficit: androidx.compose.ui.graphics.Color,
    val deficitBg: androidx.compose.ui.graphics.Color,
    val surplus: androidx.compose.ui.graphics.Color,
    val surplusBg: androidx.compose.ui.graphics.Color,
    val warning: androidx.compose.ui.graphics.Color,
    val warningBg: androidx.compose.ui.graphics.Color
)
