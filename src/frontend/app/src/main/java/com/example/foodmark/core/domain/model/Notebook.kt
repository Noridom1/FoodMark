package com.example.foodmark.core.domain.model

data class PlaceNotebook(
    val places: List<FoodVideo.PlaceVideo> = emptyList()
)

data class RecipeNotebook(
    val recipes: List<FoodVideo.RecipeVideo> = emptyList()
)