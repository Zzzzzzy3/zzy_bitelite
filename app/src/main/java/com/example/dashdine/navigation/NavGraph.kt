package com.example.dashdine.navigation

import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.navArgument
import com.example.dashdine.ui.chat.AiChatScreen
import com.example.dashdine.ui.checkout.CheckoutScreen
import com.example.dashdine.ui.checkout.OrderSuccessScreen
import com.example.dashdine.ui.dish.DishDetailScreen
import com.example.dashdine.ui.home.HomeScreen
import com.example.dashdine.ui.order.OrderListScreen
import com.example.dashdine.ui.payment.PaymentScreen
import com.example.dashdine.ui.profile.UserProfileScreen
import com.example.dashdine.ui.store.StoreDetailScreen
import com.example.dashdine.ui.theme.*
import com.example.dashdine.ui.tracking.OrderTrackingScreen

@Composable
fun AppNavGraph(navController: NavHostController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val showBottomBar = currentRoute in bottomBarRoutes

    Scaffold(
        containerColor = BackgroundWhite,
        bottomBar = {
            if (showBottomBar) {
                AppBottomNavigationBar(
                    currentRoute = currentRoute,
                    onItemClick = { screen ->
                        navController.navigate(screen.route) {
                            // 避免在回退栈中堆积多个相同 Tab
                            popUpTo(navController.graph.startDestinationId) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(paddingValues)
        ) {
            // ═══════════════════════════════════════
            // 底部导航 Tab 页面
            // ═══════════════════════════════════════

            composable(Screen.Home.route) {
                HomeScreen(
                    onStoreClick = { storeId ->
                        navController.navigate(Screen.StoreDetail.createRoute(storeId))
                    },
                    onOrderHistoryClick = {
                        navController.navigate(Screen.OrderList.route)
                    }
                )
            }

            composable(Screen.Tracking.route) {
                OrderTrackingScreen()
            }

            composable(Screen.Chat.route) {
                AiChatScreen(
                    onFoodClick = { dishName ->
                        val storeId = findStoreForDish(dishName)
                        navController.navigate(Screen.StoreDetail.createRoute(storeId))
                    }
                )
            }

            composable(Screen.Profile.route) {
                UserProfileScreen(
                    onOrderListClick = {
                        navController.navigate(Screen.OrderList.route)
                    }
                )
            }

            // ═══════════════════════════════════════
            // 详情页面（无底部导航栏）
            // ═══════════════════════════════════════

            composable(
                route = Screen.StoreDetail.route,
                arguments = listOf(navArgument("storeId") { type = NavType.StringType })
            ) { backStackEntry ->
                val storeId = backStackEntry.arguments?.getString("storeId") ?: return@composable
                StoreDetailScreen(
                    storeId = storeId,
                    onBack = { navController.popBackStack() },
                    onDishClick = { dishId ->
                        navController.navigate(Screen.DishDetail.createRoute(dishId))
                    },
                    onCheckout = { navController.navigate(Screen.Checkout.createRoute(storeId)) }
                )
            }

            composable(
                route = Screen.DishDetail.route,
                arguments = listOf(navArgument("dishId") { type = NavType.StringType })
            ) { backStackEntry ->
                val dishId = backStackEntry.arguments?.getString("dishId") ?: return@composable
                DishDetailScreen(
                    dishId = dishId,
                    onBack = { navController.popBackStack() }
                )
            }

            composable(
                route = Screen.Checkout.route,
                arguments = listOf(navArgument("storeId") { type = NavType.StringType })
            ) { backStackEntry ->
                val storeId = backStackEntry.arguments?.getString("storeId") ?: return@composable
                CheckoutScreen(
                    storeId = storeId,
                    onBack = { navController.popBackStack() },
                    onPayment = { orderId, amount ->
                        navController.navigate(Screen.Payment.createRoute(orderId, amount)) {
                            popUpTo(Screen.Home.route)
                        }
                    }
                )
            }

            composable(
                route = Screen.Payment.route,
                arguments = listOf(
                    navArgument("orderId") { type = NavType.StringType },
                    navArgument("orderAmount") { type = NavType.StringType }
                )
            ) { backStackEntry ->
                val orderId = backStackEntry.arguments?.getString("orderId") ?: return@composable
                val orderAmount = backStackEntry.arguments?.getString("orderAmount")?.toFloatOrNull() ?: 0f
                PaymentScreen(
                    orderId = orderId,
                    orderAmount = orderAmount,
                    onBack = { navController.popBackStack() },
                    onPaymentSuccess = {
                        navController.navigate(Screen.OrderSuccess.createRoute(orderId)) {
                            popUpTo(Screen.Payment.route) { inclusive = true }
                        }
                    }
                )
            }

            composable(
                route = Screen.OrderSuccess.route,
                arguments = listOf(navArgument("orderId") { type = NavType.StringType })
            ) { backStackEntry ->
                val orderId = backStackEntry.arguments?.getString("orderId") ?: return@composable
                OrderSuccessScreen(
                    orderId = orderId,
                    onBackToHome = {
                        navController.popBackStack(Screen.Home.route, false)
                    }
                )
            }

            composable(Screen.OrderList.route) {
                OrderListScreen(
                    onBack = { navController.popBackStack() }
                )
            }
        }
    }
}

@Composable
private fun AppBottomNavigationBar(
    currentRoute: String?,
    onItemClick: (Screen) -> Unit
) {
    NavigationBar(
        containerColor = SurfaceWhite,
        tonalElevation = 8.dp
    ) {
        bottomNavItems.forEach { item ->
            val selected = currentRoute == item.screen.route

            NavigationBarItem(
                selected = selected,
                onClick = { onItemClick(item.screen) },
                icon = {
                    Icon(
                        imageVector = if (selected) item.selectedIcon else item.unselectedIcon,
                        contentDescription = item.label
                    )
                },
                label = {
                    Text(
                        text = item.label,
                        fontSize = 11.sp,
                        fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = CoralOrange,
                    selectedTextColor = CoralOrange,
                    unselectedIconColor = TextHint,
                    unselectedTextColor = TextHint,
                    indicatorColor = CoralOrange.copy(alpha = 0.1f)
                )
            )
        }
    }
}

/**
 * 简单菜品名→店铺ID映射，用于 AI 推荐卡片点击跳转
 * 如果找不到精确匹配，返回默认店铺 "s1"
 */
private fun findStoreForDish(dishName: String): String {
    // s1 老王家的味道 — 菜品最多
    val s1Dishes = setOf(
        "招牌红烧肉饭", "香辣鸡腿堡套餐", "番茄牛肉面", "糖醋里脊盖饭",
        "经典麻婆豆腐", "宫保鸡丁套餐", "日式咖喱猪排饭", "酸菜鱼单人份",
        "蛋炒饭", "红烧牛肉面", "腊味煲仔饭", "扬州炒饭", "肉末茄子盖饭", "海鲜炒面",
        "香酥炸鸡翅", "凉拌黄瓜", "春卷", "蒜蓉西兰花",
        "冰镇柠檬水", "珍珠奶茶", "冰美式咖啡", "鲜榨橙汁",
        "抹茶提拉米苏", "芒果糯米饭", "巧克力熔岩蛋糕"
    )
    // s2 川味小馆 — 川菜
    val s2Dishes = setOf(
        "水煮鱼", "回锅肉", "担担面", "麻辣香锅", "辣子鸡丁饭",
        "红油抄手", "老成都冰粉", "麻辣香锅 (小份)", "红油抄手 (10个)"
    )

    return when {
        s2Dishes.any { dishName.contains(it) || it.contains(dishName) } -> "s2"
        s1Dishes.any { dishName.contains(it) || it.contains(dishName) } -> "s1"
        dishName.contains("辣") || dishName.contains("川") || dishName.contains("麻") -> "s2"
        else -> "s1"
    }
}
