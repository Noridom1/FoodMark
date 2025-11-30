package com.example.foodmark.video_sharing.data.remote

data class VideoResponse (
    val status: String,
    val data: VideoData
)

data class VideoData(
    val video_url: String,
    val user_id: String
)
