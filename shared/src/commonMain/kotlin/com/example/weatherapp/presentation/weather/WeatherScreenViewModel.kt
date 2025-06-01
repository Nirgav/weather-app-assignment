package com.example.weatherapp.presentation.weather

import com.example.weatherapp.Weather
import com.example.weatherapp.domain.core.NetworkResult
import com.example.weatherapp.domain.usecases.GetWeatherDataUseCase
import com.example.weatherapp.presentation.base.BaseViewModel
import dev.icerock.moko.permissions.DeniedAlwaysException
import dev.icerock.moko.permissions.DeniedException
import dev.icerock.moko.permissions.Permission
import dev.icerock.moko.permissions.PermissionsController
import dev.icerock.moko.permissions.RequestCanceledException
import dev.icerock.moko.permissions.location.LOCATION
import dev.jordond.compass.Priority
import dev.jordond.compass.geolocation.Locator
import dev.jordond.compass.geolocation.exception.GeolocationException
import dev.jordond.compass.geolocation.mobile.mobile
import dev.jordond.connectivity.Connectivity
import io.ktor.utils.io.errors.IOException
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class WeatherScreenViewModel(
    private val getWeatherDataUseCase: GetWeatherDataUseCase = GetWeatherDataUseCase(),
    private val permissionsController: PermissionsController,
) : BaseViewModel() {
    private val _uiState = MutableStateFlow<WeatherUiState>(WeatherUiState.Loading)
    val uiState = _uiState.asStateFlow()

    private var fetchWeatherJob: Job? = null
    private var isLocationRequestInProgress = false
    val connectivity = Connectivity()

    init {
        checkInitialPermissions()
    }

    private fun checkInitialPermissions() {
        connectivity.start()

        viewModelScope.launch {
            try {
                val isGranted = permissionsController.isPermissionGranted(Permission.LOCATION)

                if (isGranted) {
                    fetchWeatherData()
                } else {
                    _uiState.value =
                        WeatherUiState.PermissionRequired(
                            permissionState = PermissionState.NotDetermined,
                            showRationale = false,
                        )
                }
            } catch (e: Exception) {
                _uiState.value =
                    WeatherUiState.Error(
                        message = "Failed to check location permission: ${e.message}",
                        retryable = true,
                    )
            }
        }
    }

    fun requestLocationPermission() {
        viewModelScope.launch {
            try {
                permissionsController.providePermission(Permission.LOCATION)
                // If we reach here, permission was granted
                fetchWeatherData()
            } catch (deniedAlways: DeniedAlwaysException) {
                _uiState.value =
                    WeatherUiState.PermissionRequired(
                        message = "Location permission is permanently denied. Please enable it in Settings to get weather data.",
                        permissionState = PermissionState.DeniedForever,
                        showRationale = false,
                    )
            } catch (denied: DeniedException) {
                _uiState.value =
                    WeatherUiState.PermissionRequired(
                        message = "Location permission is required to get weather data for your area.",
                        permissionState = PermissionState.Denied,
                        showRationale = true,
                    )
            } catch (canceled: RequestCanceledException) {
                _uiState.value =
                    WeatherUiState.PermissionRequired(
                        message = "Permission request was canceled. Please try again.",
                        permissionState = PermissionState.Denied,
                        showRationale = true,
                    )
            } catch (e: Exception) {
                _uiState.value =
                    WeatherUiState.Error(
                        message = "Failed to request permission: ${e.message}",
                        retryable = true,
                    )
            }
        }
    }

    fun fetchWeatherData() {
        // Prevent multiple simultaneous requests
        if (isLocationRequestInProgress) {
            return
        }

        // Cancel any existing job to prevent race conditions
        fetchWeatherJob?.cancel()

        _uiState.value = WeatherUiState.Loading
        isLocationRequestInProgress = true

        fetchWeatherJob =
            viewModelScope.launch {
                try {
                    // First check if we still have permission
                    val isGranted = permissionsController.isPermissionGranted(Permission.LOCATION)

                    if (!isGranted) {
                        isLocationRequestInProgress = false
                        _uiState.value =
                            WeatherUiState.PermissionRequired(
                                message = "Location permission is required to get weather data",
                                permissionState = PermissionState.Denied,
                                showRationale = true,
                            )
                        return@launch
                    }

                    val location =
                        withContext(NonCancellable) {
                            try {
                                val locator = Locator.mobile()
                                locator.current(Priority.HighAccuracy)
                            } catch (e: Exception) {
                                isLocationRequestInProgress = false
                                throw e
                            }
                        }

                    isLocationRequestInProgress = false

                    getWeatherDataUseCase
                        .invoke(
                            location.coordinates.latitude,
                            location.coordinates.longitude,
                            forceRefresh =
                                connectivity.status() != Connectivity.Status.Disconnected,
                        ).collect { result ->
                            when (result) {
                                is NetworkResult.Success -> {
                                    _uiState.value = WeatherUiState.Success(result.data as Weather)
                                }

                                is NetworkResult.Error -> {
                                    _uiState.value =
                                        WeatherUiState.Error(
                                            message = result.message ?: "Unknown error occurred while fetching weather data",
                                            retryable = true,
                                        )
                                }
                            }
                        }
                } catch (error: CancellationException) {
                    // Handle cancellation gracefully
                    isLocationRequestInProgress = false
                    throw error
                } catch (error: Exception) {
                    isLocationRequestInProgress = false
                    handleLocationError(error)
                }
            }
    }

    private fun handleLocationError(error: Throwable) {
        when (error) {
            is GeolocationException -> {
                // Handle iOS Core Location errors and Android location errors
                when {
                    error.message?.contains("kCLErrorDomain error 1") == true ||
                        error.message?.contains("Location services are disabled") == true -> {
                        // Location services denied or disabled
                        _uiState.value =
                            WeatherUiState.PermissionRequired(
                                message = "Location access denied. Please enable location permissions in Settings.",
                                permissionState = PermissionState.Denied,
                                showRationale = true,
                            )
                    }
                    error.message?.contains("kCLErrorDomain error 0") == true ||
                        error.message?.contains("Unable to determine location") == true -> {
                        // Location service unable to determine location
                        _uiState.value =
                            WeatherUiState.Error(
                                message = "Unable to determine your location. Please ensure GPS is enabled and try again.",
                                retryable = true,
                            )
                    }
                    error.message?.contains("kCLErrorDomain error 2") == true ||
                        error.message?.contains("Network error") == true -> {
                        // Network error
                        _uiState.value =
                            WeatherUiState.Error(
                                message = "Network error while getting location. Please check your connection and try again.",
                                retryable = true,
                            )
                    }
                    error.message?.contains("Timeout") == true -> {
                        _uiState.value =
                            WeatherUiState.Error(
                                message = "Location request timed out. Please try again.",
                                retryable = true,
                            )
                    }
                    else -> {
                        _uiState.value =
                            WeatherUiState.Error(
                                message = "Location error: ${error.message ?: "Unable to get your location"}",
                                retryable = true,
                            )
                    }
                }
            }

            is IllegalStateException -> {
                _uiState.value =
                    WeatherUiState.Error(
                        message = "Location service error: ${error.message ?: "Please try again"}",
                        retryable = error !is IOException,
                    )
            }

            else -> {
                _uiState.value =
                    WeatherUiState.Error(
                        message = "Unexpected error: ${error.message ?: "Please try again"}",
                        retryable = error !is IOException,
                    )
            }
        }
    }

    fun retry() {
        viewModelScope.launch {
            val isGranted = permissionsController.isPermissionGranted(Permission.LOCATION)

            if (isGranted) {
                fetchWeatherData()
            } else {
                requestLocationPermission()
            }
        }
    }

    fun openSettings() {
        viewModelScope.launch {
            try {
                permissionsController.openAppSettings()
            } catch (e: Exception) {
                // Handle case where settings can't be opened
                _uiState.value =
                    WeatherUiState.Error(
                        message = "Unable to open settings. Please manually enable location permission for this app.",
                        retryable = false,
                    )
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        fetchWeatherJob?.cancel()
        isLocationRequestInProgress = false
    }
}
