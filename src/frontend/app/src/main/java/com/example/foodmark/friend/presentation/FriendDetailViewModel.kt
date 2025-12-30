package com.example.cs426_mobileproject.friend.presentation

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cs426_mobileproject.auth.domain.use_cases.GetLoggedInUserUseCase
import com.example.cs426_mobileproject.core.domain.model.RepoResult
import com.example.cs426_mobileproject.core.domain.usecase.GetProfileByID
import com.example.cs426_mobileproject.core.domain.usecase.GetProfileUseCase
import com.example.cs426_mobileproject.friend.domain.usecase.UnfriendUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FriendDetailViewModel @Inject constructor(
    private val getLoggedInUserUseCase: GetLoggedInUserUseCase,
    private val getProfileByID: GetProfileByID,
    private val unfriendUseCase: UnfriendUseCase,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    // Expecting these from Nav arguments (with defaults for dev)
    private val friendId: String = checkNotNull(savedStateHandle["friendId"]) {
        "friendId is required in nav arguments"
    }

    private val _ui = MutableStateFlow(FriendDetailUiState(isLoading = true))
    val ui: StateFlow<FriendDetailUiState> = _ui.asStateFlow()

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            _ui.update { it.copy(isLoading = true, error = null) }
            try {
                when (val res = getProfileByID(friendId)) {
                    is RepoResult.Success -> _ui.update { it.copy(isLoading = false, friendProfile = res.data) }
                    is RepoResult.Error -> _ui.update { it.copy(isLoading = false, error = res.message ?: "Failed to load friend") }
                }
            } catch (t: Throwable) {
                _ui.update { it.copy(isLoading = false, error = t.message ?: "Failed to load friend") }
            }
        }
    }

    fun unfriend(onSuccess: () -> Unit = {}) {
        viewModelScope.launch {
            _ui.update { it.copy(isUnfriending = true, error = null) }
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

                when (val res = unfriendUseCase(userId, friendId)) {
                    is RepoResult.Success -> {
                        _ui.update { it.copy(isUnfriending = false) }
                        onSuccess()
                    }
                    is RepoResult.Error -> {
                        _ui.update { it.copy(isUnfriending = false, error = res.message ?: "Failed to unfriend") }
                    }
                }
            } catch (t: Throwable) {
                _ui.update { it.copy(isUnfriending = false, error = t.message ?: "Failed to unfriend") }
            }
        }
    }
}
