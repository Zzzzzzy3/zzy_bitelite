package com.example.dashdine.ui.chat

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.dashdine.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AiChatScreen(
    onFoodClick: (String) -> Unit = {},
    viewModel: AiChatViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val listState = rememberLazyListState()

    // 自动滚动到最新消息
    LaunchedEffect(uiState.messages.size) {
        if (uiState.messages.isNotEmpty()) {
            listState.animateScrollToItem(uiState.messages.size - 1)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundWhite)
    ) {
        // ── 顶部栏 ──
        AiChatTopBar()

        // ── 错误提示 ──
        AnimatedVisibility(
            visible = uiState.error != null,
            enter = slideInVertically() + fadeIn(),
            exit = slideOutVertically() + fadeOut()
        ) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = WarningOrange.copy(alpha = 0.1f)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Filled.Warning,
                        contentDescription = null,
                        tint = WarningOrange,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = uiState.error ?: "",
                        color = WarningOrange,
                        fontSize = 13.sp,
                        modifier = Modifier.weight(1f)
                    )
                    TextButton(onClick = viewModel::clearError) {
                        Text("知道了", fontSize = 12.sp)
                    }
                }
            }
        }

        // ── 消息列表 ──
        LazyColumn(
            state = listState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(uiState.messages, key = { it.id }) { message ->
                ChatBubble(
                    message = message,
                    isUser = message.role == "user",
                    onFoodClick = onFoodClick
                )
            }

            // 加载指示器
            if (uiState.isLoading) {
                item {
                    Row(
                        modifier = Modifier.padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = MintGreen.copy(alpha = 0.15f),
                            modifier = Modifier.size(36.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    Icons.Filled.SmartToy,
                                    contentDescription = null,
                                    tint = MintGreen,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                        Spacer(Modifier.width(8.dp))
                        Surface(
                            shape = RoundedCornerShape(16.dp, 16.dp, 16.dp, 4.dp),
                            color = SurfaceWhite,
                            shadowElevation = 1.dp
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(16.dp),
                                    strokeWidth = 2.dp,
                                    color = CoralOrange
                                )
                                Spacer(Modifier.width(8.dp))
                                Text(
                                    "小食光正在思考...",
                                    fontSize = 13.sp,
                                    color = TextSecondary
                                )
                            }
                        }
                    }
                }
            }
        }

        // ── 底部输入栏 ──
        ChatInputBar(
            text = uiState.inputText,
            onTextChange = viewModel::onInputChange,
            onSend = viewModel::sendMessage,
            enabled = !uiState.isLoading
        )
    }
}

@Composable
private fun AiChatTopBar() {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = SurfaceWhite,
        shadowElevation = 1.dp
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 12.dp)
                .statusBarsPadding(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = CircleShape,
                color = MintGreen,
                modifier = Modifier.size(40.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        Icons.Filled.SmartToy,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
            Spacer(Modifier.width(12.dp))
            Column {
                Text(
                    "小食光 AI",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Text(
                    "你的私人美食顾问",
                    fontSize = 12.sp,
                    color = TextSecondary
                )
            }
        }
    }
}

