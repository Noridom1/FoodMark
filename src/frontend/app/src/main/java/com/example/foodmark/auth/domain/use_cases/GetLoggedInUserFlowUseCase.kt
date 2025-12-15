package com.example.foodmark.auth.domain.use_cases

import com.example.foodmark.auth.domain.respository.AuthRepository
import com.example.foodmark.core.domain.model.Profile
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetLoggedInUserFlowUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(): Flow<Profile?> {
        return authRepository.getLoggedInUserFlow()
    }
}