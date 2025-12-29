package com.example.foodmark.foodtour.data.repo

import android.util.Log
import com.example.foodmark.core.domain.model.RepoResult
import com.example.foodmark.foodtour.data.remote.FoodTourAPI
import com.example.foodmark.foodtour.domain.model.DestinationNode
import com.example.foodmark.foodtour.domain.model.RouteRecommendation
import com.example.foodmark.foodtour.domain.repo.FoodTourRepository
import javax.inject.Inject

class FoodTourRepositoryImpl @Inject constructor(
    private val api: FoodTourAPI
) : FoodTourRepository {

    override suspend fun getRecommendations(
        userId: String,
        lat: Double,
        lng: Double
    ): RepoResult<RouteRecommendation> {
        return try {
            val response = api.getRecommend(userId, lat, lng)
            Log.d("FoodTourRepositoryImpl", "Response: $response")
            RepoResult.Success(response)
        } catch (e: Exception) {
            Log.d("FoodTourRepositoryImpl", "Exception: ${e.message}")
            RepoResult.Error("Failed to get recommendations: ${e.message}", e)
        }
    }

    override suspend fun getDirection(destination: List<DestinationNode>): RepoResult<List<DestinationNode>> {
        return try {
            // Call your API method
            val response: List<DestinationNode> = api.getRouteDirection(destination)
            Log.d("FoodTourRepositoryImpl", "Response: $response")
            RepoResult.Success(response)
        } catch (e: Exception) {
            Log.e("FoodTourRepositoryImpl", "Failed to get directions", e)
            RepoResult.Error(
                message = "Failed to get directions: ${e.localizedMessage ?: e.message}",
            )
        }
    }
}