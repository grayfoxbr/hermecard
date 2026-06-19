package com.example.appauthbase.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*

import androidx.compose.runtime.collectAsState

import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp

import com.example.appauthbase.presentation.RegisterViewModel

@Composable
fun RegisterScreen(

    registerViewModel: RegisterViewModel,

    onBack: () -> Unit

) {

    val uiState by
    registerViewModel
        .ui
        .collectAsState()

    var email by remember {
        mutableStateOf("")
    }

    var password by remember {
        mutableStateOf("")
    }

    var confirmPassword by remember {
        mutableStateOf("")
    }

    Column(

        modifier =
            Modifier
                .fillMaxSize()
                .padding(24.dp),

        horizontalAlignment =
            Alignment.CenterHorizontally,

        verticalArrangement =
            Arrangement.Center

    ) {

        Text(
            text = "Create Account",

            style =
                MaterialTheme
                    .typography
                    .headlineMedium
        )

        Spacer(
            Modifier.height(24.dp)
        )

        OutlinedTextField(

            value = email,

            onValueChange = {
                email = it
            },

            enabled =
                !uiState.loading,

            label = {
                Text("Email")
            }
        )

        Spacer(
            Modifier.height(12.dp)
        )

        OutlinedTextField(

            value = password,

            onValueChange = {
                password = it
            },

            enabled =
                !uiState.loading,

            label = {
                Text("Password")
            },

            visualTransformation =
                PasswordVisualTransformation(),

            keyboardOptions =
                KeyboardOptions(
                    keyboardType =
                        KeyboardType.Password
                )
        )

        Spacer(
            Modifier.height(12.dp)
        )

        OutlinedTextField(

            value =
                confirmPassword,

            onValueChange = {
                confirmPassword =
                    it
            },

            enabled =
                !uiState.loading,

            label = {
                Text("Confirm password")
            },

            visualTransformation =
                PasswordVisualTransformation()
        )

        Spacer(
            Modifier.height(24.dp)
        )

        if (uiState.loading) {

            CircularProgressIndicator()

        } else {

            Button(

                onClick = {

                    registerViewModel.register(

                        email =
                            email,

                        password =
                            password,

                        confirm =
                            confirmPassword
                    )
                }

            ) {

                Text(
                    "Register"
                )
            }
        }

        uiState.error?.let {

            Spacer(
                Modifier.height(16.dp)
            )

            Text(

                text = it,

                color =
                    MaterialTheme
                        .colorScheme
                        .error
            )
        }

        if (uiState.success) {

            Spacer(
                Modifier.height(16.dp)
            )

            Text(
                "Account created successfully"
            )
        }

        Spacer(
            Modifier.height(16.dp)
        )

        TextButton(
            onClick = onBack
        ) {

            Text(
                "Back to login"
            )
        }
    }
}