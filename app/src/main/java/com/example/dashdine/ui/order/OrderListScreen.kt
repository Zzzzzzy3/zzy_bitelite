package com.example.dashdine.ui.order

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.dashdine.data.model.Order
import com.example.dashdine.data.model.OrderStatus
import com.example.dashdine.ui.theme.*
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderListScreen(
    onBack: () -> Unit,
    viewModel: OrderListViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        containerColor = BackgroundWhite,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "我的订单",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "返回",
                            tint = TextPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = SurfaceWhite)
            )
        }
    ) { paddingValues ->
        if (uiState.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize().padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = CoralOrange)
            }
        } else if (uiState.orders.isEmpty()) {
            Column(
                modifier = Modifier.fillMaxSize().padding(paddingValues),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Receipt,
                    contentDescription = null,
                    tint = TextHint,
                    modifier = Modifier.size(64.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "暂无订单",
                    fontSize = 16.sp,
                    color = TextSecondary,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "下单后订单将显示在这里",
                    fontSize = 13.sp,
                    color = TextHint
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(paddingValues),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(uiState.orders, key = { it.id }) { order ->
                    OrderCard(order = order)
                }
            }
        }
    }
}

@Composable
private fun OrderCard(order: Order) {
    val dateFormat = remember { SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // 订单头部
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "订单号：${order.id}",
                    fontSize = 12.sp,
                    color = TextHint
                )
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = when (order.status) {
                        OrderStatus.PENDING_PAYMENT -> WarningOrange.copy(alpha = 0.15f)
                        OrderStatus.PREPARING -> CoralOrange.copy(alpha = 0.15f)
                        OrderStatus.DELIVERING -> TagBlue.copy(alpha = 0.15f)
                        OrderStatus.COMPLETED -> SuccessGreen.copy(alpha = 0.15f)
                        OrderStatus.CANCELLED -> TextHint.copy(alpha = 0.15f)
                    }
                ) {
                    Text(
                        text = when (order.status) {
                            OrderStatus.PENDING_PAYMENT -> "待支付"
                            OrderStatus.PREPARING -> "准备中"
                            OrderStatus.DELIVERING -> "配送中"
                            OrderStatus.COMPLETED -> "已完成"
                            OrderStatus.CANCELLED -> "已取消"
                        },
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = when (order.status) {
                            OrderStatus.PENDING_PAYMENT -> WarningOrange
                            OrderStatus.PREPARING -> CoralOrange
                            OrderStatus.DELIVERING -> TagBlue
                            OrderStatus.COMPLETED -> SuccessGreen
                            OrderStatus.CANCELLED -> TextHint
                        },
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // 菜品摘要
            Text(
                text = order.items.take(3).joinToString("、") { "${it.dish.name} x${it.quantity}" } +
                    if (order.items.size > 3) " 等${order.items.size}件" else "",
                fontSize = 14.sp,
                color = TextPrimary,
                fontWeight = FontWeight.Medium,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(8.dp))

            // 底部信息
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = dateFormat.format(Date(order.createTime)),
                    fontSize = 12.sp,
                    color = TextHint
                )
                Row(verticalAlignment = Alignment.Bottom) {
                    Text(
                        text = "¥",
                        fontSize = 12.sp,
                        color = CoralOrange,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "%.1f".format(order.finalAmount),
                        fontSize = 18.sp,
                        color = CoralOrange,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
