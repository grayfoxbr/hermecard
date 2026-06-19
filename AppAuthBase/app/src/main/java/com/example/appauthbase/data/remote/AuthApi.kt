package com.example.appauthbase.data.remote

import com.example.appauthbase.data.remote.dto.RegisterRequest
import com.example.appauthbase.data.remote.dto.RegisterResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApi {

    /**
     * Cadastra um novo usuário no servidor.
     * O endpoint POST /users deve existir no hermecard-auth-api-server.
     * Nota: sem barra inicial — a base URL já termina com "/".
     */
    @POST("users")
    suspend fun register(
        @Body request: RegisterRequest
    ): Response<RegisterResponse>
}