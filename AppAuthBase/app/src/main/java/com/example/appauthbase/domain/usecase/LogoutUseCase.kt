package com.example.appauthbase.domain.usecase

import com.example.appauthbase.domain.repository.OAuthRepository

class LogoutUseCase(
    private val repo: OAuthRepository
) {

    operator fun invoke() {

        repo.logout()
    }
}