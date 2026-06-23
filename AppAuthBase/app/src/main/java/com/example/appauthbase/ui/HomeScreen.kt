package com.example.appauthbase.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(

    accessToken: String?,

    onLogout: () -> Unit

) {

    Scaffold(

        topBar = {

            TopAppBar(

                title = {
                    Text("Home")
                },

                colors =
                    TopAppBarDefaults
                        .topAppBarColors(
                            containerColor =
                                MaterialTheme
                                    .colorScheme
                                    .primaryContainer
                        ),

                actions = {

                    TextButton(
                        onClick = onLogout
                    ) {

                        Text(
                            "Sair"
                        )
                    }
                }
            )
        }

    ) { padding ->

        Column(

            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(24.dp),

            verticalArrangement =
                Arrangement.Center,

            horizontalAlignment =
                Alignment.CenterHorizontally

        ) {

            Text(
                text =
                    "Usuário autenticado",
                style =
                    MaterialTheme
                        .typography
                        .headlineMedium
            )

            Text(
                text =
                    accessToken
                        ?: "Token indisponível"
            )
        }
    }
}