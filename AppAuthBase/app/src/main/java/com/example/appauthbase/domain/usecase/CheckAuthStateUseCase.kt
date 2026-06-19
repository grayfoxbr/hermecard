package com.example.appauthbase.domain.usecase

import com.example.appauthbase.domain.repository.OAuthRepository

class CheckAuthStateUseCase(
    private val repo: OAuthRepository
) {

    operator fun invoke() =
        repo.session()
}