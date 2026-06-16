package com.example.appauthbase.auth

import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class AuthStateData(
    val isLoggedIn: Boolean = false,
    val accessToken: String? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

class AuthViewModel(private val authManager: AuthManager) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthStateData())
    val uiState: StateFlow<AuthStateData> = _uiState.asStateFlow()

    init {
        checkAuthState()
    }

    private fun checkAuthState() {
        if (authManager.isAuthorized()) {
            _uiState.value = _uiState.value.copy(
                isLoggedIn = true,
                accessToken = authManager.getAccessToken()
            )
        } else {
            _uiState.value = _uiState.value.copy(
                isLoggedIn = false,
                accessToken = null
            )
        }
    }

    fun handleAuthIntent(intent: Intent) {
        _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
        authManager.handleAuthorizationResponse(intent) { success, exception ->
            if (success) {
                checkAuthState()
            } else {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = exception?.message ?: "Authentication failed"
                )
            }
            _uiState.value = _uiState.value.copy(isLoading = false)
        }
    }

    fun logout() {
        authManager.logout()
        checkAuthState()
    }

    // Call this if you need to fetch something using a fresh token
    fun performActionWithFreshTokens(action: (String?) -> Unit) {
        authManager.performActionWithFreshTokens { accessToken, _, ex ->
            if (ex != null) {
                _uiState.value = _uiState.value.copy(
                    isLoggedIn = false,
                    accessToken = null,
                    errorMessage = "Session expired"
                )
            }
            action(accessToken)
        }
    }
}

class AuthViewModelFactory(private val authManager: AuthManager) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AuthViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AuthViewModel(authManager) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
