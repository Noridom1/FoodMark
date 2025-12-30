package com.example.cs426_mobileproject.friend.presentation.friend_request

import com.example.cs426_mobileproject.core.domain.model.Profile
import com.example.cs426_mobileproject.friend.domain.model.FriendRequest

data class FriendRequestUiState (
    val isLoading: Boolean = false,
    val error: String? = null,
    val friendRequestId: String? = "",
    val friendProfile: Profile? = null
)
