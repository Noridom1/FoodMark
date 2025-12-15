package com.example.foodmark.auth.domain.use_cases

import com.example.foodmark.auth.domain.model.AuthResult
import com.example.foodmark.auth.domain.respository.AuthRepository

class RegisterWithEmailUseCase(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(
        email: String,
        password: String
    ): AuthResult {
        return repository.registerWithEmail(
            email,
            password
        )
    }
}
