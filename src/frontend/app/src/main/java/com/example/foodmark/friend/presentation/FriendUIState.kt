package com.example.cs426_mobileproject.friend.presentation

import com.example.cs426_mobileproject.core.domain.model.FoodStore
import com.example.cs426_mobileproject.core.presentation.foodstore.storelist.FoodStoreTab
import com.example.cs426_mobileproject.friend.domain.model.Friend

data class FriendUIState(
    val isLoading: Boolean = false,
    val friends: List<Friend> = emptyList(),
    val error: String? = null
)