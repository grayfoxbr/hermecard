package com.example.appauthbase.data.repository

import com.example.appauthbase.data.remote.AuthApi
import com.example.appauthbase.data.remote.dto.RegisterRequest
import com.example.appauthbase.domain.repository.AuthRepository

class AuthRepositoryImpl(
    private val api: AuthApi
) : AuthRepository {

    override suspend fun register(email: String, password: String): Result<Unit> {
        return try {
            val response = api.register(RegisterRequest(email, password))
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                val errorBody = response.errorBody()?.string() ?: "Erro desconhecido"
                Result.failure(Exception("Erro ${response.code()}: $errorBody"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Falha de conexão: ${e.message}"))
        }
    }
}