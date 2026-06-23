package com.example.appauthbase.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.example.appauthbase.presentation.AuthViewModel
import com.example.appauthbase.presentation.RegisterViewModel
import com.example.appauthbase.ui.HomeScreen
import com.example.appauthbase.ui.LoginScreen
import com.example.appauthbase.ui.RegisterScreen

@Composable
fun MainNavigation(

  authViewModel: AuthViewModel,

  registerViewModel: RegisterViewModel,

  onLoginClick: () -> Unit

) {

  val uiState by
  authViewModel
    .uiState
    .collectAsState()

  val backStack =
    rememberNavBackStack(Login)

  LaunchedEffect(
    uiState.isLoggedIn
  ) {

    if (
      uiState.isLoggedIn &&
      backStack.lastOrNull() != Home
    ) {

      backStack.clear()

      backStack.add(
        Home
      )
    }

    if (
      !uiState.isLoggedIn &&
      backStack.lastOrNull() == Home
    ) {

      backStack.clear()

      backStack.add(
        Login
      )
    }
  }

  NavDisplay(

    backStack =
      backStack,

    onBack = {

      backStack
        .removeLastOrNull()
    },

    entryProvider =

      entryProvider {

        entry<Login> {

          LoginScreen(

            authViewModel =
              authViewModel,

            onLoginClick =
              onLoginClick,

            onRegisterClick = {

              backStack
                .add(
                  Register
                )
            }
          )
        }

        entry<Register> {

          RegisterScreen(

            registerViewModel =
              registerViewModel,

            onBack = {

              backStack
                .removeLastOrNull()
            }
          )
        }

        entry<Home> {

          HomeScreen(

            accessToken =
              uiState
                .accessToken,

            onLogout = {

              authViewModel
                .logout()
            }
          )
        }
      }
  )
}