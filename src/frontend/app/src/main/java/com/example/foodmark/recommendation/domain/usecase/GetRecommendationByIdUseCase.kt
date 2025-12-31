package com.example.cs426_mobileproject.recommendation.domain.usecase

import com.example.cs426_mobileproject.recommendation.domain.repo.RecommendationRepository
import javax.inject.Inject

class GetRecommendationByIdUseCase @Inject constructor(
    private val repository: RecommendationRepository
) {
    suspend operator fun invoke(rec_id: String) =
        repository.getRecommendationById(rec_id)
}