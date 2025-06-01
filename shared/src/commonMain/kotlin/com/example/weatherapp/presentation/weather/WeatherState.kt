package com.example.weatherapp.presentation.weather

import com.example.weatherapp.Weather

enum class PermissionState {
    Denied,
    DeniedForever,
    NotDetermined,
}

sealed class WeatherUiState {
    data object Loading : WeatherUiState()

    data class Success(
        val weather: Weather,
    ) : WeatherUiState()

    data class Error(
        val message: String,
        val retryable: Boolean = true,
    ) : WeatherUiState()

    data class PermissionRequired(
        val message: String = "Location permission is required to get weather data",
        val permissionState: PermissionState,
        val showRationale: Boolean = false,
    ) : WeatherUiState()
}
