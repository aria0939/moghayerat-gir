package com.damdarangha.moghayerat.ui.screen.welcome

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.damdarangha.moghayerat.R
import com.damdarangha.moghayerat.ui.theme.BrandAccentRed
import com.damdarangha.moghayerat.ui.theme.BrandPrimaryBlue
import com.damdarangha.moghayerat.ui.theme.WelcomeGradientEnd
import com.damdarangha.moghayerat.ui.theme.WelcomeGradientStart
import kotlinx.coroutines.delay

/**
 * صفحه ورود زیبا و خلاقانه برنامه «مغایرت‌گیر دامداران»
 *
 * ویژگی‌ها:
 * - گرادیان آبی عمیق با حالت پویا
 * - لوگوی انیمیشن‌دار (ظاهر شدن با افکت)
 * - عنوان برند با انیمیشن
 * - دکمه ورود با استایل مدرن
 * - شعار برنامه
 * - انیمیشن‌های روان و چشم‌نواز
 *
 * طراحی با الهام از برند DAMDARAN
 */
@Composable
fun WelcomeScreen(
    onEnterApp: () -> Unit
) {
    // مدیریت انیمیشن ظاهر شدن المان‌ها
    var showLogo by remember { mutableStateOf(false) }
    var showTitle by remember { mutableStateOf(false) }
    var showTagline by remember { mutableStateOf(false) }
    var showButton by remember { mutableStateOf(false) }

    // ترتیب ظاهر شدن المان‌ها
    LaunchedEffect(Unit) {
        delay(200)
        showLogo = true
        delay(500)
        showTitle = true
        delay(400)
        showTagline = true
        delay(400)
        showButton = true
    }

    // انیمیشن پویا برای گرادیان پس‌زمینه
    val infiniteTransition = rememberInfiniteTransition(label = "bg")
    val blurAmount by infiniteTransition.animateFloat(
        initialValue = 40f,
        targetValue = 60f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "blur"
    )

    // انیمیشن پالس برای لوگو
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.98f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        WelcomeGradientStart,
                        WelcomeGradientEnd
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        // حلقه‌های گرادیان محو شده در پس‌زمینه برای ایجاد عمق
        Box(
            modifier = Modifier
                .size(400.dp)
                .blur(blurAmount.dp)
                .background(
                    color = BrandPrimaryBlue.copy(alpha = 0.3f),
                    shape = RoundedCornerShape(200.dp)
                )
                .align(Alignment.Center)
        )

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
        ) {
            // ===================== لوگو =====================
            AnimatedVisibility(
                visible = showLogo,
                enter = scaleIn(
                    animationSpec = tween(700),
                    initialScale = 0.3f
                ) + fadeIn(animationSpec = tween(700)),
                exit = fadeOut()
            ) {
                Box(
                    modifier = Modifier
                        .size(180.dp)
                        .scale(pulseScale)
                        .clip(RoundedCornerShape(24.dp))
                        .background(Color.White.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_logo),
                        contentDescription = "لوگوی دامداران",
                        modifier = Modifier.size(160.dp),
                        contentScale = ContentScale.Fit
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // ===================== عنوان اصلی =====================
            AnimatedVisibility(
                visible = showTitle,
                enter = fadeIn(animationSpec = tween(700)) + scaleIn(
                    animationSpec = tween(700),
                    initialScale = 0.8f
                ),
                exit = fadeOut()
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "مغایرت‌گیر",
                        fontSize = 36.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = "دامداران",
                        fontSize = 44.sp,
                        fontWeight = FontWeight.Bold,
                        color = BrandAccentRed,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    // خط زیر عنوان
                    Box(
                        modifier = Modifier
                            .size(width = 80.dp, height = 3.dp)
                            .background(
                                color = BrandAccentRed,
                                shape = RoundedCornerShape(2.dp)
                            )
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // ===================== شعار =====================
            AnimatedVisibility(
                visible = showTagline,
                enter = fadeIn(animationSpec = tween(700)),
                exit = fadeOut()
            ) {
                Text(
                    text = "مدیریت هوشمند موجودی انبار\nو کنترل دقیق مغایرت محصولات",
                    fontSize = 16.sp,
                    color = Color.White.copy(alpha = 0.85f),
                    textAlign = TextAlign.Center,
                    lineHeight = 24.sp
                )
            }

            Spacer(modifier = Modifier.height(48.dp))

            // ===================== دکمه ورود =====================
            AnimatedVisibility(
                visible = showButton,
                enter = fadeIn(animationSpec = tween(500)) + scaleIn(
                    animationSpec = tween(500),
                    initialScale = 0.8f
                ),
                exit = fadeOut()
            ) {
                Button(
                    onClick = onEnterApp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White,
                        contentColor = BrandPrimaryBlue
                    ),
                    shape = RoundedCornerShape(28.dp),
                    elevation = ButtonDefaults.buttonElevation(
                        defaultElevation = 8.dp,
                        pressedElevation = 4.dp
                    )
                ) {
                    Text(
                        text = "ورود به برنامه",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // ===================== نسخه برنامه =====================
            AnimatedVisibility(
                visible = showButton,
                enter = fadeIn(animationSpec = tween(800)),
                exit = fadeOut()
            ) {
                Text(
                    text = "نسخه ۱.۱.۰",
                    fontSize = 12.sp,
                    color = Color.White.copy(alpha = 0.5f),
                    modifier = Modifier.alpha(0.7f)
                )
            }
        }
    }
}
