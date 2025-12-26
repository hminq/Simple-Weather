package com.example.simpleweather.di

import android.content.Context
import com.example.simpleweather.data.source.local.UserSettingDataSource
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import jakarta.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataStoreModule {
    @Singleton
    @Provides
    fun provideSettingDataStore(
        @ApplicationContext context: Context
    ): UserSettingDataSource {
        return UserSettingDataSource(context)
    }
}