package com.example.simpleweather.di

import android.content.Context
import com.example.simpleweather.domain.repository.UserSettingRepository
import com.example.simpleweather.data.source.local.UserSettingRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import jakarta.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataStoreModule {
    @Singleton
    @Provides
    fun provideSettingDataStore(
        context: Context
    ): UserSettingRepository {
        return UserSettingRepositoryImpl(context)
    }
}