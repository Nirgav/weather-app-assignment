package com.example.weatherapp.data.remote.api

import com.example.weatherapp.data.remote.core.ApiError
import com.example.weatherapp.data.remote.core.getResponse
import com.example.weatherapp.data.remote.dto.response.WeatherResponse
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.url

class WeatherApi(
    private val client: HttpClient,
) {
    suspend fun getCurrentWeather(
        latitude: Double,
        longitude: Double,
    ) = client
        .get {
            url("https://api.open-meteo.com/v1/forecast?latitude=$latitude&longitude=$longitude&current_weather=true")
        }.getResponse<WeatherResponse, ApiError<Unit>>()
}
