package com.example.foodmark.recipe.presentation

import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foodmark.auth.domain.use_cases.GetLoggedInUserUseCase
import com.example.foodmark.core.domain.model.RepoResult
import com.example.foodmark.recipe.domain.usecase.AddRecipeImageUseCase
import com.example.foodmark.recipe.domain.usecase.GetCookingStepsUseCase
import com.example.foodmark.recipe.domain.usecase.GetRecipeByIdUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RecipeDetailViewModel @Inject constructor(
    private val getLoggedInUserUseCase: GetLoggedInUserUseCase,
    private val getRecipeByIdUseCase: GetRecipeByIdUseCase,
    private val getCookingStepsUseCase: GetCookingStepsUseCase,
    private val addRecipeImageUseCase: AddRecipeImageUseCase,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    // recipeId is passed via nav: "recipe/{recipeId}"
    private val recipeIdArg: Int? = savedStateHandle["recipeId"]

    private val _userId = MutableStateFlow<String?>(null)
    private val _uiState = MutableStateFlow(RecipeDetailUiState(isLoading = true))
    val uiState: StateFlow<RecipeDetailUiState> = _uiState.asStateFlow()

    init {
        refreshUserId()
        refresh()
    }

    fun refreshUserId() {
        viewModelScope.launch {
            try {
                _userId.value = getLoggedInUserUseCase()?.id
            } catch (_: Throwable) {
                _userId.value = null
            }
        }
    }

    fun refresh() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                val userId = _userId.value ?: getLoggedInUserUseCase()?.id
                val recipeId = recipeIdArg

                if (userId == null || recipeId == null) {
                    _uiState.update { it.copy(isLoading = false, error = "Missing user or recipe id") }
                    return@launch
                }

                // Load recipe
                val recipeResult = getRecipeByIdUseCase(userId, recipeId.toString())
                if (recipeResult !is RepoResult.Success) {
                    _uiState.update { it.copy(isLoading = false, error = (recipeResult as? RepoResult.Error)?.message ?: "Failed to load recipe") }
                    return@launch
                }

                val recipe = recipeResult.data

                // Load steps
                val stepResult = getCookingStepsUseCase(userId, recipeId.toString())
                val steps = if (stepResult is RepoResult.Success) stepResult.data else emptyList()

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        recipe = recipe,
                        steps = steps,
                        error = null
                    )
                }
            } catch (t: Throwable) {
                _uiState.update { it.copy(isLoading = false, error = t.message) }
            }
        }
    }

    fun addOrChangeImage(uri: Uri) {
        val recipeId = recipeIdArg ?: return
        val userId = _userId.value ?: return

        viewModelScope.launch {
            _uiState.update { it.copy(isUploadingImage = true, error = null) }
            val result = addRecipeImageUseCase(userId, recipeId.toString(), uri)
            if (result is RepoResult.Success) {
                // Re-fetch recipe to get the new image URL
                try {
                    val recipeResult = getRecipeByIdUseCase(userId, recipeId.toString())
                    if (recipeResult is RepoResult.Success) {
                        _uiState.update { it.copy(recipe = recipeResult.data, isUploadingImage = false) }
                    } else {
                        _uiState.update { it.copy(isUploadingImage = false, error = "Image uploaded but failed to reload recipe") }
                    }
                } catch (t: Throwable) {
                    _uiState.update { it.copy(isUploadingImage = false, error = t.message) }
                }
            } else {
                _uiState.update { it.copy(isUploadingImage = false, error = (result as? RepoResult.Error)?.message ?: "Failed to upload image") }
            }
        }
    }
}