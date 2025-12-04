package com.example.simpleweather.data.mapper

import com.example.simpleweather.data.model.SpeedTypeModel
import com.example.simpleweather.data.model.TemperatureModel
import com.example.simpleweather.domain.entity.Temperature
import com.example.simpleweather.data.model.UserSettingModel
import com.example.simpleweather.domain.entity.SpeedType
import com.example.simpleweather.domain.entity.UserSetting

fun UserSettingModel.toDomain(): UserSetting {
    val defaultDomainTemperature = Temperature.CELSIUS
    val defaultWindSpeedType = SpeedType.KMH

    val domainTemperature = try {
        Temperature.valueOf(this.temperature.name)
    } catch (_: IllegalArgumentException) {
        defaultDomainTemperature
    }

    val domainWindSpeedType = try {
        SpeedType.valueOf(this.windSpeedType.name)
    } catch (_: IllegalArgumentException) {
        defaultWindSpeedType
    }

    return UserSetting(
        temperature = domainTemperature,
        windSpeedType = domainWindSpeedType,
        dailyNotification = this.dailyNotification,
        dangerNotification = this.dangerNotification
    )
}

fun UserSetting.toData(): UserSettingModel {
    val defaultDataTemperatureModel = TemperatureModel.CELSIUS
    val defaultDataWindSpeedType = SpeedTypeModel.KMH

    val dataTemperatureModel = try {
        TemperatureModel.valueOf(this.temperature.name)
    } catch (_: IllegalArgumentException) {
        defaultDataTemperatureModel
    }

    val dataWindSpeedTypeModel = try {
        SpeedTypeModel.valueOf(this.windSpeedType.name)
    } catch (_: IllegalArgumentException) {
        defaultDataWindSpeedType
    }

    return UserSettingModel(
        temperature = dataTemperatureModel,
        windSpeedType = dataWindSpeedTypeModel,
        dailyNotification = this.dailyNotification,
        dangerNotification = this.dangerNotification
    )
}