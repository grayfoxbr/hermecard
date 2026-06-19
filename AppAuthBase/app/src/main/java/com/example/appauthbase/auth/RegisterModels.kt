package com.example.appauthbase.auth

data class RegisterRequest(
    val email: String,
    val password: String
)

data class RegisterResponse(
    val id: Long,
    val email: String
)