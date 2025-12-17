package com.example.foodmark.auth.presentation.signup

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.foodmark.LocalTopBarController
import com.example.foodmark.TopBarConfig
import com.example.foodmark.auth.presentation.components.RoundPasswordTextFieldWithTitle
import com.example.foodmark.auth.presentation.components.RoundTextFieldWithTitle
import com.example.foodmark.auth.presentation.components.TwoPartLayoutBegin

@Composable
fun SignupScreen(
    viewModel: SignupViewModel = hiltViewModel(),
    onSignupSuccess: () -> Unit,
    onNavigateToLogin: () -> Unit
) {
    val state by viewModel.state.collectAsState()

    // Navigate when signup succeeds
    LaunchedEffect(state.isSuccess) {
        if (state.isSuccess) {
            onSignupSuccess()
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

    SignupScreenComposable(
        state = state,
        onEmailChange = viewModel::onEmailChange,
        onPasswordChange = viewModel::onPasswordChange,
        onConfirmPasswordChange = viewModel::onConfirmPasswordChange,
        onSignUpClick = viewModel::onSignUpClick,
        onNavigateToLogin = onNavigateToLogin
    )
}

@Composable
private fun SignupScreenComposable(
    state: SignupState,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onConfirmPasswordChange: (String) -> Unit,
    onSignUpClick: () -> Unit,
    onNavigateToLogin: () -> Unit
) {
    TwoPartLayoutBegin(
        title = "Create Account"
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.SpaceEvenly
        ) {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {

                RoundTextFieldWithTitle(
                    title = "Email",
                    value = state.email,
                    onValueChange = onEmailChange
                )
                Spacer(modifier = Modifier.height(16.dp))


                RoundPasswordTextFieldWithTitle(
                    title = "Password",
                    value = state.password,
                    onValueChange = onPasswordChange
                )
                Spacer(modifier = Modifier.height(16.dp))

                RoundPasswordTextFieldWithTitle(
                    title = "Confirm Password",
                    value = state.confirmPassword,
                    onValueChange = onConfirmPasswordChange
                )
            }

            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Button(
                    onClick = onSignUpClick,
                    modifier = Modifier.fillMaxWidth(1f / 3f),
                    enabled = !state.isLoading
                ) {
                    Text(text = if (state.isLoading) "Loading..." else "Sign Up")
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Already have an account? Log In",
                    modifier = Modifier.clickable { onNavigateToLogin() }
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SignupScreenPreview() {
    val fakeState = SignupState(
        email = "jane@example.com",
        password = "password123",
        confirmPassword = "password123",
        isLoading = false
    )
    SignupScreenComposable(
        state = fakeState,
        onEmailChange = {},
        onPasswordChange = {},
        onConfirmPasswordChange = {},
        onSignUpClick = {},
        onNavigateToLogin = {}
    )
}