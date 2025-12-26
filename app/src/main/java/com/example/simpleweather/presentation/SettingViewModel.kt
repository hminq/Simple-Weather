package com.example.simpleweather.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.simpleweather.domain.entity.SpeedType
import com.example.simpleweather.domain.entity.Temperature
import com.example.simpleweather.domain.entity.UserSetting
import com.example.simpleweather.domain.exception.DomainException
import com.example.simpleweather.domain.usecase.GetUserSettingUseCase
import com.example.simpleweather.domain.usecase.SetUserSettingUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

@HiltViewModel
class SettingViewModel @Inject constructor(
    private val getUserSettingUseCase: GetUserSettingUseCase,
    private val setUserSettingUseCase: SetUserSettingUseCase
) : ViewModel() {

    // UI State
    private val _uiState = MutableStateFlow<SettingUiState>(SettingUiState.Loading)
    val uiState: StateFlow<SettingUiState> = _uiState.asStateFlow()

    // User Settings Flow
    private val _userSetting = MutableStateFlow<UserSetting?>(null)
    val userSetting: StateFlow<UserSetting?> = _userSetting.asStateFlow()

    // Error State
    private val _error = MutableStateFlow<DomainException?>(null)
    val error: StateFlow<DomainException?> = _error.asStateFlow()

    // Success Message State
    private val _successMessage = MutableStateFlow<String?>(null)
    val successMessage: StateFlow<String?> = _successMessage.asStateFlow()

    init {
        loadUserSettings()
    }

    /**
     * Load user settings from repository
     */
    private fun loadUserSettings() {
        viewModelScope.launch {
            _uiState.value = SettingUiState.Loading
            val userSetting = getUserSettingUseCase.invoke()

            // Observe the Flow from UseCase
            // UseCase already handles exception and throws DomainException
            userSetting.onEach { setting ->
                    _userSetting.value = setting
                    _uiState.value = SettingUiState.Success(setting)
                    _error.value = null
                }
                .catch { exception ->
                    if (exception is DomainException) {
                        _error.value = exception
                        _uiState.value = SettingUiState.Error(exception)
                    } else {
                        _error.value = null
                        _uiState.value = SettingUiState.Loading
                    }
                }
                .launchIn(viewModelScope)
        }
    }

    /**
     * Update temperature unit (Celsius/Fahrenheit)
     */
    fun updateTemperatureUnit(temperature: Temperature) {
        val currentSetting = _userSetting.value ?: UserSetting()
        val updatedSetting = currentSetting.copy(temperature = temperature)
        saveUserSetting(updatedSetting)
    }

    /**
     * Update wind speed unit (km/h or mph)
     */
    fun updateWindSpeedUnit(speedType: SpeedType) {
        val currentSetting = _userSetting.value ?: UserSetting()
        val updatedSetting = currentSetting.copy(windSpeedType = speedType)
        saveUserSetting(updatedSetting)
    }

    /**
     * Update daily notification setting
     */
    fun updateDailyNotification(enabled: Boolean) {
        val currentSetting = _userSetting.value ?: UserSetting()
        val updatedSetting = currentSetting.copy(dailyNotification = enabled)
        saveUserSetting(updatedSetting)
    }

    /**
     * Update danger alert notification setting
     */
    fun updateDangerNotification(enabled: Boolean) {
        val currentSetting = _userSetting.value ?: UserSetting()
        val updatedSetting = currentSetting.copy(dangerNotification = enabled)
        saveUserSetting(updatedSetting)
    }

    /**
     * Save user setting to repository
     */
    private fun saveUserSetting(userSetting: UserSetting) {
        viewModelScope.launch {
            try {
                // UseCase handles exception and throws DomainException
                setUserSettingUseCase.invoke(userSetting)
                // Setting will be updated automatically via Flow from repository
                _successMessage.value = "Settings saved successfully"
                _error.value = null
            } catch (e: DomainException) {
                // Catch DomainException from UseCase
                _error.value = e
                _successMessage.value = null
            }
        }
    }

    /**
     * Clear error state
     */
    fun clearError() {
        _error.value = null
    }

    /**
     * Clear success message
     */
    fun clearSuccessMessage() {
        _successMessage.value = null
    }

    /**
     * UI State sealed class
     */
    sealed class SettingUiState {
        object Loading : SettingUiState()
        data class Success(val userSetting: UserSetting) : SettingUiState()
        data class Error(val exception: DomainException) : SettingUiState()
    }
}