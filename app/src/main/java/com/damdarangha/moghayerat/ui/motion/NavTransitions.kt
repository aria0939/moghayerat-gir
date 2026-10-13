package com.damdarangha.moghayerat.ui.motion

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection

/**
 * RTL-aware horizontal slide+fade transitions به سبک تلگرام اندروید.
 *
 * RTL-aware یعنی:
 *  - در RTL (فارسی): صفحه‌ی جدید از چپ وارد می‌شود، چون جهت خواندن طبیعی برعکس است.
 *  - در LTR (انگلیسی): صفحه‌ی جدید از راست وارد می‌شود.
 */

fun forwardEnterTransition(isRtl: Boolean): EnterTransition {
    val direction = if (isRtl) -1 else 1
    return slideInHorizontally(
        animationSpec = tween(MotionSpec.DURATION_FORWARD, easing = MotionSpec.EasingStandard),
        initialOffsetX = { fullWidth -> direction * fullWidth }
    ) + fadeIn(animationSpec = tween(MotionSpec.DURATION_FADE, easing = MotionSpec.EasingStandard))
}

fun forwardExitTransition(isRtl: Boolean): ExitTransition {
    val direction = if (isRtl) 1 else -1
    return slideOutHorizontally(
        animationSpec = tween(MotionSpec.DURATION_FORWARD, easing = MotionSpec.EasingStandard),
        targetOffsetX = { fullWidth -> direction * fullWidth }
    ) + fadeOut(animationSpec = tween(MotionSpec.DURATION_FADE, easing = MotionSpec.EasingStandard))
}

fun backwardEnterTransition(isRtl: Boolean): EnterTransition {
    val direction = if (isRtl) 1 else -1
    return slideInHorizontally(
        animationSpec = tween(MotionSpec.DURATION_BACKWARD, easing = MotionSpec.EasingStandard),
        initialOffsetX = { fullWidth -> direction * fullWidth }
    ) + fadeIn(animationSpec = tween(MotionSpec.DURATION_FADE, easing = MotionSpec.EasingStandard))
}

fun backwardExitTransition(isRtl: Boolean): ExitTransition {
    val direction = if (isRtl) -1 else 1
    return slideOutHorizontally(
        animationSpec = tween(MotionSpec.DURATION_BACKWARD, easing = MotionSpec.EasingStandard),
        targetOffsetX = { fullWidth -> direction * fullWidth }
    ) + fadeOut(animationSpec = tween(MotionSpec.DURATION_FADE, easing = MotionSpec.EasingStandard))
}

fun reducedEnterTransition(): EnterTransition = fadeIn(animationSpec = tween(MotionSpec.DURATION_FADE))
fun reducedExitTransition(): ExitTransition = fadeOut(animationSpec = tween(MotionSpec.DURATION_FADE))

@Composable
fun isRtlLayout(): Boolean = LocalLayoutDirection.current == LayoutDirection.Rtl

@Composable
fun ProvideMotionSettings(content: @Composable () -> Unit) {
    val settings = rememberMotionSettings()
    CompositionLocalProvider(LocalMotionSettings provides settings) {
        content()
    }
}
