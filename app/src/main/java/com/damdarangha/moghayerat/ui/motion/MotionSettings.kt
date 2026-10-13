package com.damdarangha.moghayerat.ui.motion

import android.content.Context
import android.provider.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext

/**
 * معادل prefers-reduced-motion در وب — از طریق Android AccessibilityManager تشخیص داده می‌شود.
 */
data class MotionSettings(
    val reducedMotion: Boolean = false
)

val LocalMotionSettings = compositionLocalOf { MotionSettings() }

@Composable
fun rememberMotionSettings(): MotionSettings {
    val context = LocalContext.current
    val reduced = remember(context) { isReduceMotionEnabled(context) }
    return MotionSettings(reducedMotion = reduced)
}

private fun isReduceMotionEnabled(context: Context): Boolean {
    return try {
        val animator = Settings.Global.getFloat(
            context.contentResolver,
            Settings.Global.ANIMATOR_DURATION_SCALE,
            1f
        )
        val transition = Settings.Global.getFloat(
            context.contentResolver,
            Settings.Global.TRANSITION_ANIMATION_SCALE,
            1f
        )
        animator == 0f || transition == 0f
    } catch (_: Throwable) {
        false
    }
}
