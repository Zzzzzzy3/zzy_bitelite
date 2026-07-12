package com.example.dashdine.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.*
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dashdine.ui.theme.*

data class Address(val id: String, val name: String, val phone: String, val detail: String)

@Composable
fun UserProfileScreen(
    onOrderListClick: () -> Unit = {},
    onLogout: () -> Unit = {}
) {
    // ── 可编辑状态 ──
    var userName by remember { mutableStateOf("美食爱好者") }
    var showEditDialog by remember { mutableStateOf(false) }
    val avatarColors = listOf(CoralOrange, MintGreen, TagBlue, TagPurple, WarningOrange, ErrorRed)
    var avatarColorIndex by remember { mutableStateOf(0) }

    // ── 地址管理 ──
    var addresses by remember {
        mutableStateOf(
            listOf(
                Address("a1", "用户", "130****0000", "北京市朝阳区某某街 100 号")
            )
        )
    }
    var showAddressDialog by remember { mutableStateOf(false) }

    if (showEditDialog) {
        EditProfileDialog(
            currentName = userName,
            currentColorIndex = avatarColorIndex,
            colors = avatarColors,
            onDismiss = { showEditDialog = false },
            onSave = { name, colorIdx ->
                userName = name.ifBlank { "美食爱好者" }
                avatarColorIndex = colorIdx
                showEditDialog = false
            }
        )
    }

    if (showAddressDialog) {
        AddressManagementDialog(
            addresses = addresses,
            onDismiss = { showAddressDialog = false },
            onAdd = { name, phone, detail ->
                addresses = addresses + Address(
                    id = "a${System.currentTimeMillis()}",
                    name = name, phone = phone, detail = detail
                )
            },
            onDelete = { addr ->
                addresses = addresses.filter { it.id != addr.id }
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundWhite)
            .verticalScroll(rememberScrollState())
    ) {
        // ── 用户头部 ──
        ProfileHeader(
            userName = userName,
            avatarColor = avatarColors[avatarColorIndex],
            onEditClick = { showEditDialog = true }
        )

        Spacer(Modifier.height(16.dp))

        // ── 快捷入口 ──
        QuickActions(
            onOrderListClick = onOrderListClick,
            onAddressClick = { showAddressDialog = true }
        )

        Spacer(Modifier.height(16.dp))

        // ── 功能列表 ──
        SettingsSection(onLogout = onLogout)

        Spacer(Modifier.height(24.dp))
    }
}

@Composable
private fun ProfileHeader(
    userName: String,
    avatarColor: Color,
    onEditClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .background(
                Brush.verticalGradient(
                    colors = listOf(CoralOrange, CoralOrangeLight, BackgroundWhite)
                )
            )
            .statusBarsPadding()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 32.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 头像 — 可点击
            Surface(
                shape = CircleShape,
                color = Color.White,
                modifier = Modifier.size(72.dp),
                shadowElevation = 4.dp
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(avatarColor.copy(alpha = 0.15f))
                ) {
                    Icon(
                        Icons.Filled.Person,
                        contentDescription = null,
                        tint = avatarColor,
                        modifier = Modifier.size(40.dp)
                    )
                }
            }

            Spacer(Modifier.width(16.dp))

            Column {
                Text(
                    userName,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    "130****0000",
                    fontSize = 14.sp,
                    color = Color.White.copy(alpha = 0.85f)
                )
                Spacer(Modifier.height(6.dp))
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color.White.copy(alpha = 0.25f),
                    modifier = Modifier.clickable { onEditClick() }
                ) {
                    Text(
                        "编辑资料",
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                        fontSize = 12.sp,
                        color = Color.White
                    )
                }
            }
        }
    }
}

