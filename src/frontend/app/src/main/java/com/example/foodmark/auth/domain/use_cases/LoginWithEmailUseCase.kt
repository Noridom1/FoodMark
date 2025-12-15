package com.example.foodmark.auth.domain.use_cases

import com.example.foodmark.auth.domain.model.AuthResult
import com.example.foodmark.auth.domain.respository.AuthRepository

class LoginWithEmailUseCase(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(email: String, password: String): AuthResult {
        return repository.loginWithEmail(email, password)
    }
}
