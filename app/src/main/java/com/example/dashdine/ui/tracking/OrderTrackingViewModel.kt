package com.example.dashdine.ui.tracking

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

/**
 * 订单追踪状态阶段
 */
enum class TrackingStage(val label: String, val step: Int) {
    CONFIRMED("已确认", 0),
    PREPARING("制作中", 1),
    PICKING_UP("骑手取餐", 2),
    DELIVERING("配送中", 3),
    DELIVERED("已送达", 4)
}

/**
 * 订单追踪 UI 状态
 */
data class TrackingUiState(
    val isInDelivery: Boolean = true,
    val orderId: String = "DD20260712001",
    val storeName: String = "老王家的味道",
    val riderName: String = "李师傅",
    val riderPhone: String = "130****0000",
    val riderRating: Float = 4.9f,
    val currentStage: TrackingStage = TrackingStage.DELIVERING,
    val estimatedTime: String = "预计 12 分钟后送达",
    val deliveryAddress: String = "北京市朝阳区某某街 100 号",
    val riderProgress: Float = 0.65f,  // 骑手路径进度 0-1
    val mapAnimationPlaying: Boolean = true
)

@HiltViewModel
class OrderTrackingViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(TrackingUiState())
    val uiState: StateFlow<TrackingUiState> = _uiState.asStateFlow()

    // 模拟骑手位置更新
    // 在实际项目中，这里会通过 WebSocket 或轮询获取实时位置
    fun simulateRiderMove() {
        val current = _uiState.value
        // 循环演示动画
        val newProgress = if (current.riderProgress >= 0.95f) 0.05f
        else current.riderProgress + 0.01f

        val newStage = when {
            newProgress < 0.1f -> TrackingStage.CONFIRMED
            newProgress < 0.3f -> TrackingStage.PREPARING
            newProgress < 0.5f -> TrackingStage.PICKING_UP
            newProgress < 0.95f -> TrackingStage.DELIVERING
            else -> TrackingStage.DELIVERED
        }

        _uiState.value = current.copy(
            riderProgress = newProgress,
            currentStage = newStage,
            estimatedTime = when (newStage) {
                TrackingStage.DELIVERED -> "已送达！"
                TrackingStage.DELIVERING -> "预计 ${((1 - newProgress) * 20).toInt() + 1} 分钟后送达"
                else -> current.estimatedTime
            }
        )
    }
}
