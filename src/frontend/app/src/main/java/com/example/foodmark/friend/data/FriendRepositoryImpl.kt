package com.example.cs426_mobileproject.friend.data

import android.util.Log
import co.touchlab.kermit.Logger.Companion.a
import coil.util.CoilUtils.result
import com.example.cs426_mobileproject.core.domain.model.FoodStore
import com.example.cs426_mobileproject.core.domain.model.Profile
import com.example.cs426_mobileproject.core.domain.model.RepoResult
import com.example.cs426_mobileproject.core.domain.usecase.GetProfileByID
import com.example.cs426_mobileproject.core.supabase.SupabaseClientProvider
import com.example.cs426_mobileproject.friend.domain.model.Friend
import com.example.cs426_mobileproject.friend.domain.model.FriendRequest
import com.example.cs426_mobileproject.friend.domain.repo.FriendRepository
import com.example.cs426_mobileproject.notification.domain.usecase.AddNotificationUseCase
import com.example.cs426_mobileproject.notification.domain.usecase.RemoveNotificationUseCase
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject
import java.nio.file.Files.delete
import javax.inject.Inject

class FriendRepositoryImpl @Inject constructor(
    private val addNotificationUseCase: AddNotificationUseCase,
    private val removeNotificationUseCase: RemoveNotificationUseCase,
    private val getProfileByIdUseCase: GetProfileByID,
) : FriendRepository {

    private val client = SupabaseClientProvider.client

    override suspend fun getFriendsOfUser(user_id: String): List<Friend> {
        val result = try {
            val parameters = buildJsonObject {
                put("p_user_id", JsonPrimitive(user_id))
            }

            val result = client.postgrest.rpc(
                function = "get_friends",
                parameters = parameters
            )

            result.decodeList<Friend>()
        } catch (e: Exception) {
            Log.e("FriendRepository", "Error fetching friends", e)
            emptyList()
        }
        Log.d("FriendRepository", "Friends: $result")
        return result
    }

    override suspend fun getFriendRequests(user_id: String): List<FriendRequest> {
        return try {
            val parameters = buildJsonObject {
                put("p_user_id", JsonPrimitive(user_id))
            }

            val result = client.postgrest.rpc(
                function = "get_pending_requests",
                parameters = parameters
            )

            result.decodeList<FriendRequest>()
        } catch (e: Exception) {
            Log.e("FriendRepository", "Error fetching friend requests", e)
            emptyList()
        }
    }

    override suspend fun sendFriendRequestByEmail(
        from_user_id: String,
        to_user_email: String
    ): RepoResult<Unit> {
        return try {
            // 1. Find the recipient by email
            val toUser = client
                .from("User")
                .select {
                    filter { eq("email", to_user_email) }
                }
                .decodeSingleOrNull<Profile>()

            if (toUser == null) {
                return RepoResult.Error("User not found")
            }

            // 2. Check if a friend request already exists
            val existingRequest = client
                .from("FriendRequest")
                .select {
                    filter {
                        eq("from_user_id", from_user_id)
                        eq("to_user_id", toUser.id)
                    }
                }
                .decodeSingleOrNull<FriendRequest>()

            if (existingRequest != null) {
                return RepoResult.Error("Friend request already sent or accepted")
            }

            // 2. Insert new friend request
            val newRequest = mapOf(
                "from_user_id" to from_user_id,
                "to_user_id" to toUser.id,
                "status" to "PENDING"
            )

            Log.d("FriendRepository", "New request: $newRequest")
            val friendRequest = client.from("FriendRequest")
                .insert(newRequest) {
                    select()
                }
                .decodeSingleOrNull<FriendRequest>()

            Log.d("FriendRepository", "Friend request: $friendRequest")
            if (friendRequest == null) {
                return RepoResult.Error("Failed to create friend request")
            }

            // 3. Get sender profile
            val fromUserResult = getProfileByIdUseCase(from_user_id)
            val fromUser = when (fromUserResult) {
                is RepoResult.Success -> fromUserResult.data
                is RepoResult.Error -> return RepoResult.Error("Failed to create friend request")
            }

            // 4. Send notification
            addNotificationUseCase(
                user_id = toUser.id,
                type = "FRIEND_REQUEST",
                ref_id = friendRequest.id,
                title = "New Friend Request",
                body = "${fromUser.name} has sent you a friend request."
            )

            RepoResult.Success(Unit)
        } catch (e: Exception) {
            Log.d("FriendRepository", "Error sending friend request: ${e.message}")
            RepoResult.Error(e.localizedMessage ?: "Unknown error")
        }
    }


    override suspend fun setFriendship(
        from_user_id: String,
        to_user_id: String
    ): RepoResult<Unit> {
        return try {
            // 1. Update FriendRequest status to ACCEPTED
            Log.d("FriendRepository", "setFriendship: Updating friend request: from_user_id=$from_user_id, to_user_id=$to_user_id")
            val friendRequestRes = client.from("FriendRequest")
                .update(
                    mapOf("status" to "ACCEPTED")
                ) {
                    select()
                    filter {
                        eq("from_user_id", from_user_id)
                        eq("to_user_id", to_user_id)
//                        eq("status", "PENDING")
                    }
                }

            Log.d("FriendRepository", "setFriendship: Friend request: ${friendRequestRes.data}")

            val friendRequest = friendRequestRes.decodeSingleOrNull<FriendRequest>()

            if (friendRequest == null) {
                return RepoResult.Error("Friend request not found")
            }

            // 2. Remove related notification
            val removeResult = removeNotificationUseCase(friendRequest.id)
            if (removeResult is RepoResult.Error) {
                return RepoResult.Error("Failed to remove notification: ${removeResult.message}")
            }

            RepoResult.Success(Unit)
        } catch (e: Exception) {
            RepoResult.Error(e.localizedMessage ?: "Unknown error")
        }
    }


    override suspend fun unFriend(from_user_id: String, to_user_id: String): RepoResult<Unit> {
        return try {
            // Look for friendship in both directions
            val friendship = client
                .from("FriendRequest")
                .select {
                    filter {
                        or {
                            and {
                                eq("from_user_id", from_user_id)
                                eq("to_user_id", to_user_id)
                            }
                            and {
                                eq("from_user_id", to_user_id)
                                eq("to_user_id", from_user_id)
                            }
                        }
                        eq("status", "ACCEPTED")
                    }
                }
                .decodeSingleOrNull<FriendRequest>()

            if (friendship == null) {
                RepoResult.Error("Friendship not found")
            } else {
                client.from("FriendRequest")
                    .delete {
                        filter {
                            or {
                                and {
                                    eq("from_user_id", from_user_id)
                                    eq("to_user_id", to_user_id)
                                }
                                and {
                                    eq("from_user_id", to_user_id)
                                    eq("to_user_id", from_user_id)
                                }
                            }
                            eq("status", "ACCEPTED")
                        }
                    }
//                    .decodeSingleOrNull<FriendRequest>()

                RepoResult.Success(Unit)
            }
        } catch (e: Exception) {
            Log.d("FriendRepository", "Error unfriend: ${e.message}")
            RepoResult.Error(e.localizedMessage ?: "Unknown error")
        }
    }

    override suspend fun getFriendRequestById(friendRequestId: String): FriendRequest? {
        return try {
            val res = client.from("FriendRequest")
                .select {
                    filter {
                        eq("id", friendRequestId)
                    }
                }

            Log.d("FriendRepository", "getFriendRequestById: ${res.data}")
            res.decodeSingleOrNull<FriendRequest>()
        } catch (e: Exception) {
            Log.e("FriendRepository", "Error fetching friend request (id=$friendRequestId)", e)
            null
        }
    }

    override suspend fun removeFriendRequest(friendRequestId: String): RepoResult<Unit> {
        return try {
            val existing = client.from("FriendRequest")
                .select {
                    filter {
                        eq("id", friendRequestId)
                    }
                    limit(1)
                }
                .decodeSingleOrNull<FriendRequest>()  // <-- your data model

            if (existing == null) {
                Log.w("FriendRepository", "FriendRequest $friendRequestId does not exist")
                return RepoResult.Error("Friend request not found")
            }
            else {
                Log.d("FriendRepository", "FriendRequest $friendRequestId exists")
            }

            val res = client.from("FriendRequest")
                .delete {
                    filter {
                        eq("id", friendRequestId)
                    }
                }
//                .decodeSingleOrNull<FriendRequest>()

            val notificationRes = removeNotificationUseCase(friendRequestId)
            if (notificationRes is RepoResult.Error) {
                Log.e("FriendRepository", "Remove notification failed: ${notificationRes.message}")
                return RepoResult.Error(notificationRes.message)
            }

            Log.d("FriendRepository", "Successfully removed FriendRequest & Notification for $friendRequestId")
            RepoResult.Success(Unit)

        } catch (e: Exception) {
            Log.e("FriendRepository", "Exception while removing friend request", e)
            RepoResult.Error(e.localizedMessage ?: "Unknown error")
        }
    }

}

