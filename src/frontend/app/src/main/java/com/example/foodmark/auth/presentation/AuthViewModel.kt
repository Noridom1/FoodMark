package com.example.foodmark.auth.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foodmark.auth.data.repository.AuthRepositoryImpl
import com.example.foodmark.auth.domain.respository.AuthRepository
import com.example.foodmark.auth.domain.use_cases.GetLoggedInUserFlowUseCase
import com.example.foodmark.core.domain.model.Profile
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    val authState: StateFlow<AuthState> = (authRepository as AuthRepositoryImpl)
        .authState
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), AuthState.Loading)
}



sealed class AuthState {
    object Loading : AuthState()
    data class Authenticated(val user: Profile) : AuthState()
    object Unauthenticated : AuthState()
}
