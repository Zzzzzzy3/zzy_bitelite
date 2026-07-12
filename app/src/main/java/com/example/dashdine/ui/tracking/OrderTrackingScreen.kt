package com.example.dashdine.ui.tracking

import android.content.Intent
import android.provider.ContactsContract
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.dashdine.ui.theme.*
import kotlinx.coroutines.delay
import kotlin.math.cos
import kotlin.math.sin

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderTrackingScreen(
    viewModel: OrderTrackingViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    // 骑手位置动画
    val animatedProgress = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        while (true) {
            animatedProgress.animateTo(
                targetValue = uiState.riderProgress,
                animationSpec = tween(1000, easing = LinearEasing)
            )
            viewModel.simulateRiderMove()
            delay(1500)
        }
    }

    // ── 聊天弹窗状态 ──
    var showChatDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundWhite)
    ) {
        // ── 顶部栏 ──
        TrackingTopBar()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(bottom = 24.dp)
        ) {
            DeliveryMapCard(
                animatedProgress = animatedProgress.value,
                currentStage = uiState.currentStage,
                storeName = uiState.storeName,
                deliveryAddress = uiState.deliveryAddress
            )

            RiderInfoCard(
                riderName = uiState.riderName,
                riderPhone = uiState.riderPhone,
                riderRating = uiState.riderRating,
                estimatedTime = uiState.estimatedTime,
                currentStage = uiState.currentStage,
                onPhoneClick = {
                    val intent = Intent(ContactsContract.Intents.Insert.ACTION).apply {
                        type = ContactsContract.RawContacts.CONTENT_TYPE
                        putExtra(ContactsContract.Intents.Insert.NAME, uiState.riderName)
                        putExtra(ContactsContract.Intents.Insert.PHONE, uiState.riderPhone)
                        putExtra(ContactsContract.Intents.Insert.PHONE_TYPE,
                            ContactsContract.CommonDataKinds.Phone.TYPE_WORK)
                    }
                    context.startActivity(intent)
                },
                onChatClick = { showChatDialog = true }
            )

            TrackingTimeline(currentStage = uiState.currentStage)

            OrderInfoCard(
                orderId = uiState.orderId,
                storeName = uiState.storeName,
                address = uiState.deliveryAddress
            )
        }
    }

    // ── 骑手聊天弹窗 ──
    if (showChatDialog) {
        RiderChatDialog(
            riderName = uiState.riderName,
            onDismiss = { showChatDialog = false }
        )
    }
}

@Composable
private fun TrackingTopBar() {
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
            Icon(
                Icons.Filled.Navigation,
                contentDescription = null,
                tint = CoralOrange,
                modifier = Modifier.size(28.dp)
            )
            Spacer(Modifier.width(10.dp))
            Column {
                Text("订单追踪", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                Text("实时查看配送进度", fontSize = 12.sp, color = TextSecondary)
            }
        }
    }
}

// ─────────── 配送地图 ───────────

@Composable
private fun DeliveryMapCard(
    animatedProgress: Float,
    currentStage: TrackingStage,
    storeName: String,
    deliveryAddress: String
) {
    Surface(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        shape = RoundedCornerShape(20.dp),
        shadowElevation = 4.dp,
        color = SurfaceWhite
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(260.dp)
                    .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
            ) {
                DeliveryMapCanvas(progress = animatedProgress, modifier = Modifier.fillMaxSize())
                Surface(
                    modifier = Modifier.align(Alignment.TopCenter).padding(top = 12.dp),
                    shape = RoundedCornerShape(20.dp),
                    color = when (currentStage) {
                        TrackingStage.DELIVERED -> SuccessGreen
                        else -> CoralOrange
                    }.copy(alpha = 0.9f)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (currentStage != TrackingStage.DELIVERED) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(14.dp), strokeWidth = 2.dp, color = Color.White
                            )
                            Spacer(Modifier.width(8.dp))
                        }
                        Icon(
                            if (currentStage == TrackingStage.DELIVERED) Icons.Filled.CheckCircle
                            else Icons.Filled.DeliveryDining,
                            contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp)
                        )
                        Spacer(Modifier.width(6.dp))
                        Text(
                            currentStage.label, color = Color.White,
                            fontSize = 14.sp, fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(horizontalAlignment = Alignment.Start) {
                    Text("起", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = SuccessGreen)
                    Text(storeName, fontSize = 12.sp, color = TextPrimary, fontWeight = FontWeight.Medium)
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text("终", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = ErrorRed)
                    Text(deliveryAddress.take(12) + "...", fontSize = 12.sp, color = TextPrimary, fontWeight = FontWeight.Medium)
                }
            }
        }
    }
}

