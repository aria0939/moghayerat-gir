package com.damdarangha.moghayerat.ui.motion

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.ui.unit.IntOffset

/**
 * مرکز پیکربندی انیمیشن‌های برنامه — تنها منبع حقیقت برای durations / easings / springs.
 *
 * مقادیر روی snappy و سریع تنظیم شده‌اند تا حس native و روان داشته باشیم.
 * مدت‌زمان‌ها کوتاه هستند تا هنگام ناوبری سریع، انیمیشن‌ها تداخل نکنند.
 */
object MotionSpec {

    /** مدت‌زمان push forward — کوتاه و snappy. */
    const val DURATION_FORWARD = 200

    /** مدت‌زمان pop backward — کمی سریع‌تر. */
    const val DURATION_BACKWARD = 170

    /** مدت‌زمان container transform. */
    const val DURATION_CONTAINER_TRANSFORM = 220

    /** مدت‌زمان fade. */
    const val DURATION_FADE = 130

    /** مدت‌زمان microinteractions. */
    const val DURATION_MICRO = 100

    const val SWIPE_EDGE_WIDTH_DP = 24
    const val SWIPE_COMPLETE_RATIO = 0.35f
    const val SWIPE_MIN_VELOCITY = 600f

    /** Easing استاندارد اندروید — سریع شروع، ملایم پایان. */
    val EasingStandard = FastOutSlowInEasing

    /** Easing برای ورود المان‌های جدید. */
    val EasingEmphasized = LinearOutSlowInEasing

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
