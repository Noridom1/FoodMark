package com.example.foodmark.core.domain.model

sealed class RepoResult<out T> {
    data class Success<T>(val data: T): RepoResult<T>()
    data class Error(val message: String, val cause: Throwable? = null): RepoResult<Nothing>()
}