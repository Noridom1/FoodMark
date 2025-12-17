package com.example.foodmark.recipe.presentation

import com.example.foodmark.core.domain.model.FoodVideo
import com.example.foodmark.recipe.domain.model.Recipe

enum class RecipeDisplayMode { GRID, LIST }

data class RecipeUiState(
    val recipes: List<Recipe> = emptyList(),
    val displayMode: RecipeDisplayMode = RecipeDisplayMode.LIST,
    val isLoading: Boolean = false,
    val error: String? = null
)