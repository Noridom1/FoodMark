package com.example.cs426_mobileproject.friend.domain.usecase

import com.example.cs426_mobileproject.core.domain.model.RepoResult
import com.example.cs426_mobileproject.friend.domain.repo.FriendRepository
import javax.inject.Inject

class RemoveFriendRequestUseCase @Inject constructor(
    private val friendRepository: FriendRepository
) {
    suspend operator fun invoke(friendRequestId: String) : RepoResult<Unit> {
        return friendRepository.removeFriendRequest(friendRequestId)
    }

}