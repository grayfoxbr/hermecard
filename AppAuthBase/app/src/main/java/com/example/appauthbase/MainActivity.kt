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
import com.example.appauthbase.auth.AuthManager
import com.example.appauthbase.auth.AuthViewModel
import com.example.appauthbase.auth.AuthViewModelFactory
import com.example.appauthbase.theme.AppAuthBaseTheme
import com.example.appauthbase.ui.LoginScreen

class MainActivity : ComponentActivity() {

    private lateinit var authManager: AuthManager
    private val authViewModel: AuthViewModel by viewModels {
        AuthViewModelFactory(authManager)
    }

    private lateinit var loginLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        authManager = AuthManager(applicationContext)

        loginLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            val data = result.data
            if (data != null) {
                authViewModel.handleAuthIntent(data)
            }
        }

        enableEdgeToEdge()
        setContent {
            AppAuthBaseTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    LoginScreen(
                        authViewModel = authViewModel,
                        onLoginClick = { startAuthFlow() }
                    )
                }
            }
        }
    }

    private fun startAuthFlow() {
        val authIntent = authManager.getAuthorizationRequestIntent()
        loginLauncher.launch(authIntent)
    }

    override fun onDestroy() {
        super.onDestroy()
        authManager.dispose()
    }
}
