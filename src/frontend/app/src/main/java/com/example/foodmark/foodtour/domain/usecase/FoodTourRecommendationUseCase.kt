package com.example.foodmark.foodtour.domain.usecase

import com.example.foodmark.foodtour.domain.repo.FoodTourRepository
import javax.inject.Inject

class FoodTourRecommendUseCase @Inject constructor(
    private val repository: FoodTourRepository
){
    suspend operator fun invoke(userId: String, lat: Double, lng: Double) = repository.getRecommendations(userId, lat, lng)
}