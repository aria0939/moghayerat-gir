package com.damdarangha.moghayerat.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.damdarangha.moghayerat.ui.screen.history.HistoryScreen
import com.damdarangha.moghayerat.ui.screen.home.HomeScreen
import com.damdarangha.moghayerat.ui.screen.productlist.ProductListScreen
import com.damdarangha.moghayerat.ui.screen.productmanagement.ProductManagementScreen
import com.damdarangha.moghayerat.ui.screen.reports.ReportsScreen
import com.damdarangha.moghayerat.ui.screen.settings.SettingsScreen
import com.damdarangha.moghayerat.ui.screen.stat.StatEntryScreen
import com.damdarangha.moghayerat.ui.screen.backup.BackupScreen

/**
 * مسیرهای صفحه‌های برنامه
 */
object Routes {
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

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Routes.HOME) {

        composable(Routes.HOME) {
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

        composable(
            route = Routes.PRODUCT_LIST,
            arguments = listOf(navArgument("group") { type = NavType.StringType })
        ) { backStackEntry ->
            val groupName = backStackEntry.arguments?.getString("group") ?: "STERILE"
            val group = com.damdarangha.moghayerat.domain.model.ProductGroup.valueOf(groupName)
            ProductListScreen(
                group = group,
                onProductClick = { product ->
                    // استفاده از تاریخ امروز به‌صورت پیش‌فرض
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
            ReportsScreen(onBack = { navController.popBackStack() })
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
            SettingsScreen(onBack = { navController.popBackStack() })
        }

        composable(Routes.PRODUCT_MANAGEMENT) {
            ProductManagementScreen(onBack = { navController.popBackStack() })
        }

        composable(Routes.BACKUP) {
            BackupScreen(onBack = { navController.popBackStack() })
        }
    }
}
