package com.example.dashdine.ui.checkout

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.dashdine.ui.components.Stepper
import com.example.dashdine.ui.theme.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckoutScreen(
    storeId: String,
    onBack: () -> Unit,
    onPayment: (orderId: String, amount: Float) -> Unit,
    viewModel: CheckoutViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // 地址编辑对话框状态
    var showAddressDialog by remember { mutableStateOf(false) }
    var editName by remember(uiState.userName) { mutableStateOf(uiState.userName) }
    var editPhone by remember(uiState.phone) { mutableStateOf(uiState.phone) }
    var editAddress by remember(uiState.address) { mutableStateOf(uiState.address) }

    Scaffold(
        containerColor = BackgroundWhite,
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "确认订单",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "返回"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = SurfaceWhite)
            )
        },
        bottomBar = {
            // 底部支付栏
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shadowElevation = 16.dp,
                color = SurfaceWhite
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = "实付款",
                            fontSize = 12.sp,
                            color = TextSecondary
                        )
                        Row(verticalAlignment = Alignment.Bottom) {
                            Text(
                                text = "¥",
                                fontSize = 14.sp,
                                color = CoralOrange,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "%.1f".format(uiState.finalAmount),
                                fontSize = 24.sp,
                                color = CoralOrange,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Button(
                        onClick = { viewModel.submitOrder(onPayment) },
                        enabled = !uiState.isSubmitting && uiState.cartItems.isNotEmpty(),
                        colors = ButtonDefaults.buttonColors(containerColor = CoralOrange),
                        shape = RoundedCornerShape(24.dp),
                        modifier = Modifier
                            .height(48.dp)
                            .width(140.dp)
                    ) {
                        if (uiState.isSubmitting) {
                            CircularProgressIndicator(
                                color = Color.White,
                                modifier = Modifier.size(20.dp),
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text(
                                text = "立即支付",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                        }
                    }
                }
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(bottom = 16.dp)
        ) {
            // ── 收货地址卡片 ──
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .clickable { showAddressDialog = true },
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = "地址",
                            tint = CoralOrange,
                            modifier = Modifier.size(28.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = uiState.userName,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                    color = TextPrimary
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    text = uiState.phone,
                                    fontSize = 14.sp,
                                    color = TextSecondary
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = uiState.address,
                                fontSize = 13.sp,
                                color = TextSecondary,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                            contentDescription = null,
                            tint = TextHint
                        )
                    }
                }
            }

            // ── 订单明细卡片 ──
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "订单明细",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = TextPrimary
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        // 菜品列表（支持修改数量）
                        uiState.cartItems.forEach { item ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                AsyncImage(
                                    model = item.dish.imageUrl,
                                    contentDescription = item.dish.name,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .size(48.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = item.dish.name,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = TextPrimary,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = "¥%.1f".format(item.totalPrice),
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = TextPrimary
                                    )
                                }
                                Stepper(
                                    quantity = item.quantity,
                                    onIncrease = { viewModel.onIncreaseDish(item.dish.id) },
                                    onDecrease = { viewModel.onDecreaseDish(item.dish.id) }
                                )
                            }
                        }

                        // 购物车为空时提示返回
                        if (uiState.cartItems.isEmpty()) {
                            Text(
                                text = "订单为空，请返回重新选择",
                                fontSize = 13.sp,
                                color = TextHint,
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                        }

                        HorizontalDivider(
                            color = DividerLight,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )

                        // 费用明细
                        FeeRow("商品小计", "¥%.1f".format(uiState.totalAmount))
                        FeeRow("配送费", "¥%.1f".format(uiState.deliveryFee))
                        FeeRow("包装费", "¥%.1f".format(uiState.packingFee))
                        if (uiState.discount > 0) {
                            FeeRow(
                                label = "满减优惠",
                                value = "-¥%.1f".format(uiState.discount),
                                valueColor = DiscountRed
                            )
                        }
                    }
                }
            }

            // ── 备注栏 ──
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "备注",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = TextPrimary
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        // 快速备注标签
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            viewModel.getQuickRemarks().take(4).forEach { remark ->
                                val isSelected = uiState.remark.contains(remark)
                                Surface(
                                    modifier = Modifier.clickable { viewModel.onQuickRemarkClick(remark) },
                                    shape = RoundedCornerShape(16.dp),
                                    color = if (isSelected) CoralOrange.copy(alpha = 0.15f) else BackgroundWhite,
                                    border = if (isSelected) {
                                        androidx.compose.foundation.BorderStroke(1.dp, CoralOrange)
                                    } else {
                                        androidx.compose.foundation.BorderStroke(0.5.dp, DividerLight)
                                    }
                                ) {
                                    Text(
                                        text = remark,
                                        fontSize = 12.sp,
                                        color = if (isSelected) CoralOrange else TextSecondary,
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // 备注输入框
                        OutlinedTextField(
                            value = uiState.remark,
                            onValueChange = viewModel::onRemarkChange,
                            placeholder = { Text("请输入备注（如口味、配送要求）", fontSize = 13.sp) },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = CoralOrange,
                                unfocusedBorderColor = DividerLight
                            )
                        )
                    }
                }
            }
        }
    }

    // ── 地址编辑对话框 ──
    if (showAddressDialog) {
        AlertDialog(
            onDismissRequest = { showAddressDialog = false },
            title = {
                Text("编辑收货地址", fontWeight = FontWeight.Bold)
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(
                        value = editName,
                        onValueChange = { editName = it },
                        label = { Text("收货人") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = CoralOrange,
                            unfocusedBorderColor = DividerLight
                        )
                    )
                    OutlinedTextField(
                        value = editPhone,
                        onValueChange = { editPhone = it },
                        label = { Text("手机号") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = CoralOrange,
                            unfocusedBorderColor = DividerLight
                        )
                    )
                    OutlinedTextField(
                        value = editAddress,
                        onValueChange = { editAddress = it },
                        label = { Text("详细地址") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 2,
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = CoralOrange,
                            unfocusedBorderColor = DividerLight
                        )
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.onAddressChange(editName, editPhone, editAddress)
                        showAddressDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CoralOrange),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("保存")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddressDialog = false }) {
                    Text("取消", color = TextSecondary)
                }
            }
        )
    }
}

@Composable
private fun FeeRow(label: String, value: String, valueColor: androidx.compose.ui.graphics.Color = TextPrimary) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, fontSize = 13.sp, color = TextSecondary)
        Text(text = value, fontSize = 13.sp, color = valueColor, fontWeight = FontWeight.Medium)
    }
}
