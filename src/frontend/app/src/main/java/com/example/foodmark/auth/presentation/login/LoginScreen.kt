package com.example.foodmark.auth.presentation.login


import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.tooling.preview.Preview
import com.example.cs426_mobileproject.auth.presentation.login.LoginState
import com.example.foodmark.LocalTopBarController
import com.example.foodmark.TopBarConfig
import com.example.foodmark.auth.presentation.components.RoundPasswordTextFieldWithTitle
import com.example.foodmark.auth.presentation.components.RoundTextFieldWithTitle
import com.example.foodmark.auth.presentation.components.TwoPartLayoutBegin
import com.example.foodmark.auth.presentation.login.components.ExternalLoginSection


@Composable
fun LoginScreen(viewModel: LoginViewModel = hiltViewModel(),
                onLoginSuccess: () -> Unit,
                onNavigateToRegister: () -> Unit
) {
    val state by viewModel.state.collectAsState()
    LaunchedEffect(state.isSuccess) {
        if (state.isSuccess) {
            onLoginSuccess()
        }
    }

    val topBar = LocalTopBarController.current
    val owner = remember { Any() }

    SideEffect {
        topBar.set(owner, TopBarConfig(visible = false))
    }

    DisposableEffect(owner) {
        onDispose { topBar.clear(owner) }
    }

    LoginScreenComposable(
        state = state,
        onEmailChange = viewModel::onEmailChange,
        onPasswordChange = viewModel::onPasswordChange,
        onLoginClick = viewModel::onSignInClick,
        onNavigateToSignup = onNavigateToRegister,
        onGoogleLogin = {idToken, nonce ->
            viewModel.signInWithGoogle(idToken, nonce)

        }
    )

}

@Composable
private fun LoginScreenComposable(
    state: LoginState,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onLoginClick: () -> Unit,
    onNavigateToSignup: () -> Unit,
    onGoogleLogin: (String, String) -> Unit

) {
    TwoPartLayoutBegin(
        title = "Welcome"
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.SpaceEvenly
        ) {

            Column(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                RoundTextFieldWithTitle(
                    title = "Username or Email",
                    value = state.email,
                    onValueChange = onEmailChange
                )

                Spacer(modifier = Modifier.height(16.dp))

                RoundPasswordTextFieldWithTitle(
                    title = "Password",
                    value = state.password,
                    onValueChange = onPasswordChange
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Button(
                    onClick = onLoginClick,
                    modifier = Modifier.fillMaxWidth(1f / 3f),
                    enabled = !state.isLoading
                ) {
                    Text(text = if (state.isLoading) "Loading..." else "Log In")
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Forget Password?",
                    modifier = Modifier
                        .clickable { }
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Don't have an account? Sign up",
                    modifier = Modifier.clickable { onNavigateToSignup() }
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Or log in with",)

                Spacer(modifier = Modifier.height(8.dp))

                ExternalLoginSection(onGoogleSignIn = onGoogleLogin)
            }

        }
    }
}


@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    // Fake UI state for preview
    val fakeState = LoginState(
                email = "user@example.com",
                password = "password123",
                isLoading = false
    )

    // Fake view model replacement
    LoginScreenComposable(
        state = fakeState,
        onEmailChange = {},
        onPasswordChange = {},
        onLoginClick = {},
        onNavigateToSignup = {},
        onGoogleLogin = {idToken, nonce ->
        }
    )
}
