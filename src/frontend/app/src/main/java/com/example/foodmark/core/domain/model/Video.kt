package com.example.foodmark.core.domain.model

data class VideoMeta(
    val url: String,
    val title: String? = null,
    val author: String? = null,
    val thumbnailUrl: String? = null
)

sealed class FoodVideo(
    open val meta: VideoMeta,
    open val isFavorite: Boolean = false,
    open val isTried: Boolean = false,
    open val addedAt: Long = System.currentTimeMillis()
) {
    data class PlaceVideo(
        override val meta: VideoMeta,
        val placeName: String,
        val address: String?,
        val cuisineType: String?,
        val distance: Double? = null, // in kilometers
        override val isFavorite: Boolean = false,
        override val isTried: Boolean = false,
        override val addedAt: Long = System.currentTimeMillis()
    ) : FoodVideo(meta, isFavorite, isTried, addedAt)

    data class RecipeVideo(
        override val meta: VideoMeta,
        val recipeName: String,
        val ingredients: List<String>,
        val steps: List<String>,
        override val isFavorite: Boolean = false,
        override val isTried: Boolean = false,
        override val addedAt: Long = System.currentTimeMillis()
    ) : FoodVideo(meta, isFavorite, isTried, addedAt)
}

