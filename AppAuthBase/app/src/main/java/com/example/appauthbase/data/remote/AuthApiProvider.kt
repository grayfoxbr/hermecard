package com.example.appauthbase.data.remote

import com.example.appauthbase.config.NetworkConfig
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object AuthApiProvider {

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(NetworkConfig.AUTH_BASE_URL + "/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val authApi: AuthApi by lazy {
        retrofit.create(AuthApi::class.java)
    }
}