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
        val resolvedUri = uri
        val file = fileHelper.uriToFile(resolvedUri!!)
            ?: return RepoResult.Success(Unit)
        return recipeRepository.addRecipeImage(recipeId, userId, file)
    }
}