package com.example.simpleweather.data.source.local

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.simpleweather.R
import com.example.simpleweather.data.mapper.toData
import com.example.simpleweather.data.mapper.toDomain
import com.example.simpleweather.data.model.SpeedTypeModel
import com.example.simpleweather.data.model.TemperatureModel
import com.example.simpleweather.data.model.UserSettingModel
import com.example.simpleweather.domain.entity.UserSetting
import com.example.simpleweather.domain.exception.LocalStorageException
import com.example.simpleweather.domain.repository.UserSettingRepository
import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

private val Context.dataStore by preferencesDataStore("local_setting")
private object PreferencesKeys {
    val TEMPERATURE = stringPreferencesKey("temperature_key")
    val SPEED_TYPE = stringPreferencesKey("speed_type_key")
    val DAILY_NOTIFICATION = booleanPreferencesKey("daily_noti_key")
    val DANGER_NOTIFICATION = booleanPreferencesKey("danger_noti_key")
}

class UserSettingRepositoryImpl @Inject constructor(
    context: Context
) : UserSettingRepository {
    companion object {
        private val SAVE_DATA_ERR_MSG: Int = R.string.save_data_err
    }
    private val dataStore = context.dataStore

    override suspend fun saveUserSetting(userSetting: UserSetting) {
        val userSettingModel = userSetting.toData()

        try {
            dataStore.edit { preferences ->
                // Save enum Language as a String
                preferences[PreferencesKeys.TEMPERATURE] = userSettingModel.temperature.name
                preferences[PreferencesKeys.SPEED_TYPE] = userSettingModel.windSpeedType.name
                preferences[PreferencesKeys.DAILY_NOTIFICATION] = userSettingModel.dailyNotification
                preferences[PreferencesKeys.DANGER_NOTIFICATION] = userSettingModel.dangerNotification
            }
        } catch (e: IOException) {
            throw LocalStorageException(e.message, e, SAVE_DATA_ERR_MSG)
        } catch (ex: Exception) {
            throw LocalStorageException(ex.message, ex, SAVE_DATA_ERR_MSG)
        }
    }

    override suspend fun getUserSetting(): Flow<UserSetting> {
        return dataStore.data
            .catch { _ ->
                // Fallback: emit empty preferences if read fails
                emit(emptyPreferences())
            }
            .map { preferences ->
                // Read Language (String) & convert to Enum
                val temperature = preferences[PreferencesKeys.TEMPERATURE] ?: UserSettingModel.DEFAULT_SETTING.temperature.name
                val windSpeedType = preferences[PreferencesKeys.SPEED_TYPE] ?: UserSettingModel.DEFAULT_SETTING.windSpeedType.name
                val dailyNotificationEnable = preferences[PreferencesKeys.DAILY_NOTIFICATION] ?: UserSettingModel.DEFAULT_SETTING.dailyNotification
                val dangerNotificationEnable = preferences[PreferencesKeys.DANGER_NOTIFICATION] ?: UserSettingModel.DEFAULT_SETTING.dangerNotification

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

                UserSettingModel(temperatureModel, windSpeedTypeModel, dailyNotificationEnable, dangerNotificationEnable).toDomain()
            }
    }
}