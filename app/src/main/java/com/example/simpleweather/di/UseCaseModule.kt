package com.example.simpleweather.di

import com.example.simpleweather.domain.repository.UserSettingRepository
import com.example.simpleweather.domain.usecase.GetUserSettingUseCase
import com.example.simpleweather.domain.usecase.SetUserSettingUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object UseCaseModule {
    @Provides
    fun provideGetUserSettingUseCase(
        userSettingRepository: UserSettingRepository
    ): GetUserSettingUseCase = GetUserSettingUseCase(userSettingRepository)

    @Provides
    fun provideSetUserSettingUseCase(
        userSettingRepository: UserSettingRepository
    ): SetUserSettingUseCase = SetUserSettingUseCase(userSettingRepository)
}