package com.damdarangha.moghayerat.ui.theme

import androidx.compose.ui.graphics.Color

// ============================================
// پالت رنگ جدید - الهام‌گرفته از تصویر مرجع
// مینیمال، روشن، حرفه‌ای با رنگ‌های پاستلی
// ============================================

// رنگ‌های اصلی برند
val BrandPrimaryBlue = Color(0xFF123DA8)       // آبی اصلی
val BrandPrimaryBlueLight = Color(0xFF4F7DF3) // آبی روشن
val BrandAccentRed = Color(0xFFFA727D)         // قرمز مرجانی (compat)
val BrandBackground = Color(0xFFFFFFFF)        // پس‌زمینه سفید
val BrandSurfaceWhite = Color(0xFFFFFFFF)      // سفید کارت‌ها

// رنگ‌های گروه‌های محصول (پاستلی)
val GroupSterileColor = Color(0xFF4A90E2)       // آبی برای استریل
val GroupSterileBg = Color(0xFFEBF3FF)          // پس‌زمینه آبی بسیار روشن

val GroupMilkColor = Color(0xFF26C6DA)          // فیروزه‌ای برای شیر
val GroupMilkBg = Color(0xFFE0F7FA)             // پس‌زمینه فیروزه‌ای بسیار روشن

val GroupYoghurtColor = Color(0xFFEF5350)       // قرمز مرجانی برای ماست پاکتی
val GroupYoghurtBg = Color(0xFFFFEBEE)          // پس‌زمینه قرمز بسیار روشن

val GroupDateColor = Color(0xFFAB47BC)          // بنفش برای خرما
val GroupDateBg = Color(0xFFF3E5F5)             // پس‌زمینه بنفش بسیار روشن

val GroupDonutColor = Color(0xFFFFA726)         // نارنجی/زرد برای دونات و کیک
val GroupDonutBg = Color(0xFFFFF8E1)            // پس‌زمینه زرد بسیار روشن

// رنگ‌های حالت روشن (Light Theme) - دیفالت برنامه
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

// رنگ‌های حالت تاریک (Dark Theme) - برای زمانی که کاربر انتخاب کند
val DarkBackground = Color(0xFF0F0F1A)
val DarkSurface = Color(0xFF1A1B2E)
val DarkSurfaceVariant = Color(0xFF252840)
val DarkPrimary = Color(0xFFBAC3FF)
val DarkOnPrimary = Color(0xFF001A6E)
val DarkPrimaryContainer = Color(0xFF1A237E)
val DarkOnPrimaryContainer = Color(0xFFE8EAFA)
val DarkSecondary = Color(0xFF7986CB)
val DarkOnSecondary = Color(0xFF0F0F1A)
val DarkTertiary = Color(0xFFFF8A80)
val DarkOnTertiary = Color(0xFFFFFFFF)
val DarkError = Color(0xFFFFB4AB)
val DarkOnError = Color(0xFF690005)
val DarkOnBackground = Color(0xFFE3E2E9)
val DarkOnSurface = Color(0xFFE3E2E9)
val DarkOnSurfaceVariant = Color(0xFFB4B5CC)
val DarkOutline = Color(0xFF3A3B52)

// رنگ‌های وضعیت
val SuccessBlue = BrandPrimaryBlue
val SuccessBlueLight = Color(0xFFE8EAFA)
val DeficitRed = Color(0xFFFA727D)              // قرمز مرجانی
val DeficitRedLight = Color(0xFFFFE5E5)
val WarningOrange = Color(0xFFFFBC4D)           // زرد
val WarningOrangeLight = Color(0xFFFFF3E0)
val SurplusGreen = Color(0xFF4CAF50)
val SurplusGreenLight = Color(0xFFE8F5E9)

// رنگ‌های گرادیان برای کارت Hero صفحه اصلی
val HeroGradientStart = Color(0xFFDCE9FD)      // آبی روشن
val HeroGradientEnd = Color(0xFFF0F4FF)         // آبی بسیار روشن

// رنگ‌های متفرقه
val DividerColor = Color(0xFFE0E1EF)
val DividerColorDark = Color(0xFF3A3B52)
val BottomNavBg = Color(0xFFFFFFFF)
val BottomNavActiveColor = BrandPrimaryBlue
val BottomNavInactiveColor = Color(0xFF9CA3AF)

// رنگ‌های قدیمی برای سازگاری (deprecated - در فرآینده حذف)
val WelcomeGradientStart = BrandPrimaryBlue
val WelcomeGradientEnd = Color(0xFF001A8C)
val WelcomeGradientMid = Color(0xFF1A237E)
