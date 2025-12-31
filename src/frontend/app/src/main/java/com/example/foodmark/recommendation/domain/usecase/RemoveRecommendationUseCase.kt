package com.example.cs426_mobileproject.recommendation.domain.usecase

import com.example.cs426_mobileproject.recommendation.domain.repo.RecommendationRepository
import javax.inject.Inject

class RemoveRecommendationUseCase @Inject constructor(
    private val repo: RecommendationRepository
) {
    suspend operator fun invoke(id: String) =
        repo.removeRecommendation(id)
}