package com.example.simpleweather.presentation

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.util.Log
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.example.simpleweather.R
import com.example.simpleweather.databinding.FragmentSettingBinding
import com.example.simpleweather.domain.entity.SpeedType
import com.example.simpleweather.domain.entity.Temperature
import com.example.simpleweather.domain.entity.UserSetting
import com.example.simpleweather.domain.exception.DomainException
import com.example.simpleweather.presentation.utils.ToggleSwitchHelper
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SettingFragment : Fragment() {
    companion object {
        private const val TAG: String = "FRAGMENT_SETTING"
    }
    private var _binding: FragmentSettingBinding? = null
    private val binding get() = _binding!!

    private val viewModel: SettingViewModel by viewModels()

    // Helper classes để quản lý toggle switches
    private lateinit var temperatureSwitch: ToggleSwitchHelper
    private lateinit var windSpeedSwitch: ToggleSwitchHelper
    
    // Flag to prevent triggering save when updating UI from ViewModel
    private var isUpdatingFromViewModel = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        // Observe ViewModel states
        observeViewModel()
        
        // Wait for layout to be measured before setting up
        binding.toggleGroupTemp.post {
            setupTemperatureSwitch()
            setupWindSpeedSwitch()
            setupNotificationSwitches()
        }
        setupSourceCodeClick()
        setupButtonBack()
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                // Observe user settings
                viewModel.userSetting.collect { userSetting ->
                    userSetting?.let { setting ->
                        updateUI(setting)
                    }
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                // Observe error messages
                viewModel.error.collect { error ->
                    error?.let {
                        showError(it)
                        viewModel.clearError()
                    }
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                // Observe success messages
                viewModel.successMessage.collect { message ->
                    message?.let {
                        showSuccess(it)
                        viewModel.clearSuccessMessage()
                    }
                }
            }
        }
    }

    private fun updateUI(userSetting: UserSetting) {
        // Set flag to prevent triggering save when updating from ViewModel
        isUpdatingFromViewModel = true
        
        // Setup switches if not initialized
        if (!::temperatureSwitch.isInitialized) {
            setupTemperatureSwitch()
            temperatureSwitch.setup()
        }
        if (!::windSpeedSwitch.isInitialized) {
            setupWindSpeedSwitch()
            windSpeedSwitch.setup()
        }

        // Update temperature switch
        val isCelsius = userSetting.temperature == Temperature.CELSIUS
        if (temperatureSwitch.getCurrentSelection() != isCelsius) {
            // Toggle if current selection doesn't match
            if (isCelsius) {
                binding.btnCelsius.performClick()
            } else {
                binding.btnFahrenheit.performClick()
            }
        }

        // Update wind speed switch
        val isKmh = userSetting.windSpeedType == SpeedType.KMH
        if (windSpeedSwitch.getCurrentSelection() != isKmh) {
            // Toggle if current selection doesn't match
            if (isKmh) {
                binding.btnKmh.performClick()
            } else {
                binding.btnMph.performClick()
            }
        }

        // Update notification switches (remove listener temporarily to avoid triggering save)
        binding.switchNotiDaily.setOnCheckedChangeListener(null)
        binding.switchNotiDaily.isChecked = userSetting.dailyNotification
        binding.switchNotiDaily.setOnCheckedChangeListener { _, isChecked ->
            onDailySummaryChanged(isChecked)
        }

        binding.switchNotiDanger.setOnCheckedChangeListener(null)
        binding.switchNotiDanger.isChecked = userSetting.dangerNotification
        binding.switchNotiDanger.setOnCheckedChangeListener { _, isChecked ->
            onDangerAlertChanged(isChecked)
        }
        
        // Reset flag after update
        isUpdatingFromViewModel = false
    }

    private fun showError(exception: DomainException) {
        val errorMessage = exception.messageResId?.let { 
            getString(it) 
        } ?: exception.message ?: getString(R.string.settings_load_error)
        
        Snackbar.make(binding.root, errorMessage, Snackbar.LENGTH_LONG).show()
        Log.e(TAG, "Error: $errorMessage", exception)
    }

    private fun showSuccess(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_SHORT).show()
        Log.d(TAG, "Success: $message")
    }
    private fun setupButtonBack() {
        binding.btnBack.setOnClickListener { _ -> navigateBackToWeather() }
    }

    private fun navigateBackToWeather() {
        findNavController().navigate(R.id.action_settingFragment_to_weatherFragment)
    }

    private fun setupTemperatureSwitch() {
        temperatureSwitch = ToggleSwitchHelper(
            toggleGroup = binding.toggleGroupTemp,
            toggleIndicator = binding.toggleIndicator,
            button1 = binding.btnCelsius,
            button2 = binding.btnFahrenheit,
            isFirstSelected = true, // default is Celcius
            onSelectionChanged = { isCelsius ->
                onTemperatureUnitChanged(isCelsius)
            }
        )
    }

    private fun setupWindSpeedSwitch() {
        windSpeedSwitch = ToggleSwitchHelper(
            toggleGroup = binding.toggleGroupWind,
            toggleIndicator = binding.toggleIndicatorWind,
            button1 = binding.btnKmh,
            button2 = binding.btnMph,
            isFirstSelected = true, // default is km/h
            onSelectionChanged = { isKmh ->
                onWindSpeedUnitChanged(isKmh)
            }
        )
        // Don't setup immediately, wait for data from ViewModel
        // Setup will be called after data is loaded
    }

    private fun onTemperatureUnitChanged(isCelsius: Boolean) {
        // Skip if updating from ViewModel to prevent infinite loop
        if (isUpdatingFromViewModel) return
        
        val temperature = if (isCelsius) Temperature.CELSIUS else Temperature.FAHRENHEIT
        viewModel.updateTemperatureUnit(temperature)
        Log.d(TAG, "Temperature unit changed to: ${temperature.name}")
    }

    private fun setupNotificationSwitches() {
        // Daily Summary switch
        binding.switchNotiDaily.setOnCheckedChangeListener { _, isChecked ->
            onDailySummaryChanged(isChecked)
        }

        // Danger Alert switch
        binding.switchNotiDanger.setOnCheckedChangeListener { _, isChecked ->
            onDangerAlertChanged(isChecked)
        }
    }

    private fun onWindSpeedUnitChanged(isKmh: Boolean) {
        // Skip if updating from ViewModel to prevent infinite loop
        if (isUpdatingFromViewModel) return
        
        val speedType = if (isKmh) SpeedType.KMH else SpeedType.MPH
        viewModel.updateWindSpeedUnit(speedType)
        Log.d(TAG, "Wind speed unit changed to: ${speedType.name}")
    }

    private fun onDailySummaryChanged(isEnabled: Boolean) {
        viewModel.updateDailyNotification(isEnabled)
        Log.d(TAG, "Daily Summary notification: ${if (isEnabled) "enabled" else "disabled"}")
    }

    private fun onDangerAlertChanged(isEnabled: Boolean) {
        viewModel.updateDangerNotification(isEnabled)
        Log.d(TAG, "Danger Alert notification: ${if (isEnabled) "enabled" else "disabled"}")
    }

    private fun setupSourceCodeClick() {
        val githubUrl = "https://github.com/hminq/Simple-Weather"

        // Click TextView "Source Code"
        binding.tvAboutSource.setOnClickListener {
            openUrl(githubUrl)
        }
        
        // Click ImageView icon
        binding.ivAboutSource.setOnClickListener {
            openUrl(githubUrl)
        }
    }

    private fun openUrl(url: String) {
        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            startActivity(intent)
        } catch (e: Exception) {
            Log.e(TAG, "Error opening URL: $url", e)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}