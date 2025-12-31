package com.example.cs426_mobileproject.recommendation.presentation

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cs426_mobileproject.core.domain.model.RepoResult
import com.example.cs426_mobileproject.core.domain.usecase.GetProfileByID
import com.example.cs426_mobileproject.core.domain.usecase.foodstore.GetFoodStoreByIdUseCase
import com.example.cs426_mobileproject.friend.domain.model.Friend
import com.example.cs426_mobileproject.friend.domain.usecase.GetFriendsOfUserUseCase
import com.example.cs426_mobileproject.friend.presentation.FriendUIState
import com.example.cs426_mobileproject.recommendation.domain.usecase.RecommendStoreUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class ViewFriendRecViewModel @Inject constructor(
    private val getFriendsOfUserUseCase: GetFriendsOfUserUseCase,
    private val recommendStoreUseCase: RecommendStoreUseCase,
    private val savedStateHandle: SavedStateHandle,
    private val getProfileByIdUseCase: GetProfileByID,
    private val getFoodStoreByIdUseCase: GetFoodStoreByIdUseCase,

) : ViewModel() {

    // Replace with your real user id source
    private val userId: String = savedStateHandle["userId"]
        ?: "4d91711f-cb02-4bfc-bc7a-2d7e4c0fd64e"

    private val storeId: String = savedStateHandle["storeId"]
        ?: ""

    private var storeName = ""
    private var userName = ""

    private val _uiState = MutableStateFlow(RecFriendUIState(isLoading = true))
    val uiState: StateFlow<RecFriendUIState> = _uiState.asStateFlow()

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                val friends = getFriendsOfUserUseCase(userId)
                _uiState.update { state ->
                    state.copy(
                        isLoading = false,
                        friends = friends.map { FriendRecUI(friend = it) }
                    )
                }
                val currentStore = getFoodStoreByIdUseCase(userId, storeId)
                storeName = currentStore?.name ?: ""

                val currentUser = getProfileByIdUseCase(userId)
                if (currentUser is RepoResult.Success) {
                    userName = currentUser.data.name ?: ""
                }

            } catch (t: Throwable) {
                _uiState.update { it.copy(isLoading = false, error = t.message) }
            }
        }
    }

    suspend fun getFriendsOfUser(user_id: String) : List<Friend> =
        getFriendsOfUserUseCase(user_id)

    fun recommendStore(friendId: String) {
        viewModelScope.launch {
            // Optimistic update: set loading state
            _uiState.update { state ->
                state.copy(
                    friends = state.friends.map {
                        if (it.friend.friend_id == friendId) it.copy(isLoading = true) else it
                    }
                )
            }

            try {
                Log.d("ViewFriendRecViewModel", "recommendStore: from $userId, to $friendId, storeId $storeId")
                recommendStoreUseCase(
                    from_user_id = userId,
                    to_user_id = friendId,
                    store_user_id = userId,
                    store_id = storeId,
                    from_user_name = userName,
                    store_name = storeName,
                    note = ""
                )

                // Success: mark as recommended
                _uiState.update { state ->
                    state.copy(
                        friends = state.friends.map {
                            if (it.friend.friend_id == friendId) it.copy(isRecommended = true, isLoading = false)
                            else it
                        }
                    )
                }
            } catch (t: Throwable) {
                // Reset loading flag on error
                _uiState.update { state ->
                    state.copy(
                        friends = state.friends.map {
                            if (it.friend.friend_id == friendId) it.copy(isLoading = false) else it
                        },
                        error = t.message
                    )
                }
            }
        }
    }

}