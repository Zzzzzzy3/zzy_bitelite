package com.example.dashdine.data.model

/**
 * 菜品分类实体
 */
data class Category(
    val id: String,
    val name: String,
    val storeId: String,
    val dishCount: Int = 0
)
