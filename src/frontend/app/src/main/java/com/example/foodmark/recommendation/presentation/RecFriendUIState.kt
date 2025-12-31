package com.example.cs426_mobileproject.recommendation.presentation

import com.example.cs426_mobileproject.friend.domain.model.Friend

data class FriendRecUI(
    val friend: Friend,
    val isRecommended: Boolean = false,
    val isLoading: Boolean = false
)

data class RecFriendUIState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val friends: List<FriendRecUI> = emptyList()
)