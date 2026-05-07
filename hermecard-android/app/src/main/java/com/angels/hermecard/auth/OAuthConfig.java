package com.angels.hermecard.auth;

import android.net.Uri;

/**
 * Configurações do servidor OAuth2 (hermecard-auth-api-server).
 *
 * IMPORTANTE: Em produção, substitua "10.0.2.2" pelo IP real do servidor.
 * "10.0.2.2" é o alias do emulador Android para o localhost da máquina host.
 *
 * Portas:
 *   Auth Server  → porta 8080  (hermecard-auth-api-server)
 *   Resource API → porta 8081  (hermecard-resource-api)
 */
public class OAuthConfig {

    // ─── Auth Server (porta 8080) ──────────────────────────────────────────────
    public static final String AUTH_SERVER_BASE = "http://10.0.2.2:8080";

    // Endpoints padrão do Spring Authorization Server (/.well-known/openid-configuration)
    public static final Uri AUTHORIZATION_ENDPOINT =
            Uri.parse(AUTH_SERVER_BASE + "/oauth2/authorize");

    public static final Uri TOKEN_ENDPOINT =
            Uri.parse(AUTH_SERVER_BASE + "/oauth2/token");

    public static final Uri END_SESSION_ENDPOINT =
            Uri.parse(AUTH_SERVER_BASE + "/connect/logout");

    public static final Uri USERINFO_ENDPOINT =
            Uri.parse(AUTH_SERVER_BASE + "/userinfo");

    // ─── Client credentials ────────────────────────────────────────────────────
    // Deve corresponder ao client registrado em DataInitializer.java
    public static final String CLIENT_ID     = "meu-client";
    public static final String CLIENT_SECRET = "meu-secret";

    // ─── Redirect URI ──────────────────────────────────────────────────────────
    // Scheme customizado declarado no AndroidManifest.xml
    // Precisa ser registrado também no DataInitializer (já está como redirectUri)
    public static final Uri REDIRECT_URI =
            Uri.parse("com.angels.hermecard://callback");

    // ─── Scopes ────────────────────────────────────────────────────────────────
    public static final String SCOPE = "openid profile read write";

    // ─── Resource API (porta 8081) ─────────────────────────────────────────────
    public static final String RESOURCE_API_BASE = "http://10.0.2.2:8081";
}
