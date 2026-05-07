package com.angels.hermecard.ui.login;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.angels.hermecard.auth.AuthManager;
import com.angels.hermecard.auth.TokenManager;
import com.angels.hermecard.databinding.ActivityLoginBinding;
import com.angels.hermecard.ui.home.HomeActivity;

/**
 * Tela de login que inicia o fluxo OAuth2 Authorization Code via AppAuth.
 *
 * Fluxo:
 *  1. Usuário toca "Entrar"
 *  2. App abre o navegador (Custom Tab) no endpoint de autorização do Auth Server
 *  3. Usuário faz login com usuário/senha no formulário HTML do servidor
 *  4. Servidor redireciona para com.angels.hermecard://callback?code=...
 *  5. App captura o redirect, troca o código por tokens
 *  6. Tokens salvos → navega para HomeActivity
 */
public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";

    private ActivityLoginBinding binding;
    private AuthManager authManager;

    // ActivityResultLauncher substitui o deprecado startActivityForResult
    private final ActivityResultLauncher<Intent> authLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    result -> {
                        if (result.getData() != null) {
                            handleAuthResult(result.getData());
                        } else {
                            showError("Login cancelado ou falhou.");
                            setLoading(false);
                        }
                    }
            );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        authManager = new AuthManager(this);

        // Se já tem token válido, vai direto para Home
        TokenManager tokenManager = authManager.getTokenManager();
        if (tokenManager.hasValidToken()) {
            Log.d(TAG, "Token válido encontrado. Indo para Home.");
            goToHome();
            return;
        }

        // Se tem token expirado, tenta refresh
        if (tokenManager.isLoggedIn()) {
            Log.d(TAG, "Token expirado. Tentando refresh...");
            setLoading(true);
            binding.tvStatus.setText("Renovando sessão...");
            authManager.refreshAccessToken(new AuthManager.TokenCallback() {
                @Override
                public void onSuccess(String accessToken, String refreshToken, String idToken) {
                    runOnUiThread(() -> goToHome());
                }

                @Override
                public void onError(String errorMessage) {
                    runOnUiThread(() -> {
                        setLoading(false);
                        binding.tvStatus.setText("Faça login para continuar");
                        // Não mostra erro aqui, só pede novo login
                    });
                }
            });
            return;
        }

        setupClickListeners();
    }

    private void setupClickListeners() {
        binding.btnLogin.setOnClickListener(v -> startOAuthFlow());
    }

    private void startOAuthFlow() {
        setLoading(true);
        binding.tvStatus.setText("Abrindo página de login...");

        try {
            Intent authIntent = authManager.buildAuthorizationIntent();
            authLauncher.launch(authIntent);
        } catch (Exception e) {
            Log.e(TAG, "Erro ao iniciar OAuth: " + e.getMessage(), e);
            showError("Erro ao conectar com o servidor. Verifique se o Auth Server está rodando.");
            setLoading(false);
        }
    }

    private void handleAuthResult(Intent data) {
        binding.tvStatus.setText("Autenticando...");

        authManager.handleAuthorizationResponse(data, new AuthManager.TokenCallback() {
            @Override
            public void onSuccess(String accessToken, String refreshToken, String idToken) {
                Log.d(TAG, "Login bem-sucedido!");
                runOnUiThread(() -> goToHome());
            }

            @Override
            public void onError(String errorMessage) {
                Log.e(TAG, "Erro no login: " + errorMessage);
                runOnUiThread(() -> {
                    showError(errorMessage);
                    setLoading(false);
                    binding.tvStatus.setText("Faça login para continuar");
                });
            }
        });
    }

    private void goToHome() {
        Intent intent = new Intent(this, HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void setLoading(boolean loading) {
        binding.progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
        binding.btnLogin.setEnabled(!loading);
        binding.btnLogin.setText(loading ? "Aguarde..." : "Entrar com Hermecard");
    }

    private void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        authManager.dispose();
    }
}
