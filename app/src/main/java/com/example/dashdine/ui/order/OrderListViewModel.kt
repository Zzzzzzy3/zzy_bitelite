package com.example.dashdine.ui.order

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dashdine.data.model.Order
import com.example.dashdine.data.repository.AppRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class OrderListUiState(
    val orders: List<Order> = emptyList(),
    val isLoading: Boolean = true
)

@HiltViewModel
class OrderListViewModel @Inject constructor(
    private val repository: AppRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(OrderListUiState())
    val uiState: StateFlow<OrderListUiState> = _uiState.asStateFlow()

    init {
        loadOrders()
    }

    private fun loadOrders() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            val orders = repository.getOrders()
            _uiState.value = OrderListUiState(
                orders = orders.sortedByDescending { it.createTime },
                isLoading = false
            )
        }
    }
}
