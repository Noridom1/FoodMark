package com.example.foodmark.auth.di

import com.example.foodmark.auth.domain.respository.AuthRepository
import com.example.foodmark.auth.domain.use_cases.LogOutUseCase
import com.example.foodmark.auth.domain.use_cases.LoginWithEmailUseCase
import com.example.foodmark.auth.domain.use_cases.RegisterWithEmailUseCase
import com.example.foodmark.auth.domain.use_cases.SignInWithGoogleUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AuthModule {

    @Provides
    @Singleton
    fun provideSignInWithGoogleUseCase(
        repository: AuthRepository
    ): SignInWithGoogleUseCase {
        return SignInWithGoogleUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideLoginWithEmailUseCase(
        repository: AuthRepository
    ): LoginWithEmailUseCase {
        return LoginWithEmailUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideRegisterWithEmailUseCase(
        repository: AuthRepository
    ): RegisterWithEmailUseCase {
        return RegisterWithEmailUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideLogOutUseCase(
        repository: AuthRepository
    ): LogOutUseCase {
        return LogOutUseCase(repository)
    }

}