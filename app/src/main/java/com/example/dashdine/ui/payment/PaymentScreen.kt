package com.example.dashdine.ui.payment

import android.graphics.Bitmap
import android.graphics.Color as AndroidColor
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dashdine.ui.theme.*
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.qrcode.QRCodeWriter
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel
import java.util.EnumMap

/**
 * 支付页面 — 展示订单信息和微信/支付宝扫码支付二维码
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentScreen(
    orderId: String,
    orderAmount: Float,
    onBack: () -> Unit,
    onPaymentSuccess: () -> Unit
) {
    val paymentUrl = "https://github.com/Zzzzzzy3"

    // 生成二维码 Bitmap
    val qrBitmap = remember {
        generateQrCode(paymentUrl, 512)
    }

    Scaffold(
        containerColor = BackgroundWhite,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "支付订单",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
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
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // ── 订单金额 ──
            Text(
                text = "应付金额",
                fontSize = 14.sp,
                color = TextSecondary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.Bottom) {
                Text(
                    text = "¥",
                    fontSize = 20.sp,
                    color = CoralOrange,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "%.1f".format(orderAmount),
                    fontSize = 40.sp,
                    color = CoralOrange,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "订单号：$orderId",
                fontSize = 12.sp,
                color = TextHint
            )

            Spacer(modifier = Modifier.height(32.dp))

            // ── 支付方式选择 ──
            var selectedMethod by remember { mutableStateOf(0) } // 0=微信, 1=支付宝
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                PaymentMethodTab(
                    label = "微信支付",
                    isSelected = selectedMethod == 0,
                    onClick = { selectedMethod = 0 },
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(12.dp))
                PaymentMethodTab(
                    label = "支付宝",
                    isSelected = selectedMethod == 1,
                    onClick = { selectedMethod = 1 },
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // ── 二维码 ──
            Card(
                modifier = Modifier.size(260.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Box(
                    modifier = Modifier.fillMaxSize().padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    if (qrBitmap != null) {
                        Image(
                            bitmap = qrBitmap.asImageBitmap(),
                            contentDescription = "支付二维码",
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        CircularProgressIndicator(color = CoralOrange)
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "请使用${if (selectedMethod == 0) "微信" else "支付宝"}扫一扫付款",
                fontSize = 13.sp,
                color = TextSecondary
            )

            Spacer(modifier = Modifier.height(24.dp))

            // ── 模拟支付完成按钮 ──
            Button(
                onClick = onPaymentSuccess,
                colors = ButtonDefaults.buttonColors(containerColor = CoralOrange),
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
            ) {
                Text(
                    text = "已完成支付",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "点击按钮模拟支付成功",
                fontSize = 12.sp,
                color = TextHint,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun PaymentMethodTab(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        color = if (isSelected) CoralOrange.copy(alpha = 0.1f) else SurfaceWhite,
        border = if (isSelected) {
            androidx.compose.foundation.BorderStroke(1.5.dp, CoralOrange)
        } else {
            androidx.compose.foundation.BorderStroke(1.dp, DividerLight)
        }
    ) {
        Text(
            text = label,
            fontSize = 14.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            color = if (isSelected) CoralOrange else TextSecondary,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(vertical = 12.dp)
        )
    }
}

/**
 * 使用 ZXing 生成二维码 Bitmap
 */
private fun generateQrCode(content: String, size: Int): Bitmap? {
    return try {
        val hints = EnumMap<EncodeHintType, Any>(EncodeHintType::class.java)
        hints[EncodeHintType.CHARACTER_SET] = "UTF-8"
        hints[EncodeHintType.ERROR_CORRECTION] = ErrorCorrectionLevel.M
        hints[EncodeHintType.MARGIN] = 1

        val writer = QRCodeWriter()
        val bitMatrix = writer.encode(content, BarcodeFormat.QR_CODE, size, size, hints)

        val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
        for (x in 0 until size) {
            for (y in 0 until size) {
                bitmap.setPixel(
                    x, y,
                    if (bitMatrix[x, y]) AndroidColor.BLACK else AndroidColor.WHITE
                )
            }
        }
        bitmap
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}
