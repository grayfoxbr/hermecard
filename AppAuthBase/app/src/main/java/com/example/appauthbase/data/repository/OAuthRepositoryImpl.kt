package com.example.appauthbase.data.repository

import android.content.Intent
import com.example.appauthbase.data.datasource.OAuthDataSource
import com.example.appauthbase.data.model.AuthSession
import com.example.appauthbase.domain.repository.OAuthRepository

class OAuthRepositoryImpl(
    private val dataSource: OAuthDataSource
) : OAuthRepository {

    override fun loginIntent(): Intent {
        dataSource.clear()
        return dataSource.buildIntent()
    }

    override fun session(): AuthSession {
        return AuthSession(
            loggedIn = dataSource.authorized(),
            accessToken = dataSource.token()
        )
    }

    override fun logout() {
        dataSource.clear()
    }

    override fun completeLogin(
        intent: Intent,
        callback: (Boolean, Exception?) -> Unit
    ) {
        dataSource.handleResult(intent, callback)
    }
}