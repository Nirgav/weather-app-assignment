package com.example.weatherapp.data.remote.dto.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class WeatherResponse(
    @SerialName("current_weather")
    val currentWeather: CurrentWeather? = null,
    @SerialName("current_weather_units")
    val currentWeatherUnits: CurrentWeatherUnits? = null,
    @SerialName("elevation")
    val elevation: Double? = null,
    @SerialName("generationtime_ms")
    val generationtimeMs: Double? = null,
    @SerialName("latitude")
    val latitude: Double? = null,
    @SerialName("longitude")
    val longitude: Double? = null,
    @SerialName("timezone")
    val timezone: String? = null,
    @SerialName("timezone_abbreviation")
    val timezoneAbbreviation: String? = null,
    @SerialName("utc_offset_seconds")
    val utcOffsetSeconds: Int? = null
)