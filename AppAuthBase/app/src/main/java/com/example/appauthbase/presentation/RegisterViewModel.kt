package com.example.appauthbase.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.appauthbase.domain.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class RegisterUiState(
    val loading: Boolean = false,
    val success: Boolean = false,
    val error: String? = null
)

class RegisterViewModel(
    private val repository: AuthRepository
) : ViewModel() {

    private val _ui = MutableStateFlow(RegisterUiState())
    val ui: StateFlow<RegisterUiState> = _ui

    fun register(
        email: String,
        password: String,
        confirm: String
    ) {
        if (email.isBlank()) {
            _ui.value = RegisterUiState(error = "E-mail não pode ser vazio")
            return
        }

        if (password.length < 6) {
            _ui.value = RegisterUiState(error = "Senha deve ter pelo menos 6 caracteres")
            return
        }

        if (password != confirm) {
            _ui.value = RegisterUiState(error = "As senhas não coincidem")
            return
        }

        viewModelScope.launch {
            _ui.value = RegisterUiState(loading = true)

            val result = repository.register(email, password)

            _ui.value = if (result.isSuccess) {
                RegisterUiState(success = true)
            } else {
                RegisterUiState(error = result.exceptionOrNull()?.message ?: "Erro ao cadastrar")
            }
        }
    }
}