package com.example.dashdine.ui.store

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.dashdine.ui.cart.CartSheet
import com.example.dashdine.ui.components.DishCard
import com.example.dashdine.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StoreDetailScreen(
    storeId: String,
    onBack: () -> Unit,
    onDishClick: (String) -> Unit,
    onCheckout: () -> Unit,
    viewModel: StoreDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val cartTotalQuantity by viewModel.cartTotalQuantity.collectAsState()
    val cartTotalPrice by viewModel.cartTotalPrice.collectAsState()
    var showCartSheet by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = BackgroundWhite,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = uiState.store?.name ?: "",
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        style = MaterialTheme.typography.titleMedium
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
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = SurfaceWhite
                )
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            if (uiState.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = CoralOrange)
                }
            } else {
                Column(modifier = Modifier.fillMaxSize()) {
                    // ── 封面图 ──
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                    ) {
                        AsyncImage(
                            model = uiState.store?.coverUrl,
                            contentDescription = uiState.store?.name,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                        // 渐变遮罩
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.verticalGradient(
                                        colors = listOf(
                                            Color.Transparent,
                                            Color.Black.copy(alpha = 0.35f)
                                        )
                                    )
                                )
                        )
                    }

                    // ── 店铺信息卡片 ──
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = SurfaceWhite,
                        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = uiState.store?.name ?: "",
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "⭐ ${uiState.store?.rating}",
                                    fontSize = 13.sp,
                                    color = WarningOrange
                                )
                                Text(
                                    text = "  月售${uiState.store?.monthlySales}+",
                                    fontSize = 12.sp,
                                    color = TextSecondary
                                )
                                Text(
                                    text = "  ${uiState.store?.distance}",
                                    fontSize = 12.sp,
                                    color = TextSecondary
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = buildString {
                                    val s = uiState.store
                                    if (s != null) {
                                        append("¥${s.minOrderPrice.toInt()}起送  |  ")
                                        append(if (s.deliveryFee == 0f) "免配送费" else "配送费¥${s.deliveryFee.toInt()}")
                                        append("  |  ${s.deliveryTime}")
                                    }
                                },
                                fontSize = 12.sp,
                                color = TextSecondary
                            )
                        }
                    }

                    // ── 双联动菜单区 (填充剩余空间) ──
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                    ) {
                        // 左侧分类导航 (1/4 宽)
                        LazyColumn(
                            modifier = Modifier
                                .width(90.dp)
                                .fillMaxHeight()
                                .background(BackgroundWhite)
                        ) {
                            itemsIndexed(uiState.categories) { index, category ->
                                val isSelected = index == uiState.selectedCategoryIndex
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(if (isSelected) SurfaceWhite else BackgroundWhite)
                                        .clickable { viewModel.onCategorySelected(index) }
                                        .padding(vertical = 14.dp, horizontal = 8.dp)
                                ) {
                                    Row {
                                        if (isSelected) {
                                            Box(
                                                modifier = Modifier
                                                    .width(3.dp)
                                                    .height(20.dp)
                                                    .clip(RoundedCornerShape(2.dp))
                                                    .background(CoralOrange)
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                        } else {
                                            Spacer(modifier = Modifier.width(9.dp))
                                        }
                                        Text(
                                            text = category.name,
                                            fontSize = 13.sp,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                            color = if (isSelected) CoralOrange else TextSecondary,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }
                                }
                            }
                        }

                        // 分割线
                        Box(
                            modifier = Modifier
                                .fillMaxHeight()
                                .width(0.5.dp)
                                .background(DividerLight)
                        )

                        // 右侧菜品列表 (3/4 宽)
                        val selectedCategoryId = uiState.categories.getOrNull(uiState.selectedCategoryIndex)?.id
                        val dishes = if (selectedCategoryId != null) {
                            uiState.dishesByCategory[selectedCategoryId] ?: emptyList()
                        } else emptyList()

                        LazyColumn(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                                .padding(horizontal = 12.dp),
                            contentPadding = PaddingValues(vertical = 4.dp)
                        ) {
                            item {
                                Text(
                                    text = uiState.categories.getOrNull(uiState.selectedCategoryIndex)?.name ?: "",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary,
                                    modifier = Modifier.padding(vertical = 8.dp)
                                )
                            }
                            items(dishes, key = { it.id }) { dish ->
                                DishCard(
                                    dish = dish,
                                    quantity = viewModel.getDishQuantity(dish.id),
                                    onIncrease = { viewModel.onAddDish(dish) },
                                    onDecrease = { viewModel.onRemoveDish(dish.id) },
                                    onClick = { onDishClick(dish.id) }
                                )
                            }
                        }
                    }
                }
            }

            // ── 底部悬浮购物车条 ──
            if (cartTotalQuantity > 0 && !showCartSheet) {
                Surface(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(horizontal = 16.dp, vertical = 16.dp)
                        .fillMaxWidth()
                        .shadow(12.dp, RoundedCornerShape(28.dp))
                        .clip(RoundedCornerShape(28.dp))
                        .clickable { showCartSheet = true },
                    color = Color(0xFF2D2D2D)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box {
                                Icon(
                                    imageVector = Icons.Default.ShoppingCart,
                                    contentDescription = "购物车",
                                    tint = CoralOrange,
                                    modifier = Modifier.size(28.dp)
                                )
                                Surface(
                                    modifier = Modifier.align(Alignment.TopEnd),
                                    shape = CircleShape,
                                    color = CoralOrange
                                ) {
                                    Text(
                                        text = "$cartTotalQuantity",
                                        color = Color.White,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = "¥${"%.1f".format(cartTotalPrice)}",
                                color = Color.White,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = CoralOrange
                        ) {
                            Text(
                                text = "去结算",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                modifier = Modifier
                                    .clickable { onCheckout() }
                                    .padding(horizontal = 24.dp, vertical = 10.dp)
                            )
                        }
                    }
                }
            }
        }
    }

    // ── 购物车 BottomSheet ──
    if (showCartSheet) {
        CartSheet(
            onDismiss = { showCartSheet = false },
            onCheckout = {
                showCartSheet = false
                onCheckout()
            }
        )
    }
}
