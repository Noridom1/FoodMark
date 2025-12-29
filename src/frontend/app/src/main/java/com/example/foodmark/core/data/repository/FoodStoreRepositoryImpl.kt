package com.example.foodmark.core.data.repository

import android.util.Log
import com.example.foodmark.core.domain.model.Dishes
import com.example.foodmark.core.domain.model.FoodStore
import com.example.foodmark.core.domain.model.RepoResult
import com.example.foodmark.geo.domain.model.GeoPoint
import com.example.foodmark.core.domain.repository.FoodStoreRepository
import com.example.foodmark.core.supabase.SupabaseClientProvider
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Columns.Companion.raw
import io.github.jan.supabase.storage.storage
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject
import org.slf4j.MDC.put
import java.io.File
import javax.inject.Inject

class FoodStoreRepositoryImpl @Inject constructor(
) : FoodStoreRepository {

    private val client : SupabaseClient = SupabaseClientProvider.client

    override suspend fun getUserFoodStores(user_id: String): List<FoodStore>? {
        val result = client
            .from("FoodStore")
            .select(
                columns = raw(
                    """
                user_id,
                id,
                created_at,
                name,
                location,
                address,
                img_url,
                user_note,
                favorite
                """.trimIndent()
                )
            ) {
                filter {
                    eq("user_id", user_id)
                }
            }

        Log.d("FoodStoreRepository", "FoodStores response: ${result.data}")

        return result.decodeList<FoodStore>()
    }

    override suspend fun getNearbyFoodStores(
        user_id: String,
        location: GeoPoint,
        radiusMeters: Int
    ): List<FoodStore>? {
        return try {
            val parameters = buildJsonObject {
                put("p_user_id", JsonPrimitive(user_id))
                put("lat", JsonPrimitive(location.lat))
                put("lng", JsonPrimitive(location.lng))
                put("radius_meters", JsonPrimitive(radiusMeters))
            }

            val result = client.postgrest.rpc(
                function = "get_nearby_food_stores",
                parameters = parameters
            )

            result.decodeList<FoodStore>()
        } catch (e: Exception) {
            Log.e("FoodStoreRepository", "Error fetching nearby food stores", e)
            emptyList()
        }
    }

    override suspend fun getDishes(
        user_id: String,
        store_id: String
    ): List<Dishes>? {
        val result = client
            .from("Dishes")
            .select(
                columns = raw(
                    """
                user_id,
                store_id,
                dish_id,
                name,
                price,
                created_at,
                img_url,
                favorite
                """.trimIndent()
                )
            ) {
                filter {
                    eq("user_id", user_id)
                    eq("store_id", store_id)
                }
            }

        Log.d("FoodStoreRepository", "GetDishes response: ${result.data}")

        return result.decodeList<Dishes>()
    }

    override suspend fun addFoodStore(
        user_id: String,
        store: FoodStore,
    ): RepoResult<FoodStore> {
        return try {
            // Build JSON parameters for FoodStore insert
            val parameters = buildJsonObject {
                put("p_address", JsonPrimitive(store.address ?: ""))
                put("p_img_url", JsonPrimitive(store.img_url ?: ""))
                put("p_name", JsonPrimitive(store.name))
                put("p_user_note", JsonPrimitive(store.user_note))
                put("p_user_id", JsonPrimitive(user_id))

                put("p_lat", JsonPrimitive(store.location?.lat ?: 0.0))
                put("p_lng", JsonPrimitive(store.location?.lng ?: 0.0))
            }

            // Insert FoodStore
            val res = client.postgrest.rpc(
                function = "insert_foodstore",
                parameters = parameters
            )
            Log.d("FoodStoreRepository", "AddFoodStore response: ${res.data}")

            val newStore = res.decodeSingleOrNull<FoodStore>()
                ?: return RepoResult.Error("Failed to add food store")

            Log.d("FoodStoreRepository", "AddFoodStore newStore: ${newStore.id}, $user_id")
            val dishes = getDishes(store.user_id, store.id)
                ?: return RepoResult.Error("Failed to get dishes for the food store")

            // Insert dishes (if any provided)
            if (dishes.isNotEmpty()) {
                val dishRows = dishes.map { dish ->
                    Dishes(
                        user_id = user_id,
                        store_id = newStore.id,
                        name = dish.name,
                        price = dish.price,
                        img_url = dish.img_url,
                    )
                }
                val result = client.from("Dishes").insert(dishRows)
                Log.d("FoodStoreRepository", "AddDishes response: ${result.data}")

            }

            RepoResult.Success(newStore)

        } catch (e: Exception) {
            e.printStackTrace()
            RepoResult.Error("Error adding food store: ${e.message}")
        }
    }



    // from this on is new by lhuy

    override suspend fun getFoodStoreById(user_id: String, storeId: String): FoodStore? {
        return try {
            val result = client
                .from("FoodStore")
                .select(
                    columns = raw("""
                    user_id,
                    id,
                    created_at,
                    name,
                    location,
                    address,
                    img_url,
                    user_note,
                    favorite
                """.trimIndent())
                ) {
                    filter {
                        eq("user_id", user_id)
                        eq("id", storeId)
                    }
                    limit(1)
                }

            Log.d("FoodStoreRepository", "getFoodStoreById response: ${result.data}")
            result.decodeList<FoodStore>().firstOrNull()
        } catch (e: Exception) {
            Log.e("FoodStoreRepository", "Error getFoodStoreById(user_id=$user_id, storeId=$storeId)", e)
            null
        }
    }

    override suspend fun setFoodStoreFavorite(user_id: String, storeId: String, favorite: Boolean) {
        try {
            val payload = buildJsonObject {
                put("favorite", JsonPrimitive(favorite))
            }

            client
                .from("FoodStore")
                .update(payload) {
                    filter {
                        eq("user_id", user_id)
                        eq("id", storeId)
                    }
                }

            Log.d("FoodStoreRepository", "setFoodStoreFavorite storeId=$storeId -> $favorite")
        } catch (e: Exception) {
            Log.e("FoodStoreRepository", "Error setFoodStoreFavorite(user_id=$user_id, storeId=$storeId, favorite=$favorite)", e)
            throw e
        }
    }

    override suspend fun getDishById(user_id: String, store_id: String, dishId: Int): Dishes? {
        return try {
            val result = client
                .from("Dishes") // adjust if your table name differs
                .select(
                    columns = raw("""
                    user_id,
                    store_id,
                    dish_id,
                    created_at,
                    name,
                    price,
                    img_url
                """.trimIndent())
                ) {
                    filter {
                        eq("user_id", user_id)
                        eq("store_id", store_id)
                        eq("dish_id", dishId)
                    }
                    limit(1)
                }

            Log.d("FoodStoreRepository", "getDishById response: ${result.data}")
            result.decodeList<Dishes>().firstOrNull()
        } catch (e: Exception) {
            Log.e("FoodStoreRepository", "Error getDishById(user_id=$user_id, store_id=$store_id, dishId=$dishId)", e)
            null
        }
    }

    override suspend fun setDishFavorite(user_id: String, store_id: String, dishId: Int, favorite: Boolean) {
        try {
            val payload = buildJsonObject {
                put("favorite", JsonPrimitive(favorite))
            }

            client
                .from("Dishes") // adjust if your table name differs
                .update(payload) {
                    filter {
                        eq("user_id", user_id)
                        eq("store_id", store_id)
                        eq("dish_id", dishId)
                    }
                }

            Log.d("FoodStoreRepository", "setDishFavorite store_id=$store_id dishId=$dishId -> $favorite")
        } catch (e: Exception) {
            Log.e("FoodStoreRepository", "Error setDishFavorite(user_id=$user_id, store_id=$store_id, dishId=$dishId, favorite=$favorite)", e)
            throw e
        }
    }

    // 3) New: update a FoodStore (name, address, img_url, user_note, favorite)
    override suspend fun updateFoodStore(user_id: String, store: FoodStore) {
        try {
            val payload = buildJsonObject {
                put("name", JsonPrimitive(store.name))
                // allow nulls to clear fields server-side
                put("address", store.address?.let(::JsonPrimitive) ?: JsonNull)
                put("img_url", store.img_url?.let(::JsonPrimitive) ?: JsonNull)
                put("user_note", JsonPrimitive(store.user_note))
                put("favorite", JsonPrimitive(store.favorite))
            }

            client
                .from("FoodStore")
                .update(payload) {
                    filter {
                        eq("user_id", user_id)
                        eq("id", store.id)
                    }
                }

            Log.d("FoodStoreRepository", "updateFoodStore id=${store.id} OK")
        } catch (e: Exception) {
            Log.e("FoodStoreRepository", "Error updateFoodStore(user_id=$user_id, id=${store.id})", e)
            throw e
        }
    }

    // 4) New: update a Dish (name, price, img_url, favorite)
    override suspend fun updateDish(user_id: String, storeId: String, dish: Dishes) {
        try {
            val payload = buildJsonObject {
                put("name", JsonPrimitive(dish.name))
                put("price", JsonPrimitive(dish.price))
                // allow blank to clear if you decide to send nulls; here we keep provided string
                put("img_url", JsonPrimitive(dish.img_url))
                put("favorite", JsonPrimitive(dish.favorite))
            }

            client
                .from("Dishes")
                .update(payload) {
                    filter {
                        eq("user_id", user_id)
                        eq("store_id", storeId)
                        eq("dish_id", dish.dish_id)
                    }
                }

            Log.d("FoodStoreRepository", "updateDish storeId=$storeId dishId=${dish.dish_id} OK")
        } catch (e: Exception) {
            Log.e("FoodStoreRepository", "Error updateDish(user_id=$user_id, storeId=$storeId, dishId=${dish.dish_id})", e)
            throw e
        }
    }

    override suspend fun addFoodStoreImage(
        userId: String,
        storeId: String,
        file: File
    ): RepoResult<Unit> {
        return try {
            // 1. Upload to Supabase storage bucket
            val filePath = "$userId/$storeId/${file.name}" // nice structured path
            val bytes = file.readBytes()

            client.storage
                .from("imagebucket")
                .upload (
                    path = filePath,
                    data = bytes
                )

            // 2. Get public URL
            val publicUrl = client.storage
                .from("imagebucket")
                .publicUrl(filePath)

            // 3. Update FoodStore table with new URL
            client.from("FoodStore")
                .update(
                    mapOf("img_url" to publicUrl)
                ) {
                    filter {
                        eq("user_id", userId)
                        eq("id", storeId)
                    }
                }
            Log.d("FoodStoreRepository", "Image uploaded for (userId: $userId, storeId: $storeId): $publicUrl")
            RepoResult.Success(Unit)
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e("FoodStoreRepository", "Failed to upload food store image: ${e.message}")
            RepoResult.Error(e.message ?: "Unknown error")
        }
    }

    override suspend fun addDishImage(
        userId: String,
        storeId: String,
        dishId: Int,
        file: File
    ): RepoResult<Unit> {
        return try {
            // 1. Upload to Supabase storage bucket
            val filePath = "$userId/$storeId/$dishId/${file.name}" // nice structured path
            val bytes = file.readBytes()

            client.storage
                .from("imagebucket")
                .upload (
                    path = filePath,
                    data = bytes
                )

            // 2. Get public URL
            val publicUrl = client.storage
                .from("imagebucket")
                .publicUrl(filePath)

            // 3. Update Dish table with new URL
            client.from("Dishes")
                .update(
                    mapOf("img_url" to publicUrl)
                ) {
                    filter {
                        eq("user_id", userId)
                        eq("store_id", storeId)
                        eq("dish_id", dishId)
                    }
                }
            Log.d("FoodStoreRepository", "Image uploaded for (userId: $userId, storeId: $storeId, dishId: $dishId): $publicUrl")
            RepoResult.Success(Unit)
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e("FoodStoreRepository", "Failed to upload dish image: ${e.message}")
            RepoResult.Error(e.message ?: "Unknown error")
        }
    }

}