package com.damdarangha.moghayerat.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

// ============================================
// پالت رنگ جدید - الهام‌گرفته از تصویر مرجع
// مینیمال، روشن، حرفه‌ای با رنگ‌های پاستلی
// ============================================

// رنگ‌های اصلی برند
val BrandPrimaryBlue = Color(0xFF123DA8)       // آبی اصلی
val BrandPrimaryBlueLight = Color(0xFF4F7DF3) // آبی روشن
val BrandAccentRed = Color(0xFFFA727D)         // قرمز مرجانی
val BrandBackground = Color(0xFFFFFFFF)        // پس‌زمینه سفید
val BrandSurfaceWhite = Color(0xFFFFFFFF)      // سفید کارت‌ها

// ============================================
// CompositionLocal برای اشتراک‌گذاری وضعیت تم فعلی
// این به Components اجازه می‌دهد بدانند آیا تم تاریک فعال است یا نه
// (بدون اتکا به isSystemInDarkTheme که فقط به سیستم عامل نگاه می‌کند)
// ============================================
val LocalIsDarkTheme = compositionLocalOf { false }

/**
 * تابع کمکی برای تشخیص تم فعلی
 * این تابع به CompositionLocal نگاه می‌کند که توسط MoghayeratTheme تنظیم می‌شود
 * بنابراین همیشه با تم واقعی برنامه هماهنگ است (نه با تم سیستم عامل)
 */
@Composable
fun isDarkTheme(): Boolean = LocalIsDarkTheme.current

// ============================================
// رنگ‌های گروه‌های محصول - حالت روشن (پاستلی روشن)
// ============================================
val GroupSterileColorLight = Color(0xFF4A90E2)
val GroupSterileBgLight = Color(0xFFEBF3FF)

val GroupMilkColorLight = Color(0xFF26C6DA)
val GroupMilkBgLight = Color(0xFFE0F7FA)

val GroupYoghurtColorLight = Color(0xFFEF5350)
val GroupYoghurtBgLight = Color(0xFFFFEBEE)

val GroupDateColorLight = Color(0xFFAB47BC)
val GroupDateBgLight = Color(0xFFF3E5F5)

val GroupDonutColorLight = Color(0xFFFFA726)
val GroupDonutBgLight = Color(0xFFFFF8E1)

// ============================================
// رنگ‌های گروه‌های محصول - حالت تاریک (پاستلی تیره‌تر)
// ============================================
val GroupSterileColorDark = Color(0xFF6BA8F0)
val GroupSterileBgDark = Color(0xFF1A2A4A)

val GroupMilkColorDark = Color(0xFF4DD0E1)
val GroupMilkBgDark = Color(0xFF0F2F35)

val GroupYoghurtColorDark = Color(0xFFFF7076)
val GroupYoghurtBgDark = Color(0xFF3D1518)

val GroupDateColorDark = Color(0xFFCE93D8)
val GroupDateBgDark = Color(0xFF2E1A35)

val GroupDonutColorDark = Color(0xFFFFB74D)
val GroupDonutBgDark = Color(0xFF33291A)

// ============================================
// Composable aliases - بر اساس LocalIsDarkTheme (نه isSystemInDarkTheme)
// این رنگ‌ها به‌درستی با تم فعلی برنامه هماهنگ می‌شوند
// ============================================
val GroupSterileColor @Composable get() = if (isDarkTheme()) GroupSterileColorDark else GroupSterileColorLight
val GroupSterileBg @Composable get() = if (isDarkTheme()) GroupSterileBgDark else GroupSterileBgLight

val GroupMilkColor @Composable get() = if (isDarkTheme()) GroupMilkColorDark else GroupMilkColorLight
val GroupMilkBg @Composable get() = if (isDarkTheme()) GroupMilkBgDark else GroupMilkBgLight

val GroupYoghurtColor @Composable get() = if (isDarkTheme()) GroupYoghurtColorDark else GroupYoghurtColorLight
val GroupYoghurtBg @Composable get() = if (isDarkTheme()) GroupYoghurtBgDark else GroupYoghurtBgLight

val GroupDateColor @Composable get() = if (isDarkTheme()) GroupDateColorDark else GroupDateColorLight
val GroupDateBg @Composable get() = if (isDarkTheme()) GroupDateBgDark else GroupDateBgLight

val GroupDonutColor @Composable get() = if (isDarkTheme()) GroupDonutColorDark else GroupDonutColorLight
val GroupDonutBg @Composable get() = if (isDarkTheme()) GroupDonutBgDark else GroupDonutBgLight

// ============================================
// رنگ‌های حالت روشن (Light Theme) - دیفالت برنامه
// ============================================
val LightBackground = Color(0xFFF5F7FB)
val LightSurface = Color(0xFFFFFFFF)
val LightSurfaceVariant = Color(0xFFEEF1FB)
val LightPrimary = BrandPrimaryBlue
val LightOnPrimary = Color(0xFFFFFFFF)
val LightPrimaryContainer = Color(0xFFDCE9FD)
val LightOnPrimaryContainer = Color(0xFF1A237E)
val LightSecondary = BrandPrimaryBlueLight
val LightOnSecondary = Color(0xFFFFFFFF)
val LightTertiary = Color(0xFF17B8C7)
val LightOnTertiary = Color(0xFFFFFFFF)
val LightError = Color(0xFFFA727D)
val LightOnError = Color(0xFFFFFFFF)
val LightOnBackground = Color(0xFF171D35)
val LightOnSurface = Color(0xFF171D35)
val LightOnSurfaceVariant = Color(0xFF68718B)
val LightOutline = Color(0xFFE0E5F0)

