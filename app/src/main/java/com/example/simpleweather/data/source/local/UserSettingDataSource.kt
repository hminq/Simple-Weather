package com.example.simpleweather.data.source.local

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import com.example.simpleweather.data.model.SpeedTypeModel
import com.example.simpleweather.data.model.TemperatureModel
import com.example.simpleweather.data.model.UserSettingModel
import dagger.hilt.android.qualifiers.ApplicationContext
import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

class UserSettingDataSource @Inject constructor(
    @ApplicationContext context: Context
) {
    private val dataStore = context.userSettingDataStore

    suspend fun save(userSettingModel: UserSettingModel) {
        try {
            dataStore.edit { preferences ->
                // Save enum Language as a String
                preferences[UserSettingPreferencesKeys.TEMPERATURE] = userSettingModel.temperature.name
                preferences[UserSettingPreferencesKeys.SPEED_TYPE] = userSettingModel.windSpeedType.name
                preferences[UserSettingPreferencesKeys.DAILY_NOTIFICATION] = userSettingModel.dailyNotification
                preferences[UserSettingPreferencesKeys.DANGER_NOTIFICATION] = userSettingModel.dangerNotification
            }
        } catch (e: IOException) {
            throw e
        } catch (ex: Exception) {
            throw ex
        }
    }

    fun get(): Flow<UserSettingModel> {
        return dataStore.data
            .catch { _ ->
                // Fallback: emit empty preferences if read fails
                emit(emptyPreferences())
            }
            .map { preferences ->
                // Read Language (String) & convert to Enum
                val temperature = preferences[UserSettingPreferencesKeys.TEMPERATURE] ?: UserSettingModel.DEFAULT_SETTING.temperature.name
                val windSpeedType = preferences[UserSettingPreferencesKeys.SPEED_TYPE] ?: UserSettingModel.DEFAULT_SETTING.windSpeedType.name
                val dailyNotificationEnable = preferences[UserSettingPreferencesKeys.DAILY_NOTIFICATION] ?: UserSettingModel.DEFAULT_SETTING.dailyNotification
                val dangerNotificationEnable = preferences[UserSettingPreferencesKeys.DANGER_NOTIFICATION] ?: UserSettingModel.DEFAULT_SETTING.dangerNotification

                val temperatureModel = try {
                    TemperatureModel.valueOf(temperature)
                } catch (_: IllegalArgumentException) {
                    UserSettingModel.DEFAULT_SETTING.temperature
                }

                val windSpeedTypeModel = try {
                    SpeedTypeModel.valueOf(windSpeedType)
                } catch (_: IllegalArgumentException) {
                    UserSettingModel.DEFAULT_SETTING.windSpeedType
                }

                UserSettingModel(temperatureModel, windSpeedTypeModel, dailyNotificationEnable, dangerNotificationEnable)
            }
    }
}