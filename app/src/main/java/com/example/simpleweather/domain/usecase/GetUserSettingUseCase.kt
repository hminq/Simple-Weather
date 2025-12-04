package com.example.simpleweather.domain.usecase

import com.example.simpleweather.domain.entity.UserSetting
import com.example.simpleweather.domain.repository.UserSettingRepository
import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow

class GetUserSettingUseCase @Inject constructor(
    val userSettingRepository: UserSettingRepository
): UseCase<GetUserSettingUseCase.Params, GetUserSettingUseCase.Output> {
    data class Params(
        val unit: Unit
    )

    data class Output(
        val userSetting: Flow<UserSetting>
    )

    override suspend fun invoke(params: Params): Output {
        val userSetting = userSettingRepository.getUserSetting()

        return Output(userSetting)
    }
}