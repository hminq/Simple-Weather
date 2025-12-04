package com.example.simpleweather.domain.entity


data class UserSetting(
    val temperature: Temperature = Temperature.CELSIUS,
    val windSpeedType: SpeedType = SpeedType.KMH,
    val dailyNotification: Boolean = true,
    val dangerNotification: Boolean = true,
)