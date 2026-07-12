package com.example.dashdine.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
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
import coil.compose.AsyncImage
import com.example.dashdine.data.model.Dish
import com.example.dashdine.ui.theme.*

/**
 * 菜品卡片 — 用于店铺详情右侧列表
 */
@Composable
fun DishCard(
    dish: Dish,
    quantity: Int,
    onIncrease: () -> Unit,
    onDecrease: () -> Unit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp, horizontal = 4.dp),
        verticalAlignment = Alignment.Top
    ) {
        // 左侧图片
        AsyncImage(
            model = dish.imageUrl,
            contentDescription = dish.name,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(90.dp)
                .clip(RoundedCornerShape(12.dp))
        )

        Spacer(modifier = Modifier.width(12.dp))

        // 右侧信息
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = dish.name,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold,
                color = TextPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(2.dp))

            // 描述
            Text(
                text = dish.description.take(40) + if (dish.description.length > 40) "..." else "",
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(2.dp))

            // 月售
            Text(
                text = "月售${dish.monthlySales}+",
                fontSize = 12.sp,
                color = TextHint
            )

            Spacer(modifier = Modifier.height(4.dp))

            // 价格行
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.Bottom) {
                    Text(
                        text = "¥",
                        fontSize = 12.sp,
                        color = CoralOrange,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${dish.price.toInt()}",
                        fontSize = 20.sp,
                        color = CoralOrange,
                        fontWeight = FontWeight.Bold
                    )
                    if (dish.originalPrice > dish.price) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "¥${dish.originalPrice.toInt()}",
                            fontSize = 12.sp,
                            color = TextHint,
                            textDecoration = TextDecoration.LineThrough
                        )
                    }
                }

                // 步进器
                Stepper(
                    quantity = quantity,
                    onIncrease = onIncrease,
                    onDecrease = onDecrease
                )
            }
        }
    }

    HorizontalDivider(
        color = DividerLight,
        thickness = 0.5.dp,
        modifier = Modifier.padding(top = 4.dp)
    )
}
