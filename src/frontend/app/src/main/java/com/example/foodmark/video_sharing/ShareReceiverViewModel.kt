package com.example.foodmark.video_sharing

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.foodmark.auth.data.repository.SessionManager
import com.example.foodmark.video_sharing.data.remote.VideoResponse
import com.example.foodmark.video_sharing.domain.usecase.AddVideoUserUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class ShareReceiverViewModel @Inject constructor(
    private val addVideoUserUseCase: AddVideoUserUseCase,
    private val sessionManager: SessionManager
) : ViewModel() {

    suspend fun addVideoUser(url: String): VideoResponse {
        val userId = sessionManager.getSession()
        Log.d("ShareReceiverViewModel", "Adding video to User ID: $userId")
        require(!userId.isNullOrBlank()) { "No user session found!" }

        return withContext(Dispatchers.IO) {
            addVideoUserUseCase(url, userId)
        }
    }
}