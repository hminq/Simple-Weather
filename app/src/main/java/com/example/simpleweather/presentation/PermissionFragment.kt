package com.example.simpleweather.presentation

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.simpleweather.R
import com.example.simpleweather.databinding.FragmentPermissionBinding

class PermissionFragment : Fragment() {
    companion object {
        private const val TAG: String = "FRAGMENT_PERMISSION"
    }
    private var _binding: FragmentPermissionBinding? = null
    private val binding get() = _binding!!

    // Permission launcher for location permissions
    private val locationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        // Check location permissions
        val fineLocationGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false
        val coarseLocationGranted = permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false
        
        if (fineLocationGranted || coarseLocationGranted) {
            // Navigate to WeatherFragment after location permissions granted
            findNavController().navigate(R.id.action_permissionFragment_to_weatherFragment)
        } else {
            Log.d(TAG, "Location permission denied")
            // Check if we should show rationale or guide user to Settings
            handlePermissionDenied()
        }
    }

    // Permission launcher for storage permission (for Android 9 and below)
    private val storagePermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            Log.d(TAG, "Storage permission granted")
        } else {
            Log.d(TAG, "Storage permission denied")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPermissionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        // Check if permissions are already granted
        // If yes, navigate directly to WeatherFragment without showing UI
        if (hasLocationPermissions()) {
            findNavController().navigate(R.id.action_permissionFragment_to_weatherFragment)
            return
        }
        
        // Otherwise, setup UI for requesting permissions
        setupPermissionButton()
    }
    
    private fun hasLocationPermissions(): Boolean {
        val fineLocationGranted = ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        
        val coarseLocationGranted = ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        
        return fineLocationGranted || coarseLocationGranted
    }

    private fun setupPermissionButton() {
        binding.btnGrantAccess.setOnClickListener {
            requestPermissions()
        }
    }

    private fun requestPermissions() {
        // Check if location permissions are already granted
        if (hasLocationPermissions()) {
            // Navigate to WeatherFragment after location permissions granted
            findNavController().navigate(R.id.action_permissionFragment_to_weatherFragment)
            return
        }
        
        // Check if we should show rationale before requesting
        val shouldShowRationale = shouldShowRequestPermissionRationale(
            Manifest.permission.ACCESS_FINE_LOCATION
        ) || shouldShowRequestPermissionRationale(
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
        
        if (shouldShowRationale) {
            // User previously denied, show explanation dialog
            showPermissionRationaleDialog()
        } else {
            // First time or permanently denied, request directly
            val locationPermissions = arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
            locationPermissionLauncher.launch(locationPermissions)
        }

        // Request storage permission only for Android 9 and below
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
            val storageGranted = ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED

            if (!storageGranted) {
                storagePermissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            } else {
                Log.d(TAG, "Storage permission already granted")
            }
        } else {
            // Android 10+ uses scoped storage, no permission needed for DataStore
            Log.d(TAG, "Storage permission not required on Android 10+")
        }
    }
    
    private fun handlePermissionDenied() {
        // Check if permission is permanently denied
        val fineLocationPermanentlyDenied = !shouldShowRequestPermissionRationale(
            Manifest.permission.ACCESS_FINE_LOCATION
        ) && ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_FINE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED
        
        val coarseLocationPermanentlyDenied = !shouldShowRequestPermissionRationale(
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) && ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED
        
        if (fineLocationPermanentlyDenied || coarseLocationPermanentlyDenied) {
            // Permission permanently denied, guide user to Settings
            showSettingsDialog()
        } else {
            // Permission denied but can still show rationale next time
            Log.d(TAG, "Permission denied, user can grant it next time")
        }
    }
    
    private fun showPermissionRationaleDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle(R.string.location_access_title)
            .setMessage(R.string.location_permission_rationale)
            .setPositiveButton(R.string.grant_permission) { _, _ ->
                // Request permission after user acknowledges rationale
                val locationPermissions = arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
                locationPermissionLauncher.launch(locationPermissions)
            }
            .setNegativeButton(R.string.cancel, null)
            .show()
    }
    
    private fun showSettingsDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle(R.string.location_access_title)
            .setMessage(R.string.location_permission_settings_message)
            .setPositiveButton(R.string.open_settings) { _, _ ->
                // Open app settings
                openAppSettings()
            }
            .setNegativeButton(R.string.cancel, null)
            .show()
    }
    
    private fun openAppSettings() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.fromParts("package", requireContext().packageName, null)
        }
        startActivity(intent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}