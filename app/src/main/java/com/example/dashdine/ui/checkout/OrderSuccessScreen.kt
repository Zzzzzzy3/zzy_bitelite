package com.example.dashdine.ui.checkout

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dashdine.ui.theme.*

/**
 * 支付成功弹窗页面
 */
@Composable
fun OrderSuccessScreen(
    orderId: String,
    onBackToHome: () -> Unit
) {
    // 成功勾的缩放动画
    val scale by animateFloatAsState(
        targetValue = 1f,
        animationSpec = tween(600, easing = FastOutSlowInEasing),
        label = "success_scale"
    )
    val initialScale = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        initialScale.animateTo(
            targetValue = 1f,
            animationSpec = spring(dampingRatio = 0.4f, stiffness = 300f)
        )
    }

    // 圆圈扩散动画
    val circleScale by animateFloatAsState(
        targetValue = 1f,
        animationSpec = tween(800, easing = FastOutSlowInEasing),
        label = "circle"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(SurfaceWhite),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(32.dp)
        ) {
            // 带动画的成功图标
            Box(
                modifier = Modifier.size(120.dp),
                contentAlignment = Alignment.Center
            ) {
                // 背景扩散圆
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .scale(circleScale)
                        .clip(CircleShape)
                        .background(SuccessGreen.copy(alpha = 0.1f))
                )
                // 绿色对勾圆
                Surface(
                    modifier = Modifier
                        .size(72.dp)
                        .scale(initialScale.value),
                    shape = CircleShape,
                    color = SuccessGreen
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "支付成功",
                            tint = Color.White,
                            modifier = Modifier.size(40.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "支付成功！",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "您的订单已提交，商家正在准备中",
                fontSize = 14.sp,
                color = TextSecondary,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "订单号：$orderId",
                fontSize = 12.sp,
                color = TextHint
            )

            Spacer(modifier = Modifier.height(40.dp))

            Button(
                onClick = onBackToHome,
                colors = ButtonDefaults.buttonColors(containerColor = CoralOrange),
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text(
                    text = "返回首页",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            TextButton(onClick = onBackToHome) {
                Text(
                    text = "查看订单详情",
                    color = TextSecondary,
                    fontSize = 14.sp
                )
            }
        }
    }
}
