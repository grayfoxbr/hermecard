package com.example.appauthbase.presentation

import android.content.Intent
import androidx.lifecycle.ViewModel
import com.example.appauthbase.domain.usecase.CheckAuthStateUseCase
import com.example.appauthbase.domain.usecase.GetLoginIntentUseCase
import com.example.appauthbase.domain.usecase.HandleAuthResponseUseCase
import com.example.appauthbase.domain.usecase.LogoutUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class AuthViewModel(

    private val check: CheckAuthStateUseCase,

    private val getLoginIntent: GetLoginIntentUseCase,

    private val login: HandleAuthResponseUseCase,

    private val logoutUseCase: LogoutUseCase

) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthStateData())

    /** Exposto como `uiState` — consumido por LoginScreen */
    val uiState: StateFlow<AuthStateData> = _uiState.asStateFlow()

    init {
        refresh()
    }

    private fun refresh() {
        val auth = check()
        _uiState.value = AuthStateData(
            isLoggedIn = auth.loggedIn,
            accessToken = auth.accessToken
        )
    }

    fun loginIntent(): Intent = getLoginIntent()

    fun handleIntent(intent: Intent) {
        _uiState.value = _uiState.value.copy(
            isLoading = true,
            errorMessage = null
        )

        login(intent) { success, exception ->
            if (success) {
                refresh()
            } else {
                _uiState.value = _uiState.value.copy(
                    errorMessage = exception?.message ?: "Falha na autenticação"
                )
            }
            _uiState.value = _uiState.value.copy(isLoading = false)
        }
    }

    fun logout() {
        logoutUseCase()
        refresh()
    }
}