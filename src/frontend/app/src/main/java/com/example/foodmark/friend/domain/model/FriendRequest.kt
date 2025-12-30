package com.example.cs426_mobileproject.friend.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class FriendRequest(
    val id: String,
    val from_user_id: String,
    val to_user_id: String,
    val status: String,
    val created_at: String
)