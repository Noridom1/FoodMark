package com.example.foodmark.core.domain.repository

import com.example.foodmark.core.domain.model.Dishes
import com.example.foodmark.core.domain.model.FoodStore
import com.example.foodmark.core.domain.model.RepoResult
import com.example.foodmark.geo.domain.model.GeoPoint
import java.io.File

interface FoodStoreRepository {
    suspend fun getUserFoodStores(user_id: String): List<FoodStore>?
    suspend fun getNearbyFoodStores(user_id: String, location: GeoPoint, radiusMeters: Int = 2000): List<FoodStore>?
    suspend fun getDishes(user_id: String, store_id: String): List<Dishes>?
//    suspend fun getUntriedPlaces(): List<FoodStore>
//    suspend fun getFavoritePlaces(): List<FoodStore>
//    suspend fun toggleFavorite(placeId: String): FoodStore?

    suspend fun addFoodStore(user_id: String, store: FoodStore): RepoResult<FoodStore>
    suspend fun getFoodStoreById(userId: String, storeId: String): FoodStore?
    suspend fun setFoodStoreFavorite(userId: String, storeId: String, favorite: Boolean)

    suspend fun getDishById(userId: String, storeId: String, dishId: Int): Dishes?
    suspend fun setDishFavorite(userId: String, storeId: String, dishId: Int, favorite: Boolean)

    suspend fun updateFoodStore(userId: String, store: FoodStore)
    suspend fun updateDish(userId: String, storeId: String, dish: Dishes)

    suspend fun addFoodStoreImage(userId: String, storeId: String, file: File): RepoResult<Unit>
    suspend fun addDishImage(userId: String, storeId: String, dishId: Int, file: File): RepoResult<Unit>

}