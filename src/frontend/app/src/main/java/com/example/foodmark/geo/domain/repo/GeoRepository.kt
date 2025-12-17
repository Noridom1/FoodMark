package com.example.foodmark.geo.domain.repo

import com.example.foodmark.geo.domain.model.GeoPoint

interface GeoRepository {
    suspend fun getCurrentLocation(): GeoPoint?
    suspend fun getDistance(origin: GeoPoint, destination: GeoPoint): Double
}