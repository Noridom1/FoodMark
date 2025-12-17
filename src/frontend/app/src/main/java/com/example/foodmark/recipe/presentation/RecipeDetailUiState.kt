package com.example.foodmark.recipe.presentation

import com.example.foodmark.recipe.domain.model.CookingStep
import com.example.foodmark.recipe.domain.model.Recipe

data class RecipeDetailUiState(
    val isLoading: Boolean = false,
    val isUploadingImage: Boolean = false,
    val recipe: Recipe? = null,
    val steps: List<CookingStep> = emptyList(),
    val error: String? = null
)
