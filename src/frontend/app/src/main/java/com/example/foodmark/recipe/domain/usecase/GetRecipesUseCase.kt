package com.example.foodmark.recipe.domain.usecase

import com.example.foodmark.recipe.domain.repo.RecipeRepository
import javax.inject.Inject

class GetRecipesUseCase @Inject constructor(
    private val recipeRepository: RecipeRepository
) {
    suspend operator fun invoke(userId: String) = recipeRepository.getRecipes(userId)

}