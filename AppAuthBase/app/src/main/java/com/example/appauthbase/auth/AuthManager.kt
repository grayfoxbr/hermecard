package com.example.appauthbase.auth

import android.content.Context
import android.content.Intent
import android.net.Uri
import net.openid.appauth.AppAuthConfiguration
import net.openid.appauth.AuthState
import net.openid.appauth.AuthorizationException
import net.openid.appauth.AuthorizationRequest
import net.openid.appauth.AuthorizationResponse
import net.openid.appauth.AuthorizationService
import net.openid.appauth.AuthorizationServiceConfiguration
import net.openid.appauth.ResponseTypeValues
import net.openid.appauth.TokenResponse

class AuthManager(private val context: Context) {

    private val authPrefs = context.getSharedPreferences("auth", Context.MODE_PRIVATE)
    private var authState: AuthState = readAuthState()
    private val authService: AuthorizationService

    init {
        val appAuthConfig = AppAuthConfiguration.Builder().build()
        authService = AuthorizationService(context, appAuthConfig)
    }

    // TODO: Replace with your actual IdP data
    private val authEndpoint = Uri.parse("http://localhost:8080/oauth2/authorize")
    private val tokenEndpoint = Uri.parse("http://localhost:8080/oauth2/token")
    private val clientId = "meu-client"
    private val redirectUri = Uri.parse("com.example.appauthbase:/oauth2redirect")

    private val serviceConfig = AuthorizationServiceConfiguration(
        authEndpoint,
        tokenEndpoint
    )

    fun getAuthorizationRequestIntent(): Intent {
        val authRequestBuilder = AuthorizationRequest.Builder(
            serviceConfig,
            clientId,
            ResponseTypeValues.CODE,
            redirectUri
        )

        val authRequest = authRequestBuilder
            .setScope("openid email profile")
            .build()

        return authService.getAuthorizationRequestIntent(authRequest)
    }

    fun handleAuthorizationResponse(
        intent: Intent,
        onComplete: (Boolean, Exception?) -> Unit
    ) {
        val response = AuthorizationResponse.fromIntent(intent)
        val exception = AuthorizationException.fromIntent(intent)

        authState.update(response, exception)
        writeAuthState(authState)

        if (response != null) {
            authService.performTokenRequest(
                response.createTokenExchangeRequest()
            ) { tokenResponse: TokenResponse?, authException: AuthorizationException? ->
                authState.update(tokenResponse, authException)
                writeAuthState(authState)

                if (tokenResponse != null) {
                    onComplete(true, null)
                } else {
                    onComplete(false, authException)
                }
            }
        } else {
            onComplete(false, exception)
        }
    }

    fun performActionWithFreshTokens(action: (String?, String?, Exception?) -> Unit) {
        authState.performActionWithFreshTokens(authService) { accessToken, idToken, ex ->
            if (ex != null) {
                // If token refresh fails or we're not authorized
                writeAuthState(authState)
            }
            action(accessToken, idToken, ex)
        }
    }

    fun logout() {
        authState = AuthState()
        writeAuthState(authState)
    }

    fun isAuthorized(): Boolean {
        return authState.isAuthorized
    }

    fun getAccessToken(): String? {
        return authState.accessToken
    }

    private fun readAuthState(): AuthState {
        val stateJson = authPrefs.getString("stateJson", null)
        return if (stateJson != null) {
            try {
                AuthState.jsonDeserialize(stateJson)
            } catch (e: Exception) {
                AuthState()
            }
        } else {
            AuthState()
        }
    }

    private fun writeAuthState(state: AuthState) {
        authPrefs.edit()
            .putString("stateJson", state.jsonSerializeString())
            .apply()
    }

    fun dispose() {
        authService.dispose()
    }
}
