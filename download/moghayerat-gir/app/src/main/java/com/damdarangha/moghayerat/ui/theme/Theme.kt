package com.damdarangha.moghayerat.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection

/**
 * تم اصلی برنامه - با رنگ‌های برند دامداران
 *
 * پالت رنگ:
 * - آبی اصلی: #011FAE (از لوگو)
 * - قرمز: #FA0403 (از لوگو)
 * - پس‌زمینه: #F3F3F3 (از لوگو)
 *
 * ویژگی‌ها:
 * - پشتیبانی از تم روشن و تاریک
 * - چیدمان RTL (راست‌چین)
 * - رنگ‌بندی بر اساس لوگوی دامداران
 * - حالت تاریک چشم‌آسایش (آبی کم‌رنگ‌تر)
 */
@Composable
fun MoghayeratTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
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
            success = DarkPrimary,
            successBg = DarkPrimary.copy(alpha = 0.2f),
            deficit = DarkTertiary,
            deficitBg = DarkTertiary.copy(alpha = 0.2f),
            surplus = SurplusGreen,
            surplusBg = SurplusGreen.copy(alpha = 0.2f),
            warning = WarningOrange,
            warningBg = WarningOrange.copy(alpha = 0.2f)
        )
    } else {
        StatusColors(
            success = SuccessBlue,
            successBg = SuccessBlueLight,
            deficit = DeficitRed,
            deficitBg = DeficitRedLight,
            surplus = SurplusGreen,
            surplusBg = SurplusGreenLight,
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
