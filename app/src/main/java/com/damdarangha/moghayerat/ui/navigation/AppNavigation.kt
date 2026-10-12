package com.damdarangha.moghayerat.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.damdarangha.moghayerat.ui.components.BottomNavBar
import com.damdarangha.moghayerat.ui.screen.backup.BackupScreen
import com.damdarangha.moghayerat.ui.screen.history.HistoryScreen
import com.damdarangha.moghayerat.ui.screen.home.HomeScreen
import com.damdarangha.moghayerat.ui.screen.productlist.ProductListScreen
import com.damdarangha.moghayerat.ui.screen.productmanagement.ProductManagementScreen
import com.damdarangha.moghayerat.ui.screen.reports.ReportsScreen
import com.damdarangha.moghayerat.ui.screen.settings.SettingsScreen
import com.damdarangha.moghayerat.ui.screen.stat.StatEntryScreen
import com.damdarangha.moghayerat.ui.screen.welcome.WelcomeScreen

/**
 * مسیرهای صفحه‌های برنامه
 */
object Routes {
    const val WELCOME = "welcome"
    const val HOME = "home"
    const val PRODUCT_LIST = "product_list/{group}"
    const val STAT_ENTRY = "stat_entry/{productId}/{isoDate}"
    const val REPORTS = "reports"
    const val HISTORY = "history"
    const val SETTINGS = "settings"
    const val PRODUCT_MANAGEMENT = "product_management"
    const val BACKUP = "backup"

    fun productList(group: String) = "product_list/$group"
    fun statEntry(productId: Long, isoDate: String) = "stat_entry/$productId/$isoDate"
}

/**
 * صفحاتی که دارای نوار ناوبری پایین هستند
 */
