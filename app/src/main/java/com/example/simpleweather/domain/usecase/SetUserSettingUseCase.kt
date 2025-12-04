package com.example.simpleweather.domain.usecase

import com.example.simpleweather.domain.entity.UserSetting
import com.example.simpleweather.domain.exception.DomainException
import com.example.simpleweather.domain.repository.UserSettingRepository
import jakarta.inject.Inject

class SetUserSettingUseCase @Inject constructor(
    val userSettingRepository: UserSettingRepository
) : UseCase<SetUserSettingUseCase.Params, SetUserSettingUseCase.Output> {
    data class Params(
        val userSetting: UserSetting
    )

    data class Output(
        val unit: Unit
    )

    override suspend fun invoke(params: Params) : Output {
        try {
            userSettingRepository.saveUserSetting(params.userSetting)
        } catch (e: DomainException) {
            throw e
        }

        return Output(Unit)
    }
}