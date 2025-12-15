package com.example.foodmark.auth.domain.respository

import com.example.foodmark.auth.domain.model.AuthResult
import com.example.foodmark.core.domain.model.Profile
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    suspend fun loginWithEmail(email: String, password: String) : AuthResult
    suspend fun signInWithGoogle(idToken: String, rawNonce: String): AuthResult
    suspend fun registerWithEmail(
        email: String,
        password: String
    ) : AuthResult
    suspend fun isLoggedIn() : Boolean
    suspend fun getLoggedInUserFlow() : Flow<Profile?>
    suspend fun getLoggedInUser(): Profile?
    suspend fun logOutUser(): Boolean

}