package com.example.simpleweather.domain.usecase

import com.example.simpleweather.R
import com.example.simpleweather.domain.entity.Weather
import com.example.simpleweather.domain.entity.WeatherType
import com.example.simpleweather.domain.repository.UserSettingRepository
import com.example.simpleweather.domain.repository.WeatherRepository
import jakarta.inject.Inject

class GetWeatherUseCase @Inject constructor(
    weatherRepository: WeatherRepository,
    userSettingRepository: UserSettingRepository
) : UseCase<GetWeatherUseCase.Params, GetWeatherUseCase.Output> {
    companion object {
        private val GET_WEATHER_ERROR: Int = R.string.get_weather_error
    }

    data class Params(
        val type: WeatherType,
        val lat: Double,
        val long: Double
    )

    data class Output(
        val weather: Weather
    )

    override suspend fun invoke(params: Params): Output {
        TODO("Not yet implemented")


    }
}