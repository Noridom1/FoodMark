package com.example.cs426_mobileproject.recommendation.domain.usecase

import com.example.cs426_mobileproject.recommendation.domain.repo.RecommendationRepository
import javax.inject.Inject

class RecommendStoreUseCase @Inject constructor(
    private val repo: RecommendationRepository,
) {
    suspend operator fun invoke(
        from_user_id: String,
        to_user_id: String,
        store_user_id: String,
        store_id: String,
        from_user_name: String,
        store_name: String,
        note: String
    ) =
        repo.recommendStore(
            from_user_id,
            to_user_id,
            store_user_id,
            store_id,
            from_user_name,
            store_name,
            note
        )

}