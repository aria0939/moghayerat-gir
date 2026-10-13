package com.damdarangha.moghayerat.ui.motion

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.graphicsLayer

/**
 * افکت لمسی (tactile press feedback) به سبک تلگرام اندروید.
 * المان هنگام لمس به‌آرامی scale-down می‌کند (حدود ۲٪) و وقتی رها می‌شود برمی‌گردد.
 */
fun Modifier.pressable(
    onClick: () -> Unit,
    enabled: Boolean = true,
    pressScale: Float = 0.98f,
    pressAlpha: Float = 0.92f
): Modifier = composed {
    val motionSettings = LocalMotionSettings.current
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (isPressed && enabled && !motionSettings.reducedMotion) pressScale else 1f,
        animationSpec = MotionSpec.SpringNoBounce,
        label = "pressScale"
    )
    val alpha by animateFloatAsState(
        targetValue = if (isPressed && enabled && !motionSettings.reducedMotion) pressAlpha else 1f,
        animationSpec = MotionSpec.SpringNoBounce,
        label = "pressAlpha"
    )

    this
        .graphicsLayer {
            scaleX = scale
            scaleY = scale
            this.alpha = alpha
        }
        .clickable(
            interactionSource = interactionSource,
            indication = null,
            enabled = enabled,
            onClick = onClick
        )
}

/**
 * نسخه‌ی بدون clickable — فقط افکت لمسی.
 * برای کارت‌هایی که خودشان onClick دارند (مثل Material3 Card).
 */
fun Modifier.pressFeedback(
    enabled: Boolean = true,
    pressScale: Float = 0.98f
): Modifier = composed {
    val motionSettings = LocalMotionSettings.current
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (isPressed && enabled && !motionSettings.reducedMotion) pressScale else 1f,
        animationSpec = MotionSpec.SpringNoBounce,
        label = "feedbackScale"
    )

    this.graphicsLayer {
        scaleX = scale
        scaleY = scale
    }
}
