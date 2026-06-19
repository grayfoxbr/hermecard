package com.example.appauthbase.auth

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApi {

    @POST("/users")
    suspend fun register(
        @Body request: RegisterRequest
    ): Response<RegisterResponse>

}