package com.example.foodmark.recipe.domain.model

import kotlinx.serialization.Serializable
import org.w3c.dom.ProcessingInstruction

@Serializable
data class CookingStep (
    val user_id: String,
    val recipe_id: Int,
    val step_number: Int,
    val title: String,
    val instruction: String,
    val created_at: String = ""
)