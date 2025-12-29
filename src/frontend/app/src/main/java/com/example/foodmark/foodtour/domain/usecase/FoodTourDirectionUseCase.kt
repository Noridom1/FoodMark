package com.example.foodmark.foodtour.domain.usecase

import com.example.foodmark.foodtour.domain.model.DestinationNode
import com.example.foodmark.foodtour.domain.repo.FoodTourRepository
import javax.inject.Inject

class FoodTourDirectionUseCase @Inject constructor(
    private val repository: FoodTourRepository
){
    suspend operator fun invoke(destination: List<DestinationNode>) = repository.getDirection(destination)
}