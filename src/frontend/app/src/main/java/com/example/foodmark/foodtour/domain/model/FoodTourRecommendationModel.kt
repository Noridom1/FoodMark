package com.example.foodmark.foodtour.domain.model

import android.R


data class RecommendationRequest(
    val user_id: String,
    val user_lat: Double,
    val user_lng: Double
)

data class DishOut(
    val id: String,
    val name: String,
    val price: String?,
    val rating: Double?,
    val taste: String?
)

data class StoreOut(
    val id: String,
    val name: String,
    val address: String,
    val lat: Double,
    val lng: Double,
    val distance_km: Double,
    val recommended_dishes: List<DishOut>
)

data class DestinationNode (
    val lat: Double,
    val lng: Double
)

data class RouteRecommendation(
    val route: List<StoreOut>,
    val description: String
)