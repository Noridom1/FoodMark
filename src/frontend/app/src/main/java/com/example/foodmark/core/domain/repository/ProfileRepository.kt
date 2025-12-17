package com.example.foodmark.core.domain.repository

import com.example.cs426_mobileproject.core.domain.model.Profile
import com.example.cs426_mobileproject.core.domain.model.RepoResult
import java.io.File

interface ProfileRepository {
    suspend fun getProfile(): RepoResult<Profile>
    suspend fun updateProfile(profile: Profile): RepoResult<Profile>
    suspend fun getProfileByID(user_id: String): RepoResult<Profile>
    suspend fun addProfileImage(userId: String, file: File): RepoResult<Unit>
}
