package com.example.foodmark.core.data.repository

import com.example.foodmark.core.domain.model.FoodVideo
import com.example.foodmark.core.domain.model.VideoMeta
import com.example.foodmark.core.domain.repository.NotebookRepository

class FakeNotebookRepository : NotebookRepository {
    private val placeNotebook = mutableListOf<FoodVideo.PlaceVideo>()
    private val recipeNotebook = mutableListOf<FoodVideo.RecipeVideo>()

    override suspend fun addPlace(place: FoodVideo.PlaceVideo) {
        placeNotebook.add(place)
    }

    override suspend fun addRecipe(recipe: FoodVideo.RecipeVideo) {
        recipeNotebook.add(recipe)
    }

    override suspend fun getPlaces(): List<FoodVideo.PlaceVideo> {
//        return placeNotebook.toList()
        return listOf(
            FoodVideo.PlaceVideo(
                meta = VideoMeta("fake.url.place", title = "Best Sushi in Town", author = "@foodie"),
                placeName = "Sushi Zen",
                address = "123 Tokyo Street",
                cuisineType = "Japanese",
                distance = 2.5
            )
        )
    }

    override suspend fun getRecipes(): List<FoodVideo.RecipeVideo> {
//        return recipeNotebook.toList()
        return listOf(
            FoodVideo.RecipeVideo(
                meta = VideoMeta("fake.url.recipe", title = "How to make Ramen", author = "@chef"),
                recipeName = "Ramen",
                ingredients = listOf("Noodles", "Broth", "Egg", "Pork"),
                steps = listOf("Boil noodles", "Prepare broth", "Add toppings", "Serve hot")
            )
        )
    }

    override suspend fun updatePlace(place: FoodVideo.PlaceVideo) {
        val index = placeNotebook.indexOfFirst { it.meta.url == place.meta.url }
        if (index >= 0) {
            placeNotebook[index] = place
        }
    }

    override suspend fun updateRecipe(recipe: FoodVideo.RecipeVideo) {
        val index = recipeNotebook.indexOfFirst { it.meta.url == recipe.meta.url }
        if (index >= 0) {
            recipeNotebook[index] = recipe
        }
    }
}