val bottomNavRoutes = setOf(
    Routes.HOME, Routes.REPORTS, Routes.PRODUCT_MANAGEMENT, Routes.SETTINGS
)

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    var currentRoute by remember { mutableStateOf(Routes.WELCOME) }

    NavHost(navController = navController, startDestination = Routes.WELCOME) {

        composable(Routes.WELCOME) {
            WelcomeScreen(
                onEnterApp = {
                    navController.navigate(Routes.HOME) {
                        popUpTo(Routes.WELCOME) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.HOME) {
            ScaffoldWithBottomNav(
                currentRoute = Routes.HOME,
                onNavigate = { route ->
                    when (route) {
                        Routes.HOME -> { /* همین صفحه */ }
                        Routes.REPORTS -> navController.navigate(Routes.REPORTS) {
                            popUpTo(Routes.HOME) { inclusive = false }
                            launchSingleTop = true
                        }
                        Routes.PRODUCT_MANAGEMENT -> navController.navigate(Routes.PRODUCT_MANAGEMENT) {
                            popUpTo(Routes.HOME) { inclusive = false }
                            launchSingleTop = true
                        }
                        Routes.SETTINGS -> navController.navigate(Routes.SETTINGS) {
                            popUpTo(Routes.HOME) { inclusive = false }
                            launchSingleTop = true
                        }
                    }
                }
            ) { padding ->
                Box(modifier = Modifier.fillMaxSize().padding(padding)) {
                    HomeScreen(
                        onGroupClick = { group ->
                            navController.navigate(Routes.productList(group.name))
                        },
                        onReportsClick = { navController.navigate(Routes.REPORTS) },
                        onHistoryClick = { navController.navigate(Routes.HISTORY) },
                        onSettingsClick = { navController.navigate(Routes.SETTINGS) },
                        onProductManagementClick = { navController.navigate(Routes.PRODUCT_MANAGEMENT) },
                        onBackupClick = { navController.navigate(Routes.BACKUP) }
                    )
                }
            }
        }

        composable(
            route = Routes.PRODUCT_LIST,
            arguments = listOf(navArgument("group") { type = NavType.StringType })
        ) { backStackEntry ->
            val groupName = backStackEntry.arguments?.getString("group") ?: "STERILE"
            val group = com.damdarangha.moghayerat.domain.model.ProductGroup.valueOf(groupName)
            ProductListScreen(
                group = group,
                onProductClick = { product ->
                    val today = com.damdarangha.moghayerat.util.JalaliDateConverter.todayIso()
                    navController.navigate(Routes.statEntry(product.id, today))
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable(
            route = Routes.STAT_ENTRY,
            arguments = listOf(
                navArgument("productId") { type = NavType.LongType },
                navArgument("isoDate") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val productId = backStackEntry.arguments?.getLong("productId") ?: 0L
            val isoDate = backStackEntry.arguments?.getString("isoDate") ?: ""
            StatEntryScreen(
                productId = productId,
                isoDate = isoDate,
                onBack = { navController.popBackStack() }
            )
        }

        composable(Routes.REPORTS) {
            ScaffoldWithBottomNav(
                currentRoute = Routes.REPORTS,
                onNavigate = { route ->
                    when (route) {
                        Routes.HOME -> navController.navigate(Routes.HOME) {
                            popUpTo(Routes.REPORTS) { inclusive = true }
                            launchSingleTop = true
                        }
                        Routes.REPORTS -> { /* همین صفحه */ }
                        Routes.PRODUCT_MANAGEMENT -> navController.navigate(Routes.PRODUCT_MANAGEMENT) {
                            popUpTo(Routes.REPORTS) { inclusive = true }
                            launchSingleTop = true
                        }
                        Routes.SETTINGS -> navController.navigate(Routes.SETTINGS) {
                            popUpTo(Routes.REPORTS) { inclusive = true }
                            launchSingleTop = true
                        }
                    }
                }
            ) { padding ->
                Box(modifier = Modifier.fillMaxSize().padding(padding)) {
                    ReportsScreen(onBack = { navController.popBackStack() })
                }
            }
        }

        composable(Routes.HISTORY) {
            HistoryScreen(
                onStatClick = { statId, productId, isoDate ->
                    navController.navigate(Routes.statEntry(productId, isoDate))
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable(Routes.SETTINGS) {
            ScaffoldWithBottomNav(
                currentRoute = Routes.SETTINGS,
                onNavigate = { route ->
                    when (route) {
                        Routes.HOME -> navController.navigate(Routes.HOME) {
                            popUpTo(Routes.SETTINGS) { inclusive = true }
                            launchSingleTop = true
                        }
                        Routes.REPORTS -> navController.navigate(Routes.REPORTS) {
                            popUpTo(Routes.SETTINGS) { inclusive = true }
                            launchSingleTop = true
                        }
                        Routes.PRODUCT_MANAGEMENT -> navController.navigate(Routes.PRODUCT_MANAGEMENT) {
                            popUpTo(Routes.SETTINGS) { inclusive = true }
                            launchSingleTop = true
                        }
                        Routes.SETTINGS -> { /* همین صفحه */ }
                    }
                }
            ) { padding ->
                Box(modifier = Modifier.fillMaxSize().padding(padding)) {
                    SettingsScreen(onBack = { navController.popBackStack() })
                }
            }
        }

        composable(Routes.PRODUCT_MANAGEMENT) {
            ScaffoldWithBottomNav(
                currentRoute = Routes.PRODUCT_MANAGEMENT,
                onNavigate = { route ->
                    when (route) {
                        Routes.HOME -> navController.navigate(Routes.HOME) {
                            popUpTo(Routes.PRODUCT_MANAGEMENT) { inclusive = true }
                            launchSingleTop = true
                        }
                        Routes.REPORTS -> navController.navigate(Routes.REPORTS) {
                            popUpTo(Routes.PRODUCT_MANAGEMENT) { inclusive = true }
                            launchSingleTop = true
                        }
                        Routes.PRODUCT_MANAGEMENT -> { /* همین صفحه */ }
                        Routes.SETTINGS -> navController.navigate(Routes.SETTINGS) {
                            popUpTo(Routes.PRODUCT_MANAGEMENT) { inclusive = true }
                            launchSingleTop = true
                        }
                    }
                }
            ) { padding ->
                Box(modifier = Modifier.fillMaxSize().padding(padding)) {
                    ProductManagementScreen(onBack = { navController.popBackStack() })
                }
            }
        }

        composable(Routes.BACKUP) {
            BackupScreen(onBack = { navController.popBackStack() })
        }
    }
}

/**
 * Scaffold با نوار ناوبری پایین برای صفحات اصلی
 */
@Composable
private fun ScaffoldWithBottomNav(
    currentRoute: String,
    onNavigate: (String) -> Unit,
    content: @Composable (androidx.compose.foundation.layout.PaddingValues) -> Unit
) {
    Scaffold(
        bottomBar = {
            BottomNavBar(
                currentRoute = currentRoute,
                onNavigate = onNavigate
            )
        }
    ) { padding ->
        content(padding)
    }
}
