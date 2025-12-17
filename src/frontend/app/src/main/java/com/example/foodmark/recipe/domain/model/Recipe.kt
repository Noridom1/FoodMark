package com.example.foodmark.recipe.domain.model

import kotlinx.serialization.Serializable

// Untried recipe has img_url as ""
@Serializable
data class Recipe (
    val user_id: String,
    val recipe_id: Int,
    val name: String,
    val summary: String,
    val ingredients: String,
    val img_url: String = "",
    val created_at: String = ""
)