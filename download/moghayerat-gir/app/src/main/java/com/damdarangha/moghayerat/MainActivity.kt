package com.damdarangha.moghayerat

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.damdarangha.moghayerat.ui.navigation.AppNavigation
import com.damdarangha.moghayerat.ui.theme.MoghayeratTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // مقداردهی اولیه محصولات پیش‌فرض
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val app = application as MoghayeratApplication
                // ابتدا بررسی می‌کنیم که آیا محصولی وجود دارد یا نه
                val count = app.productRepository.countActiveByGroup("STERILE")
                if (count == 0) {
                    app.productRepository.seedDefaultProductsIfEmpty()
                }
            } catch (_: Exception) {
                // در صورت بروز خطا، فقط لاگ می‌کنیم
            }
        }

        setContent {
            MoghayeratTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    AppNavigation()
                }
            }
        }
    }
}
