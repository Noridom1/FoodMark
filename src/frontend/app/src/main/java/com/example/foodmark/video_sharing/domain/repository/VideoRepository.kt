package com.example.foodmark.video_sharing.domain.repository

import com.example.foodmark.video_sharing.data.remote.VideoResponse

interface VideoRepository {
    suspend fun addVideoUser(url: String, userId: String): VideoResponse
}