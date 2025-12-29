package com.example.foodmark.foodtour.data.remote

import com.example.foodmark.foodtour.domain.model.DestinationNode
import com.example.foodmark.foodtour.domain.model.RouteRecommendation
import com.example.foodmark.video_sharing.data.remote.VideoResponse
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST

interface FoodTourAPI {
    @FormUrlEncoded
    @POST("foodtour/recommend")
    suspend fun getRecommend(
        @Field("user_id") userID: String,
        @Field("user_lat") lat: Double,
        @Field("user_lng") lng: Double
    ): RouteRecommendation

    @POST("foodtour/route")
    suspend fun getRouteDirection(
        @Body points: List<DestinationNode>
    ) : List<DestinationNode>
}