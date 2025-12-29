package com.example.foodmark.core.data.repository

import android.util.Log
import com.example.foodmark.core.domain.model.Profile
import com.example.foodmark.core.domain.model.RepoResult
import com.example.foodmark.core.domain.repository.ProfileRepository
import com.example.foodmark.core.supabase.SupabaseClientProvider.client
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.storage.storage
import kotlinx.coroutines.delay
import java.io.File
import javax.inject.Inject

class ProfileRepositoryImpl @Inject constructor() : ProfileRepository {
    override suspend fun getProfile(): RepoResult<Profile> {
        val user = client.auth.currentUserOrNull()
        val userId = user?.id
        if (userId == null) {
            return RepoResult.Error("User not logged in")
        }
        try {
            val profileResponse = client
                .from("User")
                .select() {
                    filter {
                        eq("id", userId)
                    }
                }.decodeSingleOrNull<Profile>()
            return if (profileResponse != null) {
                RepoResult.Success(profileResponse)
            } else {
                RepoResult.Error("Profile not found")
            }
        } catch (e: Exception) {
            return RepoResult.Error("Failed to fetch profile: ${e.message}", e)
        }
    }


    override suspend fun updateProfile(profile: Profile): RepoResult<Profile> {
        val user = client.auth.currentUserOrNull()
        val userId = user?.id
        if (userId == null) {
            return RepoResult.Error("User not logged in")
        }
        return try {
            client
                .from("User")
                .update(profile) {
                    filter {
                        eq("id", userId)
                    }
                }
            RepoResult.Success(profile)
        } catch (e: Exception) {
            RepoResult.Error("Failed to update profile: ${e.message}", e)
        }

    }

    override suspend fun getProfileByID(user_id: String): RepoResult<Profile> {
        try {
            val profileResponse = client
                .from("User")
                .select() {
                    filter {
                        eq("id", user_id)
                    }
                }
                .decodeSingleOrNull<Profile>()

            return if (profileResponse != null) {
                RepoResult.Success(profileResponse)
            } else {
                RepoResult.Error("Profile with id $user_id not found")
            }
        } catch (e: Exception) {
            return RepoResult.Error("Failed to fetch profile: ${e.message}", e)
        }
    }

    override suspend fun addProfileImage(userId: String, file: File): RepoResult<Unit> {
        return try {
            // 1. Upload to Supabase storage bucket
            val filePath = "$userId/${file.name}" // nice structured path
            val bytes = file.readBytes()

            client.storage
                .from("imagebucket")
                .upload (
                    path = filePath,
                    data = bytes
                )

            // 2. Get public URL
            val publicUrl = client.storage
                .from("imagebucket")
                .publicUrl(filePath)

            // 3. Update User table with new URL
            client.from("User")
                .update(
                    mapOf("img_url" to publicUrl)
                ) {
                    filter {
                        eq("id", userId)
                    }
                }
            Log.d("FoodStoreRepository", "Image uploaded for (userId: $userId): $publicUrl")
            RepoResult.Success(Unit)
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e("FoodStoreRepository", "Failed to upload profile image for (userId: $userId): ${e.message}")
            RepoResult.Error(e.message ?: "Unknown error")
        }
    }
}