@Composable
private fun DeliveryMapCanvas(progress: Float, modifier: Modifier = Modifier) {
    val startX = 0.15f; val startY = 0.75f
    val cp1X = 0.35f; val cp1Y = 0.1f
    val cp2X = 0.65f; val cp2Y = 0.85f
    val endX = 0.88f; val endY = 0.3f

    Canvas(modifier = modifier) {
        val w = size.width; val h = size.height
        drawRect(color = Color(0xFFE8F5E9).copy(alpha = 0.5f), topLeft = Offset.Zero, size = size)
        val gridColor = Color(0xFFC8E6C9).copy(alpha = 0.4f)
        for (i in 0..8) drawLine(gridColor, Offset(i * w / 8, 0f), Offset(i * w / 8, h), strokeWidth = 1f)
        for (i in 0..6) drawLine(gridColor, Offset(0f, i * h / 6), Offset(w, i * h / 6), strokeWidth = 1f)
        val riverPath = Path().apply {
            moveTo(0f, h * 0.4f); cubicTo(w * 0.3f, h * 0.35f, w * 0.6f, h * 0.5f, w, h * 0.45f)
        }
        drawPath(riverPath, color = Color(0xFFBBDEFB).copy(alpha = 0.6f), style = Stroke(width = 14f, cap = StrokeCap.Round))

        val pathStart = Offset(w * startX, h * startY)
        val pathCp1 = Offset(w * cp1X, h * cp1Y)
        val pathCp2 = Offset(w * cp2X, h * cp2Y)
        val pathEnd = Offset(w * endX, h * endY)
        val deliveryPath = Path().apply {
            moveTo(pathStart.x, pathStart.y)
            cubicTo(pathCp1.x, pathCp1.y, pathCp2.x, pathCp2.y, pathEnd.x, pathEnd.y)
        }
        drawPath(deliveryPath, color = CoralOrange.copy(alpha = 0.5f), style = Stroke(width = 4f, cap = StrokeCap.Round))

        val t = progress.coerceIn(0f, 1f)
        val riderX = (1-t)*(1-t)*(1-t)*pathStart.x + 3*(1-t)*(1-t)*t*pathCp1.x + 3*(1-t)*t*t*pathCp2.x + t*t*t*pathEnd.x
        val riderY = (1-t)*(1-t)*(1-t)*pathStart.y + 3*(1-t)*(1-t)*t*pathCp1.y + 3*(1-t)*t*t*pathCp2.y + t*t*t*pathEnd.y

        drawCircle(Color.White, 18f, pathStart)
        drawCircle(SuccessGreen, 14f, pathStart)
        val houseSize = 7f
        drawLine(SuccessGreen, Offset(pathStart.x - houseSize, pathStart.y + 4f), Offset(pathStart.x + houseSize, pathStart.y + 4f), strokeWidth = 1.5f)

        drawCircle(Color.White, 18f, pathEnd)
        drawCircle(ErrorRed, 14f, pathEnd)
        drawCircle(Color.White, 5f, pathEnd)

        val riderPos = Offset(riderX, riderY)
        val pulseRadius = 20f + 4f * sin(progress * 30f)
        drawCircle(CoralOrange.copy(alpha = 0.15f), pulseRadius, riderPos)
        drawCircle(CoralOrange.copy(alpha = 0.25f), pulseRadius * 0.65f, riderPos)

        drawCircle(Color.White, 16f, riderPos)
        drawCircle(CoralOrange, 12f, riderPos)
        val angle = if (progress < 0.5f) -0.5f else 0.3f
        val arrowPos = Offset(riderX + 8f * cos(angle).toFloat(), riderY + 8f * sin(angle).toFloat())
        drawCircle(Color.White, 5f, arrowPos)
    }
}

// ─────────── 骑手信息卡 ───────────

