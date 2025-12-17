package com.example.cs426_mobileproject.auth.presentation.login.components


import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.cs426_mobileproject.auth.presentation.components.GoogleSignInButton

@Composable
fun ExternalLoginSection(
    onGoogleSignIn: (idToken: String, nonce: String) -> Unit
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "OR",
            color = Color.White.copy(alpha = 0.5f),
            modifier = Modifier.padding(vertical = 12.dp),
            textAlign = TextAlign.Center
        )

        GoogleSignInButton(
            onGoogleSignIn = onGoogleSignIn
        )
    }
}
