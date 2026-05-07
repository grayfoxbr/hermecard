package com.angels.hermecard.auth;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKey;

/**
 * Armazena e recupera tokens OAuth2 usando SharedPreferences criptografado.
 * Os tokens são persistidos entre sessões do app.
 */
public class TokenManager {

    private static final String TAG = "TokenManager";
    private static final String PREFS_FILE = "hermecard_secure_prefs";

    private static final String KEY_ACCESS_TOKEN  = "access_token";
    private static final String KEY_REFRESH_TOKEN = "refresh_token";
    private static final String KEY_ID_TOKEN      = "id_token";
    private static final String KEY_TOKEN_TYPE    = "token_type";
    private static final String KEY_EXPIRES_AT    = "expires_at";

    private final SharedPreferences prefs;

    public TokenManager(Context context) {
        SharedPreferences p;
        try {
            MasterKey masterKey = new MasterKey.Builder(context)
                    .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                    .build();

            p = EncryptedSharedPreferences.create(
                    context,
                    PREFS_FILE,
                    masterKey,
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            );
        } catch (Exception e) {
            Log.e(TAG, "Erro ao criar EncryptedSharedPreferences, usando prefs normais", e);
            p = context.getSharedPreferences(PREFS_FILE, Context.MODE_PRIVATE);
        }
        this.prefs = p;
    }

    // ─── Salvar tokens ─────────────────────────────────────────────────────────

    public void saveTokens(String accessToken, String refreshToken,
                           String idToken, String tokenType, long expiresAt) {
        prefs.edit()
                .putString(KEY_ACCESS_TOKEN, accessToken)
                .putString(KEY_REFRESH_TOKEN, refreshToken)
                .putString(KEY_ID_TOKEN, idToken)
                .putString(KEY_TOKEN_TYPE, tokenType != null ? tokenType : "Bearer")
                .putLong(KEY_EXPIRES_AT, expiresAt)
                .apply();
    }

    // ─── Recuperar tokens ──────────────────────────────────────────────────────

    public String getAccessToken() {
        return prefs.getString(KEY_ACCESS_TOKEN, null);
    }

    public String getRefreshToken() {
        return prefs.getString(KEY_REFRESH_TOKEN, null);
    }

    public String getIdToken() {
        return prefs.getString(KEY_ID_TOKEN, null);
    }

    public long getExpiresAt() {
        return prefs.getLong(KEY_EXPIRES_AT, 0L);
    }

    // ─── Estado ────────────────────────────────────────────────────────────────

    public boolean hasValidToken() {
        String token = getAccessToken();
        if (token == null || token.isEmpty()) return false;

        long expiresAt = getExpiresAt();
        if (expiresAt == 0L) return true; // sem info de expiração, assume válido

        // Considera expirado 30 segundos antes para evitar race conditions
        return System.currentTimeMillis() < (expiresAt - 30_000);
    }

    public boolean isLoggedIn() {
        return getAccessToken() != null;
    }

    // ─── Limpar (logout) ───────────────────────────────────────────────────────

    public void clearTokens() {
        prefs.edit().clear().apply();
    }
}
