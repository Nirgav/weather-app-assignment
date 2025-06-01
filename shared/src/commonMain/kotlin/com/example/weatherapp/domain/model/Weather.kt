package com.example.weatherapp.domain.model

import kotlinx.serialization.Serializable

/**
 * Domain model representing weather data
 */
@Serializable
data class Weather(
    val temperature: Double?,
    val windSpeed: Double?,
    val timestamp: Long?,
    val weatherCode: Int? = null,
    val isDay: Boolean? = null,
    val windDirection: Int? = null,
    val timezone: String? = null,
    val timezoneAbbreviation: String? = null,
)
