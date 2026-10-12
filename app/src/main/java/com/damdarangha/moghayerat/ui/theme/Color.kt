package com.damdarangha.moghayerat.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
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
// در حالت تاریک، پس‌زمینه‌ها تیره‌تر و آیکون‌ها روشن‌تر می‌شوند
// تا کنتراست مناسب حفظ شود
// ============================================
val GroupSterileColorDark = Color(0xFF6BA8F0)    // آبی روشن‌تر برای تاریک
val GroupSterileBgDark = Color(0xFF1A2A4A)        // آبی تیره

val GroupMilkColorDark = Color(0xFF4DD0E1)        // فیروزه‌ای روشن‌تر
val GroupMilkBgDark = Color(0xFF0F2F35)            // فیروزه‌ای تیره

val GroupYoghurtColorDark = Color(0xFFFF7076)      // قرمز روشن‌تر
val GroupYoghurtBgDark = Color(0xFF3D1518)         // قرمز تیره

val GroupDateColorDark = Color(0xFFCE93D8)          // بنفش روشن‌تر
val GroupDateBgDark = Color(0xFF2E1A35)             // بنفش تیره

val GroupDonutColorDark = Color(0xFFFFB74D)        // نارنجی روشن‌تر
val GroupDonutBgDark = Color(0xFF33291A)            // زرد تیره

// ============================================
// Composable aliases - به‌صورت خودکار بر اساس تم انتخاب می‌شوند
// این مقادیر در HomeScreen و سایر صفحات استفاده می‌شوند
// ============================================
val GroupSterileColor @Composable get() = if (isSystemInDarkTheme()) GroupSterileColorDark else GroupSterileColorLight
val GroupSterileBg @Composable get() = if (isSystemInDarkTheme()) GroupSterileBgDark else GroupSterileBgLight

val GroupMilkColor @Composable get() = if (isSystemInDarkTheme()) GroupMilkColorDark else GroupMilkColorLight
val GroupMilkBg @Composable get() = if (isSystemInDarkTheme()) GroupMilkBgDark else GroupMilkBgLight

val GroupYoghurtColor @Composable get() = if (isSystemInDarkTheme()) GroupYoghurtColorDark else GroupYoghurtColorLight
val GroupYoghurtBg @Composable get() = if (isSystemInDarkTheme()) GroupYoghurtBgDark else GroupYoghurtBgLight

val GroupDateColor @Composable get() = if (isSystemInDarkTheme()) GroupDateColorDark else GroupDateColorLight
val GroupDateBg @Composable get() = if (isSystemInDarkTheme()) GroupDateBgDark else GroupDateBgLight

val GroupDonutColor @Composable get() = if (isSystemInDarkTheme()) GroupDonutColorDark else GroupDonutColorLight
val GroupDonutBg @Composable get() = if (isSystemInDarkTheme()) GroupDonutBgDark else GroupDonutBgLight

// ============================================
// رنگ‌های حالت روشن (Light Theme) - دیفالت برنامه
// ============================================
val LightBackground = Color(0xFFF5F7FB)         // پس‌زمینه روشن (سفید مایل به آبی)
val LightSurface = Color(0xFFFFFFFF)
val LightSurfaceVariant = Color(0xFFEEF1FB)     // سطح کارت‌ها
val LightPrimary = BrandPrimaryBlue
val LightOnPrimary = Color(0xFFFFFFFF)
val LightPrimaryContainer = Color(0xFFDCE9FD)   // آبی بسیار روشن برای کارت‌های فعال
val LightOnPrimaryContainer = Color(0xFF1A237E)
val LightSecondary = BrandPrimaryBlueLight
val LightOnSecondary = Color(0xFFFFFFFF)
val LightTertiary = Color(0xFF17B8C7)           // فیروزه‌ای
val LightOnTertiary = Color(0xFFFFFFFF)
val LightError = Color(0xFFFA727D)               // قرمز مرجانی
val LightOnError = Color(0xFFFFFFFF)
val LightOnBackground = Color(0xFF171D35)        // متن اصلی - سرمه‌ای
val LightOnSurface = Color(0xFF171D35)
val LightOnSurfaceVariant = Color(0xFF68718B)   // متن فرعی - خاکستری
val LightOutline = Color(0xFFE0E5F0)

