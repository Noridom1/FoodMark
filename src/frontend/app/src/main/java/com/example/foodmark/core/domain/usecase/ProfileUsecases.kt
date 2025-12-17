package com.example.foodmark.core.domain.usecase

import android.net.Uri
import com.example.foodmark.core.domain.model.Profile
import com.example.foodmark.core.domain.model.RepoResult
import com.example.foodmark.core.domain.repository.ProfileRepository

class GetProfileUseCase(private val repo: ProfileRepository) {
    suspend operator fun invoke(): RepoResult<Profile> = repo.getProfile()
}
class GetProfileByID(private val repo: ProfileRepository) {
    suspend operator fun invoke(user_id: String): RepoResult<Profile> = repo.getProfileByID(user_id)
}
