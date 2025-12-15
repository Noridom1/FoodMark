    package com.example.foodmark.auth.presentation.components

    import android.util.Log
    import com.example.foodmark.R
    import androidx.compose.foundation.layout.Arrangement
    import androidx.compose.runtime.Composable
    import androidx.compose.runtime.rememberCoroutineScope
    import androidx.compose.ui.platform.LocalContext
    import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
    import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
    import kotlinx.coroutines.launch
    import java.security.MessageDigest
    import java.util.UUID
    import androidx.compose.material3.Button
    import androidx.compose.material3.ButtonDefaults
    import androidx.compose.material3.Text
    import androidx.compose.ui.res.painterResource
    import androidx.compose.ui.unit.dp
    import androidx.compose.foundation.Image
    import androidx.compose.foundation.layout.*
    import androidx.compose.material3.Button
    import androidx.compose.material3.Text
    import androidx.compose.runtime.rememberCoroutineScope
    import androidx.compose.ui.Alignment
    import androidx.compose.ui.Modifier
    import androidx.compose.ui.graphics.Color
    import androidx.compose.ui.platform.LocalContext
    import androidx.compose.ui.res.painterResource
    import androidx.compose.ui.unit.dp
    import androidx.credentials.CredentialManager
    import androidx.credentials.GetCredentialRequest
    import kotlinx.coroutines.launch
    import java.util.*



    @Composable
    fun GoogleSignInButton(
        onGoogleSignIn: (idToken: String, nonce: String) -> Unit
    ) {
        val coroutineScope = rememberCoroutineScope()
        val context = LocalContext.current

        Button(
            onClick = {
                val credentialManager = CredentialManager.create(context)
                val rawNonce = UUID.randomUUID().toString()
                val bytes = rawNonce.toByteArray()
                val md = MessageDigest.getInstance("SHA-256")
                val digest = md.digest(bytes)
                val hashedNonce = digest.fold("") { str, it -> str + "%02x".format(it) }

                val googleIdOption = GetSignInWithGoogleOption.Builder(
                    serverClientId = "425297305230-9m1u8s9fol4f8h2umbpdhqkpdj08dp6s.apps.googleusercontent.com"
                ).setNonce(hashedNonce).build()

                coroutineScope.launch {
                    val request = GetCredentialRequest.Builder()
                        .addCredentialOption(googleIdOption)
                        .build()

                    try {
                        val result = credentialManager.getCredential(
                            request = request,
                            context = context
                        )

                        val googleIdTokenCredential = GoogleIdTokenCredential
                            .createFrom(result.credential.data)
                        val googleIdToken = googleIdTokenCredential.idToken
                        println("✅ ID Token: $googleIdToken")
                        Log.d("GoogleSignInButton", "ID Token: $googleIdToken")

                        onGoogleSignIn(googleIdToken, rawNonce)

                    } catch (e: androidx.credentials.exceptions.GetCredentialException) {
                        // User dismissed, or no credential available
                        println("⚠️ Google Sign-In cancelled: ${e.message}")
                        Log.e("GoogleSignInButton", "❌ Credential error type: ${e::class.simpleName}, message: ${e.message}", e)
                    } catch (e: Exception) {
                        // Other unexpected errors
                        e.printStackTrace()
                        println("❌ Google Sign-In failed: ${e.message}")
                        Log.d("GoogleSignInButton", "Google Sign-In failed: ${e.message}")
                    }
                }
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.White,
                contentColor = Color.Black
            ),
            modifier = Modifier
                .width(320.dp)
                .height(50.dp),
            shape = ButtonDefaults.shape
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                Image(
                    painter = painterResource(id = R.drawable.google_logo), // Add this logo in res/drawable
                    contentDescription = "Google logo",
                    modifier = Modifier
                        .size(30.dp)
                        .padding(end = 8.dp)
                )
                Text(text = "Sign in with Google")
            }
        }
    }