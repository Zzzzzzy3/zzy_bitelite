package com.example.dashdine.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dashdine.ui.theme.CoralOrange
import com.example.dashdine.ui.theme.SurfaceWhite
import com.example.dashdine.ui.theme.TextPrimary

/**
 * 数量步进器（- / 数量 / +），用于菜品列表和购物车
 */
@Composable
fun Stepper(
    quantity: Int,
    onIncrease: () -> Unit,
    onDecrease: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scaleAnim by animateFloatAsState(
        targetValue = 1f,
        animationSpec = spring(dampingRatio = 0.4f, stiffness = 400f),
        label = "stepper_scale"
    )

    if (quantity == 0) {
        // 未选中时显示圆形 + 按钮
        Surface(
            modifier = modifier
                .size(28.dp)
                .scale(scaleAnim)
                .clickable(onClick = onIncrease),
            shape = CircleShape,
            color = CoralOrange,
            shadowElevation = 2.dp
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "添加",
                    tint = Color.White,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    } else {
        // 已选中时显示 - 数量 +
        Row(
            modifier = modifier
                .height(28.dp)
                .background(Color.White, RoundedCornerShape(14.dp)),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 减号
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clickable(onClick = onDecrease),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Remove,
                    contentDescription = "减少",
                    tint = CoralOrange,
                    modifier = Modifier.size(16.dp)
                )
            }

            // 数量
            Text(
                text = "$quantity",
                color = TextPrimary,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 6.dp)
            )

            // 加号
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .background(CoralOrange, CircleShape)
                    .clickable(onClick = onIncrease),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "增加",
                    tint = Color.White,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}
