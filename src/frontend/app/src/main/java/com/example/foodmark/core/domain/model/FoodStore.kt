package com.example.foodmark.core.domain.model

import com.example.foodmark.geo.domain.model.GeoPoint
import kotlinx.serialization.Serializable

@Serializable
data class FoodStore(
    val user_id: String,
    val id: String,
    val created_at: String,
    val name: String,
    val location: GeoPoint? = null,
    val address: String? = null,
    val img_url: String? = null,
    val user_note: String = "",
    val favorite: Boolean = false
)