// ============================================
// رنگ‌های حالت تاریک (Dark Theme) - بهینه‌شده برای چشم‌آسایش
// ============================================
val DarkBackground = Color(0xFF0A0E1A)
val DarkSurface = Color(0xFF141828)
val DarkSurfaceVariant = Color(0xFF1E2438)
val DarkPrimary = Color(0xFF7C9CFF)
val DarkOnPrimary = Color(0xFF002B7C)
val DarkPrimaryContainer = Color(0xFF1A2A4A)
val DarkOnPrimaryContainer = Color(0xFFDCE9FD)
val DarkSecondary = Color(0xFF8FA8E8)
val DarkOnSecondary = Color(0xFF0A0E1A)
val DarkTertiary = Color(0xFF4DD0E1)
val DarkOnTertiary = Color(0xFF003540)
val DarkError = Color(0xFFFF8A8A)
val DarkOnError = Color(0xFF5C0006)
val DarkOnBackground = Color(0xFFE8EAF2)
val DarkOnSurface = Color(0xFFE8EAF2)
val DarkOnSurfaceVariant = Color(0xFFA8B0C5)
val DarkOutline = Color(0xFF2A3050)

// ============================================
// رنگ‌های وضعیت - theme-aware
// ============================================
val SuccessBlue @Composable get() = if (isDarkTheme()) Color(0xFF7C9CFF) else BrandPrimaryBlue
val SuccessBlueLight @Composable get() = if (isDarkTheme()) Color(0xFF1A2A4A) else Color(0xFFE8EAFA)
val DeficitRed @Composable get() = if (isDarkTheme()) Color(0xFFFF8A8A) else Color(0xFFFA727D)
val DeficitRedLight @Composable get() = if (isDarkTheme()) Color(0xFF3D1518) else Color(0xFFFFE5E5)
val WarningOrange @Composable get() = if (isDarkTheme()) Color(0xFFFFB74D) else Color(0xFFFFBC4D)
val WarningOrangeLight @Composable get() = if (isDarkTheme()) Color(0xFF33291A) else Color(0xFFFFF3E0)
val SurplusGreen @Composable get() = if (isDarkTheme()) Color(0xFF66BB6A) else Color(0xFF4CAF50)
val SurplusGreenLight @Composable get() = if (isDarkTheme()) Color(0xFF1B3320) else Color(0xFFE8F5E9)

// ============================================
// رنگ‌های گرادیان برای کارت Hero
// ============================================
val HeroGradientStart @Composable get() = if (isDarkTheme()) Color(0xFF1A2A4A) else Color(0xFFDCE9FD)
val HeroGradientEnd @Composable get() = if (isDarkTheme()) Color(0xFF0F1A35) else Color(0xFFF0F4FF)

// ============================================
// رنگ‌های متفرقه - theme-aware
// ============================================
val DividerColor @Composable get() = if (isDarkTheme()) Color(0xFF2A3050) else Color(0xFFE0E1EF)
val DividerColorDark = Color(0xFF3A3B52)
val BottomNavBg @Composable get() = if (isDarkTheme()) Color(0xFF141828) else Color(0xFFFFFFFF)
val BottomNavActiveColor @Composable get() = if (isDarkTheme()) Color(0xFF7C9CFF) else BrandPrimaryBlue
val BottomNavInactiveColor @Composable get() = if (isDarkTheme()) Color(0xFF6B7280) else Color(0xFF9CA3AF)

// ============================================
// رنگ‌های فرم‌های غیرفعال - theme-aware
// ============================================
val DisabledFieldBg @Composable get() = if (isDarkTheme()) Color(0xFF1A1F2E) else Color(0xFFF5F7FA)
val DisabledFieldBorder @Composable get() = if (isDarkTheme()) Color(0xFF2A3050) else Color(0xFFE2E8F0)
val DisabledFieldText @Composable get() = if (isDarkTheme()) Color(0xFF6B7280) else Color(0xFF94A3B8)
val DisabledFieldIcon @Composable get() = if (isDarkTheme()) Color(0xFF6B7280) else Color(0xFF94A3B8)
val EnabledFieldBorder @Composable get() = if (isDarkTheme()) Color(0xFF3A4060) else Color(0xFFCBD5E1)

// رنگ‌های قدیمی برای سازگاری
val WelcomeGradientStart @Composable get() = if (isDarkTheme()) Color(0xFF0A0E1A) else BrandPrimaryBlue
val WelcomeGradientEnd @Composable get() = if (isDarkTheme()) Color(0xFF1A237E) else Color(0xFF001A8C)
val WelcomeGradientMid = Color(0xFF1A237E)
