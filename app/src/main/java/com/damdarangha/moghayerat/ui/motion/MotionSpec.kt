package com.damdarangha.moghayerat.ui.motion

import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.ui.unit.IntOffset

/**
 * مرکز پیکربندی انیمیشن‌های برنامه — تنها منبع حقیقت برای durations / easings / springs.
 * الهام‌گرفته از motion system تلگرام اندروید.
 */
object MotionSpec {

    const val DURATION_FORWARD = 280
    const val DURATION_BACKWARD = 220
    const val DURATION_CONTAINER_TRANSFORM = 320
    const val DURATION_FADE = 180
    const val DURATION_MICRO = 120

    const val SWIPE_EDGE_WIDTH_DP = 24
    const val SWIPE_COMPLETE_RATIO = 0.35f
    const val SWIPE_MIN_VELOCITY = 600f

    val EasingStandard = CubicBezierEasing(0.2f, 0.0f, 0.0f, 1.0f)
    val EasingEmphasized = CubicBezierEasing(0.3f, 0.0f, 0.0f, 1.0f)

    val SpringMedium = spring<Float>(
        dampingRatio = Spring.DampingRatioMediumBouncy,
        stiffness = Spring.StiffnessMedium
    )

    val SpringNoBounce = spring<Float>(
        dampingRatio = Spring.DampingRatioNoBouncy,
        stiffness = Spring.StiffnessMedium
    )

    val SpringOffsetNoBounce = spring<IntOffset>(
        dampingRatio = Spring.DampingRatioNoBouncy,
        stiffness = Spring.StiffnessMedium
    )

    val SpringScale = spring<Float>(
        dampingRatio = 0.85f,
        stiffness = 350f
    )

    fun <T> forwardTween() = tween<T>(DURATION_FORWARD, easing = EasingStandard)
    fun <T> backwardTween() = tween<T>(DURATION_BACKWARD, easing = EasingStandard)
    fun <T> fadeTween() = tween<T>(DURATION_FADE, easing = EasingStandard)
}
