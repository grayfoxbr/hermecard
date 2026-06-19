package com.example.appauthbase.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.appauthbase.presentation.AuthViewModel

@Composable
fun LoginScreen(
    authViewModel: AuthViewModel,
    onLoginClick: () -> Unit,
    onRegisterClick: () -> Unit
) {
    val uiState by authViewModel.uiState.collectAsState()

    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .padding(24.dp),

        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        if (uiState.isLoading) {

            CircularProgressIndicator()

            Spacer(Modifier.height(16.dp))

            Text("Authenticating...")

        } else {

            if (uiState.isLoggedIn) {

                Text(
                    "Welcome! You are authenticated.",
                    style = MaterialTheme.typography.headlineSmall
                )

                Spacer(Modifier.height(16.dp))

                Button(
                    onClick = {
                        authViewModel.logout()
                    }
                ) {
                    Text("Logout")
                }

            } else {

                Text(
                    "AppAuth Demo",
                    style =
                        MaterialTheme.typography.headlineMedium
                )

                Spacer(Modifier.height(24.dp))

                Button(
                    onClick = onLoginClick
                ) {
                    Text("Login")
                }

                Spacer(
                    Modifier.height(8.dp)
                )

                TextButton(
                    onClick = onRegisterClick
                ) {
                    Text("Create account")
                }
            }
        }
    }
}