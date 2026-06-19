package com.example.appauthbase.domain.repository

interface AuthRepository {
    suspend fun register(email: String, password: String): Result<Unit>
}