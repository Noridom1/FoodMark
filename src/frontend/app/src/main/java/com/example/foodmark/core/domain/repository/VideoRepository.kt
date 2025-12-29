package com.example.foodmark.core.domain.repository

import com.example.foodmark.core.domain.model.FoodVideo

interface VideoRepository {
    /**
     * Process a TikTok video URL.
     * Returns a FoodVideo if it’s about food (Place or Recipe),
     * otherwise returns null.
     */
    suspend fun processVideo(url: String): FoodVideo?
}