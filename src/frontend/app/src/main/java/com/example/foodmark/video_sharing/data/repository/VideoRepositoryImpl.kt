package com.example.foodmark.video_sharing.data.repository

import android.util.Log
import com.example.cs426_mobileproject.video_sharing.data.remote.VideoApi
import com.example.cs426_mobileproject.video_sharing.data.remote.VideoResponse
import com.example.cs426_mobileproject.video_sharing.domain.repository.VideoRepository
import javax.inject.Inject

class VideoRepositoryImpl @Inject constructor(
    private val videoApi: VideoApi
) : VideoRepository {
    override suspend fun addVideoUser(userId: String, videoId: String) : VideoResponse {
        val result = videoApi.addUserVideo(userId, videoId)
        Log.d("VideoRepositoryImpl", "API Response: $result")
        return result
    }
}