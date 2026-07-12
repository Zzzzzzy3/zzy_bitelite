package com.example.dashdine.ui.auth

import androidx.lifecycle.ViewModel
import com.example.dashdine.data.auth.AuthManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

data class AuthUiState(
    val phone: String = "",
    val password: String = "",
    val name: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val isLoggedIn: Boolean = false
)

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authManager: AuthManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState(
        isLoggedIn = authManager.isLoggedIn()
    ))
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    fun onPhoneChange(phone: String) { _uiState.value = _uiState.value.copy(phone = phone, error = null) }
    fun onPasswordChange(pw: String) { _uiState.value = _uiState.value.copy(password = pw, error = null) }
    fun onNameChange(name: String) { _uiState.value = _uiState.value.copy(name = name, error = null) }
    fun clearError() { _uiState.value = _uiState.value.copy(error = null) }
    fun getCurrentUserName(): String = authManager.getCurrentUser()?.name ?: "用户"

    fun login(onSuccess: () -> Unit) {
        val state = _uiState.value
        if (state.phone.isBlank() || state.password.isBlank()) {
            _uiState.value = state.copy(error = "请填写手机号和密码")
            return
        }
        _uiState.value = state.copy(isLoading = true, error = null)
        val result = authManager.login(state.phone.trim(), state.password)
        result.onSuccess {
            _uiState.value = _uiState.value.copy(isLoading = false, isLoggedIn = true)
            onSuccess()
        }.onFailure {
            _uiState.value = _uiState.value.copy(isLoading = false, error = it.message)
        }
    }

    fun register(onSuccess: () -> Unit) {
        val state = _uiState.value
        if (state.name.isBlank() || state.phone.isBlank() || state.password.isBlank()) {
            _uiState.value = state.copy(error = "请填写完整信息")
            return
        }
        if (state.password.length < 4) {
            _uiState.value = state.copy(error = "密码至少4位")
            return
        }
        _uiState.value = state.copy(isLoading = true, error = null)
        val result = authManager.register(state.name.trim(), state.phone.trim(), state.password)
        result.onSuccess {
            _uiState.value = _uiState.value.copy(isLoading = false, isLoggedIn = true)
            onSuccess()
        }.onFailure {
            _uiState.value = _uiState.value.copy(isLoading = false, error = it.message)
        }
    }

    fun logout(onDone: () -> Unit) {
        authManager.logout()
        _uiState.value = AuthUiState()
        onDone()
    }
}
