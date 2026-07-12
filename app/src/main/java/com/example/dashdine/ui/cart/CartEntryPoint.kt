package com.example.dashdine.ui.cart

import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

/**
 * Hilt entry point for accessing CartManager from non-injected contexts (like Dialogs)
 */
@EntryPoint
@InstallIn(SingletonComponent::class)
interface CartEntryPoint {
    fun cartManager(): CartManager
}
