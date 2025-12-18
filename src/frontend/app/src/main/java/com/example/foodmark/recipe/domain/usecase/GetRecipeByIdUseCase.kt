package com.example.foodmark.recipe.domain.usecase

import com.example.foodmark.recipe.domain.repo.RecipeRepository
import javax.inject.Inject

class GetRecipeByIdUseCase @Inject constructor(
    private val recipeRepository: RecipeRepository
) {
    suspend operator fun invoke(userId: String, recipeId: String) = recipeRepository.getRecipeById(userId, recipeId)
}