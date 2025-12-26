package com.example.simpleweather.di

import com.example.simpleweather.data.repository.UserSettingRepositoryImpl
import com.example.simpleweather.domain.repository.UserSettingRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    abstract fun bindUserSettingRepository(
        impl: UserSettingRepositoryImpl
    ): UserSettingRepository
}