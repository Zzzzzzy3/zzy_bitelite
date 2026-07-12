package com.example.dashdine.ui.cart

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import com.example.dashdine.data.model.CartItem
import com.example.dashdine.ui.components.Stepper
import com.example.dashdine.ui.theme.*
import dagger.hilt.android.EntryPointAccessors
import com.example.dashdine.BiteLiteApp
import com.example.dashdine.di.AppModule

/**
 * 购物车 BottomSheet — Modal BottomSheet 风格
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartSheet(
    onDismiss: () -> Unit,
    onCheckout: () -> Unit
) {
    // 获取 CartManager 单例
    val appContext = androidx.compose.ui.platform.LocalContext.current.applicationContext as BiteLiteApp
    val entryPoint = EntryPointAccessors.fromApplication(appContext, CartEntryPoint::class.java)
    val cartManager = entryPoint.cartManager()

    val cartItems by cartManager.items.collectAsState()

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.BottomCenter
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.6f),
                shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
                color = SurfaceWhite,
                shadowElevation = 16.dp
            ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // 标题行
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "购物车",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                    TextButton(
                        onClick = { cartManager.clearCart() }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "清空",
                            tint = TextSecondary,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("清空", color = TextSecondary, fontSize = 13.sp)
                    }
                }

                // 购物车列表
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(horizontal = 16.dp)
                ) {
                    items(
                        items = cartItems,
                        key = { it.dish.id }
                    ) { item ->
                        CartItemRow(
                            item = item,
                            onIncrease = { cartManager.addDish(item.dish, cartManager.currentStoreId.value!!) },
                            onDecrease = { cartManager.removeDish(item.dish.id) }
                        )
                    }
                }

                HorizontalDivider(color = DividerLight)

                // 底部结算
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = "¥${"%.1f".format(cartManager.getTotalPrice())}",
                            color = CoralOrange,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "共${cartManager.getTotalQuantity()}件商品",
                            fontSize = 12.sp,
                            color = TextSecondary
                        )
                    }
                    Button(
                        onClick = onCheckout,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = CoralOrange
                        ),
                        shape = RoundedCornerShape(24.dp),
                        modifier = Modifier.height(48.dp)
                    ) {
                        Text(
                            text = "去结算",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                    }
                }
            }
            }
        }
    }
}

@Composable
private fun CartItemRow(
    item: CartItem,
    onIncrease: () -> Unit,
    onDecrease: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = item.dish.imageUrl,
            contentDescription = item.dish.name,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(56.dp)
                .clip(RoundedCornerShape(10.dp))
        )

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = item.dish.name,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "¥${item.dish.price.toInt()}",
                color = CoralOrange,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Stepper(
            quantity = item.quantity,
            onIncrease = onIncrease,
            onDecrease = onDecrease
        )
    }
}
