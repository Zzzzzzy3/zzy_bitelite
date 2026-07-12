package com.example.dashdine.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * 导航路由定义
 */
sealed class Screen(val route: String) {
    // ── 底部导航 Tab ──
    data object Home : Screen("home")
    data object Tracking : Screen("tracking")
    data object Chat : Screen("chat")
    data object Profile : Screen("profile")

    // ── 详情页（无底部栏） ──
    data object StoreDetail : Screen("store_detail/{storeId}") {
        fun createRoute(storeId: String) = "store_detail/$storeId"
    }
    data object DishDetail : Screen("dish_detail/{dishId}") {
        fun createRoute(dishId: String) = "dish_detail/$dishId"
    }
    data object Checkout : Screen("checkout/{storeId}") {
        fun createRoute(storeId: String) = "checkout/$storeId"
    }
    data object Payment : Screen("payment/{orderId}/{orderAmount}") {
        fun createRoute(orderId: String, orderAmount: Float) = "payment/$orderId/$orderAmount"
    }
    data object OrderSuccess : Screen("order_success/{orderId}") {
        fun createRoute(orderId: String) = "order_success/$orderId"
    }
    data object OrderList : Screen("order_list")
}

/**
 * 底部导航项定义
 */
data class BottomNavItem(
    val screen: Screen,
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
)

val bottomNavItems = listOf(
    BottomNavItem(
        screen = Screen.Home,
        label = "首页",
        selectedIcon = Icons.Filled.Home,
        unselectedIcon = Icons.Outlined.Home
    ),
    BottomNavItem(
        screen = Screen.Tracking,
        label = "追踪",
        selectedIcon = Icons.Filled.Navigation,
        unselectedIcon = Icons.Outlined.Navigation
    ),
    BottomNavItem(
        screen = Screen.Chat,
        label = "AI对话",
        selectedIcon = Icons.Filled.SmartToy,
        unselectedIcon = Icons.Outlined.SmartToy
    ),
    BottomNavItem(
        screen = Screen.Profile,
        label = "我的",
        selectedIcon = Icons.Filled.Person,
        unselectedIcon = Icons.Outlined.Person
    )
)

/** 底部导航栏要显示的路线集合 */
val bottomBarRoutes = bottomNavItems.map { it.screen.route }.toSet()
