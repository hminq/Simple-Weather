package com.example.simpleweather.data.source.local

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore

val Context.userSettingDataStore by preferencesDataStore("local_setting")

internal object UserSettingPreferencesKeys {
    val TEMPERATURE = stringPreferencesKey("temperature_key")
    val SPEED_TYPE = stringPreferencesKey("speed_type_key")
    val DAILY_NOTIFICATION = booleanPreferencesKey("daily_noti_key")
    val DANGER_NOTIFICATION = booleanPreferencesKey("danger_noti_key")
}