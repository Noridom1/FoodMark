package com.example.foodmark.foodtour.presentation

import com.example.foodmark.foodtour.domain.model.DestinationNode
import com.example.foodmark.foodtour.domain.model.RouteRecommendation

data class FoodTourUIState (
    val isLoading: Boolean = false,
    val isRecommending: Boolean = false,
    val error: String? = null,
    val routePath: List<DestinationNode> = emptyList(),
    val routeRecommendation: RouteRecommendation? = null,
    val routeDescription: String? = null
)