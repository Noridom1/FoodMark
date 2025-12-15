package com.example.foodmark.auth.domain.use_cases

import com.example.foodmark.auth.domain.respository.AuthRepository
import javax.inject.Inject

class LogOutUseCase @Inject constructor(
    private val repo: AuthRepository
) {
    suspend operator fun invoke() : Boolean {
        return repo.logOutUser()
    }
}