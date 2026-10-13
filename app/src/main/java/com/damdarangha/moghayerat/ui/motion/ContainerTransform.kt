package com.damdarangha.moghayerat.ui.motion

import androidx.compose.animation.ExitTransition
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.ui.Modifier

/**
 * Container transform سبک — جایگزین SharedTransitionLayout برای نسخه‌ی Compose 1.6.
 *
 * چون SharedTransitionLayout فقط در Compose 1.7+ در دسترس است، این نسخه‌ی سبک
 * را پیاده‌سازی کردیم که از نظر بصری شبیه container transform است:
 *  - المان جدید از scale 0.92 و alpha 0 به scale 1 و alpha 1 می‌رسد.
 *  - هنگام خروج، برعکس.
 */

fun containerEnterTransition() = scaleIn(
    animationSpec = tween(MotionSpec.DURATION_CONTAINER_TRANSFORM, easing = MotionSpec.EasingEmphasized),
    initialScale = 0.92f
) + fadeIn(
    animationSpec = tween(MotionSpec.DURATION_CONTAINER_TRANSFORM, easing = MotionSpec.EasingEmphasized)
)

fun containerExitTransition() = scaleOut(
    animationSpec = tween(MotionSpec.DURATION_CONTAINER_TRANSFORM, easing = MotionSpec.EasingStandard),
    targetScale = 1.05f
) + fadeOut(
    animationSpec = tween(MotionSpec.DURATION_FADE, easing = MotionSpec.EasingStandard)
)

fun containerPopEnterTransition() = scaleIn(
    animationSpec = tween(MotionSpec.DURATION_BACKWARD, easing = MotionSpec.EasingStandard),
    initialScale = 1.05f
) + fadeIn(
    animationSpec = tween(MotionSpec.DURATION_FADE, easing = MotionSpec.EasingStandard)
)

fun containerPopExitTransition() = scaleOut(
    animationSpec = tween(MotionSpec.DURATION_BACKWARD, easing = MotionSpec.EasingStandard),
    targetScale = 0.92f
) + fadeOut(
    animationSpec = tween(MotionSpec.DURATION_FADE, easing = MotionSpec.EasingStandard)
)

fun Modifier.containerTransformSource(): Modifier = this
