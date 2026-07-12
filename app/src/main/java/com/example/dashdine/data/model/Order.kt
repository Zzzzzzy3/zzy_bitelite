package com.example.dashdine.data.model

/**
 * 订单状态
 */
enum class OrderStatus {
    PENDING_PAYMENT,
    PREPARING,
    DELIVERING,
    COMPLETED,
    CANCELLED
}

/**
 * 购物车中的菜品项
 */
data class CartItem(
    val dish: Dish,
    val quantity: Int = 1,
    val selectedSpecs: Map<String, String> = emptyMap()
) {
    val totalPrice: Float
        get() = (dish.price + selectedSpecs.entries.sumOf { (_, specName) ->
            (dish.specs.flatMap { it.options }
                .find { it.name == specName }?.priceDelta ?: 0f).toDouble()
        }.toFloat()) * quantity
}

/**
 * 订单实体
 */
data class Order(
    val id: String,
    val storeId: String,
    val storeName: String,
    val items: List<CartItem>,
    val totalAmount: Float,
    val deliveryFee: Float = 5.0f,
    val packingFee: Float = 2.0f,
    val discount: Float = 0f,
    val status: OrderStatus = OrderStatus.PENDING_PAYMENT,
    val address: String = "",
    val phone: String = "",
    val remark: String = "",
    val createTime: Long = System.currentTimeMillis()
) {
    val finalAmount: Float
        get() = (totalAmount + deliveryFee + packingFee - discount).coerceAtLeast(0f)
}
