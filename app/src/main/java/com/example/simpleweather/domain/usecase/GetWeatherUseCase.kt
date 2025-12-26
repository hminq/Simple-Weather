package com.example.simpleweather.domain.usecase

import com.example.simpleweather.domain.entity.Weather
import com.example.simpleweather.domain.entity.WeatherType
import com.example.simpleweather.domain.repository.UserSettingRepository
import com.example.simpleweather.domain.repository.WeatherRepository
import jakarta.inject.Inject

class GetWeatherUseCase @Inject constructor(
    weatherRepository: WeatherRepository,
    userSettingRepository: UserSettingRepository
) {

    suspend fun invoke(
        type: WeatherType,
        lat: Double,
        long: Double
    ): Weather {
        TODO("Not yet implemented")
    }
}