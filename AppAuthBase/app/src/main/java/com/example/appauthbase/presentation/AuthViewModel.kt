package com.example.appauthbase.presentation

import android.content.Intent
import androidx.lifecycle.ViewModel
import com.example.appauthbase.domain.usecase.CheckAuthStateUseCase
import com.example.appauthbase.domain.usecase.HandleAuthResponseUseCase
import com.example.appauthbase.domain.usecase.LogoutUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class AuthViewModel(

    private val check:
    CheckAuthStateUseCase,

    private val login:
    HandleAuthResponseUseCase,

    private val logoutUseCase:
    LogoutUseCase

) : ViewModel() {

    private val _ui =
        MutableStateFlow(
            AuthStateData()
        )

    val ui:
            StateFlow<AuthStateData> =
        _ui.asStateFlow()

    init {
        refresh()
    }

    private fun refresh() {

        val auth =
            check()

        _ui.value =

            AuthStateData(

                isLoggedIn =
                    auth.loggedIn,

                accessToken =
                    auth.accessToken
            )
    }

    fun handleIntent(
        intent: Intent
    ) {

        _ui.value =
            _ui.value.copy(
                isLoading = true,
                errorMessage = null
            )

        login(
            intent
        ) { success, exception ->

            if (success) {

                refresh()

            } else {

                _ui.value =

                    _ui.value.copy(

                        errorMessage =
                            exception?.message
                                ?: "Authentication failed"
                    )
            }

            _ui.value =
                _ui.value.copy(
                    isLoading = false
                )
        }
    }

    fun logout() {

        logoutUseCase()

        refresh()
    }
}
