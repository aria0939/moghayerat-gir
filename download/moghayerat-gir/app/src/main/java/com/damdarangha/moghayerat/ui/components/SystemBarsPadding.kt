package com.damdarangha.moghayerat.ui.components

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/**
 * Modifier ای برای افزودن padding نوار وضعیت (notification bar) به بالای صفحه.
 *
 * این تابع مشکل overlap محتوا با نوار وضعیت گوشی را حل می‌کند.
 * صفحه به‌اندازه ارتفاع نوار وضعیت به پایین حرکت می‌کند.
 */
fun Modifier.statusBarsPadding(): Modifier = this.windowInsetsPadding(
    WindowInsets.statusBars
)

/**
 * Modifier ای برای افزودن padding نوار ناوبری (navigation bar) به پایین صفحه.
 */
fun Modifier.navigationBarsPadding(): Modifier = this.windowInsetsPadding(
    WindowInsets.navigationBars
)

/**
 * Modifier ای برای افزودن padding نوار وضعیت + نوار ناوبری.
 */
fun Modifier.systemBarsPadding(): Modifier = this.windowInsetsPadding(
    WindowInsets.systemBars
)