@Composable
private fun RiderInfoCard(
    riderName: String, riderPhone: String, riderRating: Float,
    estimatedTime: String, currentStage: TrackingStage,
    onPhoneClick: () -> Unit, onChatClick: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
        shape = RoundedCornerShape(16.dp), color = SurfaceWhite, shadowElevation = 2.dp
    ) {
        Row(modifier = Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Surface(
                shape = CircleShape, color = CoralOrange.copy(alpha = 0.12f), modifier = Modifier.size(56.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(Icons.Filled.DeliveryDining, contentDescription = null, tint = CoralOrange, modifier = Modifier.size(32.dp))
                }
            }
            Spacer(Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(riderName, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                    Spacer(Modifier.width(8.dp))
                    Surface(shape = RoundedCornerShape(8.dp), color = SuccessGreen.copy(alpha = 0.1f)) {
                        Row(modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Filled.Star, contentDescription = null, tint = WarningOrange, modifier = Modifier.size(12.dp))
                            Text("$riderRating", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = SuccessGreen)
                        }
                    }
                }
                Spacer(Modifier.height(4.dp))
                Text("骑手电话：$riderPhone", fontSize = 13.sp, color = TextSecondary)
                Spacer(Modifier.height(2.dp))
                Text(estimatedTime, fontSize = 14.sp, fontWeight = FontWeight.SemiBold,
                    color = if (currentStage == TrackingStage.DELIVERED) SuccessGreen else CoralOrange)
            }
            Column {
                FilledIconButton(
                    onClick = onPhoneClick, modifier = Modifier.size(40.dp), shape = CircleShape,
                    colors = IconButtonDefaults.filledIconButtonColors(containerColor = SuccessGreen.copy(alpha = 0.12f))
                ) { Icon(Icons.Filled.Phone, "存为联系人", tint = SuccessGreen, modifier = Modifier.size(20.dp)) }
                Spacer(Modifier.height(6.dp))
                FilledIconButton(
                    onClick = onChatClick, modifier = Modifier.size(40.dp), shape = CircleShape,
                    colors = IconButtonDefaults.filledIconButtonColors(containerColor = CoralOrange.copy(alpha = 0.12f))
                ) { Icon(Icons.Filled.Chat, "联系骑手", tint = CoralOrange, modifier = Modifier.size(20.dp)) }
            }
        }
    }
}

// ─────────── 骑手聊天弹窗 ───────────

data class RiderMsg(val text: String, val fromRider: Boolean)

@Composable
private fun RiderChatDialog(riderName: String, onDismiss: () -> Unit) {
    val presetMessages = listOf(
        RiderMsg("您好，我是您的骑手，正在配送途中 🛵", true),
        RiderMsg("预计还有10分钟左右到达", true),
        RiderMsg("好的，麻烦您了", false),
        RiderMsg("不客气！请保持电话畅通 📱", true),
    )
    var messages by remember { mutableStateOf(presetMessages) }
    var inputText by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(24.dp),
        containerColor = SurfaceWhite,
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(max = 500.dp),
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(shape = CircleShape, color = CoralOrange.copy(alpha = 0.12f), modifier = Modifier.size(36.dp)) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(Icons.Filled.DeliveryDining, null, tint = CoralOrange, modifier = Modifier.size(20.dp))
                    }
                }
                Spacer(Modifier.width(10.dp))
                Column {
                    Text(riderName, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                    Text("配送中", fontSize = 12.sp, color = SuccessGreen)
                }
            }
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                // 消息列表
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 280.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    messages.forEach { msg ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = if (msg.fromRider) Arrangement.Start else Arrangement.End
                        ) {
                            Surface(
                                shape = if (msg.fromRider)
                                    RoundedCornerShape(16.dp, 16.dp, 16.dp, 4.dp)
                                else
                                    RoundedCornerShape(16.dp, 16.dp, 4.dp, 16.dp),
                                color = if (msg.fromRider) BackgroundWhite else CoralOrange,
                                shadowElevation = if (msg.fromRider) 1.dp else 0.dp,
                                modifier = Modifier.widthIn(max = 240.dp)
                            ) {
                                Text(
                                    msg.text,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                    fontSize = 14.sp,
                                    color = if (msg.fromRider) TextPrimary else Color.White
                                )
                            }
                        }
                    }
                }

                Spacer(Modifier.height(8.dp))

                // 输入行
                Row(verticalAlignment = Alignment.CenterVertically) {
                    OutlinedTextField(
                        value = inputText,
                        onValueChange = { inputText = it },
                        placeholder = { Text("输入消息...", fontSize = 13.sp, color = TextHint) },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        shape = RoundedCornerShape(20.dp),
                        textStyle = MaterialTheme.typography.bodyMedium.copy(fontSize = 14.sp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = CoralOrange, unfocusedBorderColor = DividerLight
                        )
                    )
                    Spacer(Modifier.width(8.dp))
                    FilledIconButton(
                        onClick = {
                            if (inputText.isNotBlank()) {
                                messages = messages + RiderMsg(inputText.trim(), false)
                                inputText = ""
                                // 模拟骑手回复
                                messages = messages + RiderMsg("收到，马上处理 👍", true)
                            }
                        },
                        modifier = Modifier.size(40.dp),
                        shape = CircleShape,
                        colors = IconButtonDefaults.filledIconButtonColors(containerColor = CoralOrange)
                    ) {
                        Icon(Icons.Filled.KeyboardArrowUp, "发送", tint = Color.White, modifier = Modifier.size(18.dp))
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss, modifier = Modifier.fillMaxWidth()) {
                Text("关闭", color = TextSecondary)
            }
        }
    )
}