@Composable
private fun EditProfileDialog(
    currentName: String,
    currentColorIndex: Int,
    colors: List<Color>,
    onDismiss: () -> Unit,
    onSave: (name: String, colorIndex: Int) -> Unit
) {
    var name by remember { mutableStateOf(currentName) }
    var selectedColorIndex by remember { mutableStateOf(currentColorIndex) }

    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(24.dp),
        containerColor = SurfaceWhite,
        title = {
            Text(
                "编辑资料",
                fontWeight = FontWeight.Bold,
                color = TextPrimary,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        },
        text = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                // ── 头像预览 ──
                Surface(
                    shape = CircleShape,
                    color = Color.White,
                    modifier = Modifier.size(80.dp),
                    shadowElevation = 4.dp
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(colors[selectedColorIndex].copy(alpha = 0.15f))
                    ) {
                        Icon(
                            Icons.Filled.Person,
                            contentDescription = null,
                            tint = colors[selectedColorIndex],
                            modifier = Modifier.size(44.dp)
                        )
                    }
                }

                Spacer(Modifier.height(16.dp))

                // ── 颜色选择 ──
                Text(
                    "头像颜色",
                    fontSize = 13.sp,
                    color = TextSecondary
                )
                Spacer(Modifier.height(8.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    colors.forEachIndexed { index, color ->
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(color.copy(alpha = 0.2f))
                                .then(
                                    if (index == selectedColorIndex)
                                        Modifier.border(3.dp, color, CircleShape)
                                    else
                                        Modifier.border(2.dp, Color.Transparent, CircleShape)
                                )
                                .clickable { selectedColorIndex = index },
                            contentAlignment = Alignment.Center
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(16.dp)
                                    .clip(CircleShape)
                                    .background(color)
                            )
                        }
                    }
                }

                Spacer(Modifier.height(16.dp))

                // ── 名称输入 ──
                OutlinedTextField(
                    value = name,
                    onValueChange = { if (it.length <= 12) name = it },
                    label = { Text("昵称") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = CoralOrange,
                        cursorColor = CoralOrange,
                        focusedLabelColor = CoralOrange
                    )
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onSave(name, selectedColorIndex) },
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = CoralOrange),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("保存", color = Color.White, fontWeight = FontWeight.SemiBold)
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("取消", color = TextSecondary)
            }
        }
    )
}

@Composable
private fun QuickActions(onOrderListClick: () -> Unit, onAddressClick: () -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(16.dp),
        color = SurfaceWhite,
        shadowElevation = 2.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            QuickActionItem(
                icon = Icons.AutoMirrored.Filled.ReceiptLong,
                label = "我的订单",
                color = CoralOrange,
                onClick = onOrderListClick
            )
            QuickActionItem(
                icon = Icons.Filled.LocationOn,
                label = "收货地址",
                color = TagBlue,
                onClick = onAddressClick
            )
            QuickActionItem(
                icon = Icons.Filled.Favorite,
                label = "我的收藏",
                color = ErrorRed,
                onClick = { /* TODO */ }
            )
            QuickActionItem(
                icon = Icons.Filled.Wallet,
                label = "优惠券",
                color = WarningOrange,
                onClick = { /* TODO */ }
            )
        }
    }
}

@Composable
private fun QuickActionItem(
    icon: ImageVector,
    label: String,
    color: Color,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        IconButton(onClick = onClick, modifier = Modifier.size(52.dp)) {
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = color.copy(alpha = 0.1f),
                modifier = Modifier.size(48.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        icon,
                        contentDescription = label,
                        tint = color,
                        modifier = Modifier.size(26.dp)
                    )
                }
            }
        }
        Text(
            label,
            fontSize = 12.sp,
            color = TextPrimary,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun SettingsSection(onLogout: () -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(16.dp),
        color = SurfaceWhite,
        shadowElevation = 2.dp
    ) {
        Column(modifier = Modifier.padding(vertical = 8.dp)) {
            SettingsItem(icon = Icons.Filled.Security, label = "账户安全", color = SuccessGreen)
            HorizontalDivider(color = DividerLight, modifier = Modifier.padding(horizontal = 56.dp))
            SettingsItem(icon = Icons.Filled.Notifications, label = "消息通知", color = WarningOrange)
            HorizontalDivider(color = DividerLight, modifier = Modifier.padding(horizontal = 56.dp))
            SettingsItem(icon = Icons.Filled.Language, label = "语言切换", color = TagPurple)
            HorizontalDivider(color = DividerLight, modifier = Modifier.padding(horizontal = 56.dp))
            SettingsItem(icon = Icons.Filled.HelpOutline, label = "帮助与反馈", color = TagBlue)
            HorizontalDivider(color = DividerLight, modifier = Modifier.padding(horizontal = 56.dp))
            SettingsItem(icon = Icons.Filled.Info, label = "关于 BiteLite", color = TextSecondary)
            HorizontalDivider(color = DividerLight, modifier = Modifier.padding(horizontal = 56.dp))
            SettingsItem(icon = Icons.Filled.Logout, label = "退出登录", color = ErrorRed, onClick = onLogout)
        }
    }
}

