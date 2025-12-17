package com.example.foodmark.geo.domain.usecase

import com.example.foodmark.geo.domain.model.GeoPoint
import com.example.foodmark.geo.domain.repo.GeoRepository
import javax.inject.Inject

class GetCurrentLocationUseCase @Inject constructor(
    private val repository: GeoRepository
) {
    suspend operator fun invoke(): GeoPoint? {
        return repository.getCurrentLocation()
    }
}