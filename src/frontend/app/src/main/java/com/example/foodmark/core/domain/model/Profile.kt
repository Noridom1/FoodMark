package com.example.foodmark.core.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.util.Date

@Serializable
data class Profile(
    @SerialName("id")
    val id: String,

    @SerialName("name")
    val name: String? = null,

    @SerialName("email")
    val email: String? = null,

    @SerialName("phone")
    val phone: String? = null,

    @SerialName("img_url")
    val img_url: String? = null,

    @SerialName("dob")
    val dob: String? = null  // e.g. "1999-05-18"
)