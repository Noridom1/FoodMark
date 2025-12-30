package com.example.cs426_mobileproject.friend.domain.usecase

import com.example.cs426_mobileproject.friend.domain.repo.FriendRepository
import javax.inject.Inject

class SendFriendRequestByEmailUseCase @Inject constructor(
    private val repository: FriendRepository
) {
    suspend operator fun invoke(from_user_id: String, to_user_email: String) =
        repository.sendFriendRequestByEmail(from_user_id, to_user_email)

}