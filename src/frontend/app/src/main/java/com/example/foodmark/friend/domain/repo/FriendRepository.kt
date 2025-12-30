package com.example.cs426_mobileproject.friend.domain.repo

import com.example.cs426_mobileproject.core.domain.model.FoodStore
import com.example.cs426_mobileproject.core.domain.model.RepoResult
import com.example.cs426_mobileproject.friend.domain.model.Friend
import com.example.cs426_mobileproject.friend.domain.model.FriendRequest

interface FriendRepository {
    suspend fun getFriendsOfUser(user_id: String): List<Friend>
    suspend fun getFriendRequests(user_id: String): List<FriendRequest>
    suspend fun sendFriendRequestByEmail(from_user_id: String, to_user_email: String): RepoResult<Unit>
    suspend fun setFriendship(from_user_id: String, to_user_id: String): RepoResult<Unit>
    suspend fun unFriend(from_user_id: String, to_user_id: String): RepoResult<Unit>
    suspend fun getFriendRequestById(friendRequestId: String): FriendRequest?
    suspend fun removeFriendRequest(friendRequestId: String): RepoResult<Unit>
}