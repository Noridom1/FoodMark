package com.example.foodmark.recipe.domain.repo
import com.example.foodmark.core.domain.model.RepoResult
import com.example.foodmark.recipe.domain.model.CookingStep
import com.example.foodmark.recipe.domain.model.Recipe
import java.io.File

interface RecipeRepository {
    suspend fun getRecipes(userId: String): RepoResult<List<Recipe>>
    suspend fun addRecipeImage(userId: String, recipeId: String, file: File): RepoResult<Unit>
    suspend fun getUntriedRecipe(userId: String): RepoResult<List<Recipe>>

    suspend fun getCookingSteps(userId: String, recipeId: String): RepoResult<List<CookingStep>>
}