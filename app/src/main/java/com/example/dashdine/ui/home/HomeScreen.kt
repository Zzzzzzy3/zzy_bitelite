package com.example.dashdine.ui.home

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.dashdine.ui.components.AppSearchBar
import com.example.dashdine.ui.components.StoreCard
import com.example.dashdine.ui.theme.*

/**
 * 金刚区项目
 */
data class KingKongItem(
    val icon: ImageVector,
    val label: String,
    val color: Color
)

val kingKongItems = listOf(
    KingKongItem(Icons.Filled.DeliveryDining, "外卖", CoralOrange),
    KingKongItem(Icons.Filled.Storefront, "到店", TagBlue),
    KingKongItem(Icons.Filled.Icecream, "甜品", TagPurple),
    KingKongItem(Icons.Filled.LocalCafe, "咖啡", Color(0xFF795548)),
    KingKongItem(Icons.Filled.RamenDining, "面食", WarningOrange),
    KingKongItem(Icons.Filled.LunchDining, "快餐", SuccessGreen),
    KingKongItem(Icons.Filled.LocalBar, "饮品", Color(0xFF00BCD4)),
    KingKongItem(Icons.Filled.BakeryDining, "烘焙", Color(0xFFFFB74D))
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onStoreClick: (String) -> Unit,
    onOrderHistoryClick: () -> Unit = {},
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val filteredStores = remember(uiState.stores, uiState.searchQuery) {
        viewModel.getFilteredStores()
    }

    Scaffold(
        containerColor = BackgroundWhite
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(bottom = 16.dp)
        ) {
            // ── 顶部定位 + 搜索栏 ──
            item {
                HomeHeader(
                    location = uiState.location,
                    searchQuery = uiState.searchQuery,
                    onSearchQueryChange = viewModel::onSearchQueryChange,
                    onOrderHistoryClick = onOrderHistoryClick
                )
            }

            // ── 金刚区 ──
            item {
                KingKongSection(items = kingKongItems)
            }

            // ── 店铺列表标题 ──
            item {
                Text(
                    text = "附近好店",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }

            // ── 店铺卡片列表 ──
            if (uiState.isLoading) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = CoralOrange)
                    }
                }
            } else {
                items(filteredStores, key = { it.id }) { store ->
                    StoreCard(
                        store = store,
                        onClick = { onStoreClick(store.id) },
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun HomeHeader(
    location: String,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onOrderHistoryClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Brush.verticalGradient(
                    colors = listOf(CoralOrange, CoralOrangeLight.copy(alpha = 0.3f), BackgroundWhite)
                )
            )
            .padding(top = 48.dp, bottom = 16.dp)
    ) {
        // 定位行
        Row(
            modifier = Modifier.padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Filled.LocationOn,
                contentDescription = "定位",
                tint = Color.White,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = location,
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold
            )
            Icon(
                imageVector = Icons.Filled.KeyboardArrowDown,
                contentDescription = "切换",
                tint = Color.White,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.weight(1f))
            IconButton(
                onClick = onOrderHistoryClick,
                modifier = Modifier.size(28.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ReceiptLong,
                    contentDescription = "订单",
                    tint = Color.White,
                    modifier = Modifier.size(22.dp)
                )
            }
            Spacer(modifier = Modifier.width(4.dp))
            Icon(
                imageVector = Icons.Filled.Notifications,
                contentDescription = "通知",
                tint = Color.White,
                modifier = Modifier.size(22.dp)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // 搜索栏
        AppSearchBar(
            query = searchQuery,
            onQueryChange = onSearchQueryChange,
            modifier = Modifier.padding(horizontal = 16.dp),
            placeholder = "搜索附近的美食"
        )
    }
}

@Composable
private fun KingKongSection(items: List<KingKongItem>) {
    val infiniteTransition = rememberInfiniteTransition(label = "scroll")
    val scrollOffset = infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(20000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "scrollOffset"
    )

    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(items) { item ->
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.width(56.dp)
            ) {
                Surface(
                    modifier = Modifier.size(48.dp),
                    shape = RoundedCornerShape(16.dp),
                    color = item.color.copy(alpha = 0.12f)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = item.icon,
                            contentDescription = item.label,
                            tint = item.color,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = item.label,
                    fontSize = 11.sp,
                    color = TextSecondary,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }

    HorizontalDivider(
        color = DividerLight,
        thickness = 0.5.dp,
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
    )
}
