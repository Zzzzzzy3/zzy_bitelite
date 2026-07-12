package com.example.dashdine.ui.auth

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.dashdine.ui.theme.*

@Composable
fun RegisterScreen(
    onRegisterSuccess: () -> Unit,
    onGoLogin: () -> Unit,
    viewModel: AuthViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var passwordVisible by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundWhite)
            .imePadding()
    ) {
        // ── 顶部 ──
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = SurfaceWhite,
            shadowElevation = 1.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 8.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onGoLogin) {
                    Icon(Icons.Filled.ArrowBack, null, tint = TextPrimary)
                }
                Text(
                    "创建账户",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            }
        }

        Spacer(Modifier.height(24.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp)
        ) {
            Text(
                "加入 BiteLite 🎉",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Spacer(Modifier.height(4.dp))
            Text(
                "注册即可享受智能美食推荐",
                fontSize = 14.sp,
                color = TextSecondary
            )

            Spacer(Modifier.height(24.dp))

            // 错误提示
            AnimatedVisibility(visible = uiState.error != null) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = ErrorRed.copy(alpha = 0.08f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Filled.Warning, null, tint = ErrorRed, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(8.dp))
                        Text(uiState.error ?: "", color = ErrorRed, fontSize = 13.sp)
                    }
                }
                Spacer(Modifier.height(12.dp))
            }

            // 昵称
            OutlinedTextField(
                value = uiState.name,
                onValueChange = viewModel::onNameChange,
                label = { Text("昵称") },
                leadingIcon = { Icon(Icons.Filled.Person, null, tint = TextHint) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = CoralOrange, cursorColor = CoralOrange, focusedLabelColor = CoralOrange
                )
            )

            Spacer(Modifier.height(14.dp))

            // 手机号
            OutlinedTextField(
                value = uiState.phone,
                onValueChange = viewModel::onPhoneChange,
                label = { Text("手机号") },
                leadingIcon = { Icon(Icons.Filled.Phone, null, tint = TextHint) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Phone, imeAction = ImeAction.Next
                ),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = CoralOrange, cursorColor = CoralOrange, focusedLabelColor = CoralOrange
                )
            )

            Spacer(Modifier.height(14.dp))

            // 密码
            OutlinedTextField(
                value = uiState.password,
                onValueChange = viewModel::onPasswordChange,
                label = { Text("密码（至少4位）") },
                leadingIcon = { Icon(Icons.Filled.Lock, null, tint = TextHint) },
                trailingIcon = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            if (passwordVisible) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                            null, tint = TextHint
                        )
                    }
                },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = CoralOrange, cursorColor = CoralOrange, focusedLabelColor = CoralOrange
                )
            )

            Spacer(Modifier.height(24.dp))

            // 注册按钮
            Button(
                onClick = { viewModel.register(onRegisterSuccess) },
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = RoundedCornerShape(14.dp),
                enabled = !uiState.isLoading,
                colors = ButtonDefaults.buttonColors(containerColor = CoralOrange)
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(22.dp), strokeWidth = 2.dp, color = Color.White
                    )
                } else {
                    Text("注 册", fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = Color.White)
                }
            }

            Spacer(Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("已有账户？", fontSize = 13.sp, color = TextSecondary)
                TextButton(onClick = onGoLogin) {
                    Text("返回登录", color = CoralOrange, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                }
            }
        }
    }
}
