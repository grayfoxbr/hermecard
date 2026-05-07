package com.angels.hermecard.network;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.GET;

/**
 * Interface Retrofit que mapeia os endpoints da hermecard-resource-api (porta 8081).
 *
 * Endpoints disponíveis (de TestController.java):
 *   GET /public/hello    → sem autenticação
 *   GET /privado/hello   → requer Bearer token → retorna "olá, {username}"
 *   GET /privado/claims  → requer Bearer token → retorna todos os claims do JWT
 */
public interface ResourceApiService {

    @GET("public/hello")
    Call<String> publicHello();

    @GET("privado/hello")
    Call<String> privateHello();

    @GET("privado/claims")
    Call<Map<String, Object>> privateClaims();
}
