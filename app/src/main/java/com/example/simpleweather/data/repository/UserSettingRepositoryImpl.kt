package com.example.simpleweather.data.repository

import com.example.simpleweather.R
import com.example.simpleweather.data.mapper.toData
import com.example.simpleweather.data.mapper.toDomain
import com.example.simpleweather.data.source.local.UserSettingDataSource
import com.example.simpleweather.domain.entity.UserSetting
import com.example.simpleweather.domain.exception.LocalStorageException
import com.example.simpleweather.domain.repository.UserSettingRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserSettingRepositoryImpl @Inject constructor(
    val dataSource: UserSettingDataSource
) : UserSettingRepository {
    private val saveDataErrorRes: Int = R.string.save_data_err

    override suspend fun saveUserSetting(userSetting: UserSetting) {
        val userSettingModel = userSetting.toData()

        try {
            dataSource.save(userSettingModel)
        } catch (e: IOException) {
            throw LocalStorageException(e.message, e, saveDataErrorRes)
        } catch (ex: Exception) {
            throw LocalStorageException(ex.message, ex, saveDataErrorRes)
        }
    }

    override suspend fun getUserSetting(): Flow<UserSetting> {
        return dataSource.get()
            .map { model ->
                model.toDomain()
            }
    }
}