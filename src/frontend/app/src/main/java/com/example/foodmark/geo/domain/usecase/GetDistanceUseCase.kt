package com.example.foodmark.geo.domain.usecase

import com.example.foodmark.geo.domain.model.GeoPoint
import com.example.foodmark.geo.domain.repo.GeoRepository
import javax.inject.Inject

class GetDistanceUseCase @Inject constructor(
    private val repo : GeoRepository
) {
    suspend operator fun invoke(origin : GeoPoint, destination : GeoPoint) : Double {
        return repo.getDistance(origin, destination)
    }
}