// ============================================
// رنگ‌های حالت تاریک (Dark Theme) - بهینه‌شده برای چشم‌آسایش
// پس‌زمینه‌ها مشکی مایل به آبی، کارت‌ها کمی روشن‌تر، متن‌های سفید ملایم
// ============================================
val DarkBackground = Color(0xFF0A0E1A)          // مشکی مایل به آبی عمیق
val DarkSurface = Color(0xFF141828)              // سطح کارت‌ها
val DarkSurfaceVariant = Color(0xFF1E2438)        // سطح متغیر
val DarkPrimary = Color(0xFF7C9CFF)              // آبی روشن برای کنتراست روی تاریک
val DarkOnPrimary = Color(0xFF002B7C)
val DarkPrimaryContainer = Color(0xFF1A2A4A)      // آبی تیره برای کانتینر
val DarkOnPrimaryContainer = Color(0xFFDCE9FD)
val DarkSecondary = Color(0xFF8FA8E8)            // آبی ثانویه روشن
val DarkOnSecondary = Color(0xFF0A0E1A)
val DarkTertiary = Color(0xFF4DD0E1)              // فیروزه‌ای روشن
val DarkOnTertiary = Color(0xFF003540)
val DarkError = Color(0xFFFF8A8A)                 // قرمز روشن برای تاریک
val DarkOnError = Color(0xFF5C0006)
val DarkOnBackground = Color(0xFFE8EAF2)         // متن سفید ملایم
val DarkOnSurface = Color(0xFFE8EAF2)
val DarkOnSurfaceVariant = Color(0xFFA8B0C5)     // متن فرعی - خاکستری آبی
val DarkOutline = Color(0xFF2A3050)              // حاشیه‌ها - آبی تیره

// ============================================
// رنگ‌های وضعیت
// ============================================
val SuccessBlue @Composable get() = if (isSystemInDarkTheme()) Color(0xFF7C9CFF) else BrandPrimaryBlue
val SuccessBlueLight @Composable get() = if (isSystemInDarkTheme()) Color(0xFF1A2A4A) else Color(0xFFE8EAFA)
val DeficitRed @Composable get() = if (isSystemInDarkTheme()) Color(0xFFFF8A8A) else Color(0xFFFA727D)
val DeficitRedLight @Composable get() = if (isSystemInDarkTheme()) Color(0xFF3D1518) else Color(0xFFFFE5E5)
val WarningOrange @Composable get() = if (isSystemInDarkTheme()) Color(0xFFFFB74D) else Color(0xFFFFBC4D)
val WarningOrangeLight @Composable get() = if (isSystemInDarkTheme()) Color(0xFF33291A) else Color(0xFFFFF3E0)
val SurplusGreen @Composable get() = if (isSystemInDarkTheme()) Color(0xFF66BB6A) else Color(0xFF4CAF50)
val SurplusGreenLight @Composable get() = if (isSystemInDarkTheme()) Color(0xFF1B3320) else Color(0xFFE8F5E9)

// ============================================
// رنگ‌های گرادیان برای کارت Hero صفحه اصلی
// ============================================
val HeroGradientStart @Composable get() = if (isSystemInDarkTheme()) Color(0xFF1A2A4A) else Color(0xFFDCE9FD)
val HeroGradientEnd @Composable get() = if (isSystemInDarkTheme()) Color(0xFF0F1A35) else Color(0xFFF0F4FF)

// ============================================
// رنگ‌های متفرقه
// ============================================
val DividerColor @Composable get() = if (isSystemInDarkTheme()) Color(0xFF2A3050) else Color(0xFFE0E1EF)
val DividerColorDark = Color(0xFF3A3B52)
val BottomNavBg @Composable get() = if (isSystemInDarkTheme()) Color(0xFF141828) else Color(0xFFFFFFFF)
val BottomNavActiveColor @Composable get() = if (isSystemInDarkTheme()) Color(0xFF7C9CFF) else BrandPrimaryBlue
val BottomNavInactiveColor @Composable get() = if (isSystemInDarkTheme()) Color(0xFF6B7280) else Color(0xFF9CA3AF)

// ============================================
// رنگ‌های فرم‌های غیرفعال - مطابق تصویر مرجع
// در حالت تاریک، رنگ‌ها متناسب با تم تیره می‌شوند
// ============================================
val DisabledFieldBg @Composable get() = if (isSystemInDarkTheme()) Color(0xFF1A1F2E) else Color(0xFFF5F7FA)
val DisabledFieldBorder @Composable get() = if (isSystemInDarkTheme()) Color(0xFF2A3050) else Color(0xFFE2E8F0)
val DisabledFieldText @Composable get() = if (isSystemInDarkTheme()) Color(0xFF6B7280) else Color(0xFF94A3B8)
val DisabledFieldIcon @Composable get() = if (isSystemInDarkTheme()) Color(0xFF6B7280) else Color(0xFF94A3B8)
val EnabledFieldBorder @Composable get() = if (isSystemInDarkTheme()) Color(0xFF3A4060) else Color(0xFFCBD5E1)

// رنگ‌های قدیمی برای سازگاری (deprecated)
val WelcomeGradientStart @Composable get() = if (isSystemInDarkTheme()) Color(0xFF0A0E1A) else BrandPrimaryBlue
val WelcomeGradientEnd @Composable get() = if (isSystemInDarkTheme()) Color(0xFF1A237E) else Color(0xFF001A8C)
val WelcomeGradientMid = Color(0xFF1A237E)
