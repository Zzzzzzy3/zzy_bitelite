package com.example.dashdine.ui.dish

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dashdine.data.model.Dish
import com.example.dashdine.data.repository.AppRepository
import com.example.dashdine.ui.cart.CartManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class DishDetailUiState(
    val dish: Dish? = null,
    val isLoading: Boolean = true,
    val loadError: Boolean = false,
    val selectedSpecs: Map<String, String> = emptyMap(),
    val cartQuantity: Int = 0
)

@HiltViewModel
class DishDetailViewModel @Inject constructor(
    private val repository: AppRepository,
    private val cartManager: CartManager,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val dishId: String = savedStateHandle.get<String>("dishId") ?: ""

    private val _uiState = MutableStateFlow(DishDetailUiState())
    val uiState: StateFlow<DishDetailUiState> = _uiState.asStateFlow()

    init {
        loadDish()
    }

    private fun loadDish() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, loadError = false)
            val dish = repository.getDishById(dishId)
            if (dish == null) {
                _uiState.value = _uiState.value.copy(isLoading = false, loadError = true)
                return@launch
            }
            val qty = cartManager.getQuantity(dishId)
            // 默认选中每个规格组的第一个选项
            val defaultSpecs = dish.specs.associate { it.name to it.options.first().name }
            _uiState.value = DishDetailUiState(
                dish = dish,
                isLoading = false,
                loadError = false,
                selectedSpecs = defaultSpecs,
                cartQuantity = qty
            )
        }
    }

    fun onSpecSelected(groupName: String, optionName: String) {
        val current = _uiState.value.selectedSpecs.toMutableMap()
        current[groupName] = optionName
        _uiState.value = _uiState.value.copy(selectedSpecs = current)
    }

    fun onAddToCart() {
        val dish = _uiState.value.dish ?: return
        cartManager.addDish(dish, dish.storeId)
        _uiState.value = _uiState.value.copy(
            cartQuantity = cartManager.getQuantity(dishId)
        )
    }
}
