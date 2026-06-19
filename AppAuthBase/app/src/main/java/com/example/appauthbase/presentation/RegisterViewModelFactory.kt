package com.example.appauthbase.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

import com.example.appauthbase.data.repository.AuthRepositoryImpl

class RegisterViewModelFactory :

    ViewModelProvider.Factory {

    override fun <T : ViewModel> create(

        modelClass:
        Class<T>

    ): T {

        if (

            modelClass.isAssignableFrom(

                RegisterViewModel::class.java
            )

        ) {

            val repository =

                AuthRepositoryImpl(

                    AuthApiProvider
                        .authApi
                )

            @Suppress(
                "UNCHECKED_CAST"
            )

            return RegisterViewModel(

                repository

            ) as T
        }

        throw IllegalArgumentException(
            "Unknown ViewModel"
        )
    }
}