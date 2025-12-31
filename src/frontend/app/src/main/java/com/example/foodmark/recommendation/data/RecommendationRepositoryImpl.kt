package com.example.cs426_mobileproject.recommendation.data

import android.R.attr.order
import android.util.Log
import com.example.cs426_mobileproject.core.domain.model.RepoResult
import com.example.cs426_mobileproject.core.supabase.SupabaseClientProvider
import com.example.cs426_mobileproject.notification.domain.usecase.AddNotificationUseCase
import com.example.cs426_mobileproject.notification.domain.usecase.RemoveNotificationUseCase
import com.example.cs426_mobileproject.recommendation.domain.model.Recommendation
import com.example.cs426_mobileproject.recommendation.domain.repo.RecommendationRepository
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Order
import javax.inject.Inject

class RecommendationRepositoryImpl @Inject constructor(
    private val addNotificationUseCase: AddNotificationUseCase,
    private val removeNotificationUseCase: RemoveNotificationUseCase
) : RecommendationRepository {

    private val client = SupabaseClientProvider.client

    override suspend fun getRecommendations(user_id: String): List<Recommendation> {
        return try {
            client
                .from("Recommendation")
                .select {
                    filter { eq("to_user_id", user_id) }
                    order("created_at", Order.DESCENDING)
                }
                .decodeList<Recommendation>()
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    override suspend fun getRecommendationById(
        rec_id: String
    ): Recommendation? {
        val res = try {
            val raw = client
                .from("Recommendation")
                .select {
                    filter {
                        eq("id", rec_id)
                    }
//                    single()
                }
//                .decodeSingleOrNull<Recommendation>()
            Log.d("RecommendationRepositoryImpl", "Raw response: $raw")

            // Try decoding after logging
            val decoded = raw.decodeSingleOrNull<Recommendation>()
            Log.d("RecommendationRepositoryImpl", "Decoded: $decoded")

            decoded
        } catch (e: Exception) {
            Log.e("RecommendationRepositoryImpl", "Error fetching recommendation", e)
            e.printStackTrace()
            null
        }
        Log.d("RecommendationRepositoryImpl", "getRecommendationById: id: $rec_id, res: $res")
        return res
    }

    override suspend fun recommendStore(
        from_user_id: String,
        to_user_id: String,
        store_user_id: String,
        store_id: String,
        from_user_name: String,
        store_name: String,
        note: String
    ): RepoResult<Unit> {
        return try {
            // 1. Insert recommendation
            val recommendation = Recommendation(
                from_user_id = from_user_id,
                to_user_id = to_user_id,
                store_user_id = store_user_id,
                store_id = store_id,
                note = note
            )

            val inserted = client
                .from("Recommendation")
                .insert(recommendation) {
                    select() // return inserted row
                }
                .decodeSingle<Recommendation>()

            // 2. Insert notification for the receiver
            addNotificationUseCase(
                user_id = to_user_id,
                title = "New Recommendation",
                body = "$from_user_name recommended you $store_name",
                type = "RECOMMENDATION",
                ref_id = inserted.id.toString()
            )

            Log.d("RecommendationRepositoryImpl", "recommendStore: inserted: $inserted")
            Log.d("RecommendationRepositoryImpl", "recommendStore: notification added")
            RepoResult.Success(Unit)
        } catch (e: Exception) {
            e.printStackTrace()
            Log.d("RecommendationRepositoryImpl", "recommendStore: error: ${e.message}")
            RepoResult.Error(e.toString())
        }
    }


    override suspend fun removeRecommendation(id: String): RepoResult<Unit> {
        return try {
            // 1. Delete recommendation
            client
                .from("Recommendation")
                .delete {
                    filter { eq("id", id) }
                }

            // 2. Delete related notifications
            removeNotificationUseCase(id) // input the ref_id

            RepoResult.Success(Unit)
        } catch (e: Exception) {
            e.printStackTrace()
            RepoResult.Error(e.toString())
        }
    }

    override suspend fun addStoreFromRecommendation(rec_id: String) {
        TODO("Not yet implemented")
    }

}