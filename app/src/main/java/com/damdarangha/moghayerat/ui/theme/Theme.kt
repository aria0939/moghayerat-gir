package com.damdarangha.moghayerat.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.LayoutDirection

/**
 * تم اصلی برنامه.
 *
 * ویژگی‌ها:
 * - پشتیبانی از تم روشن و تاریک
 * - چیدمان RTL (راست‌چین)
 * - فونت Vazirmatn فارسی
 * - رنگ‌بندی سبز تیره دامداران
 */
@Composable
fun MoghayeratTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) {
        darkColorScheme(
            primary = GreenPrimary,
            onPrimary = GreenOnPrimary,
            primaryContainer = GreenPrimaryDark,
            onPrimaryContainer = GreenLight,
            secondary = GreenPrimary,
            onSecondary = GreenOnPrimary,
            tertiary = SuccessGreen,
            background = DarkBackground,
            onBackground = TextPrimaryDark,
            surface = DarkSurface,
            onSurface = TextPrimaryDark,
            surfaceVariant = DarkSurface,
            onSurfaceVariant = TextSecondaryDark,
            error = DeficitRed,
            onError = GreenOnPrimary,
            outline = DividerColorDark
        )
    } else {
        lightColorScheme(
            primary = GreenPrimary,
            onPrimary = GreenOnPrimary,
            primaryContainer = GreenLight,
            onPrimaryContainer = GreenPrimaryDark,
            secondary = GreenPrimary,
            onSecondary = GreenOnPrimary,
            tertiary = SuccessGreen,
            background = LightGrayBackground,
            onBackground = TextPrimary,
            surface = WhiteBackground,
            onSurface = TextPrimary,
            surfaceVariant = LightGrayBackground,
            onSurfaceVariant = TextSecondary,
            error = DeficitRed,
            onError = GreenOnPrimary,
            outline = DividerColor
        )
    }

    // اعمال RTL
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
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
 */
@Composable
fun statusColors(): StatusColors {
    val isDark = androidx.compose.foundation.isSystemInDarkTheme()
    return if (isDark) {
        StatusColors(
            success = SuccessGreen,
            successBg = SuccessGreen.copy(alpha = 0.2f),
            deficit = DeficitRed,
            deficitBg = DeficitRed.copy(alpha = 0.2f),
            surplus = SurplusBlue,
            surplusBg = SurplusBlue.copy(alpha = 0.2f),
            warning = WarningOrange,
            warningBg = WarningOrange.copy(alpha = 0.2f)
        )
    } else {
        StatusColors(
            success = SuccessGreen,
            successBg = SuccessGreenLight,
            deficit = DeficitRed,
            deficitBg = DeficitRedLight,
            surplus = SurplusBlue,
            surplusBg = SurplusBlueLight,
            warning = WarningOrange,
            warningBg = WarningOrangeLight
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
