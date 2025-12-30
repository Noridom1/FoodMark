package com.example.cs426_mobileproject.friend.presentation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cs426_mobileproject.core.domain.model.Profile
import com.example.cs426_mobileproject.core.domain.model.RepoResult
import com.example.cs426_mobileproject.core.domain.usecase.GetProfileUseCase
import com.example.cs426_mobileproject.friend.domain.model.Friend
import com.example.cs426_mobileproject.friend.domain.usecase.GetFriendsOfUserUseCase
import com.example.cs426_mobileproject.friend.domain.usecase.SendFriendRequestByEmailUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FriendViewModel @Inject constructor(
    private val getFriendsOfUserUseCase: GetFriendsOfUserUseCase,
    private val sendFriendRequestByEmailUseCase: SendFriendRequestByEmailUseCase,
    private val getCurrentUserUseCase: GetProfileUseCase,
    private val savedStateHandle: SavedStateHandle

) : ViewModel() {

    // Replace with your real user id source
    private var userId: String = savedStateHandle["userId"]
        ?: "4d91711f-cb02-4bfc-bc7a-2d7e4c0fd64e"

    private val _uiState = MutableStateFlow(FriendUIState(isLoading = true))
    val uiState: StateFlow<FriendUIState> = _uiState.asStateFlow()

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                val currentUser = getCurrentUserUseCase()
                if (currentUser is RepoResult.Success) {
                    userId = currentUser.data.id
                    val friends = getFriendsOfUserUseCase(userId)
                    _uiState.update { it.copy(isLoading = false, friends = friends) }

                }
                else {
                    _uiState.update { it.copy(isLoading = false, error = "Failed to get current user") }
                    return@launch
                }
            } catch (t: Throwable) {
                _uiState.update { it.copy(isLoading = false, error = t.message) }
            }
        }
    }

    suspend fun getFriendsOfUser(user_id: String) : List<Friend> =
        getFriendsOfUserUseCase(user_id)

    suspend fun addFriend(email: String) : RepoResult<Unit> =
        sendFriendRequestByEmailUseCase(userId, email)

}