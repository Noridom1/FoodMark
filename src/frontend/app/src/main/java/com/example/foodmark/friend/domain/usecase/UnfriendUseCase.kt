package com.example.cs426_mobileproject.friend.domain.usecase

import com.example.cs426_mobileproject.friend.domain.repo.FriendRepository
import javax.inject.Inject

class UnfriendUseCase @Inject constructor(
    private val repo: FriendRepository
) {
    suspend operator fun invoke(from_user_id: String, to_user_id: String) = repo.unFriend(from_user_id, to_user_id)

}