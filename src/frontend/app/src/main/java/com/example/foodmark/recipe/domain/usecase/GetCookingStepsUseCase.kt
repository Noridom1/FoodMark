package com.example.foodmark.recipe.domain.usecase

import com.example.foodmark.core.domain.model.RepoResult
import com.example.foodmark.recipe.domain.model.CookingStep
import com.example.foodmark.recipe.domain.repo.RecipeRepository
import javax.inject.Inject

class GetCookingStepsUseCase @Inject constructor(
    private val recipeRepository: RecipeRepository
) {
    suspend operator fun invoke(userId: String, recipeId: String): RepoResult<List<CookingStep>> {
        return recipeRepository.getCookingSteps(userId, recipeId)
    }
}
