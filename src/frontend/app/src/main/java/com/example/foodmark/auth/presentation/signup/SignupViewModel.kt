package com.example.foodmark.auth.presentation.signup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foodmark.auth.domain.model.AuthResult
import com.example.foodmark.auth.domain.use_cases.RegisterWithEmailUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignupViewModel @Inject constructor(
    private val registerWithEmailUseCase: RegisterWithEmailUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(SignupState())
    val state: StateFlow<SignupState> = _state

    fun onEmailChange(newEmail: String) {
        _state.value = _state.value.copy(email = newEmail)
    }

    fun onPasswordChange(newPassword: String) {
        _state.value = _state.value.copy(password = newPassword)
    }

    fun onConfirmPasswordChange(newConfirmPassword: String) {
        _state.value = _state.value.copy(confirmPassword = newConfirmPassword)
    }

    // --- Signup Action ---
    fun onSignUpClick() {
        val currentState = _state.value
        println(currentState)
        // Basic validation before hitting use case
        if (currentState.password != currentState.confirmPassword) {
            _state.value = _state.value.copy(error = "Passwords do not match")
            return
        }

        if (currentState.email.isBlank() || currentState.password.isBlank()) {
            _state.value = _state.value.copy(error = "Please fill all required fields")
            return
        }

        _state.value = _state.value.copy(isLoading = true)
        viewModelScope.launch {
            when (val result = registerWithEmailUseCase(
                email = currentState.email,
                password = currentState.password
            )) {
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