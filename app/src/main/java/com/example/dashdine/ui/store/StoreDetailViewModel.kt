package com.example.dashdine.ui.store

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dashdine.data.model.Category
import com.example.dashdine.data.model.Dish
import com.example.dashdine.data.model.Store
import com.example.dashdine.data.repository.AppRepository
import com.example.dashdine.ui.cart.CartManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class StoreDetailUiState(
    val store: Store? = null,
    val categories: List<Category> = emptyList(),
    val dishesByCategory: Map<String, List<Dish>> = emptyMap(),
    val selectedCategoryIndex: Int = 0,
    val isLoading: Boolean = true
)

@HiltViewModel
class StoreDetailViewModel @Inject constructor(
    private val repository: AppRepository,
    private val cartManager: CartManager,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val storeId: String = savedStateHandle.get<String>("storeId") ?: ""

    private val _uiState = MutableStateFlow(StoreDetailUiState())
    val uiState: StateFlow<StoreDetailUiState> = _uiState.asStateFlow()

    val cartItems = cartManager.items
    val cartTotalQuantity = MutableStateFlow(0)
    val cartTotalPrice = MutableStateFlow(0f)

    init {
        loadStoreData()
        // 监听购物车变化
        viewModelScope.launch {
            cartManager.items.collect { items ->
                cartTotalQuantity.value = items.sumOf { it.quantity }
                cartTotalPrice.value = items.sumOf { it.totalPrice.toDouble() }.toFloat()
            }
        }
    }

    private fun loadStoreData() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            val store = repository.getStoreById(storeId)
            val categories = repository.getCategoriesByStore(storeId)
            val dishesByCategory = mutableMapOf<String, List<Dish>>()
            for (category in categories) {
                dishesByCategory[category.id] = repository.getDishesByCategory(category.id)
            }

            _uiState.value = StoreDetailUiState(
                store = store,
                categories = categories,
                dishesByCategory = dishesByCategory,
                selectedCategoryIndex = 0,
                isLoading = false
            )
        }
    }

    fun onCategorySelected(index: Int) {
        _uiState.value = _uiState.value.copy(selectedCategoryIndex = index)
    }

    fun onAddDish(dish: Dish) {
        cartManager.addDish(dish, storeId)
    }

    fun onRemoveDish(dishId: String) {
        cartManager.removeDish(dishId)
    }

    fun getDishQuantity(dishId: String): Int = cartManager.getQuantity(dishId)
}
