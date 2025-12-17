package com.example.foodmark.auth.presentation.login


import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cs426_mobileproject.auth.presentation.login.LoginState
import com.example.foodmark.auth.domain.model.AuthResult
import com.example.foodmark.auth.domain.use_cases.LoginWithEmailUseCase
import com.example.foodmark.auth.domain.use_cases.SignInWithGoogleUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val signInWithGoogleUseCase: SignInWithGoogleUseCase,
    private val loginWithEmailUseCase: LoginWithEmailUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(LoginState())
    val state: StateFlow<LoginState> = _state

    fun onEmailChange(newEmail: String) {
        _state.value = _state.value.copy(email = newEmail)
    }

    fun onPasswordChange(newPassword: String) {
        _state.value = _state.value.copy(password = newPassword)
    }

    fun onSignInClick() {
        _state.value = _state.value.copy(isLoading = true)
        viewModelScope.launch {
            when (val result = loginWithEmailUseCase(state.value.email, state.value.password)) {
                is AuthResult.Success -> _state.value = _state.value.copy(isLoading = false, isSuccess = true)
                is AuthResult.Failure -> _state.value = _state.value.copy(isLoading = false, error = result.message)
            }
        }
    }


    fun signInWithGoogle(idToken: String, nonce: String) {
        Log.d("LoginViewModel", "signInWithGoogle called with idToken: $idToken")
        _state.value = _state.value.copy(isLoading = true)
        viewModelScope.launch {
            when (val result = signInWithGoogleUseCase(idToken, nonce)) {
                is AuthResult.Success -> {
                    _state.value = _state.value.copy(
                        isLoading = false,
                        isSuccess = true
                    )
                }

                is AuthResult.Failure -> {
                    _state.value = _state.value.copy(
                        isLoading = false,
                        error = result.message
                    )
                }
            }
        }
    }


    fun clearError() {
        _state.value = _state.value.copy(error = null)
    }
}
