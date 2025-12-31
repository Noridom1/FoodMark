package com.example.cs426_mobileproject.recommendation.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Recommendation (
    val id: String? = null,
    val from_user_id: String,
    val to_user_id: String,
    val store_user_id: String,
    val store_id: String,
    val note: String? = null,
    val created_at: String? = null,
)
