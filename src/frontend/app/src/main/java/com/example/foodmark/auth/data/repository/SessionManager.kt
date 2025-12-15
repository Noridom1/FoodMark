package com.example.foodmark.auth.data.repository

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton
import androidx.core.content.edit

@Singleton
class SessionManager @Inject constructor(
    private val context: Context
) {
    // Using SharedPreferences as per your existing implementation
    private val prefs: SharedPreferences = context.getSharedPreferences("auth_session", Context.MODE_PRIVATE)

    // NEW: Internal MutableStateFlow to hold the current session email reactively
    private val _sessionUserId = MutableStateFlow<String?>(null)

    // Exposed scope for launching flows that should live as long as the SessionManager
    val sessionScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    init {
        // Initialize the reactive flow with the current value from SharedPreferences
        _sessionUserId.value = prefs.getString("session_user_id", null)
        Log.d("SessionManager", "Initialized _sessionUserId with: ${_sessionUserId.value}")
    }

    // Your existing synchronous saveSession function
    fun saveSession(userId: String) {
        prefs.edit { putString("session_user_id", userId) }
        _sessionUserId.value = userId // NEW: Update the reactive flow immediately
        Log.d("SessionManager", "Session saved: $userId. _sessionUserId updated.")
    }

    fun getSession(): String? {
        val userId = prefs.getString("session_user_id", null)
        Log.d("SessionManager", "getSession() called, returning: $userId")
        return userId
    }

    // NEW: Provides a Flow for AuthRepositoryImpl to observe
    fun getSessionIdFlow(): Flow<String?> {
        Log.d("SessionManager", "getSessionIdFlow() called. Returning asStateFlow.")
        return _sessionUserId.asStateFlow()
    }

    // Your existing synchronous clearSession function
    fun clearSession() {
        prefs.edit { remove("session_user_id") }
        _sessionUserId.value = null // NEW: Clear the reactive flow immediately
        Log.d("SessionManager", "Session cleared. _sessionUserId cleared.")
    }

    /**
     * FOR DEBUGGING ONLY: Returns a hardcoded email to simulate a logged-in user.
     * In a real application, this should be removed or guarded by build configurations.
     */
    fun getDebugSession(): String {
        return "anderson@example.com"
    }
}