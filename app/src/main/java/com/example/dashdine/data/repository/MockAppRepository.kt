package com.example.dashdine.data.repository

import com.example.dashdine.data.mock.MockData
import com.example.dashdine.data.model.*
import kotlinx.coroutines.delay
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Mock Repository 实现 — 使用假数据模拟网络请求
 */
@Singleton
class MockAppRepository @Inject constructor() : AppRepository {

    private val orders = mutableListOf<Order>()

    override suspend fun getStores(): List<Store> {
        delay(300) // 模拟网络延迟
        return MockData.stores
    }

    override suspend fun getStoreById(storeId: String): Store? {
        delay(200)
        return MockData.stores.find { it.id == storeId }
    }

    override suspend fun getCategoriesByStore(storeId: String): List<Category> {
        delay(200)
        return MockData.categoriesForStore(storeId)
    }

    override suspend fun getDishesByCategory(categoryId: String): List<Dish> {
        delay(200)
        val storeId = categoryId.substringAfterLast("_")
        val allDishes = MockData.dishesForStore(storeId)
        return allDishes[categoryId] ?: emptyList()
    }

    override suspend fun getDishById(dishId: String): Dish? {
        delay(150)
        // 从所有店铺的菜品中查找
        for (store in MockData.stores) {
            val dishes = MockData.dishesForStore(store.id)
            dishes.values.flatten().find { it.id == dishId }?.let { return it }
        }
        return null
    }

    override suspend fun createOrder(order: Order): Order {
        delay(300)
        val saved = order.copy(id = "ORD${System.currentTimeMillis()}")
        orders.add(saved)
        return saved
    }

    override suspend fun getOrders(): List<Order> {
        delay(200)
        return orders.toList()
    }

    override suspend fun getOrderById(orderId: String): Order? {
        delay(150)
        return orders.find { it.id == orderId }
    }
}
