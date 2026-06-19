package com.example.appauthbase.data.remote

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object AuthApiProvider {

    private const val BASE_URL =
        "http://10.0.2.2:8080/"

    private val retrofit: Retrofit by lazy {

        Retrofit.Builder()

            .baseUrl(
                BASE_URL
            )

            .addConverterFactory(
                GsonConverterFactory.create()
            )

            .build()
    }

    val authApi: AuthApi by lazy {

        retrofit.create(
            AuthApi::class.java
        )
    }
}