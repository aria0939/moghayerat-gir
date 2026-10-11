package com.damdarangha.moghayerat.ui.components

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.statusBars
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/**
 * یک TopAppBar که به‌صورت خودکار padding نوار وضعیت (notification bar) را اضافه می‌کند.
 *
 * این تابع مشکل overlap محتوا با notification bar گوشی را حل می‌کند.
 *
 * استفاده:
 * ```
 * Scaffold(
 *     topBar = {
 *         StatusBarAwareTopAppBar(
 *             title = { Text("عنوان") },
 *             navigationIcon = { ... }
 *         )
 *     }
 * ) { padding ->
 *     // content
 * }
 * ```
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatusBarAwareTopAppBar(
    title: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    navigationIcon: @Composable () -> Unit = {},
    actions: @Composable androidx.compose.foundation.layout.RowScope.() -> Unit = {}
) {
    TopAppBar(
        title = title,
        modifier = modifier,
        navigationIcon = navigationIcon,
        actions = actions,
        windowInsets = WindowInsets.statusBars
    )
}
