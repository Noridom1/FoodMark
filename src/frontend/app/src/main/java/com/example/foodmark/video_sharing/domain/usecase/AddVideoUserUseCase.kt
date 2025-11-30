package com.example.foodmark.video_sharing.domain.usecase

import com.example.foodmark.video_sharing.data.remote.VideoResponse
import com.example.foodmark.video_sharing.domain.repository.VideoRepository
import javax.inject.Inject

class AddVideoUserUseCase @Inject constructor(
    private val videoRepository: VideoRepository
){
    suspend operator fun invoke(url: String, userId: String) : VideoResponse {
        return videoRepository.addVideoUser(url, userId)
    }
}