package com.example.appauthbase.presentation

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.appauthbase.data.datasource.OAuthDataSource
import com.example.appauthbase.data.repository.OAuthRepositoryImpl
import com.example.appauthbase.domain.usecase.CheckAuthStateUseCase
import com.example.appauthbase.domain.usecase.GetLoginIntentUseCase
import com.example.appauthbase.domain.usecase.HandleAuthResponseUseCase
import com.example.appauthbase.domain.usecase.LogoutUseCase

class AuthViewModelFactory(
    private val context: Context
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {

        if (modelClass.isAssignableFrom(AuthViewModel::class.java)) {

            val datasource = OAuthDataSource(context)
            val repository = OAuthRepositoryImpl(datasource)

            val checkAuth      = CheckAuthStateUseCase(repository)
            val getLoginIntent = GetLoginIntentUseCase(repository)
            val handleLogin    = HandleAuthResponseUseCase(repository)
            val logout         = LogoutUseCase(repository)

            @Suppress("UNCHECKED_CAST")
            return AuthViewModel(
                check          = checkAuth,
                getLoginIntent = getLoginIntent,
                login          = handleLogin,
                logoutUseCase  = logout
            ) as T
        }

        throw IllegalArgumentException("Unknown ViewModel: ${modelClass.name}")
    }
}