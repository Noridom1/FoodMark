package com.example.foodmark.recipe.presentation

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foodmark.auth.domain.use_cases.GetLoggedInUserUseCase
import com.example.foodmark.core.domain.model.RepoResult
import com.example.foodmark.core.domain.usecase.GetProfileByID
import com.example.foodmark.core.domain.usecase.GetProfileUseCase
import com.example.foodmark.recipe.domain.usecase.GetRecipeByIdUseCase
import com.example.foodmark.recipe.domain.usecase.GetRecipesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RecipeViewModel @Inject constructor(
    private val getCurrentUserUseCase: GetProfileUseCase,
    private val getLoggedInUserUseCase: GetLoggedInUserUseCase,
    private val getUserByIdUseCase: GetProfileByID,
    private val getRecipesUseCase: GetRecipesUseCase,
    private val getRecipeByIdUseCase: GetRecipeByIdUseCase

) : ViewModel() {
    private val _uiState = MutableStateFlow(RecipeUiState(isLoading = true))
    val uiState: StateFlow<RecipeUiState> = _uiState.asStateFlow()

    init {
        loadRecipes()
    }

    fun loadRecipes() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                val currentUser = getLoggedInUserUseCase()
//                val currentUser = getUserByIdUseCase("cbcf5839-9c3f-499a-b4a6-3302f734776c")

                if (currentUser != null) {
                    val recipes = getRecipesUseCase(currentUser.id)
                    if (recipes is RepoResult.Success) {
                        _uiState.value = _uiState.value.copy(recipes = recipes.data, isLoading = false)
                        Log.d("RecipeViewModel", "Recipes loaded successfully: ${recipes.data}")
                    }

                    else {
                        Log.d("RecipeViewModel", "Failed to get recipes")
                        return@launch
                    }
                } else {
                    Log.d("RecipeViewModel", "Failed to get current user")
                    return@launch
                }
            } catch (t: Throwable) {
                _uiState.value = _uiState.value.copy(error = t.message, isLoading = false)
            }
        }
    }

    fun onViewRecipe(recipeId: Int) {

    }

}