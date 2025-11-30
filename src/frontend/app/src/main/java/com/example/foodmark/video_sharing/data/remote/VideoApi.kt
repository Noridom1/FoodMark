package com.example.foodmark.video_sharing.data.remote

import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface VideoApi {
    @FormUrlEncoded
    @POST("video/add_user_video")
    suspend fun addUserVideo(
        @Field("url") url: String,
        @Field("user_id") userId: String
    ): VideoResponse
}