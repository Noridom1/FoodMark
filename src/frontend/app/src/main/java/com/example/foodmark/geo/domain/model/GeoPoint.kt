package com.example.foodmark.geo.domain.model

import com.example.foodmark.geo.domain.serializer.GeoPointSerializer
import kotlinx.serialization.Serializable

@Serializable(with = GeoPointSerializer::class)
data class GeoPoint(val lng: Double, val lat: Double)