package com.example.appauthbase.data.repository

import android.content.Intent
import com.example.appauthbase.data.datasource.OAuthDataSource
import com.example.appauthbase.data.model.AuthSession
import com.example.appauthbase.domain.repository.OAuthRepository

class OAuthRepositoryImpl(

    private val source:
    OAuthDataSource

) : OAuthRepository {

    override fun loginIntent() =
        source.buildIntent()

    override fun session() =

        AuthSession(

            source.authorized(),

            source.token()
        )

    override fun logout() {

        source.clear()
    }

    override fun completeLogin(

        intent: Intent,

        callback:
            (
            Boolean,
            Exception?
        ) -> Unit

    ) {

        source.handleResult(

            intent,

            callback
        )
    }
}