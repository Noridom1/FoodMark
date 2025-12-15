package com.example.foodmark.auth.domain.use_cases

import com.example.foodmark.auth.domain.respository.AuthRepository
import com.example.foodmark.auth.domain.model.AuthResult

class SignInWithGoogleUseCase(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(idToken: String, nonce: String): AuthResult {
        return repository.signInWithGoogle(idToken, nonce)
    }
}
