package com.damdarangha.moghayerat

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.damdarangha.moghayerat.data.datastore.ThemeMode
import com.damdarangha.moghayerat.ui.navigation.AppNavigation
import com.damdarangha.moghayerat.ui.theme.MoghayeratTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // فعال‌سازی edge-to-edge برای پشتیبانی از notch و status bar
        enableEdgeToEdge()

        // مقداردهی اولیه محصولات پیش‌فرض
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val app = application as MoghayeratApplication
                val count = app.productRepository.countActiveByGroup("STERILE")
                if (count == 0) {
                    app.productRepository.seedDefaultProductsIfEmpty()
                }
            } catch (_: Exception) {
                // در صورت بروز خطا، فقط لاگ می‌کنیم
            }
        }

        setContent {
            val app = application as MoghayeratApplication

            // خواندن تنظیمات تم از DataStore به‌صورت StateFlow
            // دیفالت: LIGHT (مطابق طراحی جدید)
            val themeMode by app.settingsDataStore.themeMode.collectAsState(initial = ThemeMode.LIGHT)

            // تعیین اینکه آیا تم تاریک باید فعال باشد
            val systemDark = isSystemInDarkTheme()
            val darkTheme = when (themeMode) {
                ThemeMode.LIGHT -> false
                ThemeMode.DARK -> true
                ThemeMode.SYSTEM -> systemDark  // پیروی از تم سیستم
            }

            MoghayeratTheme(darkTheme = darkTheme) {
                Surface(modifier = Modifier.fillMaxSize()) {
                    AppNavigation()
                }
            }
        }
    }
}
