package com.example.foodmark.core.presentation.foodstore.dishdetail

import android.net.Uri
import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foodmark.auth.domain.use_cases.GetLoggedInUserUseCase
import com.example.foodmark.core.domain.model.Dishes
import com.example.foodmark.core.domain.model.RepoResult
import com.example.foodmark.core.domain.usecase.foodstore.AddDishImageUseCase
import com.example.foodmark.core.domain.usecase.foodstore.GetDishByIdUseCase
import com.example.foodmark.core.domain.usecase.foodstore.UpdateDishUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditDishViewModel @Inject constructor(
    private val getLoggedInUserUseCase: GetLoggedInUserUseCase,
    private val getDishByIdUseCase: GetDishByIdUseCase,
    private val updateDishUseCase: UpdateDishUseCase,
    private val addDishImageUseCase: AddDishImageUseCase,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val storeId: String = checkNotNull(savedStateHandle["storeId"])
    private val dishId: Int = checkNotNull(savedStateHandle.get<Int>("dishId"))

    data class UiState(
        val isLoading: Boolean = true,
        val isSaving: Boolean = false,
        val error: String? = null,
        val original: Dishes? = null,

        val name: String = "",
        val priceText: String = "",
        val imgUrl: String = "",
        val favorite: Boolean = false,
        val priceError: String? = null
    ) {
        val price: Int? get() = priceText.toIntOrNull()
        val canSave: Boolean get() =
            !isLoading && !isSaving && name.isNotBlank() && priceError == null && price != null
        val isDirty: Boolean get() =
            original?.let {
                it.name != name ||
                        it.price.toString() != priceText ||
                        it.img_url != imgUrl ||
                        it.favorite != favorite
            } ?: false
    }

    private val _ui = MutableStateFlow(UiState())
    val ui: StateFlow<UiState> = _ui.asStateFlow()

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            _ui.update { it.copy(isLoading = true, error = null) }
            try {
                val user = getLoggedInUserUseCase()
                if (user == null) {
                    _ui.update {
                        it.copy(
                            error = "User not logged in"
                        )
                    }
                    Log.d("FoodTourViewModel", "User not logged in")
                    return@launch

                }

                val userId = user.id

                val dish = getDishByIdUseCase(userId, storeId, dishId)
                if (dish == null) {
                    _ui.update { it.copy(isLoading = false, error = "Dish not found") }
                } else {
                    _ui.update {
                        it.copy(
                            isLoading = false,
                            original = dish,
                            name = dish.name,
                            priceText = dish.price.toString(),
                            imgUrl = dish.img_url.orEmpty(),
                            favorite = dish.favorite
                        )
                    }
                }
            } catch (t: Throwable) {
                _ui.update { it.copy(isLoading = false, error = t.message) }
            }
        }
    }

    fun onNameChange(v: String) = _ui.update { it.copy(name = v) }
    fun onImgUrlChange(v: String) = _ui.update { it.copy(imgUrl = v) }
    fun onFavoriteChange(v: Boolean) = _ui.update { it.copy(favorite = v) }
    fun onPriceChange(v: String) {
        val clean = v.filter { it.isDigit() }
        val error = when {
            clean.isEmpty() -> "Price is required"
            clean.toLongOrNull() == null -> "Invalid number"
            clean.toLong() > Int.MAX_VALUE -> "Too large"
            else -> null
        }
        _ui.update { it.copy(priceText = clean, priceError = error) }
    }

    suspend fun save(): Boolean {
        val state = _ui.value
        val original = state.original ?: return false
        val price = state.price ?: return false
        if (!state.canSave || !state.isDirty) return false

        _ui.update { it.copy(isSaving = true, error = null) }
        return try {
            val user = getLoggedInUserUseCase()
            if (user == null) {
                _ui.update {
                    it.copy(
                        error = "User not logged in"
                    )
                }
                Log.d("FoodTourViewModel", "User not logged in")
            }

            val userId = user!!.id


            val updated = original.copy(
                name = state.name.trim(),
                price = price,
                img_url = state.imgUrl.trim(),
                favorite = state.favorite
            )
            updateDishUseCase(userId, storeId, updated)
            _ui.update { it.copy(isSaving = false, original = updated) }
            true
        } catch (t: Throwable) {
            _ui.update { it.copy(isSaving = false, error = t.message) }
            false
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