package com.damdarangha.moghayerat.ui.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.damdarangha.moghayerat.ui.components.BottomNavBar
import com.damdarangha.moghayerat.ui.motion.LocalMotionSettings
import com.damdarangha.moghayerat.ui.motion.SwipeBackLayout
import com.damdarangha.moghayerat.ui.motion.backwardEnterTransition
import com.damdarangha.moghayerat.ui.motion.backwardExitTransition
import com.damdarangha.moghayerat.ui.motion.containerEnterTransition
import com.damdarangha.moghayerat.ui.motion.containerExitTransition
import com.damdarangha.moghayerat.ui.motion.containerPopEnterTransition
import com.damdarangha.moghayerat.ui.motion.containerPopExitTransition
import com.damdarangha.moghayerat.ui.motion.forwardEnterTransition
import com.damdarangha.moghayerat.ui.motion.forwardExitTransition
import com.damdarangha.moghayerat.ui.motion.reducedEnterTransition
import com.damdarangha.moghayerat.ui.motion.reducedExitTransition
import com.damdarangha.moghayerat.ui.screen.backup.BackupScreen
import com.damdarangha.moghayerat.ui.screen.history.HistoryScreen
import com.damdarangha.moghayerat.ui.screen.home.HomeScreen
import com.damdarangha.moghayerat.ui.screen.productlist.ProductListScreen
import com.damdarangha.moghayerat.ui.screen.productmanagement.ProductManagementScreen
import com.damdarangha.moghayerat.ui.screen.reports.ReportsScreen
import com.damdarangha.moghayerat.ui.screen.settings.SettingsScreen
import com.damdarangha.moghayerat.ui.screen.stat.StatEntryScreen
import com.damdarangha.moghayerat.ui.screen.welcome.WelcomeScreen
import com.damdarangha.moghayerat.ui.screen.workflowmanagement.WorkflowManagementScreen

object Routes {
    const val WELCOME = "welcome"
    const val HOME = "home"
    const val PRODUCT_LIST = "product_list/{workflowId}"
    const val STAT_ENTRY = "stat_entry/{productId}/{isoDate}/{stage}"
    const val REPORTS = "reports"
    const val HISTORY = "history"
    const val SETTINGS = "settings"
    const val PRODUCT_MANAGEMENT = "product_management"
    const val WORKFLOW_MANAGEMENT = "workflow_management"
    const val BACKUP = "backup"

    fun productList(workflowId: Long) = "product_list/$workflowId"
    fun statEntry(productId: Long, isoDate: String, stage: String = "FULL") = "stat_entry/$productId/$isoDate/$stage"
}

@Composable
private fun rememberNavTransitionConfig(): NavTransitionConfig {
    val isRtl = LocalLayoutDirection.current == LayoutDirection.Rtl
    val reduced = LocalMotionSettings.current.reducedMotion
    return NavTransitionConfig(isRtl = isRtl, reducedMotion = reduced)
}

private data class NavTransitionConfig(
    val isRtl: Boolean,
    val reducedMotion: Boolean
)

private fun AnimatedContentTransitionScope<*>.defaultEnter(cfg: NavTransitionConfig): EnterTransition {
    return if (cfg.reducedMotion) reducedEnterTransition() else forwardEnterTransition(cfg.isRtl)
}

private fun AnimatedContentTransitionScope<*>.defaultExit(cfg: NavTransitionConfig): ExitTransition {
    return if (cfg.reducedMotion) reducedExitTransition() else forwardExitTransition(cfg.isRtl)
}

private fun AnimatedContentTransitionScope<*>.defaultPopEnter(cfg: NavTransitionConfig): EnterTransition {
    return if (cfg.reducedMotion) reducedEnterTransition() else backwardEnterTransition(cfg.isRtl)
}

