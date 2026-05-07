package com.angels.hermecard.network;

import com.angels.hermecard.auth.OAuthConfig;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

/**
 * Singleton que fornece uma instância configurada do Retrofit para a Resource API.
 *
 * Injeta o Bearer token em todas as requisições via OkHttp Interceptor.
 * Para usar: ApiClient.getInstance(token).getService().privateHello()
 */
public class ApiClient {

    private static ResourceApiService service;
    private static String lastToken;

    /**
     * Retorna o serviço Retrofit, recriando-o se o token mudou.
     *
     * @param accessToken Bearer token obtido do OAuth2 server
     */
    public static ResourceApiService getInstance(String accessToken) {
        if (service == null || !accessToken.equals(lastToken)) {
            lastToken = accessToken;
            service = buildService(accessToken);
        }
        return service;
    }

    /**
     * Versão sem token para endpoints públicos.
     */
    public static ResourceApiService getPublicInstance() {
        return buildService(null);
    }

    private static ResourceApiService buildService(String accessToken) {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(chain -> {
                    Request.Builder requestBuilder = chain.request().newBuilder()
                            .header("Accept", "application/json")
                            .header("Content-Type", "application/json");

                    // Injeta o Bearer token se disponível
                    if (accessToken != null && !accessToken.isEmpty()) {
                        requestBuilder.header("Authorization", "Bearer " + accessToken);
                    }

                    return chain.proceed(requestBuilder.build());
                })
                .addInterceptor(logging)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(OAuthConfig.RESOURCE_API_BASE + "/")
                .client(client)
                // ScalarsConverter para endpoints que retornam String pura
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        return retrofit.create(ResourceApiService.class);
    }
}
