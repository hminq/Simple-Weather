package com.example.simpleweather.domain.usecase

import com.example.simpleweather.domain.entity.UserSetting
import com.example.simpleweather.domain.repository.UserSettingRepository
import jakarta.inject.Inject

class SetUserSettingUseCase @Inject constructor(
    val userSettingRepository: UserSettingRepository
) {

    suspend fun invoke(userSetting: UserSetting) {
        userSettingRepository.saveUserSetting(userSetting)
    }
}