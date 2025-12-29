package com.example.foodmark.foodtour.domain.repo

import com.example.foodmark.core.domain.model.RepoResult
import com.example.foodmark.foodtour.domain.model.DestinationNode
import com.example.foodmark.foodtour.domain.model.RouteRecommendation

interface FoodTourRepository {
    suspend fun getRecommendations(
        userId: String,
        lat: Double,
        lng: Double
    ): RepoResult<RouteRecommendation>
    suspend fun getDirection(
        destination: List<DestinationNode>
    ) : RepoResult<List<DestinationNode>>
}