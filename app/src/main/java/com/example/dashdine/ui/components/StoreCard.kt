package com.example.dashdine.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.dashdine.data.model.Store
import com.example.dashdine.ui.theme.*

@Composable
fun StoreCard(
    store: Store,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .shadow(8.dp, RoundedCornerShape(20.dp), ambientColor = ShadowColor)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 左侧：店铺图片
            Box(modifier = Modifier.size(80.dp)) {
                AsyncImage(
                    model = store.logoUrl,
                    contentDescription = store.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(16.dp))
                )
                // 右下角标签
                if (store.isNew) {
                    Surface(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(4.dp),
                        shape = RoundedCornerShape(4.dp),
                        color = CoralOrange
                    ) {
                        Text(
                            text = "新店",
                            color = Color.White,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            // 右侧：店铺信息
            Column(modifier = Modifier.weight(1f)) {
                // 店名
                Text(
                    text = store.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                // 评分和月售
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "评分",
                        tint = WarningOrange,
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        text = " ${store.rating}",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = WarningOrange
                    )
                    Text(
                        text = "  月售${store.monthlySales}+",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                    Text(
                        text = "  ${store.distance}",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                // 起送价和配送费
                Row {
                    Text(
                        text = "¥${store.minOrderPrice.toInt()}起送",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                    Text(
                        text = "  |  ",
                        fontSize = 12.sp,
                        color = DividerLight
                    )
                    Text(
                        text = if (store.deliveryFee == 0f) "免配送费" else "配送费¥${store.deliveryFee.toInt()}",
                        fontSize = 12.sp,
                        color = if (store.deliveryFee == 0f) SuccessGreen else TextSecondary
                    )
                    Text(
                        text = "  |  ${store.deliveryTime}",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                // 标签行
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    store.tags.take(3).forEach { tag ->
                        val (bgColor, textColor) = when {
                            tag.contains("满减") || tag.contains("折扣") -> TagBlue to Color.White
                            tag.contains("新") || tag.contains("特惠") -> CoralOrange to Color.White
                            tag.contains("免") -> TagGreen to Color.White
                            else -> BackgroundWhite to TextSecondary
                        }
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = bgColor
                        ) {
                            Text(
                                text = tag,
                                color = textColor,
                                fontSize = 10.sp,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
