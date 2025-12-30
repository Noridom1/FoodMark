package com.example.cs426_mobileproject.friend.presentation.friend_request

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cs426_mobileproject.core.domain.model.Profile
import com.example.cs426_mobileproject.core.domain.model.RepoResult
import com.example.cs426_mobileproject.core.domain.usecase.GetProfileByID
import com.example.cs426_mobileproject.core.domain.usecase.GetProfileUseCase
import com.example.cs426_mobileproject.friend.domain.usecase.GetFriendRequestByIdUseCase
import com.example.cs426_mobileproject.friend.domain.usecase.GetFriendsOfUserUseCase
import com.example.cs426_mobileproject.friend.domain.usecase.RemoveFriendRequestUseCase
import com.example.cs426_mobileproject.friend.domain.usecase.SendFriendRequestByEmailUseCase
import com.example.cs426_mobileproject.friend.domain.usecase.SetFriendShipUseCase
import com.example.cs426_mobileproject.recommendation.presentation.RecFriendUIState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FriendRequestViewModel @Inject constructor(
    private val setFriendShipUseCase: SetFriendShipUseCase,
    private val getFriendRequestByIdUseCase: GetFriendRequestByIdUseCase,
    private val getProfileByIdUseCase: GetProfileByID,
    private val getCurrentUserUseCase: GetProfileUseCase,
    private val removeFriendRequestUseCase: RemoveFriendRequestUseCase,
    private val savedStateHandle: SavedStateHandle
) : ViewModel () {

    private val friendRequestId: String = savedStateHandle["friendRequestId"]?: ""

    private var userId = ""
    private var friendUserId = ""

    private val _uiState = MutableStateFlow(FriendRequestUiState(isLoading = true))
    val uiState: StateFlow<FriendRequestUiState> = _uiState.asStateFlow()

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                val currentUser = getCurrentUserUseCase()

                if (currentUser !is RepoResult.Success) {
                    _uiState.update { it.copy(isLoading = false, error = "Current user not found") }
                    Log.d("FriendRequestViewModel", "refresh: Current user not found")
                    return@launch
                }

                userId = currentUser.data.id

                val friendRequest = getFriendRequestByIdUseCase(friendRequestId)

                if (friendRequest == null || friendRequest.to_user_id != userId) {
                    _uiState.update { it.copy(isLoading = false, error = "Friend request not found") }
                    Log.d("FriendRequestViewModel", "refresh: Friend request not found (from_user_id=${friendRequest?.from_user_id}, userId=$userId)")
                    return@launch
                }

                friendUserId = friendRequest.from_user_id

                val friendProfile = getProfileByIdUseCase(friendUserId)
                if (friendProfile is RepoResult.Success) {
                    _uiState.update { state ->
                        state.copy(
                            isLoading = false,
                            error = null,
                            friendProfile = friendProfile.data
                        )
                    }
                }
                else {
                    _uiState.update { it.copy(isLoading = false, error = "Friend profile not found") }
                    Log.d("FriendRequestViewModel", "refresh: Friend profile not found")
                    return@launch
                }


            } catch (t: Throwable) {
                _uiState.update { it.copy(isLoading = false, error = t.message) }
                Log.d("FriendRequestViewModel", "refresh: ${t.message}")
            }
        }
    }

    suspend fun setFriendShip() {
        val result = setFriendShipUseCase(friendUserId, userId)
        if (result is RepoResult.Error) {
            _uiState.update { it.copy(error = result.message) }
            Log.d("FriendRequestViewModel", "setFriendShip: ${result.message}")
        }
        else {
            _uiState.update { it.copy(error = null) }
            Log.d("FriendRequestViewModel", "setFriendShip: Success")
        }
    }

    suspend fun removeFriendRequest() {
        val result = removeFriendRequestUseCase(friendRequestId)
        if (result is RepoResult.Error) {
            _uiState.update { it.copy(error = result.message) }
            Log.d("FriendRequestViewModel", "removeFriendRequest: ${result.message}")
        }
        else {
            _uiState.update { it.copy(error = null) }
            Log.d("FriendRequestViewModel", "removeFriendRequest: Success")
        }
    }



}