private fun AnimatedContentTransitionScope<*>.defaultPopExit(cfg: NavTransitionConfig): ExitTransition {
    return if (cfg.reducedMotion) reducedExitTransition() else backwardExitTransition(cfg.isRtl)
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val cfg = rememberNavTransitionConfig()

    NavHost(
        navController = navController,
        startDestination = Routes.WELCOME,
        enterTransition = { defaultEnter(cfg) },
        exitTransition = { defaultExit(cfg) },
        popEnterTransition = { defaultPopEnter(cfg) },
        popExitTransition = { defaultPopExit(cfg) }
    ) {

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
                        Routes.HOME -> { }
                        Routes.REPORTS -> navController.navigate(Routes.REPORTS) { launchSingleTop = true }
                        Routes.PRODUCT_MANAGEMENT -> navController.navigate(Routes.PRODUCT_MANAGEMENT) { launchSingleTop = true }
                        Routes.SETTINGS -> navController.navigate(Routes.SETTINGS) { launchSingleTop = true }
                    }
                }
            ) { padding ->
                Box(modifier = Modifier.fillMaxSize().padding(padding)) {
                    HomeScreen(
                        onWorkflowClick = { workflow: com.damdarangha.moghayerat.domain.model.Workflow ->
                            navController.navigate(Routes.productList(workflow.id))
                        },
                        onReportsClick = { navController.navigate(Routes.REPORTS) },
                        onHistoryClick = { navController.navigate(Routes.HISTORY) },
                        onSettingsClick = { navController.navigate(Routes.SETTINGS) },
                        onWorkflowManagementClick = { navController.navigate(Routes.WORKFLOW_MANAGEMENT) },
                        onBackupClick = { navController.navigate(Routes.BACKUP) }
                    )
                }
            }
        }

        composable(
            route = Routes.PRODUCT_LIST,
            arguments = listOf(navArgument("workflowId") { type = NavType.LongType })
        ) { backStackEntry ->
            val workflowId = backStackEntry.arguments?.getLong("workflowId") ?: 0L
            SwipeBackLayout(
                enabled = true,
                onSwipeComplete = { navController.popBackStack() }
            ) {
                ProductListScreen(
                    workflowId = workflowId,
                    onProductClick = { product, stage ->
                        val today = com.damdarangha.moghayerat.util.JalaliDateConverter.todayIso()
                        navController.navigate(Routes.statEntry(product.id, today, stage))
                    },
                    onBack = { navController.popBackStack() }
                )
            }
        }

        composable(
            route = Routes.STAT_ENTRY,
            arguments = listOf(
                navArgument("productId") { type = NavType.LongType },
                navArgument("isoDate") { type = NavType.StringType },
                navArgument("stage") { type = NavType.StringType; defaultValue = "FULL" }
            ),
            enterTransition = {
                if (cfg.reducedMotion) reducedEnterTransition() else containerEnterTransition()
            },
            exitTransition = {
                if (cfg.reducedMotion) reducedExitTransition() else containerExitTransition()
            },
            popEnterTransition = {
                if (cfg.reducedMotion) reducedEnterTransition() else containerPopEnterTransition()
            },
            popExitTransition = {
                if (cfg.reducedMotion) reducedExitTransition() else containerPopExitTransition()
            }
        ) { backStackEntry ->
            val productId = backStackEntry.arguments?.getLong("productId") ?: 0L
            val isoDate = backStackEntry.arguments?.getString("isoDate") ?: ""
            val stage = backStackEntry.arguments?.getString("stage") ?: "FULL"
            StatEntryScreen(
                productId = productId,
                isoDate = isoDate,
                stage = stage,
                onBack = { navController.popBackStack() }
            )
        }

        composable(Routes.REPORTS) {
            ScaffoldWithBottomNav(
                currentRoute = Routes.REPORTS,
                onNavigate = { route ->
                    when (route) {
                        Routes.HOME -> navController.navigate(Routes.HOME) { popUpTo(Routes.REPORTS) { inclusive = true } }
                        Routes.REPORTS -> { }
                        Routes.PRODUCT_MANAGEMENT -> navController.navigate(Routes.PRODUCT_MANAGEMENT) { popUpTo(Routes.REPORTS) { inclusive = true } }
                        Routes.SETTINGS -> navController.navigate(Routes.SETTINGS) { popUpTo(Routes.REPORTS) { inclusive = true } }
                    }
                }
            ) { padding ->
                Box(modifier = Modifier.fillMaxSize().padding(padding)) {
                    ReportsScreen(onBack = { navController.popBackStack() })
                }
            }
        }

        composable(Routes.HISTORY) {
            SwipeBackLayout(
                enabled = true,
                onSwipeComplete = { navController.popBackStack() }
            ) {
                HistoryScreen(
                    onStatClick = { statId, productId, isoDate ->
                        navController.navigate(Routes.statEntry(productId, isoDate, "FULL"))
                    },
                    onBack = { navController.popBackStack() }
                )
            }
        }

        composable(Routes.SETTINGS) {
            ScaffoldWithBottomNav(
                currentRoute = Routes.SETTINGS,
                onNavigate = { route ->
                    when (route) {
                        Routes.HOME -> navController.navigate(Routes.HOME) { popUpTo(Routes.SETTINGS) { inclusive = true } }
                        Routes.REPORTS -> navController.navigate(Routes.REPORTS) { popUpTo(Routes.SETTINGS) { inclusive = true } }
                        Routes.PRODUCT_MANAGEMENT -> navController.navigate(Routes.PRODUCT_MANAGEMENT) { popUpTo(Routes.SETTINGS) { inclusive = true } }
                        Routes.SETTINGS -> { }
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
                        Routes.HOME -> navController.navigate(Routes.HOME) { popUpTo(Routes.PRODUCT_MANAGEMENT) { inclusive = true } }
                        Routes.REPORTS -> navController.navigate(Routes.REPORTS) { popUpTo(Routes.PRODUCT_MANAGEMENT) { inclusive = true } }
                        Routes.PRODUCT_MANAGEMENT -> { }
                        Routes.SETTINGS -> navController.navigate(Routes.SETTINGS) { popUpTo(Routes.PRODUCT_MANAGEMENT) { inclusive = true } }
                    }
                }
            ) { padding ->
                Box(modifier = Modifier.fillMaxSize().padding(padding)) {
                    ProductManagementScreen(onBack = { navController.popBackStack() })
                }
            }
        }

        composable(Routes.WORKFLOW_MANAGEMENT) {
            SwipeBackLayout(
                enabled = true,
                onSwipeComplete = { navController.popBackStack() }
            ) {
                WorkflowManagementScreen(onBack = { navController.popBackStack() })
            }
        }

        composable(Routes.BACKUP) {
            SwipeBackLayout(
                enabled = true,
                onSwipeComplete = { navController.popBackStack() }
            ) {
                BackupScreen(onBack = { navController.popBackStack() })
            }
        }
    }
}

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