@Composable
private fun SettingsItem(icon: ImageVector, label: String, color: Color, onClick: (() -> Unit)? = null) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            shape = RoundedCornerShape(10.dp),
            color = color.copy(alpha = 0.1f),
            modifier = Modifier.size(36.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(20.dp))
            }
        }
        Spacer(Modifier.width(14.dp))
        Text(label, fontSize = 14.sp, color = TextPrimary, modifier = Modifier.weight(1f))
        Icon(
            Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = null,
            tint = TextHint,
            modifier = Modifier.size(20.dp)
        )
    }
}

// ─────────── 地址管理弹窗 ───────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddressManagementDialog(
    addresses: List<Address>,
    onDismiss: () -> Unit,
    onAdd: (name: String, phone: String, detail: String) -> Unit,
    onDelete: (Address) -> Unit
) {
    var showAddForm by remember { mutableStateOf(false) }
    var newName by remember { mutableStateOf("") }
    var newPhone by remember { mutableStateOf("") }
    var newDetail by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(24.dp),
        containerColor = SurfaceWhite,
        modifier = Modifier.fillMaxWidth(),
        title = {
            Text("收货地址", fontWeight = FontWeight.Bold, color = TextPrimary,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                modifier = Modifier.fillMaxWidth())
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                if (addresses.isEmpty()) {
                    Text("暂无地址，请添加", color = TextHint, fontSize = 14.sp,
                        modifier = Modifier.padding(vertical = 16.dp))
                } else {
                    addresses.forEach { addr ->
                        Surface(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                            shape = RoundedCornerShape(12.dp),
                            color = BackgroundWhite
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = CoralOrange.copy(alpha = 0.1f),
                                    modifier = Modifier.size(36.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(Icons.Filled.LocationOn, null, tint = CoralOrange, modifier = Modifier.size(20.dp))
                                    }
                                }
                                Spacer(Modifier.width(10.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text("${addr.name}  ${addr.phone}", fontSize = 13.sp, fontWeight = FontWeight.Medium, color = TextPrimary)
                                    Text(addr.detail, fontSize = 12.sp, color = TextSecondary, maxLines = 1)
                                }
                                IconButton(onClick = { onDelete(addr) }, modifier = Modifier.size(32.dp)) {
                                    Icon(Icons.Filled.Delete, "删除", tint = TextHint, modifier = Modifier.size(16.dp))
                                }
                            }
                        }
                    }
                }

                HorizontalDivider(color = DividerLight, modifier = Modifier.padding(vertical = 8.dp))

                // 添加新地址
                if (showAddForm) {
                    OutlinedTextField(
                        value = newName, onValueChange = { newName = it },
                        label = { Text("收货人") }, singleLine = true,
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = CoralOrange, cursorColor = CoralOrange)
                    )
                    OutlinedTextField(
                        value = newPhone, onValueChange = { newPhone = it },
                        label = { Text("手机号") }, singleLine = true,
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = CoralOrange, cursorColor = CoralOrange)
                    )
                    OutlinedTextField(
                        value = newDetail, onValueChange = { newDetail = it },
                        label = { Text("详细地址") }, maxLines = 2,
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = CoralOrange, cursorColor = CoralOrange)
                    )
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                        TextButton(onClick = { showAddForm = false }) { Text("取消", color = TextSecondary) }
                        Button(
                            onClick = {
                                if (newName.isNotBlank() && newPhone.isNotBlank() && newDetail.isNotBlank()) {
                                    onAdd(newName.trim(), newPhone.trim(), newDetail.trim())
                                    newName = ""; newPhone = ""; newDetail = ""
                                    showAddForm = false
                                }
                            },
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = CoralOrange)
                        ) { Text("保存", color = Color.White) }
                    }
                } else {
                    OutlinedButton(
                        onClick = { showAddForm = true },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Filled.Add, null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(6.dp))
                        Text("新增地址", color = CoralOrange)
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss, modifier = Modifier.fillMaxWidth()) {
                Text("完成", color = TextSecondary)
            }
        }
    )
}
