package com.example.foodmark.core.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Dishes (
    val user_id: String,
    val store_id: String,
    val dish_id: Int = 0,
    val created_at: String = "",
    val name: String,
    val price: Int,
    val img_url: String? = null,
    val favorite: Boolean = false,
//    val description: String ?= null
)