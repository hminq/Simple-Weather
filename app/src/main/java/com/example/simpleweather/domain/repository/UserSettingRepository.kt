package com.example.simpleweather.domain.repository

import com.example.simpleweather.domain.entity.UserSetting
import kotlinx.coroutines.flow.Flow

interface UserSettingRepository {
    suspend fun saveUserSetting(userSetting: UserSetting)
    suspend fun getUserSetting(): Flow<UserSetting>
}