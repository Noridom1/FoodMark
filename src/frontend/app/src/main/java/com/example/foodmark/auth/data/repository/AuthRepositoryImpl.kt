package com.example.foodmark.auth.data.repository

import android.util.Log
import com.example.foodmark.auth.domain.model.AuthResult
import com.example.foodmark.auth.domain.respository.AuthRepository
import com.example.foodmark.auth.presentation.AuthState
import com.example.foodmark.core.domain.model.Profile
import com.example.foodmark.core.domain.model.RepoResult
import com.example.foodmark.core.domain.usecase.GetProfileByID
import com.example.foodmark.core.supabase.SupabaseClientProvider
import com.example.foodmark.core.supabase.SupabaseClientProvider.client
import io.github.jan.supabase.auth.auth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val getUserByIdUseCase: GetProfileByID,
    private val sessionManager: SessionManager,
) : AuthRepository {

//    private val _loggedInUser = MutableStateFlow<Profile?>(null)
//    val loggedInUser: StateFlow<Profile?> = _loggedInUser.asStateFlow()

    private val repositoryScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    private val _authState = MutableStateFlow<AuthState>(AuthState.Loading)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()


    // Convenience: expose only the Profile (null if unauthenticated/loading)
    var loggedInUser: StateFlow<Profile?> =
        _authState.map { state ->
            (state as? AuthState.Authenticated)?.user
        }.stateIn(repositoryScope, SharingStarted.Eagerly, null)


    init {
        Log.d("AuthRepositoryImpl", "Initializing AuthRepositoryImpl")

        sessionManager.getSessionIdFlow()
            .map { userId ->
                if (userId != null) {
                    val result = getUserByIdUseCase(userId)
                    if (result is RepoResult.Success) {
                        AuthState.Authenticated(result.data)
                    } else {
                        AuthState.Unauthenticated
                    }
                } else {
                    AuthState.Unauthenticated
                }
            }
            .onEach { state ->
                _authState.value = state
                Log.d("AuthRepositoryImpl", "Auth state updated: $state")
            }
            .launchIn(repositoryScope)
    }

    override suspend fun signInWithGoogle(idToken: String, nonce: String): AuthResult {
        return try {
            val success = SupabaseClientProvider.signInWithGoogle(idToken, nonce)
            if (success) {
                val user = client.auth.currentSessionOrNull()?.user
                sessionManager.saveSession(userId = user?.id ?: "")
                AuthResult.Success("Google sign-in succeeded")
            } else {
                AuthResult.Failure("Google sign-in failed")
            }
        } catch (e: Exception) {
            AuthResult.Failure(e.message ?: "Unexpected error during Google sign-in")
        }
    }

    override suspend fun loginWithEmail(email: String, password: String): AuthResult {
        return try {
            val success = SupabaseClientProvider.loginWithEmail(email, password)
            if (success) {
                val user = client.auth.currentSessionOrNull()?.user
                sessionManager.saveSession(userId = user?.id ?: "")
                AuthResult.Success("Email login succeeded")
            } else {
                AuthResult.Failure("Email login failed")
            }
        } catch (e: Exception) {
            AuthResult.Failure(e.message ?: "Unexpected error during email login")
        }
    }

    override suspend fun registerWithEmail(email: String, password: String): AuthResult {
        return try {
            val success = SupabaseClientProvider.registerWithEmail(email, password)
            if (success) {
                val user = client.auth.currentSessionOrNull()?.user
                sessionManager.saveSession(userId = user?.id ?: "")
                AuthResult.Success("Email Register succeeded")
            } else {
                AuthResult.Failure("Email Register failed")
            }
        } catch (e: Exception) {
            AuthResult.Failure(e.message ?: "Unexpected error during email Register")
        }
    }

    override suspend fun isLoggedIn(): Boolean {
        return sessionManager.getSession() != null
    }

    suspend fun logout() {
        client.auth.signOut()
        sessionManager.clearSession()
        _authState.value = AuthState.Unauthenticated
        Log.d("AuthRepositoryImpl", "User logged out, session cleared")
    }

    // Helper to load profile
    private suspend fun getUserProfile(userId: String?): Profile? {
        if (userId.isNullOrBlank()) return null
        val result = getUserByIdUseCase(userId)
        return if (result is RepoResult.Success) result.data else null
    }

    override suspend fun getLoggedInUserFlow(): Flow<Profile?> {
        Log.d("AuthRepositoryImpl", "getLoggedInUserFlow() called. Returning: $loggedInUser")
        return loggedInUser
    }

    override suspend fun getLoggedInUser(): Profile? {
        Log.d("AuthRepositoryImpl", "getLoggedInUser() called. Returning: ${loggedInUser.value}")
        return loggedInUser.value
    }

    override suspend fun logOutUser(): Boolean {
        return try {
            // Clear session in SessionManager
            sessionManager.clearSession()

            // Sign out from Supabase
            client.auth.signOut()

            // Update internal state if needed (for AuthRepositoryImpl)
            _authState.value = AuthState.Unauthenticated

            Log.d("AuthRepositoryImpl", "User logged out successfully")
            true
        } catch (e: Exception) {
            Log.e("AuthRepositoryImpl", "Error logging out user: ${e.message}")
            false
        }
    }
}