// ─────────── 时间线 ───────────

@Composable
private fun TrackingTimeline(currentStage: TrackingStage) {
    val stages = TrackingStage.entries.toList()
    Surface(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
        shape = RoundedCornerShape(16.dp), color = SurfaceWhite, shadowElevation = 2.dp
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text("配送进度", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
            Spacer(Modifier.height(16.dp))
            stages.forEach { stage ->
                val isCompleted = stage.step < currentStage.step
                val isCurrent = stage == currentStage
                val isLast = stage == stages.last()
                Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.Top) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.width(32.dp)) {
                        Box(
                            modifier = Modifier.size(if (isCurrent) 16.dp else 12.dp)
                                .then(if (isCurrent) Modifier.shadow(4.dp, CircleShape) else Modifier),
                            contentAlignment = Alignment.Center
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = when { isCompleted -> SuccessGreen; isCurrent -> CoralOrange; else -> DividerLight },
                                modifier = Modifier.fillMaxSize()
                            ) {
                                if (isCompleted) Box(contentAlignment = Alignment.Center) {
                                    Icon(Icons.Filled.Check, null, tint = Color.White, modifier = Modifier.size(8.dp))
                                }
                            }
                        }
                        if (!isLast) Box(
                            modifier = Modifier.width(2.dp).height(32.dp)
                                .background(if (isCompleted) SuccessGreen.copy(alpha = 0.5f) else DividerLight)
                        )
                    }
                    Spacer(Modifier.width(12.dp))
                    Column(modifier = Modifier.padding(top = 2.dp)) {
                        Text(stage.label, fontSize = 14.sp,
                            fontWeight = if (isCurrent || isCompleted) FontWeight.SemiBold else FontWeight.Normal,
                            color = when { isCompleted -> SuccessGreen; isCurrent -> TextPrimary; else -> TextHint })
                        if (isCurrent) { Spacer(Modifier.height(2.dp)); Text(getStageDesc(stage), fontSize = 12.sp, color = TextSecondary) }
                    }
                }
            }
        }
    }
}

private fun getStageDesc(stage: TrackingStage): String = when (stage) {
    TrackingStage.CONFIRMED -> "商家已确认您的订单"
    TrackingStage.PREPARING -> "商家正在精心制作中"
    TrackingStage.PICKING_UP -> "骑手正赶往商家取餐"
    TrackingStage.DELIVERING -> "骑手正在配送途中"
    TrackingStage.DELIVERED -> "订单已送达，请享用！"
}

// ─────────── 订单详情卡 ───────────

@Composable
private fun OrderInfoCard(orderId: String, storeName: String, address: String) {
    Surface(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
        shape = RoundedCornerShape(16.dp), color = SurfaceWhite, shadowElevation = 2.dp
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text("订单详情", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
            Spacer(Modifier.height(12.dp))
            OrderInfoRow("订单编号", orderId)
            OrderInfoRow("商家", storeName)
            OrderInfoRow("送达地址", address)
            Spacer(Modifier.height(12.dp))
            HorizontalDivider(color = DividerLight)
            Spacer(Modifier.height(12.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                TextButton(onClick = { /* TODO */ }) {
                    Icon(Icons.Filled.Store, null, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(4.dp)); Text("联系商家", fontSize = 13.sp)
                }
                TextButton(onClick = { /* TODO */ }) {
                    Icon(Icons.Filled.HelpOutline, null, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(4.dp)); Text("帮助", fontSize = 13.sp)
                }
            }
        }
    }
}

@Composable
private fun OrderInfoRow(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, fontSize = 13.sp, color = TextSecondary)
        Text(value, fontSize = 13.sp, color = TextPrimary, fontWeight = FontWeight.Medium)
    }
}
