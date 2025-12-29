package com.example.foodmark.core.domain.repository

import com.example.foodmark.core.domain.model.FoodVideo

interface NotebookRepository {
    suspend fun addPlace(place: FoodVideo.PlaceVideo)
    suspend fun addRecipe(recipe: FoodVideo.RecipeVideo)

    suspend fun getPlaces(): List<FoodVideo.PlaceVideo>
    suspend fun getRecipes(): List<FoodVideo.RecipeVideo>

    suspend fun updatePlace(place: FoodVideo.PlaceVideo)
    suspend fun updateRecipe(recipe: FoodVideo.RecipeVideo)
}