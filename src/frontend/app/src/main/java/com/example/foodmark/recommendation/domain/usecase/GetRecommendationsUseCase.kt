package com.example.cs426_mobileproject.recommendation.domain.usecase

import com.example.cs426_mobileproject.recommendation.domain.repo.RecommendationRepository
import javax.inject.Inject

class GetRecommendationsUseCase @Inject constructor(
    private val repo: RecommendationRepository
) {
    suspend operator fun invoke(user_id: String) =
        repo.getRecommendations(user_id)

}