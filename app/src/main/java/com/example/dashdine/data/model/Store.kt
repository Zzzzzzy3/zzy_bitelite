package com.example.dashdine.data.model

/**
 * 店铺实体
 */
data class Store(
    val id: String,
    val name: String,
    val logoUrl: String,
    val coverUrl: String,
    val rating: Float,
    val monthlySales: Int,
    val minOrderPrice: Float,
    val deliveryFee: Float,
    val deliveryTime: String,
    val tags: List<String>,
    val distance: String,
    val isNew: Boolean = false,
    val hasDiscount: Boolean = false
)
