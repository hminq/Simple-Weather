package com.example.simpleweather.presentation

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.simpleweather.R
import com.example.simpleweather.databinding.FragmentWeatherBinding

class WeatherFragment : Fragment() {
    companion object {
        private const val TAG: String = "FRAGMENT_WEATHER"
    }
    private var _binding: FragmentWeatherBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWeatherBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getLocation()
        setupSettingButton()
    }

    private fun setupSettingButton() {
        binding.btnSetting.setOnClickListener { _ -> openSetting() }
    }

    private fun openSetting() {
        findNavController().navigate(R.id.action_weatherFragment_to_settingFragment)
    }

    private fun getLocation() {
        // Check if location permissions are granted
        val fineLocationGranted = ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        
        val coarseLocationGranted = ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        if (!fineLocationGranted && !coarseLocationGranted) {
            Log.w(TAG, "Location permissions not granted")
            // navigate back to PermissionFragment
            findNavController().navigate(R.id.action_weatherFragment_to_permissionFragment)
        }

        val locationManager = requireContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        
        // Get last known location
        try {
            val lastKnownLocation = if (fineLocationGranted) {
                locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                    ?: locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
            } else {
                locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
            }
            
            if (lastKnownLocation != null) {
                logLocation(lastKnownLocation.latitude, lastKnownLocation.longitude)
            } else {
                Log.w(TAG, "Last known location is not available")
            }
        } catch (e: SecurityException) {
            Log.e(TAG, "Error getting last known location", e)
        }
    }

    private fun logLocation(latitude: Double, longitude: Double) {
        Log.d(TAG, "Location - Latitude: $latitude, Longitude: $longitude")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
