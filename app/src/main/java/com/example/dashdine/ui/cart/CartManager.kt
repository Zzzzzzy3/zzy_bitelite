package com.example.dashdine.ui.cart

import com.example.dashdine.data.model.CartItem
import com.example.dashdine.data.model.Dish
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 全局购物车状态管理器 — 单例，跨页面共享
 */
@Singleton
class CartManager @Inject constructor() {

    private val _items = MutableStateFlow<List<CartItem>>(emptyList())
    val items: StateFlow<List<CartItem>> = _items.asStateFlow()

    private val _currentStoreId = MutableStateFlow<String?>(null)
    val currentStoreId: StateFlow<String?> = _currentStoreId.asStateFlow()

    /**
     * 获取当前店铺购物车总数量
     */
    fun getTotalQuantity(): Int = _items.value.sumOf { it.quantity }

    /**
     * 获取当前店铺购物车总价
     */
    fun getTotalPrice(): Float = _items.value.sumOf { it.totalPrice.toDouble() }.toFloat()

    /**
     * 添加菜品到购物车
     */
    fun addDish(dish: Dish, storeId: String) {
        val currentStore = _currentStoreId.value
        if (currentStore != null && currentStore != storeId) {
            // 切换店铺，清空购物车
            _items.value = emptyList()
        }
        _currentStoreId.value = storeId

        val currentItems = _items.value.toMutableList()
        val existingIndex = currentItems.indexOfFirst { it.dish.id == dish.id }

        if (existingIndex >= 0) {
            val existing = currentItems[existingIndex]
            currentItems[existingIndex] = existing.copy(quantity = existing.quantity + 1)
        } else {
            currentItems.add(CartItem(dish = dish, quantity = 1))
        }
        _items.value = currentItems
    }

    /**
     * 减少菜品数量（数量为0时移除）
     */
    fun removeDish(dishId: String) {
        val currentItems = _items.value.toMutableList()
        val index = currentItems.indexOfFirst { it.dish.id == dishId }
        if (index >= 0) {
            val item = currentItems[index]
            if (item.quantity > 1) {
                currentItems[index] = item.copy(quantity = item.quantity - 1)
            } else {
                currentItems.removeAt(index)
            }
        }
        _items.value = currentItems
        if (currentItems.isEmpty()) {
            _currentStoreId.value = null
        }
    }

    /**
     * 获取某个菜品的当前数量
     */
    fun getQuantity(dishId: String): Int {
        return _items.value.find { it.dish.id == dishId }?.quantity ?: 0
    }

    /**
     * 清空购物车
     */
    fun clearCart() {
        _items.value = emptyList()
        _currentStoreId.value = null
    }

    /**
     * 获取当前购物车中的菜品列表
     */
    fun getCartItems(): List<CartItem> = _items.value
}
