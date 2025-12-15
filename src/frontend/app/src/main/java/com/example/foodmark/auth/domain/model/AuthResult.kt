package com.example.foodmark.auth.domain.model

// auth/domain/model/AuthResult.kt
sealed class AuthResult {
    data class Success(val message: String = "Success") : AuthResult()
    data class Failure(val message: String = "Fail") : AuthResult()
}
