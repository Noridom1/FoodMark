package com.example.cs426_mobileproject.friend.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Friend (
    val friend_id: String,
    val friend_name: String,
    val img_url: String? = null
)
