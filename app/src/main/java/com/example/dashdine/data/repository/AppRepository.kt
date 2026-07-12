package com.example.dashdine.data.repository

import com.example.dashdine.data.model.*

/**
 * 应用数据仓库接口 — 抽象所有数据操作
 */
interface AppRepository {

    // 店铺
    suspend fun getStores(): List<Store>
    suspend fun getStoreById(storeId: String): Store?

    // 分类
    suspend fun getCategoriesByStore(storeId: String): List<Category>

    // 菜品
    suspend fun getDishesByCategory(categoryId: String): List<Dish>
    suspend fun getDishById(dishId: String): Dish?

    // 订单
    suspend fun createOrder(order: Order): Order
    suspend fun getOrders(): List<Order>
    suspend fun getOrderById(orderId: String): Order?
}
