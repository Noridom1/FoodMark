package com.example.cs426_mobileproject.friend.domain.usecase

import com.example.cs426_mobileproject.friend.domain.repo.FriendRepository
import javax.inject.Inject

class GetFriendRequestByIdUseCase @Inject constructor(
    private val friendRepository: FriendRepository
)
{
    suspend operator fun invoke(friendRequestId: String) = friendRepository.getFriendRequestById(friendRequestId)

}