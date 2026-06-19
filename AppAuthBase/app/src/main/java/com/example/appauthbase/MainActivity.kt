package com.example.appauthbase

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier

import com.example.appauthbase.navigation.MainNavigation
import com.example.appauthbase.presentation.AuthViewModel
import com.example.appauthbase.presentation.AuthViewModelFactory
import com.example.appauthbase.presentation.RegisterViewModel
import com.example.appauthbase.presentation.RegisterViewModelFactory
import com.example.appauthbase.theme.AppAuthBaseTheme

class MainActivity :
    ComponentActivity() {

    private val authViewModel:
            AuthViewModel by viewModels {

        AuthViewModelFactory(
            applicationContext
        )
    }

    private val registerViewModel:
            RegisterViewModel by viewModels {

        RegisterViewModelFactory()
    }

    private lateinit var loginLauncher:
            ActivityResultLauncher<Intent>

    override fun onCreate(
        savedInstanceState:
        Bundle?
    ) {

        super.onCreate(
            savedInstanceState
        )

        loginLauncher =
            registerForActivityResult(

                ActivityResultContracts
                    .StartActivityForResult()

            ) { result ->

                result.data
                    ?.let {

                        authViewModel
                            .handleIntent(
                                it
                            )
                    }
            }

        enableEdgeToEdge()

        setContent {

            AppAuthBaseTheme {

                Surface(

                    modifier =
                        Modifier
                            .fillMaxSize(),

                    color =
                        MaterialTheme
                            .colorScheme
                            .background

                ) {

                    MainNavigation(

                        authViewModel =
                            authViewModel,

                        registerViewModel =
                            registerViewModel,

                        onLoginClick = {

                            loginLauncher.launch(

                                authViewModel
                                    .loginIntent()
                            )
                        }
                    )
                }
            }
        }
    }
}