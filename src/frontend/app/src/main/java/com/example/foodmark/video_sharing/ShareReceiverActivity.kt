package com.example.foodmark.video_sharing

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ShareReceiverActivity : AppCompatActivity() {

    private val viewModel: ShareReceiverViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Handle the incoming intent
        if (intent?.action == Intent.ACTION_SEND && intent.type == "text/plain") {
            val sharedText = intent.getStringExtra(Intent.EXTRA_TEXT)

            if (!sharedText.isNullOrEmpty()) {
                Log.d("ShareReceiverActivity", "Received shared text: $sharedText")

                // Process the URL in the ViewModel (background)
                lifecycleScope.launch {
                    Log.d("ShareReceiverActivity", "Adding video for URL: $sharedText")

                    try {
                        val response = viewModel.addVideoUser(sharedText)
                        Log.d("ShareReceiverActivity", "VideoAPI Response: $response")
                    } catch (e: Exception) {
                        Log.e("ShareReceiverActivity", "Error adding video: ${e.message}")
                    } finally {
                        finish() // Close the activity silently after processing
                    }
                }
            } else {
                Log.d("ShareReceiverActivity", "No shared content received.")
                finish()
            }
        } else {
            Log.d("ShareReceiverActivity", "Unsupported intent.")
            finish()
        }
    }
}
