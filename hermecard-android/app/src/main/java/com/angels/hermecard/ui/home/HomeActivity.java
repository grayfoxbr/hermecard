package com.angels.hermecard.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.angels.hermecard.R;
import com.angels.hermecard.auth.TokenManager;
import com.angels.hermecard.databinding.ActivityHomeBinding;
import com.angels.hermecard.network.ApiClient;
import com.angels.hermecard.network.ResourceApiService;
import com.angels.hermecard.ui.login.LoginActivity;

import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Tela principal após o login.
 *
 * Exibe:
 *   - Dados do usuário extraídos do ID Token (JWT claims)
 *   - Botões para chamar endpoints da Resource API
 *   - Opção de logout
 */
public class HomeActivity extends AppCompatActivity {

    private static final String TAG = "HomeActivity";

    private ActivityHomeBinding binding;
    private TokenManager tokenManager;
    private String accessToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);

        tokenManager = new TokenManager(this);
        accessToken = tokenManager.getAccessToken();

        if (accessToken == null) {
            goToLogin();
            return;
        }

        // Decodifica o ID Token para mostrar info do usuário
        displayUserInfo();

        // Configura os botões de chamada à Resource API
        setupButtons();
    }

    // ─── Exibir info do usuário via ID Token ───────────────────────────────────

    private void displayUserInfo() {
        String idToken = tokenManager.getIdToken();
        if (idToken == null) {
            binding.tvUserName.setText("Usuário autenticado");
            binding.tvUserEmail.setText("—");
            return;
        }

        try {
            // JWT tem 3 partes separadas por '.': header.payload.signature
            String[] parts = idToken.split("\\.");
            if (parts.length < 2) return;

            // Decodifica o payload (parte 2) em Base64
            byte[] decodedBytes = Base64.decode(parts[1], Base64.URL_SAFE | Base64.NO_PADDING);
            String payloadJson  = new String(decodedBytes, StandardCharsets.UTF_8);

            JSONObject claims = new JSONObject(payloadJson);

            String name  = claims.optString("name", claims.optString("preferred_username", "Usuário"));
            String email = claims.optString("email", "—");
            String role  = claims.optString("role", "—");
            String sub   = claims.optString("sub", "—");

            binding.tvUserName.setText(name);
            binding.tvUserEmail.setText(email);
            binding.tvUserRole.setText("Perfil: " + role);
            binding.tvUserSub.setText("ID: " + sub);

        } catch (Exception e) {
            Log.e(TAG, "Erro ao decodificar ID Token: " + e.getMessage());
            binding.tvUserName.setText("Usuário autenticado");
        }
    }

    // ─── Botões de teste da Resource API ──────────────────────────────────────

    private void setupButtons() {
        // Endpoint público — sem token
        binding.btnPublicHello.setOnClickListener(v -> callPublicHello());

        // Endpoint privado — com Bearer token
        binding.btnPrivateHello.setOnClickListener(v -> callPrivateHello());

        // Claims do JWT — retorna todos os atributos do token
        binding.btnClaims.setOnClickListener(v -> callClaims());
    }

    private void callPublicHello() {
        setApiLoading(true);
        binding.tvApiResult.setText("Chamando /public/hello...");

        ResourceApiService api = ApiClient.getPublicInstance();
        api.publicHello().enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                runOnUiThread(() -> {
                    setApiLoading(false);
                    if (response.isSuccessful() && response.body() != null) {
                        showApiResult("✅ /public/hello\n\n" + response.body());
                    } else {
                        showApiResult("❌ Erro " + response.code());
                    }
                });
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                runOnUiThread(() -> {
                    setApiLoading(false);
                    showApiResult("❌ Falha de conexão:\n" + t.getMessage());
                });
            }
        });
    }

    private void callPrivateHello() {
        setApiLoading(true);
        binding.tvApiResult.setText("Chamando /privado/hello com token...");

        ResourceApiService api = ApiClient.getInstance(accessToken);
        api.privateHello().enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                runOnUiThread(() -> {
                    setApiLoading(false);
                    if (response.isSuccessful() && response.body() != null) {
                        showApiResult("✅ /privado/hello\n\n" + response.body());
                    } else if (response.code() == 401) {
                        showApiResult("❌ 401 Não autorizado\n\nToken inválido ou expirado.");
                    } else {
                        showApiResult("❌ Erro " + response.code());
                    }
                });
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                runOnUiThread(() -> {
                    setApiLoading(false);
                    showApiResult("❌ Falha de conexão:\n" + t.getMessage());
                });
            }
        });
    }

    private void callClaims() {
        setApiLoading(true);
        binding.tvApiResult.setText("Buscando claims do token...");

        ResourceApiService api = ApiClient.getInstance(accessToken);
        api.privateClaims().enqueue(new Callback<Map<String, Object>>() {
            @Override
            public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                runOnUiThread(() -> {
                    setApiLoading(false);
                    if (response.isSuccessful() && response.body() != null) {
                        StringBuilder sb = new StringBuilder("✅ /privado/claims\n\n");
                        for (Map.Entry<String, Object> entry : response.body().entrySet()) {
                            sb.append("• ").append(entry.getKey())
                              .append(": ").append(entry.getValue()).append("\n");
                        }
                        showApiResult(sb.toString());
                    } else if (response.code() == 401) {
                        showApiResult("❌ 401 Não autorizado\n\nToken inválido ou expirado.");
                    } else {
                        showApiResult("❌ Erro " + response.code());
                    }
                });
            }

            @Override
            public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                runOnUiThread(() -> {
                    setApiLoading(false);
                    showApiResult("❌ Falha de conexão:\n" + t.getMessage());
                });
            }
        });
    }

    // ─── Menu (logout) ─────────────────────────────────────────────────────────

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_logout) {
            confirmLogout();
            return true;
        }
        if (item.getItemId() == R.id.action_token_info) {
            showTokenInfo();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void confirmLogout() {
        new AlertDialog.Builder(this)
                .setTitle("Sair")
                .setMessage("Deseja encerrar a sessão?")
                .setPositiveButton("Sair", (dialog, which) -> logout())
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void logout() {
        tokenManager.clearTokens();
        Toast.makeText(this, "Sessão encerrada", Toast.LENGTH_SHORT).show();
        goToLogin();
    }

    private void showTokenInfo() {
        String token = tokenManager.getAccessToken();
        String preview = token != null && token.length() > 40
                ? token.substring(0, 40) + "..."
                : token;

        new AlertDialog.Builder(this)
                .setTitle("Informações do Token")
                .setMessage("Access Token (preview):\n" + preview +
                        "\n\nExpira em: " + tokenManager.getExpiresAt() +
                        "\n\nVálido: " + tokenManager.hasValidToken())
                .setPositiveButton("OK", null)
                .show();
    }

    // ─── Helpers ───────────────────────────────────────────────────────────────

    private void showApiResult(String result) {
        binding.tvApiResult.setText(result);
        binding.cardApiResult.setVisibility(View.VISIBLE);
    }

    private void setApiLoading(boolean loading) {
        binding.progressBarApi.setVisibility(loading ? View.VISIBLE : View.GONE);
        binding.btnPublicHello.setEnabled(!loading);
        binding.btnPrivateHello.setEnabled(!loading);
        binding.btnClaims.setEnabled(!loading);
    }

    private void goToLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
