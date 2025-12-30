package com.example.cs426_mobileproject.friend.domain.usecase

import com.example.cs426_mobileproject.friend.domain.repo.FriendRepository
import javax.inject.Inject

class GetFriendsOfUserUseCase @Inject constructor(
    private val repository: FriendRepository
) {
    suspend operator fun invoke(user_id: String) = repository.getFriendsOfUser(user_id)
}