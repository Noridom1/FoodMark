package com.example.foodmark.core.data.repository

import com.example.foodmark.core.domain.model.FoodVideo
import com.example.foodmark.core.domain.model.VideoMeta
import com.example.foodmark.core.domain.repository.VideoRepository

class FakeVideoRepository : VideoRepository {
    override suspend fun processVideo(url: String): FoodVideo? {
        return when {
            url.contains("place", ignoreCase = true) -> {
                FoodVideo.PlaceVideo(
                    meta = VideoMeta(url, title = "Best Sushi in Town", author = "@foodie"),
                    placeName = "Sushi Zen",
                    address = "123 Tokyo Street",
                    cuisineType = "Japanese",
                    distance = 2.5
                )
            }
            url.contains("recipe", ignoreCase = true) -> {
                FoodVideo.RecipeVideo(
                    meta = VideoMeta(url, title = "How to make Ramen", author = "@chef"),
                    recipeName = "Ramen",
                    ingredients = listOf("Noodles", "Broth", "Egg", "Pork"),
                    steps = listOf("Boil noodles", "Prepare broth", "Add toppings", "Serve hot")
                )
            }
            else -> null
        }
    }
}

