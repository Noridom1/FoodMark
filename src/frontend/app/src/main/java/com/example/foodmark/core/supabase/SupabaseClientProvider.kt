package com.example.foodmark.core.supabase

import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.Google
import io.github.jan.supabase.auth.providers.builtin.IDToken
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.postgrest.from
import android.util.Log
import com.example.foodmark.core.domain.model.Profile
import io.github.jan.supabase.storage.Storage

object SupabaseClientProvider {
    val client = createSupabaseClient (
        supabaseUrl = "https://fgkmsasdgcykscfcsynx.supabase.co",
        supabaseKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImZna21zYXNkZ2N5a3NjZmNzeW54Iiwicm9sZSI6ImFub24iLCJpYXQiOjE3NTQ3OTI3NjgsImV4cCI6MjA3MDM2ODc2OH0.zF_SbkO2o0itFfY1INLk7uP0GIpbE8L9cnaAbiiwaec"
    ) {
        install(Auth) {
            alwaysAutoRefresh = true
        }
        install(Postgrest)
        install(Storage)
    }

    suspend fun signInWithGoogle(googleIdToken: String, rawNonce: String): Boolean {
        println("✅ Email login successful")
        return try {
            client.auth.signInWith(IDToken) {
                idToken = googleIdToken
                provider = Google
                nonce = rawNonce
            }

            val user = client.auth.currentUserOrNull()
            val userId = user?.id



            if (user != null) {
                println("✅ Email login successful")
                println("👤 User ID: ${user.id}")
                true
                if (userId != null) {
                    checkOrCreateProfile(userId)
                }
                println(userId)
            } else {
                println("⚠️ Login attempted, but no user session found")
                false
            }

            println("✅ Connect Well")
            true
        } catch (e: Exception) {
            e.printStackTrace()
            println("❌ Connect fail")
            false
        }
    }
    suspend fun loginWithEmail(email: String, password: String): Boolean {
        return try {
            client.auth.signInWith(Email) {
                this.email = email
                this.password = password
            }
            val user = client.auth.currentUserOrNull()
            val userId = user?.id

            if (userId != null) {
                checkOrCreateProfile(userId)
            }

            if (user != null) {
                println("✅ Email login successful")
                println("👤 User ID: ${user.id}")
                true
            } else {
                println("⚠️ Login attempted, but no user session found")
                false
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Log.d("LoginWithEmail", "Login failed: ${e.message}")
            println("❌ Email login failed")
            false
        }
    }

    suspend fun registerWithEmail(email: String, password: String): Boolean {
        return try {
            client.auth.signUpWith(Email) {
                this.email = email
                this.password = password
            }
            true
        } catch (e: Exception) {
            e.printStackTrace()
            println("❌ Email login failed")
            false
        }
    }

    suspend fun checkOrCreateProfile(userId: String) {
        val user = client.auth.currentUserOrNull()
        val userId = user?.id ?: return println("❌ No logged in user")
        val email = user.email ?: ""
        try {
            // Try to get existing profile
            val profileResponse = client
                .from("User")
                .select() {
                    filter {
                        eq("id", userId)
                    }
                }.decodeSingleOrNull<Profile>()


            if (profileResponse == null) {
                val newProfile = Profile(
                    id = userId,
                    name = "",
                    img_url = "",
                    email = email,
                    phone = null,
                    dob = null
                )
                client
                    .from("User")
                    .insert(newProfile)
                println("✅ Created empty profile for user: $userId")
            } else {
                println("👤 Existing profile found for user: $userId")
            }
        } catch (e: Exception) {
            e.printStackTrace()
            println("❌ Failed to check/create profile")
        }
    }

}
