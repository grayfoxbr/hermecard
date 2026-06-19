package com.example.appauthbase.domain.usecase

import android.content.Intent
import com.example.appauthbase.domain.repository.OAuthRepository

class GetLoginIntentUseCase(
    private val repo: OAuthRepository
) {
    operator fun invoke(): Intent = repo.loginIntent()
}