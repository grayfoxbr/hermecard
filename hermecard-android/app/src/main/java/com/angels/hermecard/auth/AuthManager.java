package com.angels.hermecard.auth;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import net.openid.appauth.AppAuthConfiguration;
import net.openid.appauth.AuthorizationException;
import net.openid.appauth.AuthorizationRequest;
import net.openid.appauth.AuthorizationResponse;
import net.openid.appauth.AuthorizationService;
import net.openid.appauth.AuthorizationServiceConfiguration;
import net.openid.appauth.ClientSecretBasic;
import net.openid.appauth.ResponseTypeValues;
import net.openid.appauth.TokenRequest;
import net.openid.appauth.TokenResponse;

/**
 * Gerencia todo o fluxo OAuth2 Authorization Code usando a biblioteca AppAuth.
 *
 * Fluxo:
 *  1. buildAuthorizationRequest() → gera a URL de autorização
 *  2. O usuário faz login no navegador (Custom Tab)
 *  3. O servidor redireciona para com.angels.hermecard://callback?code=...
 *  4. handleAuthorizationResponse() → troca o código por tokens
 *  5. Os tokens são salvos via TokenManager
 */
public class AuthManager {

    private static final String TAG = "AuthManager";

    private final AuthorizationService authService;
    private final TokenManager tokenManager;

    public interface TokenCallback {
        void onSuccess(String accessToken, String refreshToken, String idToken);
        void onError(String errorMessage);
    }

    public AuthManager(Context context) {
        this.tokenManager = new TokenManager(context);
        this.authService = new AuthorizationService(context);
    }

    // ─── 1. Construir a requisição de autorização ──────────────────────────────

    /**
     * Cria o Intent que abre o navegador para o usuário fazer login.
     * Chame startActivityForResult(intent, REQUEST_CODE) na Activity.
     */
    public Intent buildAuthorizationIntent() {
        AuthorizationServiceConfiguration config = new AuthorizationServiceConfiguration(
                OAuthConfig.AUTHORIZATION_ENDPOINT,
                OAuthConfig.TOKEN_ENDPOINT
        );

        AuthorizationRequest authRequest = new AuthorizationRequest.Builder(
                config,
                OAuthConfig.CLIENT_ID,
                ResponseTypeValues.CODE,
                OAuthConfig.REDIRECT_URI
        )
                .setScope(OAuthConfig.SCOPE)
                .build();

        return authService.getAuthorizationRequestIntent(authRequest);
    }

    // ─── 2. Processar resposta e trocar código por tokens ─────────────────────

    /**
     * Chame este método no onActivityResult() da LoginActivity.
     * Extrai o código de autorização e faz a troca por access_token.
     */
    public void handleAuthorizationResponse(Intent data, TokenCallback callback) {
        AuthorizationResponse response = AuthorizationResponse.fromIntent(data);
        AuthorizationException error   = AuthorizationException.fromIntent(data);

        if (error != null) {
            Log.e(TAG, "Erro na autorização: " + error.getMessage());
            callback.onError("Falha na autorização: " + error.getMessage());
            return;
        }

        if (response == null) {
            callback.onError("Resposta de autorização vazia");
            return;
        }

        Log.d(TAG, "Código de autorização recebido. Trocando por tokens...");

        // Troca o código pelo token usando client_secret_basic
        TokenRequest tokenRequest = response.createTokenExchangeRequest();

        authService.performTokenRequest(
                tokenRequest,
                new ClientSecretBasic(OAuthConfig.CLIENT_SECRET),
                (tokenResponse, tokenException) -> {
                    if (tokenException != null) {
                        Log.e(TAG, "Erro na troca de token: " + tokenException.getMessage());
                        callback.onError("Falha ao obter token: " + tokenException.getMessage());
                        return;
                    }

                    if (tokenResponse == null) {
                        callback.onError("Resposta de token vazia");
                        return;
                    }

                    processTokenResponse(tokenResponse, callback);
                }
        );
    }

    // ─── 3. Refresh token ──────────────────────────────────────────────────────

    /**
     * Renova o access_token usando o refresh_token salvo.
     */
    public void refreshAccessToken(TokenCallback callback) {
        String refreshToken = tokenManager.getRefreshToken();
        if (refreshToken == null) {
            callback.onError("Nenhum refresh token disponível. Faça login novamente.");
            return;
        }

        AuthorizationServiceConfiguration config = new AuthorizationServiceConfiguration(
                OAuthConfig.AUTHORIZATION_ENDPOINT,
                OAuthConfig.TOKEN_ENDPOINT
        );

        TokenRequest refreshRequest = new TokenRequest.Builder(config, OAuthConfig.CLIENT_ID)
                .setGrantType("refresh_token")
                .setRefreshToken(refreshToken)
                .build();

        authService.performTokenRequest(
                refreshRequest,
                new ClientSecretBasic(OAuthConfig.CLIENT_SECRET),
                (tokenResponse, tokenException) -> {
                    if (tokenException != null) {
                        // Refresh token expirado — precisa de novo login
                        tokenManager.clearTokens();
                        callback.onError("Sessão expirada. Faça login novamente.");
                        return;
                    }
                    if (tokenResponse != null) {
                        processTokenResponse(tokenResponse, callback);
                    }
                }
        );
    }

    // ─── Helpers ───────────────────────────────────────────────────────────────

    private void processTokenResponse(TokenResponse tokenResponse, TokenCallback callback) {
        String accessToken  = tokenResponse.accessToken;
        String refreshToken = tokenResponse.refreshToken;
        String idToken      = tokenResponse.idToken;
        long expiresAt      = tokenResponse.accessTokenExpirationTime != null
                ? tokenResponse.accessTokenExpirationTime
                : 0L;

        Log.d(TAG, "Tokens recebidos com sucesso!");

        tokenManager.saveTokens(accessToken, refreshToken, idToken, "Bearer", expiresAt);
        callback.onSuccess(accessToken, refreshToken, idToken);
    }

    public TokenManager getTokenManager() {
        return tokenManager;
    }

    public void dispose() {
        authService.dispose();
    }
}