@Composable
private fun ChatBubble(
    message: ChatMessageUi,
    isUser: Boolean,
    onFoodClick: (String) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = if (isUser) Alignment.End else Alignment.Start
    ) {
        Row(
            modifier = Modifier.widthIn(max = 320.dp),
            verticalAlignment = Alignment.Bottom
        ) {
            if (!isUser) {
                Surface(
                    shape = CircleShape,
                    color = MintGreen.copy(alpha = 0.15f),
                    modifier = Modifier.size(32.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            Icons.Filled.SmartToy,
                            contentDescription = null,
                            tint = MintGreen,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
                Spacer(Modifier.width(8.dp))
            }

            Column(
                horizontalAlignment = if (isUser) Alignment.End else Alignment.Start
            ) {
                Surface(
                    shape = if (isUser)
                        RoundedCornerShape(18.dp, 18.dp, 4.dp, 18.dp)
                    else
                        RoundedCornerShape(18.dp, 18.dp, 18.dp, 4.dp),
                    color = if (isUser) CoralOrange else SurfaceWhite,
                    shadowElevation = if (isUser) 0.dp else 1.dp
                ) {
                    Text(
                        text = message.content,
                        color = if (isUser) Color.White else TextPrimary,
                        fontSize = 14.sp,
                        lineHeight = 22.sp,
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)
                    )
                }

                // ── 食物推荐卡片 ──
                if (message.recommendations.isNotEmpty() && !isUser) {
                    Spacer(Modifier.height(8.dp))
                    FoodRecommendationCards(
                        recommendations = message.recommendations,
                        onFoodClick = onFoodClick
                    )
                }
            }

            if (isUser) {
                Spacer(Modifier.width(8.dp))
                Surface(
                    shape = CircleShape,
                    color = CoralOrange.copy(alpha = 0.15f),
                    modifier = Modifier.size(32.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            Icons.Filled.Person,
                            contentDescription = null,
                            tint = CoralOrange,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun FoodRecommendationCards(
    recommendations: List<FoodRecommendation>,
    onFoodClick: (String) -> Unit
) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        contentPadding = PaddingValues(start = 40.dp)
    ) {
        items(recommendations) { food ->
            FoodCard(food = food, onClick = { onFoodClick(food.name) })
        }
    }
}

@Composable
private fun FoodCard(food: FoodRecommendation, onClick: () -> Unit) {
    Surface(
        modifier = Modifier
            .width(160.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        color = SurfaceWhite,
        shadowElevation = 2.dp
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            // 食物图片 — 使用渐变占位图
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .background(
                        Brush.linearGradient(
                            colors = listOf(
                                CoralOrange.copy(alpha = 0.3f),
                                MintGreen.copy(alpha = 0.2f)
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = when {
                        food.name.contains("面") -> Icons.Filled.RamenDining
                        food.name.contains("饭") || food.name.contains("鸡") -> Icons.Filled.LunchDining
                        food.name.contains("甜") || food.name.contains("蛋糕") -> Icons.Filled.Icecream
                        food.name.contains("饮") || food.name.contains("茶") || food.name.contains("水") -> Icons.Filled.LocalBar
                        food.name.contains("炸") || food.name.contains("烧") -> Icons.Filled.OutdoorGrill
                        else -> Icons.Filled.Restaurant
                    },
                    contentDescription = null,
                    tint = CoralOrange.copy(alpha = 0.6f),
                    modifier = Modifier.size(48.dp)
                )
            }

            // 信息区
            Column(
                modifier = Modifier.padding(10.dp)
            ) {
                Text(
                    text = food.name,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = food.reason,
                    fontSize = 11.sp,
                    color = TextSecondary,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    lineHeight = 15.sp
                )
                if (food.price.isNotEmpty()) {
                    Spacer(Modifier.height(6.dp))
                    Text(
                        text = food.price,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = CoralOrange
                    )
                }
            }
        }
    }
}

@Composable
private fun ChatInputBar(
    text: String,
    onTextChange: (String) -> Unit,
    onSend: () -> Unit,
    enabled: Boolean
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shadowElevation = 4.dp,
        color = SurfaceWhite
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp)
                .navigationBarsPadding(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = RoundedCornerShape(24.dp),
                color = BackgroundWhite,
                modifier = Modifier.weight(1f)
            ) {
                OutlinedTextField(
                    value = text,
                    onValueChange = onTextChange,
                    placeholder = {
                        Text(
                            "输入你想吃什么...",
                            fontSize = 14.sp,
                            color = TextHint
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    textStyle = MaterialTheme.typography.bodyMedium.copy(
                        fontSize = 14.sp,
                        color = TextPrimary
                    ),
                    singleLine = false,
                    maxLines = 3,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color.Transparent,
                        unfocusedBorderColor = Color.Transparent
                    ),
                    enabled = enabled
                )
            }
            Spacer(Modifier.width(8.dp))
            FilledIconButton(
                onClick = onSend,
                enabled = enabled && text.isNotBlank(),
                modifier = Modifier.size(44.dp),
                shape = CircleShape,
                colors = IconButtonDefaults.filledIconButtonColors(
                    containerColor = CoralOrange,
                    disabledContainerColor = DividerLight
                )
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.Send,
                    contentDescription = "发送",
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}
