package com.example.appauthbase.domain.repository

import android.content.Intent
import com.example.appauthbase.data.model.AuthSession

interface OAuthRepository {

    fun loginIntent():

            Intent

    fun session():

            AuthSession

    fun logout()

    fun completeLogin(

        intent: Intent,

        callback:
            (
            Boolean,
            Exception?
        ) -> Unit
    )
}