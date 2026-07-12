package com.example.dashdine.ui.dish

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.SubcomposeAsyncImage
import com.example.dashdine.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DishDetailScreen(
    dishId: String,
    onBack: () -> Unit,
    viewModel: DishDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val dish = uiState.dish

    // 加购动画
    var triggerAnim by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (triggerAnim) 1.3f else 1f,
        animationSpec = spring(dampingRatio = 0.3f, stiffness = 400f),
        label = "add_anim",
        finishedListener = { triggerAnim = false }
    )

    Scaffold(
        containerColor = SurfaceWhite,
        topBar = {
            if (dish != null && !uiState.isLoading) {
                TopAppBar(
                    title = {
                        Text(
                            text = dish.name,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
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
        },
        bottomBar = {
            if (dish != null && !uiState.loadError) {
                BottomAppBar(
                    containerColor = SurfaceWhite,
                    tonalElevation = 8.dp,
                    modifier = Modifier.height(72.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // 收藏按钮
                        IconButton(onClick = { /* TODO */ }) {
                            Icon(
                                imageVector = Icons.Default.FavoriteBorder,
                                contentDescription = "收藏",
                                tint = TextSecondary,
                                modifier = Modifier.size(24.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        // 价格区
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.Bottom) {
                                Text("¥", fontSize = 14.sp, color = CoralOrange, fontWeight = FontWeight.Bold)
                                Text(
                                    text = "${dish.price.toInt()}",
                                    fontSize = 28.sp,
                                    color = CoralOrange,
                                    fontWeight = FontWeight.Bold
                                )
                                if (dish.originalPrice > dish.price) {
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "¥${dish.originalPrice.toInt()}",
                                        fontSize = 14.sp,
                                        color = TextHint,
                                        textDecoration = androidx.compose.ui.text.style.TextDecoration.LineThrough
                                    )
                                }
                            }
                        }

                        // 加入购物车按钮
                        Button(
                            onClick = {
                                viewModel.onAddToCart()
                                triggerAnim = true
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = CoralOrange),
                            shape = RoundedCornerShape(24.dp),
                            modifier = Modifier
                                .height(48.dp)
                                .scale(scale)
                        ) {
                            Icon(
                                imageVector = Icons.Default.ShoppingCart,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (uiState.cartQuantity > 0) "已加${uiState.cartQuantity}份" else "加入购物车",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                        }
                    }
                }
            }
        }
    ) { paddingValues ->
        // ── 加载中 ──
        if (uiState.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize().padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = CoralOrange)
            }
        }
        // ── 加载失败 ──
        else if (uiState.loadError) {
            Column(
                modifier = Modifier.fillMaxSize().padding(paddingValues),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "菜品加载失败",
                    style = MaterialTheme.typography.titleMedium,
                    color = TextSecondary
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "请返回重试 (dishId: $dishId)",
                    fontSize = 13.sp,
                    color = TextHint
                )
            }
        }
        // ── 菜品内容 ──
        else if (dish != null) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
            ) {
                // ── 沉浸式大图 ──
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(320.dp)
                ) {
                    // 背景占位色
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(BackgroundWhite)
                    )

                    SubcomposeAsyncImage(
                        model = dish.imageUrl,
                        contentDescription = dish.name,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize(),
                        loading = {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(BackgroundWhite),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(
                                        imageVector = Icons.Outlined.Image,
                                        contentDescription = null,
                                        tint = DividerLight,
                                        modifier = Modifier.size(48.dp)
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = "加载中...",
                                        fontSize = 13.sp,
                                        color = TextHint
                                    )
                                }
                            }
                        },
                        error = {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(BackgroundWhite),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(
                                        imageVector = Icons.Outlined.Image,
                                        contentDescription = null,
                                        tint = DividerLight,
                                        modifier = Modifier.size(48.dp)
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = dish.name,
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextPrimary
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "图片加载失败",
                                        fontSize = 12.sp,
                                        color = TextHint
                                    )
                                }
                            }
                        }
                    )

                    // 渐变遮罩
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        Color.Transparent,
                                        Color.Transparent,
                                        SurfaceWhite
                                    ),
                                    startY = 0f,
                                    endY = Float.POSITIVE_INFINITY
                                )
                            )
                    )

                }

                // ── 信息面板 ──
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 16.dp)
                ) {
                    // 菜品名称
                    Text(
                        text = dish.name,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // 描述
                    Text(
                        text = dish.description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary,
                        lineHeight = 22.sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // 附加信息标签
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        if (dish.calories.isNotEmpty()) {
                            InfoChip("🔥 ${dish.calories}")
                        }
                        if (dish.allergens.isNotEmpty()) {
                            InfoChip("⚠️ ${dish.allergens}")
                        }
                        InfoChip("📦 月售${dish.monthlySales}+")
                        InfoChip("⭐ ${dish.rating}")
                    }

                    // ── 规格选择 ──
                    if (dish.specs.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(20.dp))
                        HorizontalDivider(color = DividerLight)
                        Spacer(modifier = Modifier.height(16.dp))

                        dish.specs.forEach { specGroup ->
                            Text(
                                text = specGroup.name,
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                specGroup.options.forEach { option ->
                                    val isSelected = uiState.selectedSpecs[specGroup.name] == option.name
                                    FilterChip(
                                        selected = isSelected,
                                        onClick = { viewModel.onSpecSelected(specGroup.name, option.name) },
                                        label = {
                                            Text(
                                                text = option.name + if (option.priceDelta > 0) " +¥${option.priceDelta.toInt()}" else "",
                                                fontSize = 13.sp
                                            )
                                        },
                                        colors = FilterChipDefaults.filterChipColors(
                                            selectedContainerColor = CoralOrange.copy(alpha = 0.15f),
                                            selectedLabelColor = CoralOrange
                                        ),
                                        shape = RoundedCornerShape(20.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                        }
                    }

                    // ── 食材信息 ──
                    if (dish.ingredients.isNotEmpty()) {
                        HorizontalDivider(color = DividerLight)
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "食材",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = dish.ingredients,
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary
                        )
                    }

                    Spacer(modifier = Modifier.height(100.dp)) // 为底部栏留空间
                }
            }
        }
    }
}

@Composable
private fun InfoChip(text: String) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = BackgroundWhite
    ) {
        Text(
            text = text,
            fontSize = 11.sp,
            color = TextSecondary,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}
