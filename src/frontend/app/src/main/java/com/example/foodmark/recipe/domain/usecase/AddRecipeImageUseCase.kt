package com.example.foodmark.recipe.domain.usecase

import android.net.Uri
import com.example.foodmark.core.domain.model.RepoResult
import com.example.foodmark.recipe.domain.repo.RecipeRepository
import com.example.foodmark.utils.FileHelper
import javax.inject.Inject

class AddRecipeImageUseCase @Inject constructor(
    private val recipeRepository: RecipeRepository,
    private val fileHelper: FileHelper
) {
    suspend operator fun invoke(userId: String, recipeId: String, uri: Uri) : RepoResult<Unit> {
        val file = fileHelper.uriToFile(uri) ?: return RepoResult.Error("File error")
        return recipeRepository.addRecipeImage(userId, recipeId, file)
    }
}