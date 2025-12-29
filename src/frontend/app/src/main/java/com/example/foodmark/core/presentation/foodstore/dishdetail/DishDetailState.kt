package com.example.foodmark.core.presentation.foodstore.dishdetail

import com.example.foodmark.core.domain.model.Dishes

data class DishDetailUiState(
    val isLoading: Boolean = false,
    val dish: Dishes? = null,
    val error: String? = null
)