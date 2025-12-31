package com.example.cs426_mobileproject.recommendation.domain.repo

import com.example.cs426_mobileproject.core.domain.model.RepoResult
import com.example.cs426_mobileproject.recommendation.domain.model.Recommendation

interface RecommendationRepository {
    suspend fun recommendStore(
        from_user_id: String,
        to_user_id: String,
        store_user_id: String,
        store_id: String,
        from_user_name: String,
        store_name: String,
        note: String
    ): RepoResult<Unit>
    suspend fun getRecommendations(user_id: String): List<Recommendation>
    suspend fun getRecommendationById(rec_id: String) : Recommendation?
    suspend fun removeRecommendation(id: String): RepoResult<Unit>
    suspend fun addStoreFromRecommendation(rec_id: String)
}