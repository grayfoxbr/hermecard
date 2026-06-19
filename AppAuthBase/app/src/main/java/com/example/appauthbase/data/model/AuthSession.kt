package com.example.appauthbase.data.model

data class AuthSession(

    val loggedIn: Boolean,

    val accessToken: String?
)