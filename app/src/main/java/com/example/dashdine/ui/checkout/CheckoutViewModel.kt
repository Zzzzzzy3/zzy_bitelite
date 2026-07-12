package com.example.dashdine.ui.checkout

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dashdine.data.mock.MockData
import com.example.dashdine.data.model.Order
import com.example.dashdine.data.model.OrderStatus
import com.example.dashdine.data.repository.AppRepository
import com.example.dashdine.ui.cart.CartManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CheckoutUiState(
    val cartItems: List<com.example.dashdine.data.model.CartItem> = emptyList(),
    val totalAmount: Float = 0f,
    val deliveryFee: Float = 5f,
    val packingFee: Float = 2f,
    val discount: Float = 0f,
    val finalAmount: Float = 0f,
    val address: String = "",
    val phone: String = "",
    val userName: String = "",
    val remark: String = "",
    val isSubmitting: Boolean = false,
    val orderId: String? = null
)

@HiltViewModel
class CheckoutViewModel @Inject constructor(
    private val repository: AppRepository,
    private val cartManager: CartManager,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val storeId: String = savedStateHandle.get<String>("storeId") ?: ""

    private val _uiState = MutableStateFlow(CheckoutUiState())
    val uiState: StateFlow<CheckoutUiState> = _uiState.asStateFlow()

    private val quickRemarks = listOf("不要辣", "少辣", "多放葱", "不要香菜", "少盐", "多加醋")

    init {
        val addressInfo = MockData.defaultAddress
        val items = cartManager.getCartItems()
        val total = items.sumOf { it.totalPrice.toDouble() }.toFloat()
        val deliveryFee = 5f
        val packingFee = 2f
        // 满30减5
        val discount = if (total >= 30) 5f else 0f
        val finalAmount = (total + deliveryFee + packingFee - discount).coerceAtLeast(0f)

        _uiState.value = CheckoutUiState(
            cartItems = items,
            totalAmount = total,
            deliveryFee = deliveryFee,
            packingFee = packingFee,
            discount = discount,
            finalAmount = finalAmount,
            address = addressInfo.address,
            phone = addressInfo.phone,
            userName = addressInfo.name
        )
    }

    fun onRemarkChange(remark: String) {
        _uiState.value = _uiState.value.copy(remark = remark)
    }

    fun onQuickRemarkClick(remark: String) {
        val current = _uiState.value.remark
        _uiState.value = _uiState.value.copy(
            remark = if (current.contains(remark)) {
                current.replace(remark, "").trim()
            } else {
                if (current.isEmpty()) remark else "$current, $remark"
            }
        )
    }

    fun onAddressChange(name: String, phone: String, address: String) {
        _uiState.value = _uiState.value.copy(
            userName = name,
            phone = phone,
            address = address
        )
    }

    fun getQuickRemarks(): List<String> = quickRemarks

    fun onIncreaseDish(dishId: String) {
        val item = cartManager.getCartItems().find { it.dish.id == dishId } ?: return
        cartManager.addDish(item.dish, storeId)
        recalculateTotals()
    }

    fun onDecreaseDish(dishId: String) {
        cartManager.removeDish(dishId)
        val items = cartManager.getCartItems()
        if (items.isEmpty()) {
            // 所有菜品被移除，清空状态（由 UI 层处理返回）
            _uiState.value = _uiState.value.copy(
                cartItems = emptyList(),
                totalAmount = 0f,
                discount = 0f,
                finalAmount = 0f
            )
            return
        }
        recalculateTotals()
    }

    private fun recalculateTotals() {
        val items = cartManager.getCartItems()
        val total = items.sumOf { it.totalPrice.toDouble() }.toFloat()
        val deliveryFee = 5f
        val packingFee = 2f
        val discount = if (total >= 30) 5f else 0f
        val finalAmount = (total + deliveryFee + packingFee - discount).coerceAtLeast(0f)

        _uiState.value = _uiState.value.copy(
            cartItems = items,
            totalAmount = total,
            deliveryFee = deliveryFee,
            packingFee = packingFee,
            discount = discount,
            finalAmount = finalAmount
        )
    }

    fun submitOrder(onSuccess: (String, Float) -> Unit) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSubmitting = true)

            val state = _uiState.value
            val order = Order(
                id = "",
                storeId = storeId,
                storeName = "",
                items = state.cartItems,
                totalAmount = state.totalAmount,
                deliveryFee = state.deliveryFee,
                packingFee = state.packingFee,
                discount = state.discount,
                address = state.address,
                phone = state.phone,
                remark = state.remark
            )

            val savedOrder = repository.createOrder(order)
            val finalAmount = savedOrder.finalAmount
            cartManager.clearCart()
            _uiState.value = _uiState.value.copy(
                isSubmitting = false,
                orderId = savedOrder.id
            )
            onSuccess(savedOrder.id, finalAmount)
        }
    }
}
