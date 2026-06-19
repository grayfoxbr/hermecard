package com.example.appauthbase.domain.usecase

import android.content.Intent
import com.example.appauthbase.domain.repository.OAuthRepository

class HandleAuthResponseUseCase(
    private val repo:
    OAuthRepository
) {

    operator fun invoke(

        intent: Intent,

        callback:
            (
            Boolean,
            Exception?
        ) -> Unit

    ) {

        repo.completeLogin(
            intent,
            callback
        )
    }
}