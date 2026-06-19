package com.example.appauthbase.presentation

data class AuthStateData(

    val isLoggedIn: Boolean = false,

    val accessToken: String? = null,

    val isLoading: Boolean = false,

    val errorMessage: String? = null
)