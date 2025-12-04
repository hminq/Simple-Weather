package com.example.simpleweather.data.model

data class UserSettingModel(
    val temperature: TemperatureModel = TemperatureModel.CELSIUS,
    val windSpeedType: SpeedTypeModel = SpeedTypeModel.KMH,
    val dailyNotification: Boolean = true,
    val dangerNotification: Boolean = true
) {
    companion object {
        val DEFAULT_SETTING: UserSettingModel = UserSettingModel()
    }
}

