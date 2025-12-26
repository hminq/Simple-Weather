package com.example.simpleweather.domain.usecase

import com.example.simpleweather.domain.entity.UserSetting
import com.example.simpleweather.domain.repository.UserSettingRepository
import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow

class GetUserSettingUseCase @Inject constructor(
    val userSettingRepository: UserSettingRepository
) {

    suspend fun invoke(): Flow<UserSetting> {
        return userSettingRepository.getUserSetting()
    }
}