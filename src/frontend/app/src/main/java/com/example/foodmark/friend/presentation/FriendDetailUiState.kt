package com.example.cs426_mobileproject.friend.presentation

import com.example.cs426_mobileproject.core.domain.model.Profile

data class FriendDetailUiState(
    val isLoading: Boolean = false,
    val isUnfriending: Boolean = false,
    val error: String? = null,
    val friendProfile: Profile? = null
)
