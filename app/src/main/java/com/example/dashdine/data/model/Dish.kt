package com.example.dashdine.data.model

/**
 * 菜品规格选项
 */
data class SpecOption(
    val id: String,
    val name: String,
    val priceDelta: Float = 0f
)

/**
 * 菜品规格组
 */
data class SpecGroup(
    val name: String,
    val options: List<SpecOption>
)

/**
 * 菜品实体
 */
data class Dish(
    val id: String,
    val name: String,
    val imageUrl: String,
    val description: String,
    val price: Float,
    val originalPrice: Float = price,
    val monthlySales: Int = 0,
    val rating: Float = 5.0f,
    val categoryId: String,
    val storeId: String,
    val specs: List<SpecGroup> = emptyList(),
    val allergens: String = "",
    val calories: String = "",
    val ingredients: String = ""
)
