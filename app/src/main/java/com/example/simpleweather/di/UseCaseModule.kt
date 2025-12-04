package com.example.simpleweather.di

import android.content.Context
import com.example.simpleweather.data.source.local.UserSettingRepositoryImpl
import com.example.simpleweather.domain.repository.UserSettingRepository
import com.example.simpleweather.domain.usecase.GetUserSettingUseCase
import com.example.simpleweather.domain.usecase.SetUserSettingUseCase
import com.example.simpleweather.domain.usecase.UseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import jakarta.inject.Named
import jakarta.inject.Singleton
import kotlin.jvm.JvmSuppressWildcards

@Module
@InstallIn(SingletonComponent::class)
object UseCaseModule {
    @Provides
    @Named("getUserSetting")
    @JvmSuppressWildcards
    fun provideGetUserSettingUseCase(
        userSettingRepository: UserSettingRepository
    ): UseCase<GetUserSettingUseCase.Params, GetUserSettingUseCase.Output> = GetUserSettingUseCase(userSettingRepository)

    @Provides
    @Named("setUserSetting")
    @JvmSuppressWildcards
    fun provideSetUserSettingUseCase(
        userSettingRepository: UserSettingRepository
    ): UseCase<SetUserSettingUseCase.Params, SetUserSettingUseCase.Output> = SetUserSettingUseCase(userSettingRepository)
}