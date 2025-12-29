package com.example.foodmark.core.presentation.foodstore.dishdetail

import android.net.Uri
import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foodmark.auth.domain.use_cases.GetLoggedInUserUseCase
import com.example.foodmark.core.domain.model.RepoResult
import com.example.foodmark.core.domain.usecase.foodstore.AddDishImageUseCase
import com.example.foodmark.core.domain.usecase.foodstore.GetDishByIdUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DishDetailViewModel @Inject constructor(
    private val getLoggedInUserUseCase: GetLoggedInUserUseCase,
    private val getDishByIdUseCase: GetDishByIdUseCase,
    private val addDishImageUseCase: AddDishImageUseCase,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val storeId: String = checkNotNull(savedStateHandle["storeId"])
    private val dishId: Int = checkNotNull(savedStateHandle.get<Int>("dishId"))

    private val _uiState = MutableStateFlow(DishDetailUiState(isLoading = true))
    val uiState: StateFlow<DishDetailUiState> = _uiState.asStateFlow()

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                val user = getLoggedInUserUseCase()
                if (user == null) {
                    _uiState.update {
                        it.copy(
                            error = "User not logged in"
                        )
                    }
                    Log.d("FoodTourViewModel", "User not logged in")
                    return@launch

                }

                val userId = user.id

                val dish = getDishByIdUseCase(userId, storeId, dishId)
                _uiState.update { it.copy(isLoading = false, dish = dish) }
            } catch (t: Throwable) {
                _uiState.update { it.copy(isLoading = false, error = t.message) }
            }
        }
    }

    suspend fun addDishImage(uri: Uri) : Boolean {
        val user = getLoggedInUserUseCase()
        if (user == null) {
            Log.d("DishDetailViewModel", "addDishImage: User not logged in")
            return false
        }
        val uploadRes = addDishImageUseCase(user.id, storeId, dishId, uri)
        if (uploadRes is RepoResult.Success) {
            Log.d("DishDetailViewModel", "addDishImage: Upload success")
            return true
        }
        else {
            Log.d("DishDetailViewModel", "addDishImage: Upload failed: $uploadRes")
            return false
        }
    }
}