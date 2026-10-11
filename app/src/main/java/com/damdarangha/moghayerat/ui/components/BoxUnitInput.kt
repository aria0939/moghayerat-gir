package com.damdarangha.moghayerat.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.damdarangha.moghayerat.domain.model.BoxAndUnits
import com.damdarangha.moghayerat.domain.model.Product
import com.damdarangha.moghayerat.domain.model.persianToEnglishDigits

/**
 * کامپوننت ورودی باکس و عدد با طراحی بهبودیافته.
 *
 * ویژگی‌های جدید:
 * - دکمه پاک‌سازی سریع (X) در هر فیلد
 * - مکان‌نما در وسط فیلد (نه سمت راست)
 * - افکت‌های زیبا هنگام فوکوس:
 *   * تغییر رنگ حاشیه با انیمیشن
 *   * تغییر ارتفاع سایه
 *   * تغییر اندازه آیکون پاک‌سازی
 *   * تغییر شفافیت کارت
 * - خوانایی بهتر با اعداد فارسی
 * - پشتیبانی از باکس رند (غیرفعال کردن فیلد عدد)
 */
@Composable
fun BoxUnitInput(
    label: String,
    value: BoxAndUnits,
    product: Product,
    onValueChange: (BoxAndUnits) -> Unit,
    modifier: Modifier = Modifier,
    showTotal: Boolean = true,
    enabled: Boolean = true
) {
    val isRoundBox = product.isRoundBox

    val boxesInteractionSource = remember { MutableInteractionSource() }
    val unitsInteractionSource = remember { MutableInteractionSource() }
    val boxesFocused by boxesInteractionSource.collectIsFocusedAsState()
    val unitsFocused by unitsInteractionSource.collectIsFocusedAsState()
    val isAnyFocused = (boxesFocused || unitsFocused) && enabled

    // انیمیشن ارتفاع
    val elevation by animateFloatAsState(
        targetValue = if (isAnyFocused) 8f else 0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "elevation"
    )

    // انیمیشن شفافیت
    val cardAlpha by animateFloatAsState(
        targetValue = if (isAnyFocused) 1f else 0.95f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioNoBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "cardAlpha"
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .alpha(cardAlpha),
        colors = CardDefaults.cardColors(
            containerColor = if (isAnyFocused) {
                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
            } else {
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
            }
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = elevation.dp),
        shape = RoundedCornerShape(16.dp),
        border = if (isAnyFocused) {
            BorderStroke(2.dp, MaterialTheme.colorScheme.primary)
        } else null
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(
                text = label,
                style = MaterialTheme.typography.titleSmall,
                color = if (isAnyFocused) MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.onSurface,
                fontWeight = if (isAnyFocused) FontWeight.Bold else FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(10.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                EnhancedNumericField(
                    value = value.boxes,
                    label = "باکس",
                    onValueChange = { newBoxes ->
                        onValueChange(BoxAndUnits(boxes = newBoxes, units = value.units))
                    },
                    modifier = Modifier.weight(1f),
                    enabled = enabled,
                    interactionSource = boxesInteractionSource,
                    focused = boxesFocused
                )

                Spacer(modifier = Modifier.width(4.dp))

                EnhancedNumericField(
                    value = value.units,
                    label = "عدد",
                    onValueChange = { newUnits ->
                        if (!isRoundBox) {
                            onValueChange(BoxAndUnits(boxes = value.boxes, units = newUnits))
                        }
                    },
                    modifier = Modifier.weight(1f),
                    enabled = enabled && !isRoundBox,
                    interactionSource = unitsInteractionSource,
                    focused = unitsFocused
                )
            }

            if (!isRoundBox && showTotal) {
                Spacer(modifier = Modifier.height(8.dp))
                val total = value.boxes * product.boxCapacity + value.units
                Text(
                    text = "ظرفیت باکس: ${product.boxCapacity.toPersianDigitsString()} | معادل کل: ${total.toPersianDigitsString()} عدد",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.End,
                    modifier = Modifier.fillMaxWidth()
                )
            } else if (isRoundBox) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "این محصول با باکس رند ثبت می‌شود",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.End,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

/**
 * فیلد عددی بهبودیافته با دکمه پاک‌سازی و افکت فوکوس.
 */
@Composable
private fun EnhancedNumericField(
    value: Int,
    label: String,
    onValueChange: (Int) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    interactionSource: MutableInteractionSource,
    focused: Boolean
) {
    var textFieldValue by remember {
        mutableStateOf(
            TextFieldValue(
                text = if (value == 0) "" else value.toString(),
                selection = androidx.compose.ui.text.TextRange(0)
            )
        )
    }

    // به‌روزرسانی TextFieldValue وقتی value از بیرون تغییر میکند
    LaunchedEffect(value) {
        val newText = if (value == 0) "" else value.toString()
        if (textFieldValue.text != newText) {
            textFieldValue = TextFieldValue(
                text = newText,
                selection = androidx.compose.ui.text.TextRange(newText.length)
            )
        }
    }

    val borderAlpha by animateFloatAsState(
        targetValue = if (focused) 1f else 0.5f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioNoBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "borderAlpha"
    )

    val clearBtnScale by animateFloatAsState(
        targetValue = if (focused) 1.1f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "clearBtnScale"
    )

    Box(
        modifier = modifier
            .height(56.dp)
            .background(
                color = if (focused) {
                    MaterialTheme.colorScheme.surface
                } else {
                    MaterialTheme.colorScheme.surface.copy(alpha = 0.7f)
                },
                shape = RoundedCornerShape(12.dp)
            )
            .border(
                width = if (focused) 2.dp else 1.dp,
                color = if (focused) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.outline.copy(alpha = borderAlpha)
                },
                shape = RoundedCornerShape(12.dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 12.dp)
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = if (focused) MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.alpha(if (focused) 0.9f else 0.7f)
            )

            Spacer(modifier = Modifier.width(8.dp))

            BasicTextField(
                value = textFieldValue,
                onValueChange = { newValue ->
                    val normalized = newValue.text.persianToEnglishDigits()
                    val filtered = normalized.filter { it.isDigit() }
                    if (filtered.isEmpty()) {
                        textFieldValue = TextFieldValue(
                            text = "",
                            selection = androidx.compose.ui.text.TextRange(0)
                        )
                        onValueChange(0)
                    } else {
                        val intValue = filtered.toIntOrNull() ?: 0
                        if (intValue >= 0) {
                            textFieldValue = TextFieldValue(
                                text = filtered,
                                selection = newValue.selection.let {
                                    val start = it.start.coerceIn(0, filtered.length)
                                    val end = it.end.coerceIn(0, filtered.length)
                                    androidx.compose.ui.text.TextRange(start, end)
                                }
                            )
                            onValueChange(intValue)
                        }
                    }
                },
                modifier = Modifier.weight(1f),
                enabled = enabled,
                textStyle = TextStyle(
                    fontFamily = MaterialTheme.typography.bodyLarge.fontFamily,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium,
                    color = if (enabled) MaterialTheme.colorScheme.onSurface
                    else MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Start
                ),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                interactionSource = interactionSource
            )

            AnimatedVisibility(
                visible = value != 0 && enabled,
                enter = fadeIn() + scaleIn(),
                exit = fadeOut()
            ) {
                IconButton(
                    onClick = {
                        textFieldValue = TextFieldValue(
                            text = "",
                            selection = androidx.compose.ui.text.TextRange(0)
                        )
                        onValueChange(0)
                    },
                    modifier = Modifier
                        .size(28.dp)
                        .scale(clearBtnScale)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "پاک کردن",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

private fun Int.toPersianDigitsString(): String {
    val persianDigits = arrayOf('۰', '۱', '۲', '۳', '۴', '۵', '۶', '۷', '۸', '۹')
    return this.toString().map { c ->
        if (c.isDigit()) persianDigits[c - '0'] else c
    }.joinToString("")
}
