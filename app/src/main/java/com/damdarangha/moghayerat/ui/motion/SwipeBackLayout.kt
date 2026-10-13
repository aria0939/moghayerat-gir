package com.damdarangha.moghayerat.ui.motion

import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import kotlin.math.abs

/**
 * لایه‌ی swipe-back که صفحه را به دنبال انگشت کاربر می‌برد.
 *
 * ویژگی‌ها:
 *  - تشخیص شروع drag از لبه‌ی شروع زبان (در RTL = لبه راست، در LTR = لبه چپ).
 *  - صفحه به‌صورت پیوسته دنبال انگشت می‌رود.
 *  - با عبور از آستانه (default 35٪ عرض صفحه)، navigation تکمیل می‌شود.
 *  - در غیر این صورت، صفحه با spring ملایم برمی‌گردد.
 *
 * نکته: صفحاتی که unsaved state دارند (مثل فرم ورود آمار) نباید از این لایه استفاده کنند.
 */
@Composable
fun SwipeBackLayout(
    enabled: Boolean = true,
    onSwipeComplete: () -> Unit,
    content: @Composable () -> Unit
) {
    val density = LocalDensity.current
    val motionSettings = LocalMotionSettings.current
    val isRtl = LocalLayoutDirection.current == LayoutDirection.Rtl

    val effectiveEnabled = enabled && !motionSettings.reducedMotion

    var screenWidthPx by remember { mutableFloatStateOf(0f) }
    var dragOffsetPx by remember { mutableFloatStateOf(0f) }
    var isDragging by remember { mutableStateOf(false) }

    val thresholdPx = remember(screenWidthPx) {
        screenWidthPx * MotionSpec.SWIPE_COMPLETE_RATIO
    }

    val animatedOffset by androidx.compose.animation.core.animateFloatAsState(
        targetValue = dragOffsetPx,
        animationSpec = MotionSpec.SpringNoBounce,
        label = "swipeOffset"
    )

    val displayOffset = if (isDragging) dragOffsetPx else animatedOffset
    val progress = if (screenWidthPx > 0) (displayOffset / screenWidthPx).coerceIn(0f, 1f) else 0f

    Box(
        modifier = Modifier
            .fillMaxSize()
            .onSizeChanged { size ->
                screenWidthPx = size.width.toFloat()
            }
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    translationX = displayOffset
                    val scale = 1f - 0.05f * progress
                    scaleX = scale
                    scaleY = scale
                    alpha = 1f - 0.3f * progress
                }
        ) {
            content()
        }

        if (effectiveEnabled) {
            val edgeWidthPx = with(density) { MotionSpec.SWIPE_EDGE_WIDTH_DP.dp.toPx() }
            val edgeAlignment = if (isRtl) Alignment.CenterEnd else Alignment.CenterStart

            Box(
                modifier = Modifier
                    .align(edgeAlignment)
                    .fillMaxSize()
                    .width(with(density) { edgeWidthPx.toDp() })
                    .pointerInput(effectiveEnabled) {
                        if (!effectiveEnabled) return@pointerInput
                        detectDragGestures(
                            onDragStart = { _ ->
                                isDragging = true
                                dragOffsetPx = 0f
                            },
                            onDragEnd = {
                                isDragging = false
                                val shouldComplete = abs(dragOffsetPx) >= thresholdPx
                                if (shouldComplete) {
                                    dragOffsetPx = screenWidthPx
                                    onSwipeComplete()
                                } else {
                                    dragOffsetPx = 0f
                                }
                            },
                            onDragCancel = {
                                isDragging = false
                                dragOffsetPx = 0f
                            },
                            onDrag = { change: PointerInputChange, dragAmount: Offset ->
                                val dragDelta = if (isRtl) dragAmount.x else -dragAmount.x
                                if (dragDelta > 0) {
                                    dragOffsetPx = (dragOffsetPx + dragDelta).coerceIn(0f, screenWidthPx)
                                } else if (dragOffsetPx > 0) {
                                    dragOffsetPx = (dragOffsetPx + dragDelta).coerceIn(0f, screenWidthPx)
                                }
                                change.consume()
                            }
                        )
                    }
            )
        }
    }
}
